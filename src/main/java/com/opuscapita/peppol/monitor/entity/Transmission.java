package com.opuscapita.peppol.monitor.entity;

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
@Table(name = "transmissions", indexes = {
        @Index(name = "ix_transmission_id", columnList = "transmission_id"),
        @Index(name = "ix_message_id", columnList = "message_id"),
        @Index(name = "ix_transmission_fname", columnList = "filename"),
        @Index(name = "ix_transmission_invoice", columnList = "invoice_number"),
        @Index(name = "ix_transmission_status", columnList = "status"),
        @Index(name = "ix_transmission_source", columnList = "source"),
})
public class Transmission {

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "message_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Message message;

    @Lob
    @Column(name = "history")
    private String rawHistory;

    @Transient
    private List<DocumentLog> logs;

    private String fieldTruncate( String field, int maxLen ) {

  		try {
  			if( maxLen < 2) {
  				return "";
  			}

  			if( field == null ) {
  				return null;
  			}

  			if( field.length() <= maxLen) {
  				return field;
  			}

  			String newField = field.substring(0, maxLen - 2) + "..";
  			return newField;
  		} catch (Exception e)  {
  			System.err.println( "issue to truncate " + field + e.getMessage() );
  		}
  		return field;

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
        this.transmissionId = this.fieldTruncate( transmissionId, 100);
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {

        this.filename = this.fieldTruncate( filename, 150);
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
        this.sender = this.fieldTruncate( sender, 35);
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = this.fieldTruncate( receiver, 35);
    }

    public String getAccessPoint() {
        return accessPoint;
    }

    public void setAccessPoint(String accessPoint) {
        this.accessPoint = this.fieldTruncate( accessPoint, 35);
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = this.fieldTruncate( invoiceNumber, 50);
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = this.fieldTruncate( invoiceDate, 50);
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

    public String getDocumentTypeId() {
        return documentTypeId;
    }

    public void setDocumentTypeId(String documentTypeId) {
        this.documentTypeId = documentTypeId;
    }

    public String getRawHistory() {
        return rawHistory;
    }

    public void setRawHistory(String rawHistory) {
        this.rawHistory = rawHistory;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public List<DocumentLog> getLogs() {
        return logs;
    }

    public void setLogs(List<DocumentLog> logs) {
        this.logs = logs;
    }

}
