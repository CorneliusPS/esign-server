package co.id.bcafinance.finalproject.dto;
/*
IntelliJ IDEA 2023.3.3 (Ultimate Edition)
Build #IU-233.14015.106, built on January 25, 2024
@Author Cornelius
Java Developer
Created on 7/3/2024 16:49 PM
@Last Modified 7/3/2024 16:49 PM
Version 1.0
*/

import java.util.Date;

public class GetLogDocumentDTO {
    private long idLogDocument;

   private String description;

   private Date timestamp;

    public long getIdLogDocument() {
        return idLogDocument;
    }

    public void setIdLogDocument(long idLogDocument) {
        this.idLogDocument = idLogDocument;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
    

