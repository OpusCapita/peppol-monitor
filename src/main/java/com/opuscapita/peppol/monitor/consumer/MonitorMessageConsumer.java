package com.opuscapita.peppol.monitor.consumer;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.metadata.AccessPointInfo;
import com.opuscapita.peppol.commons.container.metadata.ContainerMessageMetadata;
import com.opuscapita.peppol.commons.container.state.ProcessStep;
import com.opuscapita.peppol.commons.queue.consume.ContainerMessageConsumer;
import com.opuscapita.peppol.monitor.entity.*;
import com.opuscapita.peppol.monitor.entity.Process;
import com.opuscapita.peppol.monitor.repository.AccessPointRepository;
import com.opuscapita.peppol.monitor.repository.MessageService;
import com.opuscapita.peppol.monitor.repository.ParticipantRepository;
import com.opuscapita.peppol.monitor.repository.ProcessService;
import com.opuscapita.peppol.monitor.util.ProcessHistorySerializer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MonitorMessageConsumer implements ContainerMessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(MonitorMessageConsumer.class);

    private MessageService messageService;
    private ProcessService processService;
    private ProcessHistorySerializer historySerializer;
    private AccessPointRepository accessPointRepository;
    private ParticipantRepository participantRepository;

    @Autowired
    public MonitorMessageConsumer(MessageService messageService, ProcessService processService, ProcessHistorySerializer historySerializer,
                                  AccessPointRepository accessPointRepository, ParticipantRepository participantRepository) {
        this.messageService = messageService;
        this.processService = processService;
        this.historySerializer = historySerializer;
        this.accessPointRepository = accessPointRepository;
        this.participantRepository = participantRepository;
    }

    @Override
    public void consume(@NotNull ContainerMessage cm) {
        logger.info("Monitor received the message: " + cm.toKibana());

        String messageId = cm.getMetadata().getMessageId();
        String transmissionId = cm.getMetadata().getTransmissionId();

        Message message = messageService.getMessage(messageId);
        if (message == null) {
            message = createMessageEntity(cm);
        }

        Process process = processService.getProcess(transmissionId);
        if (process == null) {
            process = createProcessEntity(cm, message);
        } else {
            process = updateProcessEntity(cm, process);
        }

        processService.saveProcess(process);
        messageService.saveMessage(message);

        logger.info("Monitor saved the message: " + cm.getFileName());
    }

    private Process updateProcessEntity(ContainerMessage cm, Process process) {
        ContainerMessageMetadata metadata = cm.getMetadata();

        process.setFilename(cm.getFileName());
        process.setStatus(extractStatusInfo(cm));
        process.setSender(getParticipant(metadata.getSenderId()));
        process.setReceiver(getParticipant(metadata.getRecipientId()));
        process.setDocumentType(null); // need to find a way to set this
        process.setDocumentTypeId(metadata.getDocumentTypeIdentifier());
        process.setProfileId(metadata.getProfileTypeIdentifier());
        process.setRawHistory(historySerializer.toJson(cm.getHistory().getLogs()));

        return process;
    }

    private Process createProcessEntity(ContainerMessage cm, Message message) {
        ContainerMessageMetadata metadata = cm.getMetadata();

        Process process = new Process();
        process.setMessage(message);
        process.setTransmissionId(metadata.getTransmissionId());
        process.setArrivedAt(metadata.getTimestamp());

        process = updateProcessEntity(cm, process);
        message.getProcesses().add(process);

        return process;
    }

    private Message createMessageEntity(ContainerMessage cm) {
        ContainerMessageMetadata metadata = cm.getMetadata();

        Message message = new Message();
        message.setMessageId(metadata.getMessageId());
        message.setSource(cm.getSource());
        message.setDirection(cm.getFlow());
        message.setAccessPoint(getAccessPointId(cm.getApInfo()));

        message = messageService.saveMessage(message);
        return message;
    }

    private String getParticipant(String participantId) {
        Participant participant = participantRepository.findById(participantId).orElse(null);
        if (participant != null) {
            return participant.getId();
        }

        logger.debug("Participant: " + participantId + " couldn't found, creating a new one.");
        participant = new Participant(participantId);
        participant = participantRepository.save(participant);
        return participant.getId();
    }

    private String getAccessPointId(AccessPointInfo accessPointInfo) {
        if (accessPointInfo == null) {
            return null;
        }

        AccessPoint accessPoint = accessPointRepository.findById(accessPointInfo.getId()).orElse(null);
        if (accessPoint != null) {
            return accessPoint.getId();
        }

        logger.debug("AccessPoint: " + accessPointInfo.getId() + " couldn't found, creating a new one.");
        accessPoint = new AccessPoint(accessPointInfo);
        accessPoint = accessPointRepository.save(accessPoint);
        return accessPoint.getId();
    }

    private MessageStatus extractStatusInfo(ContainerMessage cm) {
        if (cm.getHistory().hasError()) {
            return MessageStatus.failed;
        }
        if (cm.getStep().equals(ProcessStep.INBOUND)) {
            return MessageStatus.received;
        }
        if (cm.getStep().equals(ProcessStep.PROCESS)) {
            return MessageStatus.processing;
        }
        if (cm.getStep().equals(ProcessStep.VALIDATION)) {
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

}
