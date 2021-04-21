package com.opuscapita.peppol.monitor.consumer;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.metadata.AccessPointInfo;
import com.opuscapita.peppol.commons.container.metadata.ContainerBusinessMetadata;
import com.opuscapita.peppol.commons.container.metadata.ContainerMessageMetadata;
import com.opuscapita.peppol.commons.container.state.ProcessStep;
import com.opuscapita.peppol.commons.queue.consume.ContainerMessageConsumer;
import com.opuscapita.peppol.monitor.entity.*;
import com.opuscapita.peppol.monitor.mlrreport.MlrReportManager;
import com.opuscapita.peppol.monitor.repository.AccessPointRepository;
import com.opuscapita.peppol.monitor.repository.MessageService;
import com.opuscapita.peppol.monitor.repository.ParticipantRepository;
import com.opuscapita.peppol.monitor.repository.TransmissionService;
import com.opuscapita.peppol.monitor.util.TransmissionHistorySerializer;
import com.opuscapita.peppol.commons.container.state.Source;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class MonitorMessageConsumer implements ContainerMessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(MonitorMessageConsumer.class);

    private MlrReportManager mlrManager;
    private MessageService messageService;
    private TransmissionService transmissionService;
    private AccessPointRepository accessPointRepository;
    private ParticipantRepository participantRepository;
    private TransmissionHistorySerializer historySerializer;

    @Autowired
    public MonitorMessageConsumer(MlrReportManager mlrManager, MessageService messageService,
                                  TransmissionService transmissionService, AccessPointRepository accessPointRepository,
                                  ParticipantRepository participantRepository, TransmissionHistorySerializer historySerializer) {
        this.mlrManager = mlrManager;
        this.messageService = messageService;
        this.historySerializer = historySerializer;
        this.transmissionService = transmissionService;
        this.accessPointRepository = accessPointRepository;
        this.participantRepository = participantRepository;
    }

    @Override
    public void consume(@NotNull ContainerMessage cm) throws Exception {
        logger.info("Monitor received the message: " + toKibana(cm));

/*
Apr 13, 2021 @ 15:34:14.916	2021-04-13 13:34:14.916 ERROR 1 --- [    container-1] o.h.i.ExceptionMapperStandardImpl        : HHH000346: Error during managed flush [org.hibernate.exception.ConstraintViolationException: could not execute statement]
Apr 13, 2021 @ 15:34:14.914	2021-04-13 13:34:14.914 ERROR 1 --- [    container-1] o.h.engine.jdbc.spi.SqlExceptionHelper   : Column 'transmission_id' cannot be null
Apr 13, 2021 @ 15:34:14.914	2021-04-13 13:34:14.914  WARN 1 --- [    container-1] o.h.engine.jdbc.spi.SqlExceptionHelper   : SQL Error: 1048, SQLState: 23000
*/

        if (cm.getMetadata() == null) {
            logger.warn("Ignoring message without a valid metadata: " + cm.getFileName());
            return;
        }

        String messageId = cm.getMetadata().getMessageId();
        String transmissionId = cm.getMetadata().getTransmissionId();
        Message message = messageService.getMessage(messageId);

        if (message == null) {
            message = createMessageEntity(cm);
        }

        Transmission transmission = transmissionService.getTransmission(transmissionId);

        if (transmission == null) {
            transmission = createTransmissionEntity(cm, message);
        } else if (transmission.getStatus().isFinal()) {
            return;

        } else {
            transmission = updateTransmissionEntity(cm, transmission);
        }

        try {
            transmissionService.saveTransmission(transmission);
            logger.info("Monitor saved the message: " + transmission.getFilename() + " with status: " + transmission.getStatus());

        } catch (Exception e) {
            logger.info("Exception " + e );
            handleDBErrors(transmission, cm, e);
        }

        mlrManager.sendToMlrReporter(cm, transmission.getStatus());

    }

    private Transmission updateTransmissionEntity(ContainerMessage cm, Transmission transmission) {
        ContainerMessageMetadata metadata = cm.getMetadata();
        ContainerBusinessMetadata business = metadata.getBusinessMetadata();
        business = business == null ? new ContainerBusinessMetadata() : business;

        transmission.setFilename(cm.getFileName());
        transmission.setStatus(extractStatusInfo(cm));
        transmission.setSource(cm.getSource());
        transmission.setSender(getParticipant(metadata.getSenderId(), business.getSenderName()));
        transmission.setReceiver(getParticipant(metadata.getRecipientId(), business.getReceiverName()));

        //TODO
        //Use accesspoint from sender if sender type is GW
        if( cm.getSource() == Source.GW )  {
          transmission.setAccessPoint(metadata.getSendingAccessPoint());
        }
        else {
          transmission.setAccessPoint(getAccessPointId(cm.getApInfo()));
        }

        transmission.setInvoiceNumber(business.getDocumentId());
        transmission.setInvoiceDate(business.getIssueDate());
        if (cm.getRoute() != null && cm.getRoute().getDestination() != null) {
            transmission.setDirection(cm.getRoute().getDestination());
        }
        if (metadata.getValidationRule() != null && metadata.getValidationRule().getId() != null) {
            transmission.setDocumentTypeId(metadata.getValidationRule().getId().toString());
        }
        if (MessageStatus.delivered.equals(transmission.getStatus()) || MessageStatus.fixed.equals(transmission.getStatus())) {
            transmission.setRawHistory(null);
        } else {
            transmission.setRawHistory(historySerializer.toJson(cm.getHistory().getLogs().stream().filter(log -> !log.isWarning()).collect(Collectors.toList())));
        }

        return transmission;
    }

    private Transmission createTransmissionEntity(ContainerMessage cm, Message message) {
        ContainerMessageMetadata metadata = cm.getMetadata();

        Transmission transmission = new Transmission();
        transmission.setMessage(message);
        transmission.setTransmissionId(metadata.getTransmissionId());
        transmission.setArrivedAt(metadata.getTimestamp());

        transmission = updateTransmissionEntity(cm, transmission);
        message.getTransmissionList().add(transmission);

        return transmission;
    }

    private Message createMessageEntity(ContainerMessage cm) {
        Message message = new Message();
        message.setMessageId(cm.getMetadata().getMessageId());
        message = messageService.saveMessage(message);
        return message;
    }

    private String getParticipant(String participantId, String participantName) {
        if (StringUtils.isBlank(participantId)) {
            return null;
        }

        Participant participant = participantRepository.findById(participantId).orElse(null);
        if (participant == null) {
            logger.debug("Participant: " + participantId + " couldn't found, creating a new one.");
            participant = new Participant(participantId);
        }

        if (StringUtils.isNotBlank(participantName)) {
            participant.setName(participantName);
        }

        try {
            participant = participantRepository.saveAndFlush(participant);
        } catch (Exception e) {
            logger.debug("Couldn't save the participant (" + participant.getId() + "), reason: " + e.getMessage());
        }
        return participant.getId();
    }

    private String getAccessPointId(AccessPointInfo accessPointInfo) {
        if (accessPointInfo == null) {
            return null;
        }

        AccessPoint accessPoint = accessPointRepository.findById(accessPointInfo.getId()).orElse(null);
        if (accessPoint != null) {
            logger.debug("AccessPoint: " + accessPointInfo.getId() + " found, updating fields.");
            accessPoint.update(accessPointInfo);
        } else {
            logger.debug("AccessPoint: " + accessPointInfo.getId() + " couldn't found, creating a new one.");
            accessPoint = new AccessPoint(accessPointInfo);
        }

        try {
            accessPoint = accessPointRepository.saveAndFlush(accessPoint);
        } catch (Exception e) {
            logger.warn("Couldn't save the access point (" + accessPoint.getId() + "), reason: " + e.getMessage());
        }
        return accessPoint.getId();
    }

    private MessageStatus extractStatusInfo(ContainerMessage cm) {
        if (cm.getHistory().hasError()) {
            return MessageStatus.failed;
        }
        if (cm.getStep().equals(ProcessStep.INBOUND)) {
            return MessageStatus.received;
        }
        if (cm.getStep().equals(ProcessStep.PROCESSOR)) {
            return MessageStatus.processing;
        }
        if (cm.getStep().equals(ProcessStep.VALIDATOR)) {
            return MessageStatus.validating;
        }
        if (cm.getStep().equals(ProcessStep.OUTBOUND)) {
            return MessageStatus.sending;
        }
        if (cm.getStep().equals(ProcessStep.NETWORK)) {
            return MessageStatus.delivered;
        }
        return MessageStatus.unknown;
    }

    // https://opuscapita.atlassian.net/wiki/spaces/IIPEP/pages/773882045/Monitoring+Service+Concurrency+Issues
    private void handleDBErrors(Transmission transmission, ContainerMessage cm, Exception e) throws Exception {
        logger.debug("Error occurred while saving the message: " + transmission.getFilename() + " [status: " + transmission.getStatus() + "], reason: " + e.getMessage());

        if (transmission.getStatus().isFinal()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            consume(cm);
        }
    }

    private String toKibana(ContainerMessage cm) {
        ContainerMessageMetadata metadata = cm.getMetadata();
        String result = "[file: {filename}, step: {step}, source: {source}, status: {status}, messageId: {messageId}, transmissionId: {transmissionId}]";
        result = result.replace("{filename}", "{" + cm.getFileName() + "}");
        result = result.replace("{step}", "{" + cm.getStep().name() + "}");
        result = result.replace("{source}", "{" + cm.getSource().name() + "}");
        result = result.replace("{status}", "{" + cm.getHistory().getLastLog() + "}");
        result = result.replace("{messageId}", "{" + (metadata == null ? "-" : metadata.getMessageId()) + "}");
        result = result.replace("{transmissionId}", "{" + (metadata == null ? "-" : metadata.getTransmissionId()) + "}");
        return result;
    }

}
