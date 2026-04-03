package com.diu.lostfinder.controller;

import com.diu.lostfinder.entity.Claim;
import com.diu.lostfinder.entity.Item;
import com.diu.lostfinder.entity.User;
import com.diu.lostfinder.repository.ClaimRepository;
import com.diu.lostfinder.repository.ItemRepository;
import com.diu.lostfinder.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ClaimController {

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    // Show claim form for an item
    @GetMapping("/claim/{itemId}")
    public String showClaimForm(@PathVariable Long itemId, Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        Item item = itemRepository.findById(itemId).orElse(null);
        if (item == null) {
            return "redirect:/items";
        }

        model.addAttribute("item", item);
        model.addAttribute("claim", new Claim());
        return "claim-form";
    }

    // Submit claim
    @PostMapping("/claim/submit")
    public String submitClaim(@RequestParam Long itemId,
                              @RequestParam String description,
                              @RequestParam(required = false) String proof,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {

        try {
            String email = authentication.getName();
            User claimant = userRepository.findByEmail(email).orElse(null);

            if (claimant == null) {
                redirectAttributes.addFlashAttribute("error", "User not found!");
                return "redirect:/login";
            }

            Item item = itemRepository.findById(itemId).orElse(null);
            if (item == null) {
                redirectAttributes.addFlashAttribute("error", "Item not found!");
                return "redirect:/items";
            }

            Claim claim = new Claim();
            claim.setItem(item);
            claim.setClaimant(claimant);
            claim.setDescription(description);
            claim.setProof(proof);
            claim.setStatus(Claim.ClaimStatus.PENDING);
            claim.setCreatedAt(LocalDateTime.now());

            claimRepository.save(claim);

            redirectAttributes.addFlashAttribute("success", "Claim submitted successfully! Waiting for admin approval.");
            return "redirect:/my-claims";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to submit claim: " + e.getMessage());
            return "redirect:/claim/" + itemId;
        }
    }

    // View my claims
    @GetMapping("/my-claims")
    public String myClaims(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        String email = authentication.getName();
        User claimant = userRepository.findByEmail(email).orElse(null);

        if (claimant == null) {
            return "redirect:/login";
        }

        List<Claim> claims = claimRepository.findByClaimantId(claimant.getId());
        model.addAttribute("claims", claims);
        model.addAttribute("user", claimant);

        return "my-claims";
    }
}