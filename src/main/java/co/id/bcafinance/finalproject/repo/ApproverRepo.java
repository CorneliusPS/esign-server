package co.id.bcafinance.finalproject.repo;
/*
IntelliJ IDEA 2023.3.3 (Ultimate Edition)
Build #IU-233.14015.106, built on January 25, 2024
@Author Cornelius
Java Developer
Created on 5/29/2024 15:30 PM
@Last Modified 5/29/2024 15:30 PM
Version 1.0
*/

import co.id.bcafinance.finalproject.model.Approver;
import co.id.bcafinance.finalproject.model.Document;
import co.id.bcafinance.finalproject.model.User;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApproverRepo extends JpaRepository<Approver, Long>{

    List<Approver> findByDocument(Document document);


    Optional<Approver> findByDocumentAndUser(Document document, User user);

    List<Approver> findByUser(User user);

    Optional<Approver> findByIdApproverAndUser(Long idApprover, User user);

    Optional<Approver> findByDocumentAndApprovalOrder(Document document, int i);

    List<Approver> findByUserAndIsCurrentTrue(User user);

}
