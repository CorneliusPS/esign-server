package co.id.bcafinance.finalproject.repo;
/*
IntelliJ IDEA 2023.3.3 (Ultimate Edition)
Build #IU-233.14015.106, built on January 25, 2024
@Author Cornelius
Java Developer
Created on 5/14/2024 13:38 PM
@Last Modified 5/14/2024 13:38 PM
Version 1.0
*/

import co.id.bcafinance.finalproject.model.Document;
import co.id.bcafinance.finalproject.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentRepo extends JpaRepository<Document, Long> {
    Optional<Document> findByIdDocument(Long idDocument);
    Page<Document> findByIdDocumentContainingIgnoreCase(Pageable pageable, Long idDocument);
    Page<Document> findByFileNameContainingIgnoreCase(Pageable pageable, String documentType);
    Page<Document> findByFileTypeContainingIgnoreCase(Pageable pageable, String fileType);


//    Page<Document> findByUploadBy(User user, Pageable pageable);

    Page<Document> findByUploadBy_IdUser(long l, Pageable pageable);

    List<Document> findByUploadBy(User user);

    List<Document> findByIsSigned(boolean b);

}
