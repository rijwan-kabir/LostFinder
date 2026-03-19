package com.diu.lostfinder.controller;

import com.diu.lostfinder.entity.PasswordResetToken;
import com.diu.lostfinder.entity.User;
import com.diu.lostfinder.repository.PasswordResetTokenRepository;
import com.diu.lostfinder.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Controller
public class ForgotPasswordController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // Show forgot password form
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    // Process forgot password form - WITH TRANSACTION
    @PostMapping("/forgot-password")
    @Transactional  // ← THIS FIXES THE ERROR
    public String processForgotPassword(@RequestParam("email") String email,
                                        Model model) {

        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            model.addAttribute("error", "No account found with this email address!");
            return "forgot-password";
        }

        User user = userOpt.get();

        // Delete any existing tokens for this user (NOW WORKS WITH @Transactional)
        tokenRepository.deleteByUser(user);

        // Create new token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        tokenRepository.save(resetToken);

        // Generate reset link
        String resetLink = "http://localhost:8081/reset-password?token=" + token;

        // For testing - show link directly
        model.addAttribute("success", "Password reset link generated! Click here: " + resetLink);

        return "forgot-password";
    }

    // Show reset password form
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {

        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);

        if (tokenOpt.isEmpty()) {
            model.addAttribute("error", "Invalid password reset token!");
            return "forgot-password";
        }

        PasswordResetToken resetToken = tokenOpt.get();

        if (resetToken.isExpired()) {
            model.addAttribute("error", "Password reset link has expired! Please request a new one.");
            tokenRepository.delete(resetToken);
            return "forgot-password";
        }

        if (resetToken.isUsed()) {
            model.addAttribute("error", "This password reset link has already been used!");
            return "forgot-password";
        }

        model.addAttribute("token", token);
        return "reset-password";
    }

    // Process reset password - WITH TRANSACTION
    @PostMapping("/reset-password")
    @Transactional  // ← ADD THIS TOO
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String password,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       Model model,
                                       RedirectAttributes redirectAttributes) {

        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);

        if (tokenOpt.isEmpty()) {
            model.addAttribute("error", "Invalid password reset token!");
            return "forgot-password";
        }

        PasswordResetToken resetToken = tokenOpt.get();

        if (resetToken.isExpired()) {
            model.addAttribute("error", "Password reset link has expired!");
            tokenRepository.delete(resetToken);
            return "forgot-password";
        }

        if (resetToken.isUsed()) {
            model.addAttribute("error", "This password reset link has already been used!");
            return "forgot-password";
        }

        // Validate passwords
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Passwords do not match!");
            model.addAttribute("token", token);
            return "reset-password";
        }

        if (password.length() < 6) {
            model.addAttribute("error", "Password must be at least 6 characters!");
            model.addAttribute("token", token);
            return "reset-password";
        }

        // Update user password
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        // Mark token as used
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        redirectAttributes.addFlashAttribute("success", "Password reset successfully! Please login with your new password.");
        return "redirect:/login";
    }
}