package DAL.DAO;

import DTO.nhanvienDTO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class NhanVienDAO {

    // 1. Lấy danh sách (Nếu keyword rỗng "" thì nó lấy tất cả)
    public ArrayList<nhanvienDTO> timKiem(String keyword) {
        ArrayList<nhanvienDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM nhanvien WHERE hoten LIKE ? OR SDT LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                nhanvienDTO nv = new nhanvienDTO();
                // Lấy từ DB nhét vào hộp DTO
                nv.setMaNV(rs.getString("MANV"));
                nv.setHoTen(rs.getString("hoten"));
                
                // Xử lý Enum Giới tính: Lấy String từ DB ép qua Enum
                String gioiTinhStr = rs.getString("gioitinh");
                if (gioiTinhStr != null) {
                    if (gioiTinhStr.equalsIgnoreCase("Nam")) nv.setGioiTinh(nhanvienDTO.GioiTinh.NAM);
                    else if (gioiTinhStr.equalsIgnoreCase("Nữ") || gioiTinhStr.equalsIgnoreCase("Nu")) nv.setGioiTinh(nhanvienDTO.GioiTinh.NU);
                    else nv.setGioiTinh(nhanvienDTO.GioiTinh.KHAC);
                }
                
                nv.setSdt(rs.getString("SDT"));
                nv.setDiaChi(rs.getString("diachi"));
                nv.setThamNien(rs.getDouble("thamnien")); // Tham niên trong DTO của ông là double
                nv.setLuong(rs.getDouble("luong"));
                
                // Trạng thái trong DB là int (1/0), trong DTO là boolean
                int tt = rs.getInt("trangthai");
                nv.settrangthai(tt == 1); 
                
                list.add(nv); // Gom hàng lên xe tải
            }
        } catch (Exception ex) {
            System.out.println("Lỗi tải danh sách Nhân Viên: " + ex.getMessage());
        }
        return list;
    }

    // 2. Thêm Nhân Viên Mới
    public boolean insert(nhanvienDTO nv) {
        String sql = "INSERT INTO nhanvien (MANV, hoten, gioitinh, SDT, diachi, thamnien, luong, trangthai) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // Lấy từ hộp DTO móc ra đưa cho SQL
            ps.setString(1, nv.getMaNV());
            ps.setString(2, nv.getHoTen());
            
            // Chuyển Enum thành String để lưu vô DB
            String gt = "Khác";
            if (nv.getGioiTinh() == nhanvienDTO.GioiTinh.NAM) gt = "Nam";
            else if (nv.getGioiTinh() == nhanvienDTO.GioiTinh.NU) gt = "Nữ";
            ps.setString(3, gt);
            
            ps.setString(4, nv.getSdt());
            ps.setString(5, nv.getDiaChi());
            ps.setDouble(6, nv.getThamNien()); // SetDouble vì DTO là double
            ps.setDouble(7, nv.getLuong());
            
            // Chuyển boolean thành int (1/0)
            ps.setInt(8, nv.istrangthai() ? 1 : 0);
            
            return ps.executeUpdate() > 0;
        } catch (Exception ex) {
            System.out.println("Lỗi thêm Nhân Viên: " + ex.getMessage());
            return false;
        }
    }

    // 3. Sửa Nhân Viên
    public boolean update(nhanvienDTO nv) {
        String sql = "UPDATE nhanvien SET hoten=?, gioitinh=?, SDT=?, diachi=?, thamnien=?, luong=?, trangthai=? WHERE MANV=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, nv.getHoTen());
            
            String gt = "Khác";
            if (nv.getGioiTinh() == nhanvienDTO.GioiTinh.NAM) gt = "Nam";
            else if (nv.getGioiTinh() == nhanvienDTO.GioiTinh.NU) gt = "Nữ";
            ps.setString(2, gt);
            
            ps.setString(3, nv.getSdt());
            ps.setString(4, nv.getDiaChi());
            ps.setDouble(5, nv.getThamNien()); // Sửa lại chỗ bị lỗi đánh máy của ông
            ps.setDouble(6, nv.getLuong());
            ps.setInt(7, nv.istrangthai() ? 1 : 0);
            ps.setString(8, nv.getMaNV()); 
            
            return ps.executeUpdate() > 0;
        } catch (Exception ex) {
            System.out.println("Lỗi sửa Nhân Viên: " + ex.getMessage());
            return false;
        }
    }

    // 4. Xóa Nhân Viên
    public boolean delete(String maNV) {
        String sql = "DELETE FROM nhanvien WHERE MANV = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maNV);
            return ps.executeUpdate() > 0;
        } catch (Exception ex) {
            System.out.println("Lỗi xóa Nhân Viên (Có thể do khóa ngoại): " + ex.getMessage());
            return false;
        }
    }
}