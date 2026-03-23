package com.diu.lostfinder.service;

import com.diu.lostfinder.entity.User;
import com.diu.lostfinder.entity.User.Role;

import java.util.List;
import java.util.Optional;

public interface AdminService {

    List<User> getAllUsers();

    Optional<User> getUserById(Long id);

    User updateUserRole(Long userId, Role role);

    void deleteUser(Long userId);

    List<User> searchUsers(String keyword);

    long getUserCount();

    long getAdminCount();

    User createAdminUser(User adminUser);

    User updateUser(User user);

    void deleteUserRelatedData(Long userId);
}