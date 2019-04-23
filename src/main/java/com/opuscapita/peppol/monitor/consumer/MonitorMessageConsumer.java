package com.opuscapita.peppol.monitor.consumer;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.metadata.AccessPointInfo;
import com.opuscapita.peppol.commons.container.metadata.ContainerMessageMetadata;
import com.opuscapita.peppol.commons.container.state.ProcessStep;
import com.opuscapita.peppol.commons.queue.consume.ContainerMessageConsumer;
import com.opuscapita.peppol.monitor.entity.*;
import com.opuscapita.peppol.monitor.entity.Transmission;
import com.opuscapita.peppol.monitor.repository.AccessPointRepository;
import com.opuscapita.peppol.monitor.repository.MessageService;
import com.opuscapita.peppol.monitor.repository.ParticipantRepository;
import com.opuscapita.peppol.monitor.repository.TransmissionService;
import com.opuscapita.peppol.monitor.util.TransmissionHistorySerializer;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MonitorMessageConsumer implements ContainerMessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(MonitorMessageConsumer.class);

    private MessageService messageService;
    private TransmissionService transmissionService;
    private AccessPointRepository accessPointRepository;
    private ParticipantRepository participantRepository;
    private TransmissionHistorySerializer historySerializer;

    @Autowired
    public MonitorMessageConsumer(MessageService messageService, TransmissionService transmissionService,
                                  AccessPointRepository accessPointRepository, ParticipantRepository participantRepository,
                                  TransmissionHistorySerializer historySerializer) {
        this.messageService = messageService;
        this.historySerializer = historySerializer;
        this.transmissionService = transmissionService;
        this.accessPointRepository = accessPointRepository;
        this.participantRepository = participantRepository;
    }

    @Override
    public void consume(@NotNull ContainerMessage cm) {
        logger.info("Monitor received the message: " + toKibana(cm));

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
        } else {
            transmission = updateTransmissionEntity(cm, transmission);
        }
        transmissionService.saveTransmission(transmission);

        logger.info("Monitor saved the message: " + cm.getFileName());
    }

    private Transmission updateTransmissionEntity(ContainerMessage cm, Transmission transmission) {
        ContainerMessageMetadata metadata = cm.getMetadata();

        if (checkForRaceConditionIssue(cm, transmission)) {
            return transmission;
        }

        transmission.setFilename(cm.getFileName());
        transmission.setStatus(extractStatusInfo(cm));
        transmission.setSource(cm.getSource());
        transmission.setDirection(cm.getFlow());
        transmission.setSender(getParticipant(metadata.getSenderId()));
        transmission.setReceiver(getParticipant(metadata.getRecipientId()));
        transmission.setAccessPoint(getAccessPointId(cm.getApInfo()));
        transmission.setDocumentType(null); // need to find a way to set this
        transmission.setDocumentTypeId(metadata.getDocumentTypeIdentifier());
        transmission.setProfileId(metadata.getProfileTypeIdentifier());
        transmission.setRawHistory(historySerializer.toJson(cm.getHistory().getLogs()));

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

    private String getParticipant(String participantId) {
        if (StringUtils.isBlank(participantId)) {
            return null;
        }

        Participant participant = participantRepository.findById(participantId).orElse(null);
        if (participant != null) {
            return participant.getId();
        }

        logger.debug("Participant: " + participantId + " couldn't found, creating a new one.");
        participant = new Participant(participantId);
        try {
            participant = participantRepository.save(participant);
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
            accessPoint = accessPointRepository.save(accessPoint);
        } catch (Exception e) {
            logger.debug("Couldn't save the access point (" + accessPoint.getId() + "), reason: " + e.getMessage());
        }
        return accessPoint.getId();
    }

    // a transmission cannot go backwards, sometimes outbound events come faster than validator events
    private boolean checkForRaceConditionIssue(ContainerMessage cm, Transmission transmission) {
        if (transmission.getStatus() == null) {
            return false;
        }
        if ((MessageStatus.delivered.equals(transmission.getStatus()) || MessageStatus.sending.equals(transmission.getStatus()))
                && ProcessStep.VALIDATOR.equals(cm.getStep())) {
            return true;
        }
        if (MessageStatus.validating.equals(transmission.getStatus()) && ProcessStep.PROCESSOR.equals(cm.getStep())) {
            return true;
        }
        if (MessageStatus.processing.equals(transmission.getStatus()) && ProcessStep.INBOUND.equals(cm.getStep())) {
            return true;
        }
        return false;
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
