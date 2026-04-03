package com.diu.lostfinder.repository;

import com.diu.lostfinder.config.SqlLogger;
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

    // INSERT - Register User
    public int save(User user) {
        String sql = "INSERT INTO users (email, password, full_name, student_id, department, phone, role, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        SqlLogger.log("INSERT", sql, user.getEmail(), "*****", user.getFullName(), user.getStudentId(),
                user.getDepartment(), user.getPhone(), user.getRole(), user.getCreatedAt());
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

    // SELECT - Find by Email (Login)
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        SqlLogger.log("SELECT", sql, email);
        try {
            User user = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), email);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // SELECT - Find by ID
    public Optional<User> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        SqlLogger.log("SELECT", sql, id);
        try {
            User user = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), id);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // SELECT - All Users
    public List<User> findAll() {
        String sql = "SELECT * FROM users ORDER BY id DESC";
        SqlLogger.log("SELECT", sql);
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
    }

    // UPDATE - User Profile
    public int update(User user) {
        String sql = "UPDATE users SET full_name = ?, email = ?, student_id = ?, department = ?, phone = ?, password = ? WHERE id = ?";
        SqlLogger.log("UPDATE", sql, user.getFullName(), user.getEmail(), user.getStudentId(),
                user.getDepartment(), user.getPhone(), "*****", user.getId());
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

    // UPDATE - User Role (Make/Remove Admin)
    public int updateRole(Long userId, String role) {
        String sql = "UPDATE users SET role = ? WHERE id = ?";
        SqlLogger.log("UPDATE", sql, role, userId);
        return jdbcTemplate.update(sql, role, userId);
    }

    // SELECT - Count Users
    public long count() {
        String sql = "SELECT COUNT(*) FROM users";
        SqlLogger.log("SELECT", sql);
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    // SELECT - Count by Role
    public long countByRole(String role) {
        String sql = "SELECT COUNT(*) FROM users WHERE role = ?";
        SqlLogger.log("SELECT", sql, role);
        return jdbcTemplate.queryForObject(sql, Long.class, role);
    }

    // SELECT - Search Users
    public List<User> search(String keyword) {
        String sql = "SELECT * FROM users WHERE full_name LIKE ? OR email LIKE ?";
        String pattern = "%" + keyword + "%";
        SqlLogger.log("SELECT", sql, pattern, pattern);
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class), pattern, pattern);
    }

    // DELETE - User (with cascade)
    public int deleteById(Long id) {
        SqlLogger.log("DELETE", "DELETE FROM password_reset_tokens WHERE user_id = ?", id);
        jdbcTemplate.update("DELETE FROM password_reset_tokens WHERE user_id = ?", id);

        SqlLogger.log("DELETE", "DELETE FROM item_images WHERE item_id IN (SELECT id FROM items WHERE user_id = ?)", id);
        jdbcTemplate.update("DELETE FROM item_images WHERE item_id IN (SELECT id FROM items WHERE user_id = ?)", id);

        SqlLogger.log("DELETE", "DELETE FROM items WHERE user_id = ?", id);
        jdbcTemplate.update("DELETE FROM items WHERE user_id = ?", id);

        String sql = "DELETE FROM users WHERE id = ?";
        SqlLogger.log("DELETE", sql, id);
        return jdbcTemplate.update(sql, id);
    }
}