package com.diu.lostfinder.repository;

import com.diu.lostfinder.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int save(User user) {
        String sql = "INSERT INTO users (email, password, full_name, student_id, department, phone, role, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                user.getEmail(),
                user.getPassword(),
                user.getFullName(),
                user.getStudentId(),
                user.getDepartment(),
                user.getPhone(),
                user.getRole(),
                user.getCreatedAt()
        );
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), email);
            System.out.println("=== UserRepository.findByEmail ===");
            System.out.println("Email: " + email);
            System.out.println("User found: " + (user != null));
            if (user != null) {
                System.out.println("Password hash length: " + (user.getPassword() != null ? user.getPassword().length() : 0));
            }
            return Optional.ofNullable(user);
        } catch (Exception e) {
            System.out.println("User not found: " + email);
            return Optional.empty();
        }
    }

    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY id DESC";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
    }

    // ========== FIXED: Added password field in update ==========
    public int update(User user) {
        String sql = "UPDATE users SET full_name = ?, email = ?, student_id = ?, department = ?, phone = ?, password = ? WHERE id = ?";
        return jdbcTemplate.update(sql,
                user.getFullName(),
                user.getEmail(),
                user.getStudentId(),
                user.getDepartment(),
                user.getPhone(),
                user.getPassword(),
                user.getId()
        );
    }

    public int updateRole(Long userId, String role) {
        String sql = "UPDATE users SET role = ? WHERE id = ?";
        return jdbcTemplate.update(sql, role, userId);
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM users";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public long countByRole(String role) {
        String sql = "SELECT COUNT(*) FROM users WHERE role = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, role);
    }

    public List<User> search(String keyword) {
        String sql = "SELECT * FROM users WHERE full_name LIKE ? OR email LIKE ?";
        String pattern = "%" + keyword + "%";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class), pattern, pattern);
    }

    public int deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM password_reset_tokens WHERE user_id = ?", id);
        jdbcTemplate.update("DELETE FROM item_images WHERE item_id IN (SELECT id FROM items WHERE user_id = ?)", id);
        jdbcTemplate.update("DELETE FROM items WHERE user_id = ?", id);
        return jdbcTemplate.update("DELETE FROM users WHERE id = ?", id);
    }
}