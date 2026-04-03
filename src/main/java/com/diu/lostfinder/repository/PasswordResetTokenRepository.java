package com.diu.lostfinder.repository;

import com.diu.lostfinder.config.SqlLogger;
import com.diu.lostfinder.entity.PasswordResetToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PasswordResetTokenRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // INSERT - Save Reset Token
    public int save(PasswordResetToken token) {
        String sql = "INSERT INTO password_reset_tokens (token, user_id, expiry_date, used) VALUES (?, ?, ?, ?)";
        SqlLogger.log("INSERT", sql, token.getToken(), token.getUser().getId(), token.getExpiryDate(), token.isUsed() ? 1 : 0);
        return jdbcTemplate.update(sql,
                token.getToken(),
                token.getUser().getId(),
                token.getExpiryDate(),
                token.isUsed() ? 1 : 0
        );
    }

    // SELECT - Find by Token
    public Optional<PasswordResetToken> findByToken(String token) {
        String sql = "SELECT * FROM password_reset_tokens WHERE token = ?";
        SqlLogger.log("SELECT", sql, token);
        try {
            PasswordResetToken resetToken = jdbcTemplate.queryForObject(sql,
                    (rs, rowNum) -> {
                        PasswordResetToken t = new PasswordResetToken();
                        t.setId(rs.getLong("id"));
                        t.setToken(rs.getString("token"));
                        t.setExpiryDate(rs.getTimestamp("expiry_date").toLocalDateTime());
                        t.setUsed(rs.getBoolean("used"));
                        return t;
                    },
                    token);
            return Optional.ofNullable(resetToken);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // DELETE - Delete by User ID
    public int deleteByUserId(Long userId) {
        String sql = "DELETE FROM password_reset_tokens WHERE user_id = ?";
        SqlLogger.log("DELETE", sql, userId);
        return jdbcTemplate.update(sql, userId);
    }

    // DELETE - Delete by User (overload)
    public int deleteByUser(Long userId) {
        return deleteByUserId(userId);
    }

    // UPDATE - Mark Token as Used
    public int markAsUsed(Long id) {
        String sql = "UPDATE password_reset_tokens SET used = 1 WHERE id = ?";
        SqlLogger.log("UPDATE", sql, id);
        return jdbcTemplate.update(sql, id);
    }
}