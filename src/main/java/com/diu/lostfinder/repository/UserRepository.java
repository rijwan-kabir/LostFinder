package com.diu.lostfinder.repository;

import com.diu.lostfinder.entity.User;
import com.diu.lostfinder.entity.User.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    // Add these methods
    long countByRole(Role role);

    List<User> findByFullNameContainingOrEmailContaining(String fullName, String email);
}