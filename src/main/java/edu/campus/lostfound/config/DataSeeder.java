package edu.campus.lostfound.config;

import edu.campus.lostfound.model.Category;
import edu.campus.lostfound.model.User;
import edu.campus.lostfound.model.UserRole;
import edu.campus.lostfound.repository.CategoryRepository;
import edu.campus.lostfound.repository.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder implements CommandLineRunner {
    private final CategoryRepository categories;
    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;

    public DataSeeder(CategoryRepository categories, UserRepository users, PasswordEncoder passwordEncoder,
            @Value("${app.bootstrap.admin-email:}") String adminEmail,
            @Value("${app.bootstrap.admin-password:}") String adminPassword) {
        this.categories = categories;
        this.users = users;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    @Transactional
    public void run(String... args) {
        for (String name : List.of("Electronics", "Documents", "Keys", "Clothing", "Bags", "Accessories", "Other")) {
            if (categories.findByNameIgnoreCase(name).isEmpty()) categories.save(new Category(name));
        }
        if (!adminEmail.isBlank() && !adminPassword.isBlank() && !users.existsByEmailIgnoreCase(adminEmail)) {
            users.save(new User(adminEmail.trim().toLowerCase(), passwordEncoder.encode(adminPassword),
                    "Campus Administrator", UserRole.ADMIN));
        }
    }
}