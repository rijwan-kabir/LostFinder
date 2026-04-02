package com.diu.lostfinder.repository;

import com.diu.lostfinder.entity.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ItemRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int save(Item item) {
        String sql = "INSERT INTO items (title, description, category, location, type, status, date, user_id, main_image_url, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                item.getTitle(),
                item.getDescription(),
                item.getCategory(),
                item.getLocation(),
                item.getType(),
                item.getStatus(),
                item.getDate(),
                item.getPostedBy().getId(),
                item.getMainImageUrl(),
                item.getCreatedAt()
        );
    }

    public Optional<Item> findById(Long id) {
        String sql = "SELECT * FROM items WHERE id = ?";
        try {
            Item item = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Item.class), id);
            return Optional.ofNullable(item);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public List<Item> findAll() {
        String sql = "SELECT * FROM items ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Item.class));
    }

    public List<Item> findByStatus(String status) {
        String sql = "SELECT * FROM items WHERE status = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Item.class), status);
    }

    public List<Item> findByUserId(Long userId) {
        String sql = "SELECT * FROM items WHERE user_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Item.class), userId);
    }

    public List<Item> findByType(String type) {
        String sql = "SELECT * FROM items WHERE type = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Item.class), type);
    }

    public List<Item> findByPostedByUserId(Long userId) {
        String sql = "SELECT * FROM items WHERE user_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Item.class), userId);
    }

    public int approveItem(Long id, Long approvedBy) {
        String sql = "UPDATE items SET status = 'APPROVED', approved_at = NOW(), approved_by = ? WHERE id = ?";
        return jdbcTemplate.update(sql, approvedBy, id);
    }

    public int rejectItem(Long id) {
        String sql = "UPDATE items SET status = 'REJECTED' WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    public int deleteByPostedByUserId(Long userId) {
        String sql = "DELETE FROM items WHERE user_id = ?";
        return jdbcTemplate.update(sql, userId);
    }

    public int deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM item_images WHERE item_id = ?", id);
        return jdbcTemplate.update("DELETE FROM items WHERE id = ?", id);
    }

    public long count() {
        String sql = "SELECT COUNT(*) FROM items";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public long countApproved() {
        String sql = "SELECT COUNT(*) FROM items WHERE status = 'APPROVED'";
        return jdbcTemplate.queryForObject(sql, Long.class);
    }
}