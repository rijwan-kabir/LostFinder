package com.diu.lostfinder.repository;

import com.diu.lostfinder.config.SqlLogger;
import com.diu.lostfinder.entity.Item;
import com.diu.lostfinder.entity.User;  // ← এই line যোগ করো
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ItemRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // INSERT - Post Lost/Found Item
    public int save(Item item) {
        String sql = "INSERT INTO items (title, description, category, location, type, status, date, user_id, main_image_url, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        SqlLogger.log("INSERT", sql, item.getTitle(), item.getDescription(), item.getCategory(),
                item.getLocation(), item.getType(), item.getStatus(), item.getDate(),
                item.getPostedBy().getId(), item.getMainImageUrl(), item.getCreatedAt());
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

    // SELECT - Find by ID
    public Optional<Item> findById(Long id) {
        String sql = "SELECT i.*, u.id as user_id, u.full_name as posted_by_name, u.email as posted_by_email " +
                "FROM items i " +
                "LEFT JOIN users u ON i.user_id = u.id " +
                "WHERE i.id = ?";
        SqlLogger.log("SELECT", sql, id);
        try {
            Item item = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                Item i = new Item();
                i.setId(rs.getLong("id"));
                i.setTitle(rs.getString("title"));
                i.setDescription(rs.getString("description"));
                i.setCategory(rs.getString("category"));
                i.setLocation(rs.getString("location"));
                i.setType(rs.getString("type"));
                i.setStatus(rs.getString("status"));
                i.setDate(rs.getTimestamp("date") != null ? rs.getTimestamp("date").toLocalDateTime() : null);
                i.setMainImageUrl(rs.getString("main_image_url"));
                i.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
                i.setApprovedAt(rs.getTimestamp("approved_at") != null ? rs.getTimestamp("approved_at").toLocalDateTime() : null);
                i.setRejectionReason(rs.getString("rejection_reason"));

                // Set User object
                User user = new User();
                user.setId(rs.getLong("user_id"));
                user.setFullName(rs.getString("posted_by_name"));
                user.setEmail(rs.getString("posted_by_email"));
                i.setPostedBy(user);

                return i;
            }, id);
            return Optional.ofNullable(item);
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    // SELECT - All Items
    public List<Item> findAll() {
        String sql = "SELECT i.*, u.id as user_id, u.full_name as posted_by_name, u.email as posted_by_email " +
                "FROM items i " +
                "LEFT JOIN users u ON i.user_id = u.id " +
                "ORDER BY i.created_at DESC";
        SqlLogger.log("SELECT", sql);
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Item i = new Item();
            i.setId(rs.getLong("id"));
            i.setTitle(rs.getString("title"));
            i.setDescription(rs.getString("description"));
            i.setCategory(rs.getString("category"));
            i.setLocation(rs.getString("location"));
            i.setType(rs.getString("type"));
            i.setStatus(rs.getString("status"));
            i.setDate(rs.getTimestamp("date") != null ? rs.getTimestamp("date").toLocalDateTime() : null);
            i.setMainImageUrl(rs.getString("main_image_url"));
            i.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
            i.setApprovedAt(rs.getTimestamp("approved_at") != null ? rs.getTimestamp("approved_at").toLocalDateTime() : null);
            i.setRejectionReason(rs.getString("rejection_reason"));

            User user = new User();
            user.setId(rs.getLong("user_id"));
            user.setFullName(rs.getString("posted_by_name"));
            user.setEmail(rs.getString("posted_by_email"));
            i.setPostedBy(user);

            return i;
        });
    }

    // SELECT - Items by Status (PENDING, APPROVED, REJECTED)
    public List<Item> findByStatus(String status) {
        String sql = "SELECT i.*, u.id as user_id, u.full_name as posted_by_name, u.email as posted_by_email " +
                "FROM items i " +
                "LEFT JOIN users u ON i.user_id = u.id " +
                "WHERE i.status = ? " +
                "ORDER BY i.created_at DESC";
        SqlLogger.log("SELECT", sql, status);
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Item i = new Item();
            i.setId(rs.getLong("id"));
            i.setTitle(rs.getString("title"));
            i.setDescription(rs.getString("description"));
            i.setCategory(rs.getString("category"));
            i.setLocation(rs.getString("location"));
            i.setType(rs.getString("type"));
            i.setStatus(rs.getString("status"));
            i.setDate(rs.getTimestamp("date") != null ? rs.getTimestamp("date").toLocalDateTime() : null);
            i.setMainImageUrl(rs.getString("main_image_url"));
            i.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
            i.setApprovedAt(rs.getTimestamp("approved_at") != null ? rs.getTimestamp("approved_at").toLocalDateTime() : null);
            i.setRejectionReason(rs.getString("rejection_reason"));

            User user = new User();
            user.setId(rs.getLong("user_id"));
            user.setFullName(rs.getString("posted_by_name"));
            user.setEmail(rs.getString("posted_by_email"));
            i.setPostedBy(user);

            return i;
        }, status);
    }

    // SELECT - Items by User ID
    public List<Item> findByUserId(Long userId) {
        String sql = "SELECT i.*, u.id as user_id, u.full_name as posted_by_name, u.email as posted_by_email " +
                "FROM items i " +
                "LEFT JOIN users u ON i.user_id = u.id " +
                "WHERE i.user_id = ? " +
                "ORDER BY i.created_at DESC";
        SqlLogger.log("SELECT", sql, userId);
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Item i = new Item();
            i.setId(rs.getLong("id"));
            i.setTitle(rs.getString("title"));
            i.setDescription(rs.getString("description"));
            i.setCategory(rs.getString("category"));
            i.setLocation(rs.getString("location"));
            i.setType(rs.getString("type"));
            i.setStatus(rs.getString("status"));
            i.setDate(rs.getTimestamp("date") != null ? rs.getTimestamp("date").toLocalDateTime() : null);
            i.setMainImageUrl(rs.getString("main_image_url"));
            i.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
            i.setApprovedAt(rs.getTimestamp("approved_at") != null ? rs.getTimestamp("approved_at").toLocalDateTime() : null);
            i.setRejectionReason(rs.getString("rejection_reason"));

            User user = new User();
            user.setId(rs.getLong("user_id"));
            user.setFullName(rs.getString("posted_by_name"));
            user.setEmail(rs.getString("posted_by_email"));
            i.setPostedBy(user);

            return i;
        }, userId);
    }

    // SELECT - Items by Type (LOST/FOUND)
    public List<Item> findByType(String type) {
        String sql = "SELECT * FROM items WHERE type = ? ORDER BY created_at DESC";
        SqlLogger.log("SELECT", sql, type);
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Item.class), type);
    }

    // SELECT - Items by Posted By User ID
    public List<Item> findByPostedByUserId(Long userId) {
        String sql = "SELECT * FROM items WHERE user_id = ?";
        SqlLogger.log("SELECT", sql, userId);
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Item.class), userId);
    }

    // UPDATE - Approve Item
    public int approveItem(Long id, Long approvedBy) {
        String sql = "UPDATE items SET status = 'APPROVED', approved_at = NOW(), approved_by = ? WHERE id = ?";
        SqlLogger.log("UPDATE", sql, approvedBy, id);
        return jdbcTemplate.update(sql, approvedBy, id);
    }

    // UPDATE - Reject Item
    public int rejectItem(Long id) {
        String sql = "UPDATE items SET status = 'REJECTED' WHERE id = ?";
        SqlLogger.log("UPDATE", sql, id);
        return jdbcTemplate.update(sql, id);
    }

    // DELETE - Items by User
    public int deleteByPostedByUserId(Long userId) {
        String sql = "DELETE FROM items WHERE user_id = ?";
        SqlLogger.log("DELETE", sql, userId);
        return jdbcTemplate.update(sql, userId);
    }

    // DELETE - Single Item
    public int deleteById(Long id) {
        SqlLogger.log("DELETE", "DELETE FROM item_images WHERE item_id = ?", id);
        jdbcTemplate.update("DELETE FROM item_images WHERE item_id = ?", id);

        String sql = "DELETE FROM items WHERE id = ?";
        SqlLogger.log("DELETE", sql, id);
        return jdbcTemplate.update(sql, id);
    }

    // SELECT - Count Items
    public long count() {
        String sql = "SELECT COUNT(*) FROM items";
        SqlLogger.log("SELECT", sql);
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    // SELECT - Count Approved Items
    public long countApproved() {
        String sql = "SELECT COUNT(*) FROM items WHERE status = 'APPROVED'";
        SqlLogger.log("SELECT", sql);
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    // Count by user
    public long countByUserId(Long userId) {
        String sql = "SELECT COUNT(*) FROM items WHERE user_id = ?";
        SqlLogger.log("SELECT", sql, userId);
        return jdbcTemplate.queryForObject(sql, Long.class, userId);
    }

    // Count by user and type
    public long countByUserIdAndType(Long userId, String type) {
        String sql = "SELECT COUNT(*) FROM items WHERE user_id = ? AND type = ?";
        SqlLogger.log("SELECT", sql, userId, type);
        return jdbcTemplate.queryForObject(sql, Long.class, userId, type);
    }

    // Get recent items by user
    public List<Item> findRecentByUserId(Long userId, int limit) {
        String sql = "SELECT i.*, u.id as user_id, u.full_name as posted_by_name, u.email as posted_by_email " +
                "FROM items i " +
                "LEFT JOIN users u ON i.user_id = u.id " +
                "WHERE i.user_id = ? " +
                "ORDER BY i.created_at DESC LIMIT ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Item i = new Item();
            i.setId(rs.getLong("id"));
            i.setTitle(rs.getString("title"));
            i.setDescription(rs.getString("description"));
            i.setCategory(rs.getString("category"));
            i.setLocation(rs.getString("location"));
            i.setType(rs.getString("type"));
            i.setStatus(rs.getString("status"));
            i.setDate(rs.getTimestamp("date") != null ? rs.getTimestamp("date").toLocalDateTime() : null);
            i.setMainImageUrl(rs.getString("main_image_url"));
            i.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
            i.setApprovedAt(rs.getTimestamp("approved_at") != null ? rs.getTimestamp("approved_at").toLocalDateTime() : null);
            i.setRejectionReason(rs.getString("rejection_reason"));

            User user = new User();
            user.setId(rs.getLong("user_id"));
            user.setFullName(rs.getString("posted_by_name"));
            user.setEmail(rs.getString("posted_by_email"));
            i.setPostedBy(user);
            return i;
        }, userId, limit);
    }

    // Count by status
    public long countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM items WHERE status = ?";
        SqlLogger.log("SELECT", sql, status);
        return jdbcTemplate.queryForObject(sql, Long.class, status);
    }

    // Count by type
    public long countByType(String type) {
        String sql = "SELECT COUNT(*) FROM items WHERE type = ?";
        SqlLogger.log("SELECT", sql, type);
        return jdbcTemplate.queryForObject(sql, Long.class, type);
    }

    // Update status
    public int updateStatus(Long id, String status) {
        String sql = "UPDATE items SET status = ? WHERE id = ?";
        SqlLogger.log("UPDATE", sql, status, id);
        return jdbcTemplate.update(sql, status, id);
    }

    // Get recent items
    public List<Item> findRecentItems(int limit) {
        String sql = "SELECT i.*, u.full_name as posted_by_name FROM items i LEFT JOIN users u ON i.user_id = u.id ORDER BY i.created_at DESC LIMIT ?";
        SqlLogger.log("SELECT", sql, limit);
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Item.class), limit);
    }

    // Get category statistics
    public List<Map<String, Object>> getCategoryStats() {
        String sql = "SELECT category, COUNT(*) as count FROM items GROUP BY category ORDER BY count DESC";
        SqlLogger.log("SELECT", sql);
        return jdbcTemplate.queryForList(sql);
    }

    // Find by type and status (for filtering)
    public List<Item> findByTypeAndStatus(String type, String status) {
        String sql = "SELECT i.*, u.full_name as posted_by_name, u.email as posted_by_email " +
                "FROM items i " +
                "LEFT JOIN users u ON i.user_id = u.id " +
                "WHERE i.type = ? AND i.status = ? " +
                "ORDER BY i.created_at DESC";
        SqlLogger.log("SELECT", sql, type, status);
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Item item = mapItemWithUser(rs);
            return item;
        }, type, status);
    }

    // Find by category and status
    public List<Item> findByCategoryAndStatus(String category, String status) {
        String sql = "SELECT i.*, u.full_name as posted_by_name, u.email as posted_by_email " +
                "FROM items i " +
                "LEFT JOIN users u ON i.user_id = u.id " +
                "WHERE i.category = ? AND i.status = ? " +
                "ORDER BY i.created_at DESC";
        SqlLogger.log("SELECT", sql, category, status);
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Item item = mapItemWithUser(rs);
            return item;
        }, category, status);
    }

    // Search by keyword (title or description)
    public List<Item> searchByKeyword(String keyword) {
        String sql = "SELECT i.*, u.full_name as posted_by_name, u.email as posted_by_email " +
                "FROM items i " +
                "LEFT JOIN users u ON i.user_id = u.id " +
                "WHERE (i.title LIKE ? OR i.description LIKE ?) AND i.status = 'APPROVED' " +
                "ORDER BY i.created_at DESC";
        String searchPattern = "%" + keyword + "%";
        SqlLogger.log("SELECT", sql, searchPattern, searchPattern);
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Item item = mapItemWithUser(rs);
            return item;
        }, searchPattern, searchPattern);
    }

    // Helper method to map Item with User
    private Item mapItemWithUser(java.sql.ResultSet rs) throws java.sql.SQLException {
        Item item = new Item();
        item.setId(rs.getLong("id"));
        item.setTitle(rs.getString("title"));
        item.setDescription(rs.getString("description"));
        item.setCategory(rs.getString("category"));
        item.setLocation(rs.getString("location"));
        item.setType(rs.getString("type"));
        item.setStatus(rs.getString("status"));
        item.setDate(rs.getTimestamp("date") != null ? rs.getTimestamp("date").toLocalDateTime() : null);
        item.setMainImageUrl(rs.getString("main_image_url"));
        item.setCreatedAt(rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
        item.setApprovedAt(rs.getTimestamp("approved_at") != null ? rs.getTimestamp("approved_at").toLocalDateTime() : null);
        item.setRejectionReason(rs.getString("rejection_reason"));

        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setFullName(rs.getString("posted_by_name"));
        user.setEmail(rs.getString("posted_by_email"));
        item.setPostedBy(user);

        return item;
    }


    // ItemRepository.java te ei method gulo add koro

    // Get recent LOST items (global - all users, only APPROVED status)
    public List<Item> findRecentLostItemsGlobal(int limit) {
        String sql = "SELECT i.*, u.id as user_id, u.full_name as posted_by_name, u.email as posted_by_email " +
                "FROM items i " +
                "LEFT JOIN users u ON i.user_id = u.id " +
                "WHERE i.type = 'LOST' AND i.status = 'APPROVED' " +
                "ORDER BY i.created_at DESC LIMIT ?";
        SqlLogger.log("SELECT", sql, limit);
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapItemWithUser(rs), limit);
    }

    // Get recent FOUND items (global - all users, only APPROVED status)
    public List<Item> findRecentFoundItemsGlobal(int limit) {
        String sql = "SELECT i.*, u.id as user_id, u.full_name as posted_by_name, u.email as posted_by_email " +
                "FROM items i " +
                "LEFT JOIN users u ON i.user_id = u.id " +
                "WHERE i.type = 'FOUND' AND i.status = 'APPROVED' " +
                "ORDER BY i.created_at DESC LIMIT ?";
        SqlLogger.log("SELECT", sql, limit);
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapItemWithUser(rs), limit);
    }
}