package dao;

import database.DatabaseManager;
import models.BorrowingRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BorrowingDAO {
    private final Connection conn;

    public BorrowingDAO() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    public boolean addRecord(BorrowingRecord record) {
        String sql = "INSERT INTO borrowing_records (book_id, member_id, borrow_date, due_date, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, record.getBookId());
            ps.setInt(2, record.getMemberId());
            ps.setString(3, record.getBorrowDate());
            ps.setString(4, record.getDueDate());
            ps.setString(5, record.getStatus());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) record.setRecordId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error adding borrowing record: " + e.getMessage());
        }
        return false;
    }

    public List<BorrowingRecord> getAllRecords() {
        List<BorrowingRecord> records = new ArrayList<>();
        String sql = """
                SELECT br.*, b.title AS book_title, m.name AS member_name
                FROM borrowing_records br
                JOIN books b ON br.book_id = b.book_id
                JOIN members m ON br.member_id = m.member_id
                ORDER BY br.record_id""";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) records.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Error retrieving borrowing records: " + e.getMessage());
        }
        return records;
    }

    public BorrowingRecord getRecordById(int id) {
        String sql = """
                SELECT br.*, b.title AS book_title, m.name AS member_name
                FROM borrowing_records br
                JOIN books b ON br.book_id = b.book_id
                JOIN members m ON br.member_id = m.member_id
                WHERE br.record_id = ?""";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving record: " + e.getMessage());
        }
        return null;
    }

    public List<BorrowingRecord> searchByMember(String keyword) {
        List<BorrowingRecord> records = new ArrayList<>();
        String sql = """
                SELECT br.*, b.title AS book_title, m.name AS member_name
                FROM borrowing_records br
                JOIN books b ON br.book_id = b.book_id
                JOIN members m ON br.member_id = m.member_id
                WHERE LOWER(m.name) LIKE ? OR CAST(br.member_id AS TEXT) = ?
                ORDER BY br.record_id""";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword.toLowerCase() + "%");
            ps.setString(2, keyword);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) records.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching by member: " + e.getMessage());
        }
        return records;
    }

    public List<BorrowingRecord> searchByBook(String keyword) {
        List<BorrowingRecord> records = new ArrayList<>();
        String sql = """
                SELECT br.*, b.title AS book_title, m.name AS member_name
                FROM borrowing_records br
                JOIN books b ON br.book_id = b.book_id
                JOIN members m ON br.member_id = m.member_id
                WHERE LOWER(b.title) LIKE ? OR CAST(br.book_id AS TEXT) = ?
                ORDER BY br.record_id""";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword.toLowerCase() + "%");
            ps.setString(2, keyword);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) records.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching by book: " + e.getMessage());
        }
        return records;
    }

    public boolean updateRecord(BorrowingRecord record) {
        String sql = "UPDATE borrowing_records SET book_id = ?, member_id = ?, borrow_date = ?, due_date = ?, status = ? WHERE record_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, record.getBookId());
            ps.setInt(2, record.getMemberId());
            ps.setString(3, record.getBorrowDate());
            ps.setString(4, record.getDueDate());
            ps.setString(5, record.getStatus());
            ps.setInt(6, record.getRecordId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating record: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteRecord(int id) {
        String sql = "DELETE FROM borrowing_records WHERE record_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting record: " + e.getMessage());
        }
        return false;
    }

    private BorrowingRecord mapRow(ResultSet rs) throws SQLException {
        BorrowingRecord r = new BorrowingRecord(
                rs.getInt("record_id"),
                rs.getInt("book_id"),
                rs.getInt("member_id"),
                rs.getString("borrow_date"),
                rs.getString("due_date"),
                rs.getString("status")
        );
        r.setBookTitle(rs.getString("book_title"));
        r.setMemberName(rs.getString("member_name"));
        return r;
    }
}
