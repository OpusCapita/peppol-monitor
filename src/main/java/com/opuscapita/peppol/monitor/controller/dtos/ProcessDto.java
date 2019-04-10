package com.opuscapita.peppol.monitor.controller.dtos;

import com.opuscapita.peppol.commons.container.state.ProcessFlow;
import com.opuscapita.peppol.commons.container.state.Source;
import com.opuscapita.peppol.monitor.entity.MessageStatus;
import com.opuscapita.peppol.monitor.entity.Process;

import java.util.Date;

public class ProcessDto {

    public Long id;
    public String messageId;
    public String transmissionId;
    public String filename;
    public MessageStatus status;
    public MessageStatus messageStatus;
    public String sender;
    public String receiver;
    public String accessPoint;
    public Source source;
    public ProcessFlow direction;
    public String documentType;
    public String documentTypeId;
    public String profileId;
    public Date arrivedAt;

    public static ProcessDto of(Process process) {
        if (process == null) {
            return null;
        }

        ProcessDto dto = new ProcessDto();
        dto.id = process.getId();
        dto.messageId = process.getMessage().getMessageId();
        dto.accessPoint = process.getAccessPoint();
        dto.source = process.getSource();
        dto.direction = process.getDirection();
        dto.transmissionId = process.getTransmissionId();
        dto.filename = process.getFilename();
        dto.status = process.getStatus();
        dto.messageStatus = process.getMessage().getLastProcess().getStatus();
        dto.sender = process.getSender();
        dto.receiver = process.getReceiver();
        dto.documentType = process.getDocumentType();
        dto.documentTypeId = process.getDocumentTypeId();
        dto.profileId = process.getProfileId();
        dto.arrivedAt = process.getArrivedAt();
        return dto;
    }

}
