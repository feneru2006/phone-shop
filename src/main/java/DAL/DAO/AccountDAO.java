package DAL.DAO;

import DTO.accountDTO;
import java.sql.*;
import java.util.ArrayList;

public class AccountDAO {
    public accountDTO findByUsername(String username) {
        String sql = "SELECT * FROM account WHERE ten = ? LIMIT 1";
        try (Connection conn = DAL.DAO.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new accountDTO(
                            rs.getString("id"),
                            rs.getString("ten"),
                            rs.getString("pass"),
                            rs.getString("quyen")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public java.util.ArrayList<DTO.accountDTO> selectAll() {
        java.util.ArrayList<DTO.accountDTO> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM account";
        try (Connection conn = DAL.DAO.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new DTO.accountDTO(
                        rs.getString("id"),
                        rs.getString("ten"),
                        rs.getString("pass"),
                        rs.getString("quyen")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public ArrayList<String[]> getAvailableEmployees() {
        ArrayList<String[]> list = new ArrayList<>();
        String sql = "SELECT MANV, hoten FROM nhanvien WHERE MANV NOT IN (SELECT id FROM account)";
        try (Connection conn = DAL.DAO.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new String[]{rs.getString("MANV"), rs.getString("hoten")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ==========================================================
    // THÊM MỚI: Lấy danh sách tài khoản theo mã quyền
    // ==========================================================
    public ArrayList<accountDTO> selectByRole(String maQuyen) {
        ArrayList<accountDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM account WHERE quyen = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maQuyen);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new accountDTO(
                            rs.getString("id"),
                            rs.getString("ten"),
                            rs.getString("pass"),
                            rs.getString("quyen")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(accountDTO acc) {
        String sql = "INSERT INTO account (id, ten, pass, quyen) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, acc.getId());
            ps.setString(2, acc.getTen());
            ps.setString(3, acc.getPass());
            ps.setString(4, acc.getQuyen());

            return ps.executeUpdate() > 0; // Trả về true nếu chèn thành công ít nhất 1 dòng
        } catch (SQLException e) {
            // Nếu báo lỗi Foreign Key, khả năng cao là ID (Mã NV) chưa tồn tại trong bảng nhanvien
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(accountDTO acc) {
        String sql = "UPDATE account SET ten = ?, pass = ?, quyen = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, acc.getTen());
            ps.setString(2, acc.getPass());
            ps.setString(3, acc.getQuyen());
            ps.setString(4, acc.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String id) {
        String sql = "DELETE FROM account WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}