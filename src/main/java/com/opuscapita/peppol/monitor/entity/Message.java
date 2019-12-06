package com.opuscapita.peppol.monitor.entity;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A message is a virtual representation of a physical document
 * It has transmission list, defined for each transmission of the document to the AP flow
 */
@Entity
@DynamicUpdate
@Table(name = "messages", indexes = {@Index(name = "ix_message_id", columnList = "message_id")})
public class Message {

    @Id
    @Column(name = "id")
    @GeneratedValue
    private Long id;

    @Column(name = "message_id", unique = true, nullable = false, length = 100)
    private String messageId;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    @ElementCollection(targetClass = Transmission.class)
    private List<Transmission> transmissionList;

    public Message() {
        this.transmissionList = new ArrayList<>();
    }

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

    public List<Transmission> getTransmissionList() {
        return transmissionList;
    }

    public void setTransmissionList(List<Transmission> transmissionList) {
        this.transmissionList = transmissionList;
    }

    public Transmission getLastTransmission() {
        return this.transmissionList.stream().max(Comparator.comparingLong(Transmission::getId)).orElse(null);
    }
}
