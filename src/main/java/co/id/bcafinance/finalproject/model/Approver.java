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

    private boolean isApproved;

    @Column(name = "Otp")
    private Integer otp;

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
}
    

