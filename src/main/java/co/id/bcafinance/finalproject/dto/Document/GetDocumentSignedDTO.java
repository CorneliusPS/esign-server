package co.id.bcafinance.finalproject.dto.Document;
/*
IntelliJ IDEA 2023.3.3 (Ultimate Edition)
Build #IU-233.14015.106, built on January 25, 2024
@Author Cornelius
Java Developer
Created on 5/27/2024 11:21 AM
@Last Modified 5/27/2024 11:21 AM
Version 1.0
*/

import javax.persistence.Column;
import java.util.Date;

public class GetDocumentSignedDTO {
    private Long idDocument;

    private String documentSign;

    private String documentName;

    private byte[] fileData;

    private String statusSignedUser;

    private Boolean isSigned;


    public Long getIdDocument() {
        return idDocument;
    }

    public void setIdDocument(Long idDocument) {
        this.idDocument = idDocument;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }


    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    public Boolean getSigned() {
        return isSigned;
    }

    public void setSigned(Boolean signed) {
        isSigned = signed;
    }

    public String getDocumentSign() {
        return documentSign;
    }

    public void setDocumentSign(String documentSign) {
        this.documentSign = documentSign;
    }

    public String getStatusSignedUser() {
        return statusSignedUser;
    }

    public void setStatusSignedUser(String statusSignedUser) {
        this.statusSignedUser = statusSignedUser;
    }
}
    

