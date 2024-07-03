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
Created on 7/3/2024 16:13 PM
@Last Modified 7/3/2024 16:13 PM
Version 1.0
*/
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "MstLogDocument")
public class LogDocument {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "IdLogDocument")
        private Long idLogDocument;

        @ManyToOne
        @JoinColumn(name = "IdDocument")
        private Document document;

        @ManyToOne
        @JoinColumn(name = "IdUser")
        private User user;

        @Column(name = "Action")
        private String action;

        @Column(name = "Timestamp")
        private Date timestamp;

        @Column(name = "Description")
        private String description;

    public Long getIdLogDocument() {
        return idLogDocument;
    }

    public void setIdLogDocument(Long idLogDocument) {
        this.idLogDocument = idLogDocument;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
    

