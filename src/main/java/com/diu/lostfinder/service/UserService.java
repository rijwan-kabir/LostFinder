package com.diu.lostfinder.service;

import com.diu.lostfinder.entity.User;
import java.util.List;
import java.util.Optional;

public interface UserService {

    User registerUser(User user);

    boolean emailExists(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    List<User> getAllUsers();
}