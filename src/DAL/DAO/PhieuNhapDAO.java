package DAL.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import DTO.phieunhapDTO;
import DTO.CTphieunhapDTO;
import DTO.ChitietSPDTO;

public class PhieuNhapDAO {

    public List<phieunhapDTO> getAll() {
        List<phieunhapDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM phieunhap ORDER BY Ngaynhap DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                list.add(new phieunhapDTO(
                    rs.getString("MAPNH"),
                    rs.getString("MANV"),
                    rs.getTimestamp("Ngaynhap").toLocalDateTime(),
                    rs.getDouble("tongtien"),
                    rs.getString("MANCC")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();    
        }
        return list;
    }

    public boolean thucHienNhapHang(phieunhapDTO pn, List<CTphieunhapDTO> listCTPN, List<ChitietSPDTO> listIMEI) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); 

            String sqlPN = "INSERT INTO phieunhap(MAPNH, MANV, Ngaynhap, tongtien, MANCC) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement psPN = conn.prepareStatement(sqlPN)) {
                psPN.setString(1, pn.getMaPNH());
                psPN.setString(2, pn.getMaNV());
                psPN.setTimestamp(3, Timestamp.valueOf(pn.getNgayNhap()));
                psPN.setDouble(4, pn.getTongTien());
                psPN.setString(5, pn.getMaNCC());
                psPN.executeUpdate();
            }

            String sqlCTPN = "INSERT INTO CTphieunhap(MACTPN, MASP, MAPNH, SL, dongia) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement psCTPN = conn.prepareStatement(sqlCTPN)) {
                for (CTphieunhapDTO ct : listCTPN) {
                    psCTPN.setString(1, ct.getMaCTPN());
                    psCTPN.setString(2, ct.getMaSP());
                    psCTPN.setString(3, ct.getMaPNH());
                    psCTPN.setInt(4, ct.getSl());
                    psCTPN.setDouble(5, ct.getDonGia());
                    psCTPN.addBatch();
                }
                psCTPN.executeBatch();
            }

            String sqlCTSP = "INSERT INTO ctsp(MACTSP, MASP, MANCC, tinhtrang, MACTPN) VALUES (?, ?, ?, 'Sẵn có', ?)";
            try (PreparedStatement psCTSP = conn.prepareStatement(sqlCTSP)) {
                for (ChitietSPDTO imei : listIMEI) {
                    psCTSP.setString(1, imei.getMaCTSP());
                    psCTSP.setString(2, imei.getMaSP());
                    psCTSP.setString(3, imei.getMaNCC());
                    psCTSP.setString(4, imei.getMaCTPN());
                    psCTSP.addBatch();
                }
                psCTSP.executeBatch();
            }

            conn.commit(); 
            return true;

        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (Exception ex) { ex.printStackTrace(); } 
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (Exception e) { e.printStackTrace(); }
            }
        }
    }
    
    public phieunhapDTO getById(String maPNH) {
        phieunhapDTO result = null;
        String sql = "SELECT * FROM phieunhap WHERE MAPNH = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maPNH);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                result = new phieunhapDTO(
                    rs.getString("MAPNH"),
                    rs.getString("MANV"),
                    rs.getTimestamp("Ngaynhap").toLocalDateTime(),
                    rs.getDouble("tongtien"),
                    rs.getString("MANCC")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean update(phieunhapDTO pn) {
        String sql = "UPDATE phieunhap SET MANV=?, tongtien=?, MANCC=? WHERE MAPNH=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, pn.getMaNV());
            ps.setDouble(2, pn.getTongTien());
            ps.setString(3, pn.getMaNCC());
            ps.setString(4, pn.getMaPNH());
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String maPNH) {
        String sql = "DELETE FROM phieunhap WHERE MAPNH=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maPNH);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Lỗi Xóa Phiếu Nhập: Dữ liệu đang bị ràng buộc khóa ngoại (Foreign Key)!");
            e.printStackTrace();
        }
        return false;
    }

    public List<CTphieunhapDTO> getChiTietPhieuNhap(String maPNH) {
        List<CTphieunhapDTO> listCT = new ArrayList<>();
        String sql = "SELECT * FROM CTphieunhap WHERE MAPNH = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maPNH);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                CTphieunhapDTO ct = new CTphieunhapDTO();
                ct.setMaCTPN(rs.getString("MACTPN"));
                ct.setMaSP(rs.getString("MASP"));
                ct.setMaPNH(rs.getString("MAPNH"));
                ct.setSl(rs.getInt("SL"));
                ct.setDonGia(rs.getDouble("dongia"));
                listCT.add(ct);
            }
        } catch (Exception e) {
            System.out.println("Lỗi lấy chi tiết phiếu nhập: " + e.getMessage());
            e.printStackTrace();
        }
        return listCT;
    }

    public double getTongTienNhapTrongThang(int month, int year) {
        String sql = "SELECT SUM(tongtien) AS TongTien FROM phieunhap WHERE MONTH(Ngaynhap) = ? AND YEAR(Ngaynhap) = ?";
        double tongTien = 0;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, month);
            ps.setInt(2, year);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                tongTien = rs.getDouble("TongTien");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tongTien;
    }

}
