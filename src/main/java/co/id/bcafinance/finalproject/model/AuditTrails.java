package co.id.bcafinance.finalproject.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

/*
IntelliJ IDEA 2023.3.3 (Ultimate Edition)
Build #IU-233.14015.106, built on January 25, 2024
@Author Cornelius
Java Developer
Created on 6/29/2024 15:36 PM
@Last Modified 6/29/2024 15:36 PM
Version 1.0
*/
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "MstAuditTrails")
public class AuditTrails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdAuditTrails")
    private Long idAuditTrails;

    @Column(name = "Action")
    private String action;

    @Column(name="Timestamp")
    private Date timestamp;

    @ManyToOne
    @JoinColumn(name = "User")
    private User user;

    @ManyToOne
    @JoinColumn(name = "Document")
    private Document document;


    public Long getIdAuditTrails() {
        return idAuditTrails;
    }

    public void setIdAuditTrails(Long idAuditTrails) {
        this.idAuditTrails = idAuditTrails;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
}
    

