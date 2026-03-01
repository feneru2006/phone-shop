package DAL.DAO;

import DTO.PhieubaohanhDTO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhieubaohanhDAO {

    // ===============================
    // Lấy tất cả
    // ===============================
    public List<PhieubaohanhDTO> getAll() {

        List<PhieubaohanhDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM phieubaohanh";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                PhieubaohanhDTO bh = new PhieubaohanhDTO();

                bh.setMaBH(rs.getString("MABH"));
                bh.setMaCTHD(rs.getString("MACTHD"));
                bh.setMaKH(rs.getString("MAKH"));
                bh.setNgayBD(rs.getDate("ngayBD").toLocalDate());
                bh.setThoiHan(rs.getInt("thoihan"));
                bh.setTrangthai(rs.getString("trangthai"));

                list.add(bh);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // ===============================
    // Insert
    // ===============================
    public boolean insert(PhieubaohanhDTO bh) {

        String sql = """
                INSERT INTO phieubaohanh
                (MABH, MACTHD, MAKH, ngayBD, thoihan, trangthai)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, bh.getMaBH());
            ps.setString(2, bh.getMaCTHD());
            ps.setString(3, bh.getMaKH());
            ps.setDate(4, Date.valueOf(bh.getNgayBD()));
            ps.setInt(5, bh.getThoiHan());
            ps.setString(6, bh.getTrangthai());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // ===============================
    // Update
    // ===============================
    public boolean update(PhieubaohanhDTO bh) {

        String sql = """
                UPDATE phieubaohanh
                SET MACTHD=?,
                    MAKH=?,
                    ngayBD=?,
                    thoihan=?,
                    trangthai=?
                WHERE MABH=?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, bh.getMaCTHD());
            ps.setString(2, bh.getMaKH());
            ps.setDate(3, Date.valueOf(bh.getNgayBD()));
            ps.setInt(4, bh.getThoiHan());
            ps.setString(5, bh.getTrangthai());
            ps.setString(6, bh.getMaBH());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // ===============================
    // Delete
    // ===============================
    public boolean delete(String maBH) {

        String sql = "DELETE FROM phieubaohanh WHERE MABH=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maBH);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}