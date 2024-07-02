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

import co.id.bcafinance.finalproject.model.Document;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

public class GetDocumentPaginationDTO {
    private Long idDocument;

    private String documentName;

    private String fileName;

    private String fileType;

    private byte[] fileData;

    private Boolean isSigned;


    private String  documentStatus;

    public Long getIdDocument() {
        return idDocument;
    }

    public void setIdDocument(Long idDocument) {
        this.idDocument = idDocument;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
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

    public String getDocumentStatus() {
        return documentStatus;
    }

    public void setDocumentStatus(String documentStatus) {
        this.documentStatus = documentStatus;
    }
}
    

