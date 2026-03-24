package DAL.DAO;

import DTO.hoadonDTO;
import java.sql.*;
import java.util.ArrayList;

public class HoaDonDAO {
    private final Connection conn = DBConnection.getConnection();

    public boolean insert(hoadonDTO hd) {
        String sql = "INSERT INTO hoadon (MAHD, MANV, MAKH, ngaylap, tongtien) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hd.getMaHD());
            ps.setString(2, hd.getMaNV());
            ps.setString(3, hd.getMaKH());
            ps.setTimestamp(4, Timestamp.valueOf(hd.getNgayLap()));
            ps.setDouble(5, hd.getTongTien());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }
    // Lấy số lượng hóa đơn hiện có để làm cơ sở tạo mã mới
    public int getRowCount() {
        String sql = "SELECT COUNT(*) FROM hoadon";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { 
            e.printStackTrace(); 
        }
        return 0;
    }

    // Phục vụ yêu cầu "Xem lịch sử mua sắm"
    public ArrayList<hoadonDTO> getLichSuMuaHang(String maKH) {
        ArrayList<hoadonDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM hoadon WHERE MAKH = ? ORDER BY ngaylap DESC";
        // ... (Viết ResultSet mapping tương tự getAll)
        return list;
    }

    public ArrayList<DTO.hoadonDTO> getAll() {
        ArrayList<DTO.hoadonDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM hoadon"; //lấy dữ liệu từ database ở bảng hoadon

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                DTO.hoadonDTO hd = new DTO.hoadonDTO();
                // Bơm dữ liệu từ Database vào DTO
                hd.setMaHD(rs.getString("MAHD"));
                hd.setMaKH(rs.getString("MAKH"));
                hd.setMaNV(rs.getString("MANV"));
                hd.setNgayLap(rs.getTimestamp("NGAYLAP").toLocalDateTime()); 
                hd.setTongTien(rs.getDouble("TONGTIEN")); 
                
                list.add(hd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}