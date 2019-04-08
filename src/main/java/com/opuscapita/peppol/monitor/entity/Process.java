package com.opuscapita.peppol.monitor.entity;

import com.opuscapita.peppol.commons.container.state.ProcessFlow;
import com.opuscapita.peppol.commons.container.state.Source;
import com.opuscapita.peppol.commons.container.state.log.DocumentLog;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@DynamicUpdate
@Table(name = "processes", indexes = {@Index(name = "ix_transmission_id", columnList = "transmission_id")})
public class Process {

    @Id
    @Column(name = "id")
    @GeneratedValue
    private Long id;

    @Column(name = "transmission_id", unique = true, nullable = false, length = 50)
    private String transmissionId;

    @Column(name = "filename", nullable = false)
    private String filename;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private MessageStatus status;

    @Column(name = "source", nullable = false)
    @Enumerated(EnumType.STRING)
    private Source source;

    @Column(name = "direction")
    @Enumerated(EnumType.STRING)
    private ProcessFlow direction;

    @Column(name = "sender", length = 35)
    private String sender;

    @Column(name = "receiver", length = 35)
    private String receiver;

    @Column(name = "access_point", length = 35)
    private String accessPoint;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "document_type_id")
    private String documentTypeId;

    @Column(name = "profile_id")
    private String profileId;

    @Column(name = "arrived_at")
    private Date arrivedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "message_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Message message;

    @Lob
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

    public String getTransmissionId() {
        return transmissionId;
    }

    public void setTransmissionId(String transmissionId) {
        this.transmissionId = transmissionId;
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

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
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
