package com.diu.lostfinder.repository;

import com.diu.lostfinder.config.SqlLogger;
import com.diu.lostfinder.entity.Claim;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ClaimRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Save claim
    public int save(Claim claim) {
        String sql = "INSERT INTO claims (item_id, claimant_id, description, proof, status, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        SqlLogger.log("INSERT", sql, claim.getItem().getId(), claim.getClaimant().getId(),
                claim.getDescription(), claim.getProof(), claim.getStatus().toString(), claim.getCreatedAt());
        return jdbcTemplate.update(sql,
                claim.getItem().getId(),
                claim.getClaimant().getId(),
                claim.getDescription(),
                claim.getProof(),
                claim.getStatus().toString(),
                claim.getCreatedAt()
        );
    }

    // Find by ID
    public Optional<Claim> findById(Long id) {
        String sql = "SELECT * FROM claims WHERE id = ?";
        SqlLogger.log("SELECT", sql, id);
        try {
            Claim claim = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Claim.class), id);
            return Optional.ofNullable(claim);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // Find by claimant ID
    public List<Claim> findByClaimantId(Long claimantId) {
        String sql = "SELECT * FROM claims WHERE claimant_id = ? ORDER BY created_at DESC";
        SqlLogger.log("SELECT", sql, claimantId);
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Claim.class), claimantId);
    }

    // Find by item ID
    public List<Claim> findByItemId(Long itemId) {
        String sql = "SELECT * FROM claims WHERE item_id = ? ORDER BY created_at DESC";
        SqlLogger.log("SELECT", sql, itemId);
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Claim.class), itemId);
    }

    // Find all claims
    public List<Claim> findAll() {
        String sql = "SELECT * FROM claims ORDER BY created_at DESC";
        SqlLogger.log("SELECT", sql);
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Claim.class));
    }

    // Find pending claims
    public List<Claim> findPendingClaims() {
        String sql = "SELECT * FROM claims WHERE status = 'PENDING' ORDER BY created_at ASC";
        SqlLogger.log("SELECT", sql);
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Claim.class));
    }

    // Find by status
    public List<Claim> findByStatus(String status) {
        String sql = "SELECT * FROM claims WHERE status = ? ORDER BY created_at DESC";
        SqlLogger.log("SELECT", sql, status);
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Claim.class), status);
    }

    // Count total claims
    public long count() {
        String sql = "SELECT COUNT(*) FROM claims";
        SqlLogger.log("SELECT", sql);
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    // Count by status
    public long countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM claims WHERE status = ?";
        SqlLogger.log("SELECT", sql, status);
        return jdbcTemplate.queryForObject(sql, Long.class, status);
    }

    // Count pending claims
    public long countPendingClaims() {
        return countByStatus("PENDING");
    }

    // Find recent claims
    public List<Claim> findRecentClaims(int limit) {
        String sql = "SELECT * FROM claims ORDER BY created_at DESC LIMIT ?";
        SqlLogger.log("SELECT", sql, limit);
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Claim.class), limit);
    }

    // ===== SINGLE updateStatus METHOD (remove duplicate) =====
    public int updateStatus(Long id, String status, Long processedBy, String notes) {
        String sql = "UPDATE claims SET status = ?, processed_at = NOW(), processed_by = ?, admin_notes = ? WHERE id = ?";
        SqlLogger.log("UPDATE", sql, status, processedBy, notes, id);
        return jdbcTemplate.update(sql, status, processedBy, notes, id);
    }

    // Delete by claim ID
    public int deleteById(Long id) {
        String sql = "DELETE FROM claims WHERE id = ?";
        SqlLogger.log("DELETE", sql, id);
        return jdbcTemplate.update(sql, id);
    }

    // Delete by item ID
    public int deleteByItemId(Long itemId) {
        String sql = "DELETE FROM claims WHERE item_id = ?";
        SqlLogger.log("DELETE", sql, itemId);
        return jdbcTemplate.update(sql, itemId);
    }

    // Delete by claimant ID
    public int deleteByClaimantId(Long claimantId) {
        String sql = "DELETE FROM claims WHERE claimant_id = ?";
        SqlLogger.log("DELETE", sql, claimantId);
        return jdbcTemplate.update(sql, claimantId);
    }
}