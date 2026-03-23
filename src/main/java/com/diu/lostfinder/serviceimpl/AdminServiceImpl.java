package com.diu.lostfinder.serviceimpl;

import com.diu.lostfinder.entity.Item;
import com.diu.lostfinder.entity.User;
import com.diu.lostfinder.entity.User.Role;
import com.diu.lostfinder.repository.*;
import com.diu.lostfinder.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public User updateUserRole(Long userId, Role role) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(role);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public List<User> searchUsers(String keyword) {
        return userRepository.findByFullNameContainingOrEmailContaining(keyword, keyword);
    }

    @Override
    public long getUserCount() {
        return userRepository.count();
    }

    @Override
    public long getAdminCount() {
        return userRepository.countByRole(Role.ADMIN);
    }

    @Override
    @Transactional
    public User createAdminUser(User adminUser) {
        adminUser.setPassword(passwordEncoder.encode(adminUser.getPassword()));
        adminUser.setRole(Role.ADMIN);
        adminUser.setCreatedAt(java.time.LocalDateTime.now());
        return userRepository.save(adminUser);
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUserRelatedData(Long userId) {
        // Delete password reset tokens first
        passwordResetTokenRepository.deleteByUserId(userId);

        // Delete all items posted by user
        itemRepository.deleteByPostedByUserId(userId);
    }
}