package co.id.bcafinance.finalproject.repo;

import co.id.bcafinance.finalproject.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {
    /**
     Query untuk proses registrasi
     */
    Optional<User> findTop1ByUsernameOrEmail(String usr, String mail);

    /**
     Query untuk proses mencari user berdasarkan username
     */
    Optional<User> findByUsername(String usr);

    /**
     Query untuk proses mencari user berdasarkan id dengan opsi pagination
     */

    Page<User> findByIdUser(Pageable pageable, Long id);

    /**
     * Query untuk proses pencarian permintaan dengan opsi filter dan pagination
     */
    @Query("SELECT b FROM User b WHERE LOWER(b.username) LIKE LOWER(CONCAT('%', :status, '%'))")
    Page<User> findUserByUsernameContainingIgnoreCase(Pageable pageable, String status);



//    @Query("SELECT x FROM User x WHERE  x.idUser = ?1 AND x.post.id = ?2")
//    Optional<User> findByIdUserAndPost(Long idUSer, Long id);
}

