package co.id.bcafinance.finalproject.repo;

import co.id.bcafinance.finalproject.model.Akses;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AksesRepo extends JpaRepository<Akses, Long> {
    /**
     * Query untuk proses pencarian akses dengan opsi filter dan pagination
     */
    Page<Akses> findByNamaAksesContainingIgnoreCase(Pageable pageable, String namaAkses);

    /**
     * Query untuk proses penambahan akses baru
     * Query untuk proses register
     */
    Optional<Akses> findByNamaAkses(String namaAkses);
}
