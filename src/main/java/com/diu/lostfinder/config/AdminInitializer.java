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
        if (userRepository.countByRole("ADMIN") == 0) {
            User admin = new User();
            admin.setFullName("System Administrator");
            admin.setEmail("admin@diu.edu.bd");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setStudentId("ADMIN001");
            admin.setDepartment("Administration");
            admin.setPhone("01700000000");
            admin.setRole("ADMIN");
            admin.setCreatedAt(java.time.LocalDateTime.now());
            userRepository.save(admin);

            System.out.println("\n========================================");
            System.out.println("✅ ADMIN USER CREATED!");
            System.out.println("📧 Email: admin@diu.edu.bd");
            System.out.println("🔑 Password: admin123");
            System.out.println("========================================\n");
        }
    }
}