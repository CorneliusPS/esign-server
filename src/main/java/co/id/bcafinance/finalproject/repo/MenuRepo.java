package co.id.bcafinance.finalproject.repo;
/*
IntelliJ IDEA 2023.3.3 (Ultimate Edition)
Build #IU-233.14015.106, built on January 25, 2024
@Author Cornelius
Java Developer
Created on 4/24/2024 14:20 PM
@Last Modified 4/24/2024 14:20 PM
Version 1.0
*/

import co.id.bcafinance.finalproject.model.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepo extends JpaRepository<Menu, Long> {
    /**
     * Query untuk proses pencarian menu dengan opsi filter dan pagination
     */
    Page<Menu> findByNamaMenuContainingIgnoreCase(Pageable pageable, String nMenu);

    /**
     * Query untuk proses pencarian menu dengan opsi filter dan pagination
     */
    Page<Menu> findByPathMenuContainingIgnoreCase(Pageable pageable, String pathMenu);

}

