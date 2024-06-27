package co.id.bcafinance.finalproject.configuration;

import co.id.bcafinance.finalproject.core.BcryptImpl;
import co.id.bcafinance.finalproject.model.Akses;
import co.id.bcafinance.finalproject.model.Menu;
import co.id.bcafinance.finalproject.model.User;
import co.id.bcafinance.finalproject.repo.AksesRepo;
import co.id.bcafinance.finalproject.repo.MenuRepo;
import co.id.bcafinance.finalproject.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component
public class DataSeeder implements CommandLineRunner {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private AksesRepo aksesRepo;
    @Autowired
    private MenuRepo menuRepo;

    public void createAdmin() {

        User user = new User();

        Optional<User> getOneUser = userRepo.findByUsername("admin");
        List<Akses> getAllAkses = aksesRepo.findAll();

        if (getOneUser.isPresent()) {
            System.out.println("");
        } else if (!getAllAkses.isEmpty()) {
            System.out.println("");
        } else {
            // Digunakan untuk membuat admin, menu, dan beberapa akses pada saat server pertama kali dijalankan
            try {
                Akses adminAkses = aksesRepo.save(new Akses(null, "admin", Collections.emptyList(), 1L, new Date(), null, null));


                // Buat Menu
                Menu newMenu = menuRepo.save(new Menu(null, "home", "/", Collections.emptyList(), 1L, new Date(), null, null));

                // Buat List untuk Menu
                List<Menu> ltMenus = new ArrayList<>();
                ltMenus.add(newMenu);

                // Buat Akses
                aksesRepo.save(new Akses(null, "customer", ltMenus, 1L, new Date(), null, null));
                user.setEmail("admin@bcafinance.co.id");

                user.setUsername("admin");
                user.setPassword(BcryptImpl.hash("testDrive123$" + "admin"));
                user.setFullName("Admin");
                user.setAkses(adminAkses);
                userRepo.save(user);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    @Override
    public void run(String... args) throws Exception {

        createAdmin();

    }
}

