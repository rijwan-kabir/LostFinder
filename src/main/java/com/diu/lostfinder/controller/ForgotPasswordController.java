package com.diu.lostfinder.controller;

import com.diu.lostfinder.entity.PasswordResetToken;
import com.diu.lostfinder.entity.User;
import com.diu.lostfinder.repository.PasswordResetTokenRepository;
import com.diu.lostfinder.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
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

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email,
                                        Model model) {

        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            model.addAttribute("error", "No account found with this email address!");
            return "forgot-password";
        }

        User user = userOpt.get();

        tokenRepository.deleteByUserId(user.getId());

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        tokenRepository.save(resetToken);

        String resetLink = "http://localhost:8081/reset-password?token=" + token;

        model.addAttribute("success", "Password reset link: " + resetLink);

        return "forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {

        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);

        if (tokenOpt.isEmpty()) {
            model.addAttribute("error", "Invalid password reset token!");
            return "forgot-password";
        }

        PasswordResetToken resetToken = tokenOpt.get();

        if (resetToken.isExpired()) {
            model.addAttribute("error", "Password reset link has expired!");
            tokenRepository.deleteByUserId(resetToken.getUser().getId());
            return "forgot-password";
        }

        if (resetToken.isUsed()) {
            model.addAttribute("error", "This password reset link has already been used!");
            return "forgot-password";
        }

        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
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
            tokenRepository.deleteByUserId(resetToken.getUser().getId());
            return "forgot-password";
        }

        if (resetToken.isUsed()) {
            model.addAttribute("error", "This password reset link has already been used!");
            return "forgot-password";
        }

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

        // FIX: Get user from resetToken, NOT from separate findById
        User user = resetToken.getUser();  // ← This is the fix

        if (user == null) {
            model.addAttribute("error", "User not found!");
            return "forgot-password";
        }

        user.setPassword(passwordEncoder.encode(password));
        userRepository.update(user);

        tokenRepository.markAsUsed(resetToken.getId());

        redirectAttributes.addFlashAttribute("success", "Password reset successfully! Please login with your new password.");
        return "redirect:/login";
    }
}