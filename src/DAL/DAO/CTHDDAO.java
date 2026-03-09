package DAL.DAO;

import DTO.CTHDDTO;
import java.sql.*;
import java.util.ArrayList;

public class CTHDDAO {
    private Connection conn = DBConnection.getConnection();

    public boolean insert(CTHDDTO ct) {
        // Đưa trực tiếp MACTSP (IMEI) vào bảng CTHD
        String sql = "INSERT INTO CTHD (MACTHD, MAHD, MACTSP, Dongia, Thanhtien) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ct.getMaCTHD());
            ps.setString(2, ct.getMaHD());
            ps.setString(3, ct.getMaCTSP()); 
            ps.setDouble(4, ct.getDonGia());
            ps.setDouble(5, ct.getThanhTien());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public ArrayList<CTHDDTO> getListByMaHD(String maHD) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}