package com.diu.lostfinder.controller;

import com.diu.lostfinder.entity.Item;
import com.diu.lostfinder.entity.User;
import com.diu.lostfinder.repository.ItemRepository;
import com.diu.lostfinder.repository.UserRepository;
import com.diu.lostfinder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository; // ← নতুন

    @GetMapping("/")
    public String home(@RequestParam(value = "logout", required = false) String logout,
                       Model model,
                       Authentication authentication) {
        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully!");
        }

        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email).orElse(null);
            if (user != null) {
                model.addAttribute("isLoggedIn", true);
                model.addAttribute("user", user);
                boolean isAdmin = authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                model.addAttribute("isAdmin", isAdmin);
            }
        } else {
            model.addAttribute("isLoggedIn", false);
        }

        List<Item> recentItems = itemRepository.findByStatus("APPROVED").stream()
                .limit(3)
                .collect(java.util.stream.Collectors.toList());
        model.addAttribute("recentItems", recentItems);

        return "index";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (isAdmin) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/dashboard";
            }
        }
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(User user, Model model) {
        try {
            if (userService.emailExists(user.getEmail())) {
                model.addAttribute("error", "Email already registered!");
                return "register";
            }
            userService.registerUser(user);
            model.addAttribute("success", "Registration successful! Please login.");
            return "login";
        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String showLoginForm(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (isAdmin) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/dashboard";
            }
        }
        return "login";
    }

    @GetMapping("/check-auth")
    @ResponseBody
    public String checkAuth(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "❌ Not authenticated! Please login first.";
        }
        return "✅ Authenticated!\n" +
                "Username: " + authentication.getName() + "\n" +
                "Authorities: " + authentication.getAuthorities() + "\n" +
                "Is Authenticated: " + authentication.isAuthenticated();
    }
}