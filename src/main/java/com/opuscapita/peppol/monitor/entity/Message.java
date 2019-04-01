package com.opuscapita.peppol.monitor.entity;

import com.opuscapita.peppol.commons.container.state.ProcessFlow;
import com.opuscapita.peppol.commons.container.state.Source;
import com.opuscapita.peppol.commons.container.state.log.DocumentLog;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "messages", indexes = {@Index(name = "ix_message_id", columnList = "message_id")})
public class Message {

    @Id
    @Column(name = "id")
    @GeneratedValue
    private Long id;

    @Column(name = "message_id", nullable = false, length = 50)
    private String messageId;

    @Column(name = "filename", nullable = false)
    private String filename;

    @Column(name = "status")
    private MessageStatus status;

    @Column(name = "sender")
    private String sender;

    @Column(name = "receiver")
    private String receiver;

    @Column(name = "access_point")
    private String accessPoint;

    @Column(name = "source", nullable = false)
    private Source source;

    @Column(name = "direction")
    private ProcessFlow direction;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "document_type_id")
    private String documentTypeId;

    @Column(name = "profile_id")
    private String profileId;

    @Column(name = "arrived_at")
    private Date arrivedAt;

    @Column(name = "history")
    private String rawHistory;

    @Transient
    private List<DocumentLog> logs;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
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

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public ProcessFlow getDirection() {
        return direction;
    }

    public void setDirection(ProcessFlow direction) {
        this.direction = direction;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(String documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public Date getArrivedAt() {
        return arrivedAt;
    }

    public void setArrivedAt(Date arrivedAt) {
        this.arrivedAt = arrivedAt;
    }

    public String getRawHistory() {
        return rawHistory;
    }

    public void setRawHistory(String rawHistory) {
        this.rawHistory = rawHistory;
    }

    public List<DocumentLog> getLogs() {
        return logs;
    }

    public void setLogs(List<DocumentLog> logs) {
        this.logs = logs;
    }

}
