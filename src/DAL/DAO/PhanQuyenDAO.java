package DAL.DAO;

import DTO.phanquyenDTO;
import javax.sound.sampled.DataLine;
import java.sql.*;
import java.util.ArrayList;

public class PhanQuyenDAO {
    // Lay danh sach chuc nang theo ma quyen
    public ArrayList<String> getChucNangByMaQuyen(String maQuyen) {
        ArrayList<String> dsChucNang = new ArrayList<>();
        String sql = "SELECT MACN FROM phanquyen WHERE MAQUYEN = ?";

        try (Connection conn = DAL.DAO.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maQuyen);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dsChucNang.add(rs.getString("MACN"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsChucNang;
    }
}