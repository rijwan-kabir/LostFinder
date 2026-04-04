package com.diu.lostfinder.repository;

import com.diu.lostfinder.config.SqlLogger;
import com.diu.lostfinder.entity.Claim;
import com.diu.lostfinder.entity.Item;
import com.diu.lostfinder.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class ClaimRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    private static final String BASE_SELECT =
            "SELECT c.id AS claim_id, c.description AS claim_description, c.proof AS claim_proof, " +
                    "  c.status AS claim_status, c.created_at AS claim_created_at, " +
                    "  c.processed_at AS claim_processed_at, c.admin_notes AS claim_admin_notes, " +
                    "  c.item_id AS fk_item_id, c.claimant_id AS fk_claimant_id, " +
                    "  i.id AS item_id, i.title AS item_title, i.location AS item_location, " +
                    "  i.description AS item_description, i.type AS item_type, i.status AS item_status, " +
                    "  i.main_image_url AS item_image, i.date AS item_date, " +
                    "  u.id AS claimant_id, u.full_name AS claimant_name, u.email AS claimant_email, " +
                    "  u.phone AS claimant_phone, u.student_id AS claimant_student_id, u.department AS claimant_department, " +
                    "  pb.id AS poster_id, pb.full_name AS poster_name, pb.email AS poster_email " +
                    "FROM claims c " +
                    "LEFT JOIN items i ON c.item_id = i.id " +
                    "LEFT JOIN users u ON c.claimant_id = u.id " +
                    "LEFT JOIN users pb ON i.user_id = pb.id ";

    private final RowMapper<Claim> claimRowMapper = (rs, rowNum) -> {
        Claim claim = new Claim();
        claim.setId(rs.getLong("claim_id"));
        claim.setDescription(rs.getString("claim_description"));
        claim.setProof(rs.getString("claim_proof"));
        String statusStr = rs.getString("claim_status");
        if (statusStr != null) {
            claim.setStatus(Claim.ClaimStatus.valueOf(statusStr));
        }
        claim.setCreatedAt(rs.getTimestamp("claim_created_at") != null ?
                rs.getTimestamp("claim_created_at").toLocalDateTime() : null);
        claim.setProcessedAt(rs.getTimestamp("claim_processed_at") != null ?
                rs.getTimestamp("claim_processed_at").toLocalDateTime() : null);
        claim.setAdminNotes(rs.getString("claim_admin_notes"));

        // Map Item
        long itemId = rs.getLong("item_id");
        if (itemId > 0) {
            Item item = new Item();
            item.setId(itemId);
            item.setTitle(rs.getString("item_title"));
            item.setLocation(rs.getString("item_location"));
            item.setDescription(rs.getString("item_description"));
            item.setType(rs.getString("item_type"));
            item.setStatus(rs.getString("item_status"));
            item.setMainImageUrl(rs.getString("item_image"));
            item.setDate(rs.getTimestamp("item_date") != null ?
                    rs.getTimestamp("item_date").toLocalDateTime() : null);

            // Map item poster
            long posterId = rs.getLong("poster_id");
            if (posterId > 0) {
                User poster = new User();
                poster.setId(posterId);
                poster.setFullName(rs.getString("poster_name"));
                poster.setEmail(rs.getString("poster_email"));
                item.setPostedBy(poster);
            }
            claim.setItem(item);
        }

        // Map Claimant
        long claimantId = rs.getLong("claimant_id");
        if (claimantId > 0) {
            User claimant = new User();
            claimant.setId(claimantId);
            claimant.setFullName(rs.getString("claimant_name"));
            claimant.setEmail(rs.getString("claimant_email"));
            claimant.setPhone(rs.getString("claimant_phone"));
            claimant.setStudentId(rs.getString("claimant_student_id"));
            claimant.setDepartment(rs.getString("claimant_department"));
            claim.setClaimant(claimant);
        }

        return claim;
    };

    // ===================== SAVE =====================
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

    // ===================== FIND BY ID =====================
    public Optional<Claim> findById(Long id) {
        String sql = BASE_SELECT + "WHERE c.id = ?";
        SqlLogger.log("SELECT", sql, id);
        try {
            Claim claim = jdbcTemplate.queryForObject(sql, claimRowMapper, id);
            return Optional.ofNullable(claim);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // ===================== FIND BY CLAIMANT ID =====================
    public List<Claim> findByClaimantId(Long claimantId) {
        String sql = BASE_SELECT + "WHERE c.claimant_id = ? ORDER BY c.created_at DESC";
        SqlLogger.log("SELECT", sql, claimantId);
        return jdbcTemplate.query(sql, claimRowMapper, claimantId);
    }

    // ===================== FIND BY ITEM ID =====================
    public List<Claim> findByItemId(Long itemId) {
        String sql = BASE_SELECT + "WHERE c.item_id = ? ORDER BY c.created_at DESC";
        SqlLogger.log("SELECT", sql, itemId);
        return jdbcTemplate.query(sql, claimRowMapper, itemId);
    }

    // ===================== FIND ALL =====================
    public List<Claim> findAll() {
        String sql = BASE_SELECT + "ORDER BY c.created_at DESC";
        SqlLogger.log("SELECT", sql);
        return jdbcTemplate.query(sql, claimRowMapper);
    }

    // ===================== FIND PENDING =====================
    public List<Claim> findPendingClaims() {
        String sql = BASE_SELECT + "WHERE c.status = 'PENDING' ORDER BY c.created_at ASC";
        SqlLogger.log("SELECT", sql);
        return jdbcTemplate.query(sql, claimRowMapper);
    }

    // ===================== FIND BY STATUS =====================
    public List<Claim> findByStatus(String status) {
        String sql = BASE_SELECT + "WHERE c.status = ? ORDER BY c.created_at DESC";
        SqlLogger.log("SELECT", sql, status);
        return jdbcTemplate.query(sql, claimRowMapper, status);
    }

    // ===================== COUNTS =====================
    public long count() {
        String sql = "SELECT COUNT(*) FROM claims";
        SqlLogger.log("SELECT", sql);
        return jdbcTemplate.queryForObject(sql, Long.class);
    }

    public long countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM claims WHERE status = ?";
        SqlLogger.log("SELECT", sql, status);
        return jdbcTemplate.queryForObject(sql, Long.class, status);
    }

    public long countPendingClaims() {
        return countByStatus("PENDING");
    }

    // ===================== RECENT CLAIMS =====================
    public List<Claim> findRecentClaims(int limit) {
        String sql = BASE_SELECT + "ORDER BY c.created_at DESC LIMIT ?";
        SqlLogger.log("SELECT", sql, limit);
        return jdbcTemplate.query(sql, claimRowMapper, limit);
    }

    // ===================== UPDATE STATUS =====================
    public int updateStatus(Long id, String status, Long processedBy, String notes) {
        String sql = "UPDATE claims SET status = ?, processed_at = NOW(), processed_by = ?, admin_notes = ? WHERE id = ?";
        SqlLogger.log("UPDATE", sql, status, processedBy, notes, id);
        return jdbcTemplate.update(sql, status, processedBy, notes, id);
    }

    // ===================== DELETE =====================
    public int deleteById(Long id) {
        String sql = "DELETE FROM claims WHERE id = ?";
        SqlLogger.log("DELETE", sql, id);
        return jdbcTemplate.update(sql, id);
    }

    public int deleteByItemId(Long itemId) {
        String sql = "DELETE FROM claims WHERE item_id = ?";
        SqlLogger.log("DELETE", sql, itemId);
        return jdbcTemplate.update(sql, itemId);
    }

    public int deleteByClaimantId(Long claimantId) {
        String sql = "DELETE FROM claims WHERE claimant_id = ?";
        SqlLogger.log("DELETE", sql, claimantId);
        return jdbcTemplate.update(sql, claimantId);
    }
}