package com.diu.lostfinder.service;

import com.diu.lostfinder.entity.User;
import com.diu.lostfinder.entity.User.Role;  // ← Add this import
import com.diu.lostfinder.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get user by ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Update user role (make admin or remove admin)
    @Transactional
    public User updateUserRole(Long userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(role);
        return userRepository.save(user);
    }

    // Delete user
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    // Search users
    public List<User> searchUsers(String keyword) {
        return userRepository.findByFullNameContainingOrEmailContaining(keyword, keyword);
    }

    // Get user count
    public long getUserCount() {
        return userRepository.count();
    }

    // Get admin count
    public long getAdminCount() {
        return userRepository.countByRole(Role.ADMIN);
    }

    // Create admin (for first-time setup)
    @Transactional
    public User createAdminUser(User adminUser) {
        adminUser.setPassword(passwordEncoder.encode(adminUser.getPassword()));
        adminUser.setRole(Role.ADMIN);
        adminUser.setCreatedAt(java.time.LocalDateTime.now());
        return userRepository.save(adminUser);
    }
}