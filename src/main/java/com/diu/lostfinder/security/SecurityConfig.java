package com.diu.lostfinder.security;

import com.diu.lostfinder.serviceimpl.CustomUserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsServiceImpl userDetailsService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

            if (isAdmin) {
                response.sendRedirect("/admin/dashboard");
            } else {
                response.sendRedirect("/dashboard");
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Static resources - সব public করতে হবে
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/**.css", "/style.css").permitAll()

                        // ===== IMPORTANT: Uploads folder public করতে হবে =====
                        .requestMatchers("/uploads/**").permitAll()  // ← এই line যোগ করো

                        // Public pages
                        .requestMatchers("/", "/index", "/register", "/login",
                                "/forgot-password", "/reset-password",
                                "/items", "/items/**").permitAll()

                        // Dashboard and other pages need authentication
                        .requestMatchers("/dashboard", "/post-lost", "/post-found",
                                "/my-items", "/my-claims", "/claim/**").authenticated()

                        // Admin only pages
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(authenticationSuccessHandler())
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .rememberMe(remember -> remember
                        .key("uniqueAndSecret")
                        .tokenValiditySeconds(86400)
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}