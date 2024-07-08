package co.id.bcafinance.finalproject.model;
/*
IntelliJ IDEA 2023.3.3 (Ultimate Edition)
Build #IU-233.14015.106, built on January 25, 2024
@Author Cornelius
Java Developer
Created on 5/13/2024 15:10 PM
@Last Modified 5/13/2024 15:10 PM
Version 1.0
*/

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "MstDocument")

public class    Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdDocument")
    private Long idDocument;

    @Column(name = "DocumentSign")
    private String documentSign;

    // List User yang telah menandatangani dokumen, nanti nya akan diisikan string saat setelah user melakukan tanda tangan
    @Column(name = "StatusSignedUser")
    private String statusSignedUser;

    @Column(name = "DocumentName")
    private String documentName;

    @Column(name = "FileName")
    private String fileName;

    @Column(name = "FileType")
    private String fileType;

    @Lob
    @Column(name = "FileData" , columnDefinition = "varbinary(max)")
    private byte[] fileData;

    @Column(name = "IsSigned")
    private Boolean isSigned;

    public enum ApprovalType {
        PARALLEL,
        SERIAL
    }
    @Enumerated(EnumType.STRING)
    @Column(name = "ApprovalType")
    private ApprovalType approvalType;


    @Column(name = "DocumentStatus")
    private String documentStatus;

    @Column(name = "FlagCount")
    private Integer flagCount;

    @Column(name = "NumberOfApprovers")
    private Integer numberOfApprovers;


    /**
     Start Group Audit trails
     */

    @ManyToOne
    @JoinColumn(name = "UploadBy")
    private User uploadBy;

    @Column(name = "UploadDate")
    private Date uploadDate = new Date();

    @Column(name = "ModifiedBy")
    private Long modifiedBy;

    @Column(name = "ModifiedDate")
    private Date modifiedDate;

    /**
     End Group Audit trails
     */

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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public User getUploadBy() {
        return uploadBy;
    }

    public void setUploadBy(User uploadBy) {
        this.uploadBy = uploadBy;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public Long getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Long modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }


    public String getDocumentStatus() {
        return documentStatus;
    }

    public void setDocumentStatus(String documentStatus) {
        this.documentStatus = documentStatus;
    }

    public ApprovalType getApprovalType() {
        return approvalType;
    }

    public void setApprovalType(ApprovalType approvalType) {
        this.approvalType = approvalType;
    }

    public Integer getFlagCount() {
        return flagCount;
    }

    public void setFlagCount(Integer flagCount) {
        this.flagCount = flagCount;
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

    public Integer getNumberOfApprovers() {
        return numberOfApprovers;
    }

    public void setNumberOfApprovers(Integer numberOfApprovers) {
        this.numberOfApprovers = numberOfApprovers;
    }
}
    

