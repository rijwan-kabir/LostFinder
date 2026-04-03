package com.diu.lostfinder.controller;

import com.diu.lostfinder.entity.Claim;
import com.diu.lostfinder.entity.Item;
import com.diu.lostfinder.entity.User;
import com.diu.lostfinder.repository.ClaimRepository;
import com.diu.lostfinder.repository.ItemRepository;
import com.diu.lostfinder.repository.UserRepository;
import com.diu.lostfinder.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private UserRepository userRepository;

    // ==================== DASHBOARD ====================
    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("userCount", adminService.getUserCount());
        model.addAttribute("adminCount", adminService.getAdminCount());
        model.addAttribute("totalItems", itemRepository.count());
        model.addAttribute("pendingItemsCount", itemRepository.countByStatus("PENDING"));
        model.addAttribute("pendingClaimsCount", claimRepository.countPendingClaims());
        model.addAttribute("users", adminService.getAllUsers());
        return "admin/dashboard";
    }

    // ==================== MANAGE USERS ====================
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

    @PostMapping("/users/make-admin/{id}")
    public String makeAdmin(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = adminService.getUserById(id).orElse(null);
            if (user != null && !"ADMIN".equals(user.getRole())) {
                adminService.updateUserRole(id, "ADMIN");
                redirectAttributes.addFlashAttribute("success", user.getFullName() + " is now an ADMIN!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to make admin: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/remove-admin/{id}")
    public String removeAdmin(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = adminService.getUserById(id).orElse(null);
            long adminCount = adminService.getAdminCount();
            if (adminCount == 1 && "ADMIN".equals(user.getRole())) {
                redirectAttributes.addFlashAttribute("error", "Cannot remove the last admin!");
            } else if (user != null) {
                adminService.updateUserRole(id, "USER");
                redirectAttributes.addFlashAttribute("success", "Admin role removed from " + user.getFullName());
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to remove admin: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = adminService.getUserById(id).orElse(null);
            long adminCount = adminService.getAdminCount();
            if (adminCount == 1 && user != null && "ADMIN".equals(user.getRole())) {
                redirectAttributes.addFlashAttribute("error", "Cannot delete the last admin!");
            } else {
                adminService.deleteUser(id);
                redirectAttributes.addFlashAttribute("success", "User deleted successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    // ==================== MANAGE ITEMS ====================
    @GetMapping("/items")
    public String manageItems(Model model) {
        model.addAttribute("pendingItems", itemRepository.findByStatus("PENDING"));
        model.addAttribute("approvedItems", itemRepository.findByStatus("APPROVED"));
        model.addAttribute("rejectedItems", itemRepository.findByStatus("REJECTED"));
        return "admin/items";
    }

    @GetMapping("/items/approve/{id}")
    public String approveItem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Item item = itemRepository.findById(id).orElse(null);
            if (item != null) {
                itemRepository.approveItem(id, null);
                redirectAttributes.addFlashAttribute("success", "Item '" + item.getTitle() + "' approved!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to approve item: " + e.getMessage());
        }
        return "redirect:/admin/items";
    }

    @GetMapping("/items/reject/{id}")
    public String rejectItem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Item item = itemRepository.findById(id).orElse(null);
            if (item != null) {
                itemRepository.rejectItem(id);
                redirectAttributes.addFlashAttribute("success", "Item '" + item.getTitle() + "' rejected!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to reject item: " + e.getMessage());
        }
        return "redirect:/admin/items";
    }

    @GetMapping("/items/delete/{id}")
    public String deleteItem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Item item = itemRepository.findById(id).orElse(null);
            if (item != null) {
                itemRepository.deleteById(id);
                redirectAttributes.addFlashAttribute("success", "Item deleted successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete item: " + e.getMessage());
        }
        return "redirect:/admin/items";
    }

    // ==================== MANAGE CLAIMS ====================
    @GetMapping("/claims")
    public String manageClaims(Model model) {
        model.addAttribute("pendingClaims", claimRepository.findPendingClaims());
        model.addAttribute("allClaims", claimRepository.findAll());
        model.addAttribute("approvedClaims", claimRepository.findByStatus("APPROVED"));
        model.addAttribute("rejectedClaims", claimRepository.findByStatus("REJECTED"));
        return "admin/claims";
    }

    @GetMapping("/claims/view/{id}")
    public String viewClaim(@PathVariable Long id, Model model) {
        Claim claim = claimRepository.findById(id).orElse(null);
        model.addAttribute("claim", claim);
        return "admin/claim-view";
    }

    @PostMapping("/claims/approve/{id}")
    public String approveClaim(@PathVariable Long id,
                               @RequestParam(required = false) String adminNotes,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        try {
            Claim claim = claimRepository.findById(id).orElse(null);
            if (claim != null) {
                String email = authentication.getName();
                User admin = userRepository.findByEmail(email).orElse(null);
                claimRepository.updateStatus(id, "APPROVED", admin != null ? admin.getId() : null, adminNotes);

                // Update item status to RETURNED
                if (claim.getItem() != null) {
                    itemRepository.updateStatus(claim.getItem().getId(), "RETURNED");
                }
                redirectAttributes.addFlashAttribute("success", "Claim approved successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to approve claim: " + e.getMessage());
        }
        return "redirect:/admin/claims";
    }

    @PostMapping("/claims/reject/{id}")
    public String rejectClaim(@PathVariable Long id,
                              @RequestParam String rejectionReason,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        try {
            Claim claim = claimRepository.findById(id).orElse(null);
            if (claim != null) {
                String email = authentication.getName();
                User admin = userRepository.findByEmail(email).orElse(null);
                claimRepository.updateStatus(id, "REJECTED", admin != null ? admin.getId() : null, rejectionReason);
                redirectAttributes.addFlashAttribute("success", "Claim rejected!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to reject claim: " + e.getMessage());
        }
        return "redirect:/admin/claims";
    }

    // ==================== VIEW REPORTS ====================
    @GetMapping("/reports")
    public String viewReports(Model model) {
        // User Statistics
        model.addAttribute("totalUsers", adminService.getUserCount());
        model.addAttribute("totalAdmins", adminService.getAdminCount());
        model.addAttribute("regularUsers", adminService.getUserCount() - adminService.getAdminCount());

        // Item Statistics
        model.addAttribute("totalItems", itemRepository.count());
        model.addAttribute("lostItems", itemRepository.countByType("LOST"));
        model.addAttribute("foundItems", itemRepository.countByType("FOUND"));
        model.addAttribute("pendingItems", itemRepository.countByStatus("PENDING"));
        model.addAttribute("approvedItems", itemRepository.countByStatus("APPROVED"));
        model.addAttribute("rejectedItems", itemRepository.countByStatus("REJECTED"));

        // Claim Statistics
        model.addAttribute("totalClaims", claimRepository.count());
        model.addAttribute("pendingClaims", claimRepository.countPendingClaims());
        model.addAttribute("approvedClaims", claimRepository.countByStatus("APPROVED"));
        model.addAttribute("rejectedClaims", claimRepository.countByStatus("REJECTED"));

        // Recent Activity
        model.addAttribute("recentItems", itemRepository.findRecentItems(10));
        model.addAttribute("recentClaims", claimRepository.findRecentClaims(10));

        // Category Distribution
        model.addAttribute("categoryStats", itemRepository.getCategoryStats());

        return "admin/reports";
    }
}