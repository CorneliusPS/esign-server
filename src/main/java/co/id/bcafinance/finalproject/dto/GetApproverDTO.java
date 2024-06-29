package co.id.bcafinance.finalproject.dto;
/*
IntelliJ IDEA 2023.3.3 (Ultimate Edition)
Build #IU-233.14015.106, built on January 25, 2024
@Author Cornelius
Java Developer
Created on 6/14/2024 15:16 PM
@Last Modified 6/14/2024 15:16 PM
Version 1.0
*/

import co.id.bcafinance.finalproject.model.Document;
import co.id.bcafinance.finalproject.model.User;

import java.util.Date;

public class GetApproverDTO {
    private Long idApprover;
    private Document document;

    private User idUser;

    private boolean isApproved;

    private Date signedDate;

    public Long getIdApprover() {
        return idApprover;
    }

    public void setIdApprover(Long idApprover) {
        this.idApprover = idApprover;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public User getIdUser() {
        return idUser;
    }

    public void setIdUser(User idUser) {
        this.idUser = idUser;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public Date getSignedDate() {
        return signedDate;
    }

    public void setSignedDate(Date signedDate) {
        this.signedDate = signedDate;
    }
}
    

