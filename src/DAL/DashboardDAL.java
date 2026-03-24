package DAL;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import DTO.DashboardDTO;
import DTO.logDTO;
import Utility.DateUtils;
import Utility.Validator;

public class DashboardDAL {
    
    private Connection conn = DAL.DAO.DBConnection.getConnection();

    public DashboardDTO getDashboardData() {
        DashboardDTO dto = new DashboardDTO();
        
        if (conn == null) {
            System.err.println("Kết nối Database thất bại!");
            return dto;
        }

        try {
            // 1. Tổng sản phẩm
            String sqlSP = "SELECT COUNT(*) FROM sanpham WHERE isDeleted = 0";
            try (PreparedStatement psSP = conn.prepareStatement(sqlSP);
                 ResultSet rsSP = psSP.executeQuery()) {
                if (rsSP.next()) dto.setTongSanPham(rsSP.getInt(1));
            }

            // 2. Tổng doanh thu
            String sqlDT = "SELECT SUM(tongtien) FROM hoadon";
            try (PreparedStatement psDT = conn.prepareStatement(sqlDT);
                 ResultSet rsDT = psDT.executeQuery()) {
                if (rsDT.next()) {
                    double doanhThu = rsDT.getDouble(1);
                    dto.setTongDoanhThu(Validator.isNonNegative(doanhThu) ? doanhThu : 0.0);
                }
            }

            // 3. Tổng nhân viên
            String sqlNV = "SELECT COUNT(*) FROM nhanvien"; 
            try (PreparedStatement psNV = conn.prepareStatement(sqlNV);
                 ResultSet rsNV = psNV.executeQuery()) {
                if (rsNV.next()) dto.setTongNhanVien(rsNV.getInt(1));
            }
            
            // 4. Tổng khách hàng
            String sqlKH = "SELECT COUNT(*) FROM khachhang"; 
            try (PreparedStatement psKH = conn.prepareStatement(sqlKH);
                 ResultSet rsKH = psKH.executeQuery()) {
                if (rsKH.next()) dto.setTongKhachHang(rsKH.getInt(1));
            }

            // 5. Nhật ký mới nhất
            dto.setDanhSachLog(getRecentLogs(4));

        } catch (SQLException e) {
            System.err.println("Lỗi DashboardDAL: " + e.getMessage());
        }
        return dto;
    }

    private ArrayList<logDTO> getRecentLogs(int limit) {
        ArrayList<logDTO> logs = new ArrayList<>();
        String sqlLog = "SELECT hanhvi, thucthe, chitiethv, thoidiem FROM log ORDER BY thoidiem DESC LIMIT ?";
        
        try (PreparedStatement psLog = conn.prepareStatement(sqlLog)) {
            psLog.setInt(1, limit);
            try (ResultSet rsLog = psLog.executeQuery()) {
                while (rsLog.next()) {
                    logDTO log = new logDTO();
                    log.setHanhvi(rsLog.getString("hanhvi"));
                    log.setThucthe(rsLog.getString("thucthe"));
                    log.setChitiethv(rsLog.getString("chitiethv"));
                    
                    java.sql.Timestamp ts = rsLog.getTimestamp("thoidiem");
                    if (ts != null) {
                        log.setThoidiem(ts.toLocalDateTime());
                    }
                    logs.add(log);
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi lấy Log: " + e.getMessage());
        }
        return logs;
    }
    
    public ArrayList<Object[]> getLowStockProducts(int threshold) {
        ArrayList<Object[]> list = new ArrayList<>();
        // Đã sửa soluongton thành SLton
        String sql = "SELECT tenSP, SLton FROM sanpham WHERE SLton <= ? AND isDeleted = 0 ORDER BY SLton ASC";
        
        // Sử dụng biến conn của class thay vì tạo mới để tránh lỗi rò rỉ kết nối
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, threshold);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Đọc đúng tên cột SLton
                    list.add(new Object[]{ rs.getString("tenSP"), rs.getInt("SLton") });
                }
            }
        } catch (SQLException e) {
            System.err.println("Lỗi tải sản phẩm sắp hết hàng: " + e.getMessage());
        }
        return list;
    }
}