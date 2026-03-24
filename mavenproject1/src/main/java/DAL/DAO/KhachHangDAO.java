package DAL.DAO;

import DTO.khachhangDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class KhachHangDAO {

    public ArrayList<khachhangDTO> timKiem(String keyword) {
        ArrayList<khachhangDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM khachhang WHERE hoten LIKE ? OR SDT LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                khachhangDTO kh = new khachhangDTO();
                kh.setMaKH(rs.getString("MAKH"));
                kh.setHoTen(rs.getString("hoten"));
                
                // Ép kiểu String từ DB sang Enum để nhét vô DTO
                String gioiTinhStr = rs.getString("gioitinh");
                if (gioiTinhStr != null) {
                    if (gioiTinhStr.equalsIgnoreCase("Nam")) kh.setGioiTinh(khachhangDTO.GioiTinh.NAM);
                    else if (gioiTinhStr.equalsIgnoreCase("Nữ") || gioiTinhStr.equalsIgnoreCase("Nu")) kh.setGioiTinh(khachhangDTO.GioiTinh.NU);
                    else kh.setGioiTinh(khachhangDTO.GioiTinh.KHAC);
                }
                
                kh.setDiaChi(rs.getString("diachi"));
                kh.setSdt(rs.getString("SDT"));
                list.add(kh);
            }
        } catch (Exception ex) {
            System.out.println("Lỗi tải danh sách Khách Hàng: " + ex.getMessage());
        }
        return list;
    }

    public boolean insert(khachhangDTO kh) {
        String sql = "INSERT INTO khachhang (MAKH, hoten, gioitinh, diachi, SDT) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, kh.getMaKH());
            ps.setString(2, kh.getHoTen());
            
            // Ép kiểu Enum ra String để lưu xuống DB
            String gt = "Khác";
            if (kh.getGioiTinh() != null) {
                if (kh.getGioiTinh() == khachhangDTO.GioiTinh.NAM) gt = "Nam";
                else if (kh.getGioiTinh() == khachhangDTO.GioiTinh.NU) gt = "Nữ";
            }
            ps.setString(3, gt);
            
            ps.setString(4, kh.getDiaChi());
            ps.setString(5, kh.getSdt());
            
            return ps.executeUpdate() > 0;
        } catch (Exception ex) {
            System.out.println("Lỗi thêm Khách Hàng: " + ex.getMessage());
            return false;
        }
    }

    public boolean update(khachhangDTO kh) {
        String sql = "UPDATE khachhang SET hoten=?, gioitinh=?, diachi=?, SDT=? WHERE MAKH=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, kh.getHoTen());
            
            // Ép kiểu Enum ra String để lưu xuống DB
            String gt = "Khác";
            if (kh.getGioiTinh() != null) {
                if (kh.getGioiTinh() == khachhangDTO.GioiTinh.NAM) gt = "Nam";
                else if (kh.getGioiTinh() == khachhangDTO.GioiTinh.NU) gt = "Nữ";
            }
            ps.setString(2, gt);
            
            ps.setString(3, kh.getDiaChi());
            ps.setString(4, kh.getSdt());
            ps.setString(5, kh.getMaKH());
            
            return ps.executeUpdate() > 0;
        } catch (Exception ex) {
            System.out.println("Lỗi sửa Khách Hàng: " + ex.getMessage());
            return false;
        }
    }

    public boolean delete(String maKH) {
        String sql = "DELETE FROM khachhang WHERE MAKH = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maKH);
            return ps.executeUpdate() > 0;
        } catch (Exception ex) {
            System.out.println("Lỗi xóa Khách Hàng (Dính khóa ngoại Hóa Đơn): " + ex.getMessage());
            return false;
        }
    }

    // Lấy lịch sử giao dịch của Khách
    public ArrayList<Object[]> getLichSuMuaHang(String maKH) {
        ArrayList<Object[]> list = new ArrayList<>();
        String sql = "SELECT hd.MAHD, nv.HOTEN AS TenNV, hd.NGAYLAP, hd.TONGTIEN " +
                     "FROM hoadon hd " +
                     "LEFT JOIN nhanvien nv ON hd.MANV = nv.MANV " +
                     "WHERE hd.MAKH = ? ORDER BY hd.NGAYLAP DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maKH);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getString("MAHD"), 
                    rs.getString("TenNV") != null ? rs.getString("TenNV") : "Không xác định", 
                    rs.getString("NGAYLAP"), 
                    rs.getDouble("TONGTIEN")
                });
            }
        } catch (Exception ex) { 
            ex.printStackTrace(); 
        }
        return list;
    }
}