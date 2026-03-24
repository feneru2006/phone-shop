package DAL.DAO;

import DTO.ChitietSPDTO;
import java.sql.*;
import java.util.ArrayList;

public class ChitietSPDAO {
    // Gọi DBConnection từ cùng package DAL.DAO
    private Connection conn = DBConnection.getConnection();

    /**
     * Lấy danh sách các máy cụ thể (IMEI) đang còn trong kho của một sản phẩm
     * Phục vụ cho giao diện bán hàng khi thu ngân chọn 1 sản phẩm
     */
    public ArrayList<ChitietSPDTO> getAvailableByMaSP(String maSP) {
        ArrayList<ChitietSPDTO> list = new ArrayList<>();
        // Theo Database của bạn, máy chưa bán có tình trạng là 'Sẵn có'
        String sql = "SELECT * FROM ctsp WHERE MASP = ? AND tinhtrang = 'Sẵn có'";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSP);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                // Khởi tạo đối tượng khớp 100% với DTO của bạn
                // public ChitietSPDTO(String maCTSP, String maSP, String maNCC, String Tinhtrang, String maCTPN)
                list.add(new ChitietSPDTO(
                    rs.getString("MACTSP"), 
                    rs.getString("MASP"),
                    rs.getString("MANCC"), 
                    rs.getString("tinhtrang"),
                    rs.getString("MACTPN")
                ));
            }
        } catch (SQLException e) { 
            System.err.println("Lỗi tại ChitietSPDAO.getAvailableByMaSP: " + e.getMessage());
            e.printStackTrace(); 
        }
        return list;
    }

    /**
     * Cập nhật tình trạng của máy cụ thể (Ví dụ: từ 'Sẵn có' -> 'Đã bán')
     * Phục vụ cho chức năng thanh toán (xuLyThanhToan trong HoaDonBUS)
     */
    public boolean updateTinhTrang(String maCTSP, String tinhTrangMoi) {
        String sql = "UPDATE ctsp SET tinhtrang = ? WHERE MACTSP = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tinhTrangMoi);
            ps.setString(2, maCTSP);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { 
            System.err.println("Lỗi tại ChitietSPDAO.updateTinhTrang: " + e.getMessage());
            e.printStackTrace(); 
            return false;
        }
    }
}