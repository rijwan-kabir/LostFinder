package com.diu.lostfinder.controller;

import com.diu.lostfinder.entity.User;
import com.diu.lostfinder.service.AdminService;
import com.diu.lostfinder.service.ItemService;
import com.diu.lostfinder.entity.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;  // ← Add this import

import java.util.List;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private ItemService itemService;  // ← Add this

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("userCount", adminService.getUserCount());
        model.addAttribute("adminCount", adminService.getAdminCount());

        // Get approved items only for total
        List<Item> approvedItems = itemService.getApprovedItems();
        model.addAttribute("totalItems", approvedItems.size());

        // Get pending items count
        List<Item> pendingItems = itemService.getPendingItems();
        model.addAttribute("pendingItemsCount", pendingItems.size());

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

    // Add these methods
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
}