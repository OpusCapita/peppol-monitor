package com.opuscapita.peppol.monitor.controller.dtos;

import com.opuscapita.peppol.commons.container.state.Source;
import com.opuscapita.peppol.monitor.entity.MessageStatus;
import com.opuscapita.peppol.monitor.entity.Transmission;

import java.util.Date;

public class TransmissionDto {

    public Long id;
    public String messageId;
    public String transmissionId;
    public String filename;
    public MessageStatus status;
    public MessageStatus messageStatus;
    public String sender;
    public String receiver;
    public String accessPoint;
    public String invoiceNumber;
    public String invoiceDate;
    public Source source;
    public String direction;
    public String documentTypeId;
    public Date arrivedAt;

    public static TransmissionDto of(Transmission transmission) {
        if (transmission == null) {
            return null;
        }

        TransmissionDto dto = new TransmissionDto();
        dto.id = transmission.getId();
        dto.messageId = transmission.getMessage().getMessageId();
        dto.accessPoint = transmission.getAccessPoint();
        dto.invoiceNumber = transmission.getInvoiceNumber();
        dto.invoiceDate = transmission.getInvoiceDate();
        dto.source = transmission.getSource();
        dto.direction = transmission.getDirection();
        dto.documentTypeId = transmission.getDocumentTypeId();
        dto.transmissionId = transmission.getTransmissionId();
        dto.filename = transmission.getFilename();
        dto.status = transmission.getStatus();
        dto.messageStatus = transmission.getMessage().getLastTransmission().getStatus();
        dto.sender = transmission.getSender();
        dto.receiver = transmission.getReceiver();
        dto.arrivedAt = transmission.getArrivedAt();
        return dto;
    }

}
