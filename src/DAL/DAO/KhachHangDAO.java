package DAL.DAO;

import DTO.khachhangDTO;
import java.sql.*;
import java.util.ArrayList;

public class KhachHangDAO {
    private Connection conn = DBConnection.getConnection();

    public ArrayList<khachhangDTO> getAll() {
        ArrayList<khachhangDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM khachang";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new khachhangDTO(
                    rs.getString("MAKH"), rs.getString("hoten"),
                    khachhangDTO.GioiTinh.valueOf(rs.getString("gioitinh").toUpperCase()),
                    rs.getString("SDT"), rs.getString("diachi")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public boolean insert(khachhangDTO kh) {
        String sql = "INSERT INTO khachang (MAKH, hoten, gioitinh, SDT, diachi) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, kh.getMaKH());
            ps.setString(2, kh.getHoTen());
            // Xử lý enum giới tính cẩn thận tùy theo DTO của bạn
            ps.setString(3, kh.getGioiTinh().name().substring(0, 1).toUpperCase() + kh.getGioiTinh().name().substring(1).toLowerCase());
            ps.setString(4, kh.getSdt());
            ps.setString(5, kh.getDiaChi());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
}