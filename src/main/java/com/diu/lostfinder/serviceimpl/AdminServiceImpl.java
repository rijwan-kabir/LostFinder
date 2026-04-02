package com.diu.lostfinder.serviceimpl;

import com.diu.lostfinder.entity.User;
import com.diu.lostfinder.repository.UserRepository;
import com.diu.lostfinder.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public void updateUserRole(Long userId, String role) {
        userRepository.updateRole(userId, role);
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public List<User> searchUsers(String keyword) {
        return userRepository.search(keyword);
    }

    @Override
    public long getUserCount() {
        return userRepository.count();
    }

    @Override
    public long getAdminCount() {
        return userRepository.countByRole("ADMIN");
    }

    @Override
    public void updateUser(User user) {
        userRepository.update(user);
    }

    @Override
    public void deleteUserRelatedData(Long userId) {
    }
}