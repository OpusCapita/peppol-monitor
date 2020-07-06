package com.opuscapita.peppol.monitor.entity;

import com.opuscapita.peppol.commons.container.state.Source;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

@Entity
@DynamicUpdate
@Table(name = "archive", indexes = {
        @Index(name = "ix_archive_filename", columnList = "filename"),
        @Index(name = "ix_archive_invoice", columnList = "invoice_number")
})
public class Archive {

    @Id
    @Column(name = "id")
    @GeneratedValue
    private Long id;

    @Column(name = "transmission_id", unique = true, nullable = false, length = 100)
    private String transmissionId;

    @Column(name = "filename", nullable = false, length = 150)
    private String filename;

    @Column(name = "status", length = 10)
    @Enumerated(EnumType.STRING)
    private MessageStatus status;

    @Column(name = "source", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private Source source;

    @Column(name = "direction", length = 10)
    @Enumerated(EnumType.STRING)
    private Source direction;

    @Column(name = "sender", length = 35)
    private String sender;

    @Column(name = "receiver", length = 35)
    private String receiver;

    @Column(name = "access_point", length = 35)
    private String accessPoint;

    @Column(name = "invoice_number", length = 50)
    private String invoiceNumber;

    @Column(name = "invoice_date", length = 50)
    private String invoiceDate;

    @Column(name = "document_type_id", length = 10)
    private String documentTypeId;

    @Column(name = "arrived_at")
    private Date arrivedAt;

    @Version
    private Integer version;

    public static Archive convert(Transmission transmission) {
        Archive archive = new Archive();
        archive.setTransmissionId(transmission.getTransmissionId());
        archive.setFilename(transmission.getFilename());
        archive.setStatus(transmission.getStatus());
        archive.setSource(transmission.getSource());
        archive.setDirection(transmission.getDirection());
        archive.setSender(transmission.getSender());
        archive.setReceiver(transmission.getReceiver());
        archive.setAccessPoint(transmission.getAccessPoint());
        archive.setInvoiceNumber(transmission.getInvoiceNumber());
        archive.setInvoiceDate(transmission.getInvoiceDate());
        archive.setDocumentTypeId(transmission.getDocumentTypeId());
        archive.setArrivedAt(transmission.getArrivedAt());
        return archive;
    }

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

    public Source getDirection() {
        return direction;
    }

    public void setDirection(Source direction) {
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

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public Date getArrivedAt() {
        return arrivedAt;
    }

    public void setArrivedAt(Date arrivedAt) {
        this.arrivedAt = arrivedAt;
    }

    public String getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(String documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

}
