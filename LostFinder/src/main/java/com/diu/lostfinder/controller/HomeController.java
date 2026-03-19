package com.diu.lostfinder.controller;

import com.diu.lostfinder.entity.User;
import com.diu.lostfinder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public String home(@RequestParam(value = "logout", required = false) String logout, Model model) {
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully!");
        }
        return "index";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(User user, Model model) {
        try {
            // Check if email already exists
            if (userService.emailExists(user.getEmail())) {
                model.addAttribute("error", "Email already registered!");
                return "register";
            }

            // Register user
            userService.registerUser(user);
            model.addAttribute("success", "Registration successful! Please login.");
            return "login";

        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }
}