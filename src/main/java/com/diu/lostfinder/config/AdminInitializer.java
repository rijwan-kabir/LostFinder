package com.diu.lostfinder.config;

import com.diu.lostfinder.entity.User;
import com.diu.lostfinder.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if any admin exists
        boolean hasAdmin = userRepository.findAll().stream()
                .anyMatch(user -> user.getRole() == User.Role.ADMIN);

        if (!hasAdmin) {
            // Create default admin
            User admin = new User();
            admin.setFullName("Administrator");
            admin.setEmail("admin@diu.edu.bd");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setStudentId("ADMIN001");
            admin.setDepartment("Administration");
            admin.setPhone("01700000000");
            admin.setRole(User.Role.ADMIN);
            admin.setCreatedAt(java.time.LocalDateTime.now());
            userRepository.save(admin);

            System.out.println("========================================");
            System.out.println("Admin user created!");
            System.out.println("Email: admin@diu.edu.bd");
            System.out.println("Password: admin123");
            System.out.println("========================================");
        }
    }
}