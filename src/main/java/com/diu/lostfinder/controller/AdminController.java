package com.diu.lostfinder.controller;

import com.diu.lostfinder.entity.User;
import com.diu.lostfinder.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")  // Changed from hasRole to hasAuthority
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        System.out.println("=== Admin Dashboard Accessed ==="); // Debug log
        model.addAttribute("userCount", adminService.getUserCount());
        model.addAttribute("adminCount", adminService.getAdminCount());
        model.addAttribute("users", adminService.getAllUsers());
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(@RequestParam(value = "search", required = false) String search,
                              Model model) {
        if (search != null && !search.isEmpty()) {
            model.addAttribute("users", adminService.searchUsers(search));
            model.addAttribute("searchKeyword", search);
        } else {
            model.addAttribute("users", adminService.getAllUsers());
        }
        return "admin/users";
    }
}