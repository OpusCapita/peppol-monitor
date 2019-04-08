package com.opuscapita.peppol.monitor.entity;

import com.opuscapita.peppol.commons.container.state.ProcessFlow;
import com.opuscapita.peppol.commons.container.state.Source;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "access_point", length = 35)
    private String accessPoint;

    @Column(name = "source", nullable = false)
    @Enumerated(EnumType.STRING)
    private Source source;

    @Column(name = "direction")
    @Enumerated(EnumType.STRING)
    private ProcessFlow direction;

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

    public List<Process> getProcesses() {
        return processes;
    }

    public void setProcesses(List<Process> processes) {
        this.processes = processes;
    }
}
