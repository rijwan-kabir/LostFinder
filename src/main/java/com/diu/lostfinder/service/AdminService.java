package com.diu.lostfinder.service;

import com.diu.lostfinder.entity.User;
import java.util.List;
import java.util.Optional;

public interface AdminService {

    List<User> getAllUsers();

    Optional<User> getUserById(Long id);

    void updateUserRole(Long userId, String role);

    void deleteUser(Long userId);

    List<User> searchUsers(String keyword);

    long getUserCount();

    long getAdminCount();

    void updateUser(User user);

    void deleteUserRelatedData(Long userId);
}