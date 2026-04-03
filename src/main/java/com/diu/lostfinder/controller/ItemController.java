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
import java.util.Optional;

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

    @GetMapping("/item/{id}")
    public String itemDetail(@PathVariable Long id, Model model, Authentication authentication) {
        Optional<Item> optionalItem = itemService.getItemById(id);

        if (optionalItem.isEmpty()) {
            return "redirect:/items";
        }

        Item item = optionalItem.get();
        model.addAttribute("item", item);

        if (authentication != null && authentication.isAuthenticated()) {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email).orElse(null);
            model.addAttribute("user", user);
            model.addAttribute("isLoggedIn", true);
        } else {
            model.addAttribute("isLoggedIn", false);
        }

        return "item-detail";
    }

    @PostMapping("/post-item")
    public String postItem(@RequestParam("type") String type,
                           @RequestParam("title") String title,
                           @RequestParam("category") String category,
                           @RequestParam("location") String location,
                           @RequestParam("date") String dateString,
                           @RequestParam("description") String description,
                           @RequestParam(value = "images", required = false) List<MultipartFile> images,
                           @RequestParam(value = "mainImage", required = false) Integer mainImageIndex,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {

        try {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "User not found!");
                return "redirect:/login";
            }

            Item item = new Item();
            item.setTitle(title);
            item.setDescription(description);
            item.setCategory(category);
            item.setLocation(location);
            item.setType(type);
            item.setStatus("PENDING");
            item.setPostedBy(user);
            item.setCreatedAt(LocalDateTime.now());

            LocalDateTime dateTime;
            if (dateString != null && !dateString.isEmpty()) {
                LocalDate localDate = LocalDate.parse(dateString);
                dateTime = localDate.atStartOfDay();
            } else {
                dateTime = LocalDateTime.now();
            }
            item.setDate(dateTime);

            if (images != null && !images.isEmpty()) {
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
                }
            }

            itemService.saveItem(item);

            redirectAttributes.addFlashAttribute("success", "Item posted successfully! Waiting for admin approval.");
            return "redirect:/my-items";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to post item: " + e.getMessage());
            if ("LOST".equals(type)) {
                return "redirect:/post-lost";
            } else {
                return "redirect:/post-found";
            }
        }
    }
}