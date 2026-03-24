package DAL.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BanHangDAO {

    // 1. Lấy mã lớn nhất để tăng tự động
    private int getMaxID(Connection conn, String tableName, String columnName, String prefix) {
        int max = 0;
        String sql = "SELECT " + columnName + " FROM " + tableName;
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String val = rs.getString(columnName);
                if (val != null && val.toUpperCase().startsWith(prefix.toUpperCase())) {
                    try {
                        int num = Integer.parseInt(val.substring(prefix.length()));
                        if (num > max) max = num;
                    } catch (Exception e) {}
                }
            }
        } catch (Exception e) {}
        return max;
    }

    // 2. Tính giá khuyến mãi
    public Object[] getGiaKhuyenMai(String maSP, double giaGoc) {
        double giaCuoiCung = giaGoc;
        String chuoiKhuyenMai = ""; 
        String sql = "SELECT ct.phantramgg, ct.giasaugiam, gg.MAGG FROM ctgg ct JOIN giamgia gg ON ct.MAGG = gg.MAGG WHERE ct.MASP = ? AND gg.batdau <= CURRENT_DATE() AND gg.ketthuc >= CURRENT_DATE() ORDER BY ct.phantramgg DESC LIMIT 1"; 
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSP);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double giaSauGiam = rs.getDouble("giasaugiam");
                int phanTram = rs.getInt("phantramgg");
                String maGG = rs.getString("MAGG"); 
                if (giaSauGiam > 0 && giaSauGiam < giaGoc) {
                    giaCuoiCung = giaSauGiam;
                    chuoiKhuyenMai = " (KM " + maGG + ": -" + (giaGoc - giaSauGiam) + "đ)";
                } else if (phanTram > 0) {
                    giaCuoiCung = giaGoc - (giaGoc * phanTram / 100.0);
                    chuoiKhuyenMai = " (KM " + maGG + ": -" + phanTram + "%)";
                }
            }
        } catch (Exception e) {}
        return new Object[]{giaCuoiCung, chuoiKhuyenMai};
    }

    // 3. Đếm số máy rảnh
    public int getSoLuongMayRanh(String maSP) {
        String sql = "SELECT COUNT(MACTSP) AS SoMayRanh FROM ctsp WHERE MASP = ? AND tinhtrang != '0' AND LOWER(tinhtrang) NOT LIKE '%bán%'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSP);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("SoMayRanh");
        } catch (Exception ex) {}
        return 0;
    }

    // 4. Tìm Khách theo SĐT
    public String getTenKhachBySDT(String sdt) {
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement ps = conn.prepareStatement("SELECT HOTEN FROM khachhang WHERE SDT = ?")) {
            ps.setString(1, sdt);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("HOTEN");
        } catch (Exception ex) { ex.printStackTrace(); }
        return null;
    }

    // 5. Thêm Khách Nhanh
    public String themKhachHangNhanh(String ten, String sdt, String gioiTinh, String diaChi) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            String checkSql = "SELECT HOTEN FROM khachhang WHERE SDT = ?";
            try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setString(1, sdt);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) throw new Exception("SĐT này đã tồn tại (Tên: " + rs.getString("HOTEN") + ")");
            }
            int maxKH = getMaxID(conn, "khachhang", "MAKH", "KH");
            String maKH = String.format("KH%02d", maxKH + 1);

            String insertSql = "INSERT INTO khachhang (MAKH, hoten, gioitinh, diachi, SDT) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                ps.setString(1, maKH);
                ps.setString(2, ten);
                ps.setString(3, gioiTinh);
                ps.setString(4, diaChi);
                ps.setString(5, sdt);
                ps.executeUpdate();
            }
            return maKH;
        }
    }

    // 6. Lấy 1 IMEI chưa bán, không trùng trong giỏ
    public String getIMEIKhaDung(String maSP, List<String> imeiTrongGio) {
        String sql = "SELECT MACTSP FROM ctsp WHERE MASP = ? AND tinhtrang != '0' AND LOWER(tinhtrang) NOT LIKE '%bán%'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSP);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String imei = rs.getString("MACTSP").trim();
                if (!imeiTrongGio.contains(imei)) return imei; 
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        return null;
    }

    // 7. Giá gốc của 1 IMEI
    public double getGiaGocByIMEI(String imei) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT sp.GIA FROM sanpham sp JOIN ctsp ct ON sp.MASP = ct.MASP WHERE ct.MACTSP = ? LIMIT 1")) {
            ps.setString(1, imei);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("GIA");
        } catch (Exception ex) {}
        return 0;
    }

    // 8. Lấy Lịch Sử Giao Dịch
    public List<Object[]> getLichSuHoaDon() {
        List<Object[]> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM hoadon ORDER BY MAHD DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                java.sql.Timestamp ts = rs.getTimestamp("NGAYLAP");
                String thoiGian = (ts != null) ? new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ts) : "";
                list.add(new Object[]{
                    rs.getString("MAHD"), rs.getString("MAKH"), rs.getString("MANV"), thoiGian, rs.getDouble("TONGTIEN")
                });
            }
        } catch (Exception ex) { ex.printStackTrace(); }
        return list;
    }

    // 9. TRANSACTION CHỐT ĐƠN SIÊU TO KHỔNG LỒ
    public List<String> chotDonHang(String sdt, String tenNV, double tongTien, List<Object[]> gioHang) throws Exception {
        List<String> listMaCTHD = new ArrayList<>();
        java.sql.Timestamp thoiGianLap = new java.sql.Timestamp(System.currentTimeMillis());
        java.sql.Date ngayLapChiCoNgay = new java.sql.Date(System.currentTimeMillis()); 

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); 
            try {
                String maKH = "";
                try (PreparedStatement ps = conn.prepareStatement("SELECT MAKH FROM khachhang WHERE SDT = ?")) {
                    ps.setString(1, sdt); ResultSet rs = ps.executeQuery();
                    if (rs.next()) maKH = rs.getString("MAKH");
                }
                if (maKH.isEmpty()) throw new Exception("SĐT không tồn tại trong DB!");
                
                String maNV = ""; 
                if (!tenNV.isEmpty()) {
                    try (PreparedStatement ps = conn.prepareStatement("SELECT MANV FROM nhanvien WHERE HOTEN = ?")) {
                        ps.setString(1, tenNV); ResultSet rs = ps.executeQuery();
                        if (rs.next()) maNV = rs.getString("MANV");
                    }
                }
                if (maNV.isEmpty()) {
                    try (PreparedStatement ps = conn.prepareStatement("SELECT MANV FROM nhanvien LIMIT 1")) {
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) maNV = rs.getString("MANV");
                    }
                }

                int maxHD = getMaxID(conn, "hoadon", "MAHD", "HD");
                String maHD = String.format("HD%02d", maxHD + 1);
                
                String sqlHD = "INSERT INTO hoadon (MAHD, MANV, MAKH, NGAYLAP, TONGTIEN) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sqlHD)) {
                    ps.setString(1, maHD); ps.setString(2, maNV); ps.setString(3, maKH); 
                    ps.setTimestamp(4, thoiGianLap); ps.setInt(5, (int) tongTien); 
                    ps.executeUpdate();
                }

                int currentCTHD = getMaxID(conn, "cthd", "MACTHD", "CTHD");
                int currentBH = getMaxID(conn, "phieubaohanh", "MABH", "BH");

                for (Object[] item : gioHang) {
                    String imei = (String) item[0];
                    double donGia = (double) item[1];
                    int soThangBH = (int) item[2];

                    currentCTHD++;
                    String maCTHD = String.format("CTHD%02d", currentCTHD);
                    listMaCTHD.add(maCTHD); 

                    String sqlCTHD = "INSERT INTO cthd (MACTHD, MAHD, MACTSP, Dongia, Thanhtien) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(sqlCTHD)) {
                        ps.setString(1, maCTHD); ps.setString(2, maHD); ps.setString(3, imei); 
                        ps.setInt(4, (int) donGia); ps.setInt(5, (int) donGia); 
                        ps.executeUpdate(); 
                    }

                    try (PreparedStatement ps = conn.prepareStatement("UPDATE ctsp SET tinhtrang = 'Đã bán' WHERE MACTSP = ? LIMIT 1")) {
                        ps.setString(1, imei); ps.executeUpdate();
                    }
                    try (PreparedStatement ps = conn.prepareStatement("UPDATE sanpham SET SLton = SLton - 1 WHERE MASP = (SELECT MASP FROM ctsp WHERE MACTSP = ? LIMIT 1)")) {
                        ps.setString(1, imei); ps.executeUpdate();
                    }
                    
                    if (soThangBH > 0) { 
                        currentBH++;
                        String maBH = String.format("BH%02d", currentBH);
                        String sqlBH = "INSERT INTO phieubaohanh (MABH, MACTHD, MAKH, ngayBD, thoihan, trangthai) VALUES (?, ?, ?, ?, ?, ?)";
                        try (PreparedStatement psBH = conn.prepareStatement(sqlBH)) {
                            psBH.setString(1, maBH);      psBH.setString(2, maCTHD);    
                            psBH.setString(3, maKH);      psBH.setDate(4, ngayLapChiCoNgay);    
                            psBH.setInt(5, soThangBH);    psBH.setString(6, "Đang bảo hành"); 
                            psBH.executeUpdate();
                        }
                    }
                }
                conn.commit();// LƯU TẤT CẢ XUỐNG DB THÀNH CÔNG
                listMaCTHD.add(0, maHD); 
            } catch (Exception ex) {
                conn.rollback(); // LỖI LÀ QUAY XE HỦY HẾT
                throw ex; 
            } finally {
                conn.setAutoCommit(true); 
            }
        }
        return listMaCTHD;
    }
}