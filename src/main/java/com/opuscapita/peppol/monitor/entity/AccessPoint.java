package com.opuscapita.peppol.monitor.entity;

import com.opuscapita.peppol.commons.container.metadata.AccessPointInfo;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@DynamicUpdate
@Table(name = "access_points")
public class AccessPoint {

    @Id
    @Column(name = "id", length = 20)
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "subject")
    private String subject;

    @Column(name = "emails")
    private String emailList;

    @Column(name = "contact_person")
    private String contactPerson;

    public AccessPoint() {

    }

    public AccessPoint(AccessPointInfo accessPointInfo) {
        this.id = accessPointInfo.getId();
        this.name = accessPointInfo.getName();
        this.subject = accessPointInfo.getSubject();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getEmailList() {
        return emailList;
    }

    public void setEmailList(String emailList) {
        this.emailList = emailList;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    @Override
    public String toString() {
        return "AccessPoint {id='" + id + "', name='" + name + "'}";
    }

}
