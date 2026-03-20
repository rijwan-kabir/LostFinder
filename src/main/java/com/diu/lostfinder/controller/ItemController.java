package com.diu.lostfinder.controller;

import com.diu.lostfinder.entity.Item;
import com.diu.lostfinder.entity.User;
import com.diu.lostfinder.repository.UserRepository;
import com.diu.lostfinder.service.FileUploadService;
import com.diu.lostfinder.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ItemController {

    @Autowired
    private ItemService itemService;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/post-lost")
    public String showPostLostForm(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        model.addAttribute("item", new Item());
        model.addAttribute("type", "LOST");
        return "post-lost";
    }

    @GetMapping("/post-found")
    public String showPostFoundForm(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        model.addAttribute("item", new Item());
        model.addAttribute("type", "FOUND");
        return "post-found";
    }

    @PostMapping("/post-item")
    public String postItem(
            @RequestParam("type") String type,
            @RequestParam("title") String title,
            @RequestParam("category") String category,
            @RequestParam("location") String location,
            @RequestParam("date") String dateString,
            @RequestParam("description") String description,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @RequestParam(value = "mainImage", required = false) Integer mainImageIndex,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        System.out.println("========== POST ITEM RECEIVED ==========");
        System.out.println("Type: " + type);
        System.out.println("Title: " + title);
        System.out.println("Category: " + category);
        System.out.println("Location: " + location);
        System.out.println("Date: " + dateString);
        System.out.println("Description: " + description);
        System.out.println("Images: " + (images != null ? images.size() : 0));
        System.out.println("========================================");

        try {
            // Get current user
            String email = authentication.getName();
            User user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "User not found!");
                return "redirect:/login";
            }

            // Create new Item
            Item item = new Item();
            item.setTitle(title);
            item.setDescription(description);
            item.setCategory(category);
            item.setLocation(location);
            item.setType(Item.ItemType.valueOf(type));
            item.setStatus(Item.ItemStatus.PENDING);
            item.setPostedBy(user);
            item.setCreatedAt(LocalDateTime.now());

            // Handle date - parse from string
            LocalDateTime dateTime;
            if (dateString != null && !dateString.isEmpty()) {
                LocalDate localDate = LocalDate.parse(dateString);
                dateTime = localDate.atStartOfDay();
            } else {
                dateTime = LocalDateTime.now();
            }
            item.setDate(dateTime);
            System.out.println("Parsed date: " + dateTime);

            // Upload images
            if (images != null && !images.isEmpty()) {
                // Filter out empty files
                List<MultipartFile> validImages = images.stream()
                        .filter(f -> f != null && !f.isEmpty())
                        .toList();

                if (!validImages.isEmpty()) {
                    List<String> imageUrls = fileUploadService.uploadImages(validImages);
                    item.setImageUrls(imageUrls);

                    if (mainImageIndex != null && mainImageIndex < imageUrls.size()) {
                        item.setMainImageUrl(imageUrls.get(mainImageIndex));
                    } else if (!imageUrls.isEmpty()) {
                        item.setMainImageUrl(imageUrls.get(0));
                    }
                    System.out.println("Uploaded " + imageUrls.size() + " images");
                }
            }

            // Save item
            Item savedItem = itemService.saveItem(item);
            System.out.println("Item saved with ID: " + savedItem.getId());

            redirectAttributes.addFlashAttribute("success", "Item posted successfully! Waiting for admin approval.");
            return "redirect:/my-items";

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to post item: " + e.getMessage());
            if ("LOST".equals(type)) {
                return "redirect:/post-lost";
            } else {
                return "redirect:/post-found";
            }
        }
    }
}