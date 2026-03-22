package DAL.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import DTO.ChitietSPDTO;

public class CtspDAO {

    public List<ChitietSPDTO> getAll() {
        List<ChitietSPDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM ctsp";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new ChitietSPDTO(
                        rs.getString("MACTSP"),
                        rs.getString("MASP"),
                        rs.getString("MANCC"),
                        rs.getString("tinhtrang"),
                        rs.getString("MACTPN")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ChitietSPDTO> getByMaSP(String maSP) {
        List<ChitietSPDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM ctsp WHERE MASP = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maSP);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new ChitietSPDTO(
                        rs.getString("MACTSP"),
                        rs.getString("MASP"),
                        rs.getString("MANCC"),
                        rs.getString("tinhtrang"),
                        rs.getString("MACTPN")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(ChitietSPDTO ctsp) {
        String sql = "INSERT INTO ctsp(MACTSP, MASP, MANCC, tinhtrang, MACTPN) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ctsp.getMaCTSP());
            ps.setString(2, ctsp.getMaSP());
            ps.setString(3, ctsp.getMaNCC());
            ps.setString(4, ctsp.getTinhtrang());
            ps.setString(5, ctsp.getMaCTPN());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(ChitietSPDTO ctsp) {
        String sql = "UPDATE ctsp SET MASP=?, MANCC=?, tinhtrang=?, MACTPN=? WHERE MACTSP=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ctsp.getMaSP());
            ps.setString(2, ctsp.getMaNCC());
            ps.setString(3, ctsp.getTinhtrang()); 
            ps.setString(4, ctsp.getMaCTPN());
            ps.setString(5, ctsp.getMaCTSP());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String maCTSP) {
        String sql = "DELETE FROM ctsp WHERE MACTSP=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maCTSP);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public List<ChitietSPDTO> getByMaCTPN(String maCTPN) {
        List<ChitietSPDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM ctsp WHERE MACTPN = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maCTPN);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new ChitietSPDTO(
                        rs.getString("MACTSP"),
                        rs.getString("MASP"),
                        rs.getString("MANCC"),
                        rs.getString("tinhtrang"),
                        rs.getString("MACTPN")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
     public ArrayList<ChitietSPDTO> getAvailableByMaSP(String maSP) {
        ArrayList<ChitietSPDTO> list = new ArrayList<>();
        // Theo Database của bạn, máy chưa bán có tình trạng là 'Sẵn có'
        String sql = "SELECT * FROM ctsp WHERE MASP = ? AND tinhtrang = 'Sẵn có'";
        
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
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
        
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
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