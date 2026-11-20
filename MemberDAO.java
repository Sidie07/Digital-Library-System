package dao;

import database.DatabaseManager;
import models.Member;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberDAO {
    private final Connection conn;

    public MemberDAO() {
        this.conn = DatabaseManager.getInstance().getConnection();
    }

    public boolean addMember(Member member) {
        String sql = "INSERT INTO members (name, email, membership_type) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, member.getName());
            ps.setString(2, member.getEmail());
            ps.setString(3, member.getMembershipType());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) member.setMemberId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE")) {
                System.err.println("A member with that email already exists.");
            } else {
                System.err.println("Error adding member: " + e.getMessage());
            }
        }
        return false;
    }

    public List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members ORDER BY member_id";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) members.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Error retrieving members: " + e.getMessage());
        }
        return members;
    }

    public Member getMemberById(int id) {
        String sql = "SELECT * FROM members WHERE member_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving member: " + e.getMessage());
        }
        return null;
    }

    public List<Member> searchMembers(String keyword) {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members WHERE LOWER(name) LIKE ? OR CAST(member_id AS TEXT) = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword.toLowerCase() + "%");
            ps.setString(2, keyword);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) members.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error searching members: " + e.getMessage());
        }
        return members;
    }

    public boolean updateMember(Member member) {
        String sql = "UPDATE members SET name = ?, email = ?, membership_type = ? WHERE member_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, member.getName());
            ps.setString(2, member.getEmail());
            ps.setString(3, member.getMembershipType());
            ps.setInt(4, member.getMemberId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE")) {
                System.err.println("A member with that email already exists.");
            } else {
                System.err.println("Error updating member: " + e.getMessage());
            }
        }
        return false;
    }

    public boolean deleteMember(int id) {
        String sql = "DELETE FROM members WHERE member_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting member: " + e.getMessage());
        }
        return false;
    }

    private Member mapRow(ResultSet rs) throws SQLException {
        return new Member(
                rs.getInt("member_id"),
                rs.getString("name"),
                rs.getString("email"),
                rs.getString("membership_type")
        );
    }
}
