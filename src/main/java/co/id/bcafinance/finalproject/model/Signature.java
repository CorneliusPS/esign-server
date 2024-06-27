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
    Created on 5/13/2024 15:36 PM
    @Last Modified 5/13/2024 15:36 PM
    Version 1.0
    */
    @NoArgsConstructor
    @AllArgsConstructor
    @Entity
    @Table(name = "MstSignature")
    public class Signature {
    
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "IdSignature")
        private Long idSignature;
    
        @Lob
        @Column(name = "SignatureData" , columnDefinition = "varbinary(max)")
        private byte[] signatureData;
    
        @ManyToOne
        @JoinColumn(name = "IdUser")
        private User user;
    
        //DocumentId
        @ManyToOne
        @JoinColumn(name = "IdDocument")
        private Document document;
    
        @Column(name = "SignedDate")
        private Date signedDate = new Date();
    
        /**
         Start Group Audit trails
         */
        @Column(name = "CreatedBy", nullable = false)
        private Long createdBy = 1L;
    
        @Column(name = "CreatedDate", nullable = false)
        private Date createdDate = new Date();
    
        @Column(name = "ModifiedBy")
        private Long modifiedBy;
    
        @Column(name = "ModifiedDate")
        private Date modifiedDate;
    
        /**
         End Group Audit trails
         */
    
        public Long getIdSignature() {
            return idSignature;
        }
    
        public void setIdSignature(Long idSignature) {
            this.idSignature = idSignature;
        }
    
        public byte[] getSignatureData() {
            return signatureData;
        }
    
        public void setSignatureData(byte[] signatureData) {
            this.signatureData = signatureData;
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
    
        public Date getSignedDate() {
            return signedDate;
        }
    
        public void setSignedDate(Date signedDate) {
            this.signedDate = signedDate;
        }
    
        public Long getCreatedBy() {
            return createdBy;
        }
    
        public void setCreatedBy(Long createdBy) {
            this.createdBy = createdBy;
        }
    
        public Date getCreatedDate() {
            return createdDate;
        }
    
        public void setCreatedDate(Date createdDate) {
            this.createdDate = createdDate;
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
    }
        
    
