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

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private DataSource dataSource;

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

        // ✅ IMPORTANT: Call loadStats() to add statistics to model
        loadStats(model);

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

    // ✅ FIXED: Added totalClaims calculation
    public void loadStats(Model model) {
        long totalItems = 0;
        long totalUsers = 0;
        long lostItems = 0;
        long foundItems = 0;
        long pendingItems = 0;
        long totalClaims = 0;  // ✅ Added claims count

        try (Connection conn = dataSource.getConnection()) {
            // Total Approved Items
            String sql1 = "SELECT COUNT(*) FROM items WHERE status = 'APPROVED'";
            try (PreparedStatement ps1 = conn.prepareStatement(sql1);
                 ResultSet rs1 = ps1.executeQuery()) {
                if (rs1.next()) {
                    totalItems = rs1.getLong(1);
                }
            }

            // Total Users
            String sql2 = "SELECT COUNT(*) FROM users";
            try (PreparedStatement ps2 = conn.prepareStatement(sql2);
                 ResultSet rs2 = ps2.executeQuery()) {
                if (rs2.next()) {
                    totalUsers = rs2.getLong(1);
                }
            }

            // Lost Items (all items with type 'LOST', regardless of status)
            String sql3 = "SELECT COUNT(*) FROM items WHERE type = 'LOST'";
            try (PreparedStatement ps3 = conn.prepareStatement(sql3);
                 ResultSet rs3 = ps3.executeQuery()) {
                if (rs3.next()) {
                    lostItems = rs3.getLong(1);
                }
            }

            // Found Items (all items with type 'FOUND', regardless of status)
            String sql4 = "SELECT COUNT(*) FROM items WHERE type = 'FOUND'";
            try (PreparedStatement ps4 = conn.prepareStatement(sql4);
                 ResultSet rs4 = ps4.executeQuery()) {
                if (rs4.next()) {
                    foundItems = rs4.getLong(1);
                }
            }

            // Pending Items
            String sql5 = "SELECT COUNT(*) FROM items WHERE status = 'PENDING'";
            try (PreparedStatement ps5 = conn.prepareStatement(sql5);
                 ResultSet rs5 = ps5.executeQuery()) {
                if (rs5.next()) {
                    pendingItems = rs5.getLong(1);
                }
            }

            // ✅ NEW: Total Claims (if claims table exists)
            try {
                String sql6 = "SELECT COUNT(*) FROM claims";
                try (PreparedStatement ps6 = conn.prepareStatement(sql6);
                     ResultSet rs6 = ps6.executeQuery()) {
                    if (rs6.next()) {
                        totalClaims = rs6.getLong(1);
                    }
                }
            } catch (Exception e) {
                // If claims table doesn't exist yet, set to 0
                totalClaims = 0;
                System.out.println("Claims table not found, setting totalClaims to 0");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Model attributes
        model.addAttribute("statTotalItems", totalItems);
        model.addAttribute("statTotalUsers", totalUsers);
        model.addAttribute("statLostItems", lostItems);
        model.addAttribute("statFoundItems", foundItems);
        model.addAttribute("statPendingItems", pendingItems);
        model.addAttribute("totalClaims", totalClaims);
    }
}