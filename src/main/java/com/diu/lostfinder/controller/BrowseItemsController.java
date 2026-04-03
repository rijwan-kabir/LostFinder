package com.diu.lostfinder.controller;

import com.diu.lostfinder.entity.Item;
import com.diu.lostfinder.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class BrowseItemsController {

    @Autowired
    private ItemRepository itemRepository;

    // Browse all approved items (public page)
    @GetMapping("/items")
    public String browseItems(@RequestParam(value = "type", required = false) String type,
                              @RequestParam(value = "category", required = false) String category,
                              @RequestParam(value = "search", required = false) String search,
                              Model model,
                              Authentication authentication) {

        List<Item> items;

        // Filter by type (LOST/FOUND)
        if (type != null && !type.isEmpty()) {
            items = itemRepository.findByTypeAndStatus(type, "APPROVED");
        }
        // Filter by category
        else if (category != null && !category.isEmpty()) {
            items = itemRepository.findByCategoryAndStatus(category, "APPROVED");
        }
        // Search by keyword
        else if (search != null && !search.isEmpty()) {
            items = itemRepository.searchByKeyword(search);
        }
        // All approved items
        else {
            items = itemRepository.findByStatus("APPROVED");
        }

        // Check if user is logged in
        boolean isLoggedIn = (authentication != null && authentication.isAuthenticated());

        model.addAttribute("items", items);
        model.addAttribute("isLoggedIn", isLoggedIn);
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("searchKeyword", search);

        return "browse-items";
    }

    // View single item details
    @GetMapping("/items/{id}")
    public String viewItem(@PathVariable Long id, Model model, Authentication authentication) {
        Item item = itemRepository.findById(id).orElse(null);
        if (item == null) {
            return "redirect:/items";
        }

        // Only show approved items to public
        if (!"APPROVED".equals(item.getStatus())) {
            return "redirect:/items";
        }

        boolean isLoggedIn = (authentication != null && authentication.isAuthenticated());

        model.addAttribute("item", item);
        model.addAttribute("isLoggedIn", isLoggedIn);

        return "item-detail";
    }
}