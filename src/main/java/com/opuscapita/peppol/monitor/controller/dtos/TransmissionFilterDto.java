package com.opuscapita.peppol.monitor.controller.dtos;

import com.opuscapita.peppol.commons.container.state.Source;
import com.opuscapita.peppol.monitor.entity.MessageStatus;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class TransmissionFilterDto {

    private String id;
    private String messageId;
    private String filename;
    private String sender;
    private String receiver;
    private String accessPoint;
    private String invoiceNumber;
    private Date startDate;
    private Date endDate;
    private String history;
    private String errorType;
    private List<Source> sources;
    private List<Source> destinations;
    private List<String> documentTypeIds;
    private List<MessageStatus> statuses;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getAccessPoint() {
        return accessPoint;
    }

    public void setAccessPoint(String accessPoint) {
        this.accessPoint = accessPoint;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public String getErrorType() {
        if ("null".equals(errorType)) {
            return null;
        }
        return errorType;
    }

    public void setErrorType(String errorType) {
        this.errorType = errorType;
    }

    public List<Source> getSources() {
        return sources;
    }

    public void setSources(List<Source> sources) {
        this.sources = sources;
    }

    public List<String> getDestinations() {
        if (destinations != null) {
            return destinations.stream().map(d -> d.name().toLowerCase()).collect(Collectors.toList());
        }
        return null;
    }

    public void setDestinations(List<Source> destinations) {
        this.destinations = destinations;
    }

    public List<String> getDocumentTypeIds() {
        return documentTypeIds;
    }

    public void setDocumentTypeIds(List<String> documentTypeIds) {
        this.documentTypeIds = documentTypeIds;
    }

    public List<MessageStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<MessageStatus> statuses) {
        this.statuses = statuses;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
