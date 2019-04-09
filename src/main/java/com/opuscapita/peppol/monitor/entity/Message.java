package com.opuscapita.peppol.monitor.entity;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A message is a virtual representation of a physical document
 * It has process list, defined for each process of the document
 */
@Entity
@DynamicUpdate
@Table(name = "messages", indexes = {@Index(name = "ix_message_id", columnList = "message_id")})
public class Message {

    @Id
    @Column(name = "id")
    @GeneratedValue
    private Long id;

    @Column(name = "message_id", unique = true, nullable = false, length = 50)
    private String messageId;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true)
    @ElementCollection(targetClass = Process.class)
    private List<Process> processes;

    public Message() {
        this.processes = new ArrayList<>();
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

    public List<Process> getProcesses() {
        return processes;
    }

    public void setProcesses(List<Process> processes) {
        this.processes = processes;
    }

    public Process getLastProcess() {
        return this.processes.stream().max(Comparator.comparingLong(Process::getId)).orElse(null);
    }
}
