package com.diu.lostfinder.controller;

import com.diu.lostfinder.entity.Item;
import com.diu.lostfinder.entity.User;
import com.diu.lostfinder.repository.ItemRepository;
import com.diu.lostfinder.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        Long userId = user.getId();

        // User statistics
        long totalItems = itemRepository.countByUserId(userId);
        long lostItems = itemRepository.countByUserIdAndType(userId, "LOST");
        long foundItems = itemRepository.countByUserIdAndType(userId, "FOUND");
        long claims = 0;

        // User's own recent items
        List<Item> recentItems = itemRepository.findRecentByUserId(userId, 5);

        // ========== CRITICAL: Get global recent lost & found items ==========
        List<Item> recentLostItems = null;
        List<Item> recentFoundItems = null;

        try {
            recentLostItems = itemRepository.findRecentLostItemsGlobal(5);
            recentFoundItems = itemRepository.findRecentFoundItemsGlobal(5);

            // Debug print
            System.out.println("========== DASHBOARD DATA ==========");
            System.out.println("Recent Lost Items count: " + (recentLostItems != null ? recentLostItems.size() : 0));
            System.out.println("Recent Found Items count: " + (recentFoundItems != null ? recentFoundItems.size() : 0));

            if (recentLostItems != null && !recentLostItems.isEmpty()) {
                for (Item item : recentLostItems) {
                    System.out.println("LOST: " + item.getTitle() + " | Location: " + item.getLocation() + " | Status: " + item.getStatus());
                }
            }

            if (recentFoundItems != null && !recentFoundItems.isEmpty()) {
                for (Item item : recentFoundItems) {
                    System.out.println("FOUND: " + item.getTitle() + " | Location: " + item.getLocation() + " | Status: " + item.getStatus());
                }
            }
            System.out.println("====================================");

        } catch (Exception e) {
            System.out.println("Error getting recent items: " + e.getMessage());
            e.printStackTrace();
        }

        model.addAttribute("user", user);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("lostItems", lostItems);
        model.addAttribute("foundItems", foundItems);
        model.addAttribute("claims", claims);
        model.addAttribute("recentItems", recentItems);

        // ========== CRITICAL: These two lines are MUST ==========
        model.addAttribute("recentLostItems", recentLostItems);
        model.addAttribute("recentFoundItems", recentFoundItems);

        return "dashboard";
    }

    @GetMapping("/my-items")
    public String myItems(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        List<Item> myItems = itemRepository.findByUserId(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("myItems", myItems);

        return "my-items";
    }
}