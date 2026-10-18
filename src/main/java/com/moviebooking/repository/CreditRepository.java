package com.moviebooking.repository;

import com.moviebooking.model.CreditTransaction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

/**
 * JDBC Repository for Loyalty Points & Transaction Auditing.
 */
@Repository
public class CreditRepository {

    private final JdbcTemplate jdbcTemplate;

    public CreditRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int getBalance(int userId) {
        String sql = "SELECT balance FROM user_credits WHERE user_id = ?";
        List<Integer> list = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("balance"), userId);
        if (list.isEmpty()) {
            initBalance(userId);
            return 0;
        }
        return list.get(0);
    }

    public void initBalance(int userId) {
        String sql = "INSERT INTO user_credits (user_id, balance) VALUES (?, 0) ON DUPLICATE KEY UPDATE balance = balance";
        jdbcTemplate.update(sql, userId);
    }

    public void updateBalance(int userId, int newBalance) {
        String sql = "UPDATE user_credits SET balance = ? WHERE user_id = ?";
        int updated = jdbcTemplate.update(sql, newBalance, userId);
        if (updated == 0) {
            String insertSql = "INSERT INTO user_credits (user_id, balance) VALUES (?, ?)";
            jdbcTemplate.update(insertSql, userId, newBalance);
        }
    }

    public void addTransaction(CreditTransaction tx) {
        String sql = "INSERT INTO credit_transactions (user_id, transaction_type, amount, description, balance_after, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, tx.getUserId(), tx.getTransactionType(), tx.getAmount(), tx.getDescription(), tx.getBalanceAfter(), Timestamp.valueOf(java.time.LocalDateTime.now()));
    }

    public List<CreditTransaction> getTransactionsByUserId(int userId) {
        String sql = "SELECT * FROM credit_transactions WHERE user_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, new CreditTransactionRowMapper(), userId);
    }

    private static class CreditTransactionRowMapper implements RowMapper<CreditTransaction> {
        @Override
        public CreditTransaction mapRow(ResultSet rs, int rowNum) throws SQLException {
            CreditTransaction tx = new CreditTransaction();
            tx.setTransactionId(rs.getInt("transaction_id"));
            tx.setUserId(rs.getInt("user_id"));
            tx.setTransactionType(rs.getString("transaction_type"));
            tx.setAmount(rs.getInt("amount"));
            tx.setDescription(rs.getString("description"));
            tx.setBalanceAfter(rs.getInt("balance_after"));
            Timestamp ts = rs.getTimestamp("created_at");
            if (ts != null) {
                tx.setCreatedAt(ts.toLocalDateTime());
            }
            return tx;
        }
    }
}
