package com.diu.lostfinder.service;

import com.diu.lostfinder.entity.User;

public interface UserService {
    User registerUser(User user);
    boolean emailExists(String email);
    User findByEmail(String email);
    User findById(Long id);
}