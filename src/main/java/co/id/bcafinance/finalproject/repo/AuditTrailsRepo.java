package co.id.bcafinance.finalproject.repo;
/*
IntelliJ IDEA 2023.3.3 (Ultimate Edition)
Build #IU-233.14015.106, built on January 25, 2024
@Author Cornelius
Java Developer
Created on 6/29/2024 15:40 PM
@Last Modified 6/29/2024 15:40 PM
Version 1.0
*/

import co.id.bcafinance.finalproject.model.AuditTrails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditTrailsRepo extends JpaRepository<AuditTrails, Long> {


}
