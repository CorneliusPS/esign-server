package co.id.bcafinance.finalproject.repo;
/*
IntelliJ IDEA 2023.3.3 (Ultimate Edition)
Build #IU-233.14015.106, built on January 25, 2024
@Author Cornelius
Java Developer
Created on 7/3/2024 16:29 PM
@Last Modified 7/3/2024 16:29 PM
Version 1.0
*/

import co.id.bcafinance.finalproject.model.Document;
import co.id.bcafinance.finalproject.model.LogDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LogDocumentRepo extends JpaRepository<LogDocument, Long> {

    List<LogDocument> findByDocument(Document document);

}
