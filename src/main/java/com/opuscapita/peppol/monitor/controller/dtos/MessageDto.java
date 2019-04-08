package com.opuscapita.peppol.monitor.controller.dtos;

import com.opuscapita.peppol.commons.container.state.ProcessFlow;
import com.opuscapita.peppol.commons.container.state.Source;
import com.opuscapita.peppol.monitor.entity.Message;
import com.opuscapita.peppol.monitor.entity.MessageStatus;
import com.opuscapita.peppol.monitor.entity.Process;

import java.util.Date;

public class MessageDto {

    public Long id;
    public String messageId;
    public String transmissionId;
    public String filename;
    public MessageStatus status;
    public String sender;
    public String receiver;
    public String accessPoint;
    public Source source;
    public ProcessFlow direction;
    public String documentType;
    public String documentTypeId;
    public String profileId;
    public Date arrivedAt;

    public static MessageDto of(Message message) {
        Process process = message.getProcesses().get(message.getProcesses().size() - 1);

        MessageDto dto = new MessageDto();
        dto.id = message.getId();
        dto.messageId = message.getMessageId();
        dto.accessPoint = message.getAccessPoint();
        dto.source = message.getSource();
        dto.direction = message.getDirection();
        dto.transmissionId = process.getTransmissionId();
        dto.filename = process.getFilename();
        dto.status = process.getStatus();
        dto.sender = process.getSender();
        dto.receiver = process.getReceiver();
        dto.documentType = process.getDocumentType();
        dto.documentTypeId = process.getDocumentTypeId();
        dto.profileId = process.getProfileId();
        dto.arrivedAt = process.getArrivedAt();
        return dto;
    }

}
