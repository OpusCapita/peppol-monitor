package com.opuscapita.peppol.monitor.consumer;

import com.opuscapita.peppol.commons.container.ContainerMessage;
import com.opuscapita.peppol.commons.container.metadata.AccessPointInfo;
import com.opuscapita.peppol.commons.container.metadata.PeppolMessageMetadata;
import com.opuscapita.peppol.commons.container.state.ProcessStep;
import com.opuscapita.peppol.commons.queue.consume.ContainerMessageConsumer;
import com.opuscapita.peppol.monitor.entity.AccessPoint;
import com.opuscapita.peppol.monitor.entity.Message;
import com.opuscapita.peppol.monitor.entity.MessageStatus;
import com.opuscapita.peppol.monitor.entity.Participant;
import com.opuscapita.peppol.monitor.repository.AccessPointRepository;
import com.opuscapita.peppol.monitor.repository.MessageService;
import com.opuscapita.peppol.monitor.repository.ParticipantRepository;
import com.opuscapita.peppol.monitor.util.MessageHistorySerializer;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MonitorMessageConsumer implements ContainerMessageConsumer {

    private static final Logger logger = LoggerFactory.getLogger(MonitorMessageConsumer.class);

    private MessageService messageService;
    private MessageHistorySerializer historySerializer;
    private AccessPointRepository accessPointRepository;
    private ParticipantRepository participantRepository;

    @Autowired
    public MonitorMessageConsumer(MessageService messageService, MessageHistorySerializer historySerializer,
                                  AccessPointRepository accessPointRepository, ParticipantRepository participantRepository) {
        this.messageService = messageService;
        this.historySerializer = historySerializer;
        this.accessPointRepository = accessPointRepository;
        this.participantRepository = participantRepository;
    }

    @Override
    public void consume(@NotNull ContainerMessage cm) {
        logger.info("Monitor received the message: " + cm.toKibana());

        String messageId = cm.getMetadata().getMessageId();
        Message message = messageService.getMessage(messageId);
        if (message == null) {
            message = createMessageEntity(cm);
        } else {
            message = updateMessageEntity(cm, message);
        }
        messageService.saveMessage(message);

        logger.info("Monitor saved the message: " + cm.getFileName());
    }

    private Message updateMessageEntity(ContainerMessage cm, Message message) {
        return setCommonFields(cm, message);
    }

    private Message createMessageEntity(ContainerMessage cm) {
        PeppolMessageMetadata metadata = cm.getMetadata();

        Message message = new Message();
        message.setMessageId(metadata.getMessageId());
        message.setSource(cm.getEndpoint().getSource());
        message.setDirection(cm.getEndpoint().getFlow());
        message.setArrivedAt(metadata.getReceivedTimeStamp());

        return setCommonFields(cm, message);
    }

    private Message setCommonFields(ContainerMessage cm, Message message) {
        PeppolMessageMetadata metadata = cm.getMetadata();

        message.setFilename(FilenameUtils.getName(cm.getFileName()));
        message.setStatus(extractStatusInfo(cm));
        message.setSender(getParticipant(metadata.getSenderId()));
        message.setReceiver(getParticipant(metadata.getRecipientId()));
        message.setAccessPoint(getAccessPointId(cm.getApInfo()));
        message.setDocumentType(null); // need to find a way to set this
        message.setDocumentTypeId(metadata.getDocumentTypeIdentifier());
        message.setProfileId(metadata.getProfileTypeIdentifier());
        message.setRawHistory(historySerializer.toJson(cm.getHistory().getLogs()));

        return message;
    }

    private Participant getParticipant(String participantId) {
        Participant participant = null;
        try {
            participant = participantRepository.getOne(participantId);
        } catch (Exception e) {
            logger.debug("Participant: " + participantId + " couldn't found, creating a new one.");
        }
        if (participant != null) {
            return participant;
        }
        participant = new Participant(participantId);
        return participantRepository.save(participant);
    }

    private String getAccessPointId(AccessPointInfo accessPointInfo) {
        if (accessPointInfo == null) {
            return null;
        }
        AccessPoint accessPoint = null;
        try {
            accessPoint = accessPointRepository.getOne(accessPointInfo.getId());
        } catch (Exception e) {
            logger.debug("AccessPoint: " + accessPointInfo.getId() + " couldn't found, creating a new one.");
        }
        if (accessPoint != null) {
            return accessPoint.getId();
        }
        accessPoint = new AccessPoint(accessPointInfo);
        return accessPointRepository.save(accessPoint).getId();
    }

    private MessageStatus extractStatusInfo(ContainerMessage cm) {
        if (cm.getHistory().hasError()) {
            return MessageStatus.failed;
        }
        if (cm.getEndpoint().getStep().equals(ProcessStep.INBOUND)) {
            return MessageStatus.received;
        }
        if (cm.getEndpoint().getStep().equals(ProcessStep.PROCESS)) {
            return MessageStatus.processing;
        }
        if (cm.getEndpoint().getStep().equals(ProcessStep.VALIDATION)) {
            return MessageStatus.validating;
        }
        if (cm.getEndpoint().getStep().equals(ProcessStep.OUTBOUND)) {
            return MessageStatus.sending;
        }
        if (cm.getEndpoint().getStep().equals(ProcessStep.NETWORK)) {
            return MessageStatus.delivered;
        }
        return MessageStatus.unknown;
    }

}
