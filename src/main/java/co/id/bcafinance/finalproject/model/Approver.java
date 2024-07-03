package co.id.bcafinance.finalproject.model;
/*
IntelliJ IDEA 2023.3.3 (Ultimate Edition)
Build #IU-233.14015.106, built on January 25, 2024
@Author Cornelius
Java Developer
Created on 5/29/2024 15:24 PM
@Last Modified 5/29/2024 15:24 PM
Version 1.0
*/

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "MstApprover")
public class Approver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "IdApprover")
    private Long idApprover;

    @ManyToOne
    @JoinColumn(name = "IdDocument")
    private Document document;


    @ManyToOne
    @JoinColumn(name = "IdUser")
    private User user;

    @Column(name = "IsApproved")
    private boolean isApproved;


    @Column(name = "SignedDate")
    private Date signedDate;

    @Column(name = "Otp")
    private Integer otp;

    @Column(name = "IsAuthenticated")
    private boolean isAuthenticated;

    @Column(name = "ApprovalOrder")
    private Integer approvalOrder;

    // flag untuk menandankan sekarang giliran user ini untuk approve
    @Column(name = "IsCurrent")
    private boolean isCurrent;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public Integer getOtp() {
        return otp;
    }

    public void setOtp(Integer otp) {
        this.otp = otp;
    }

    public Date getSignedDate() {
        return signedDate;
    }

    public void setSignedDate(Date signedDate) {
        this.signedDate = signedDate;
    }

    public boolean isAuthenticated() {
        return isAuthenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        isAuthenticated = authenticated;
    }

    public Integer getApprovalOrder() {
        return approvalOrder;
    }

    public void setApprovalOrder(Integer approvalOrder) {
        this.approvalOrder = approvalOrder;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public void setCurrent(boolean current) {
        isCurrent = current;
    }
}
    

