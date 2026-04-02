package com.diu.lostfinder.repository;

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

    public int save(PasswordResetToken token) {
        String sql = "INSERT INTO password_reset_tokens (token, user_id, expiry_date, used) VALUES (?, ?, ?, ?)";
        return jdbcTemplate.update(sql,
                token.getToken(),
                token.getUser().getId(),
                token.getExpiryDate(),
                token.isUsed() ? 1 : 0
        );
    }

    public Optional<PasswordResetToken> findByToken(String token) {
        String sql = "SELECT * FROM password_reset_tokens WHERE token = ?";
        try {
            PasswordResetToken resetToken = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(PasswordResetToken.class), token);
            return Optional.ofNullable(resetToken);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public int deleteByUserId(Long userId) {
        String sql = "DELETE FROM password_reset_tokens WHERE user_id = ?";
        return jdbcTemplate.update(sql, userId);
    }

    public int deleteByUser(Long userId) {
        return deleteByUserId(userId);
    }

    public int markAsUsed(Long id) {
        String sql = "UPDATE password_reset_tokens SET used = 1 WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
}