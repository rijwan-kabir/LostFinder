package com.diu.lostfinder.controller;

import com.diu.lostfinder.entity.Item;
import com.diu.lostfinder.entity.User;
import com.diu.lostfinder.service.AdminService;
import com.diu.lostfinder.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private ItemService itemService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("userCount", adminService.getUserCount());
        model.addAttribute("adminCount", adminService.getAdminCount());
        model.addAttribute("totalItems", itemService.getApprovedItems().size());
        model.addAttribute("pendingItemsCount", itemService.getPendingItems().size());
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

    @PostMapping("/users/make-admin/{id}")
    public String makeAdmin(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = adminService.getUserById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if ("ADMIN".equals(user.getRole())) {
                redirectAttributes.addFlashAttribute("error",
                        user.getFullName() + " is already an admin!");
                return "redirect:/admin/users";
            }

            adminService.updateUserRole(id, "ADMIN");

            redirectAttributes.addFlashAttribute("success",
                    "✅ " + user.getFullName() + " is now an ADMIN!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Failed to make admin: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/remove-admin/{id}")
    public String removeAdmin(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = adminService.getUserById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            long adminCount = adminService.getAdminCount();
            if (adminCount == 1 && "ADMIN".equals(user.getRole())) {
                redirectAttributes.addFlashAttribute("error",
                        "❌ Cannot remove the last admin! At least one admin must exist.");
                return "redirect:/admin/users";
            }

            if (!"ADMIN".equals(user.getRole())) {
                redirectAttributes.addFlashAttribute("error",
                        user.getFullName() + " is not an admin!");
                return "redirect:/admin/users";
            }

            adminService.updateUserRole(id, "USER");

            redirectAttributes.addFlashAttribute("success",
                    "✅ Admin role removed from " + user.getFullName());

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Failed to remove admin: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/items")
    public String manageItems(Model model) {
        List<Item> pendingItems = itemService.getPendingItems();
        List<Item> approvedItems = itemService.getApprovedItems();

        model.addAttribute("pendingItems", pendingItems);
        model.addAttribute("approvedItems", approvedItems);
        return "admin/items";
    }

    @GetMapping("/items/approve/{id}")
    public String approveItem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            itemService.approveItem(id);
            redirectAttributes.addFlashAttribute("success", "Item approved successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to approve item: " + e.getMessage());
        }
        return "redirect:/admin/items";
    }

    @GetMapping("/items/reject/{id}")
    public String rejectItem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            itemService.rejectItem(id);
            redirectAttributes.addFlashAttribute("success", "Item rejected!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to reject item: " + e.getMessage());
        }
        return "redirect:/admin/items";
    }

    @GetMapping("/items/view/{id}")
    public String viewItem(@PathVariable Long id, Model model) {
        Item item = itemService.getItemById(id).orElse(null);
        model.addAttribute("item", item);
        return "admin/item-view";
    }

    @GetMapping("/users/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = adminService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        return "admin/user-edit";
    }

    @PostMapping("/users/update/{id}")
    public String updateUser(@PathVariable Long id,
                             @RequestParam String fullName,
                             @RequestParam String email,
                             @RequestParam String studentId,
                             @RequestParam String department,
                             @RequestParam String phone,
                             RedirectAttributes redirectAttributes) {
        try {
            User user = adminService.getUserById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setFullName(fullName);
            user.setEmail(email);
            user.setStudentId(studentId);
            user.setDepartment(department);
            user.setPhone(phone);

            adminService.updateUser(user);

            redirectAttributes.addFlashAttribute("success",
                    "✅ User " + user.getFullName() + " updated successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Failed to update user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/view/{id}")
    public String viewUser(@PathVariable Long id, Model model) {
        User user = adminService.getUserById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        return "admin/user-view";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            User user = adminService.getUserById(id).orElse(null);

            long adminCount = adminService.getAdminCount();
            if (adminCount == 1 && user != null && "ADMIN".equals(user.getRole())) {
                redirectAttributes.addFlashAttribute("error",
                        "❌ Cannot delete the last admin!");
                return "redirect:/admin/users";
            }

            adminService.deleteUser(id);

            redirectAttributes.addFlashAttribute("success",
                    "✅ User deleted successfully!");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Failed to delete user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
}