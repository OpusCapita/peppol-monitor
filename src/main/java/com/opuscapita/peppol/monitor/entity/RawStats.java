package com.opuscapita.peppol.monitor.entity;

import com.opuscapita.peppol.commons.container.state.ProcessFlow;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Raw Stats Table for Oxalis
 */
@Entity
@DynamicUpdate
@Table(name = "raw_stats")
public class RawStats {

    @Id
    @GeneratedValue
    private long id;

    @Column(name = "ap", nullable = false, length = 35)
    private String apInfo;

    @Column(name = "direction")
    @Enumerated(EnumType.STRING)
    private ProcessFlow direction;

    @Column(name = "tstamp", columnDefinition = "timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP")
    private Timestamp timestamp;

    @Column(name = "sender", nullable = false, length = 35)
    private String sender;

    @Column(name = "receiver", nullable = false, length = 35)
    private String receiver;

    @Column(name = "doc_type", nullable = false)
    private String documentType;

    @Column(name = "profile")
    private String profileId;

    @Column(name = "channel")
    private String channel;

    public RawStats() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getApInfo() {
        return apInfo;
    }

    public void setApInfo(String apInfo) {
        this.apInfo = apInfo;
    }

    public ProcessFlow getDirection() {
        return direction;
    }

    public void setDirection(ProcessFlow direction) {
        this.direction = direction;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
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

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

}
