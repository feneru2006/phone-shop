package BUS;

import DAL.DAO.*;
import DTO.*;
import java.sql.*;
import java.util.ArrayList;

public class HoaDonBUS {
    private HoaDonDAO hdDAO = new HoaDonDAO();
    private CTHDDAO cthdDAO = new CTHDDAO();
    private ChitietSPDAO ctspDAO = new ChitietSPDAO();
    private ReportService reportService = new ReportService(); // Kéo từ cấu trúc của bạn

    /**
     * Hàm Xử lý Giao dịch (Lập hóa đơn & Cập nhật kho)
     */
    public String xuLyThanhToan(hoadonDTO hd, ArrayList<CTHDDTO> dsCTHD, ArrayList<String> dsMaSP) {
        if (dsCTHD == null || dsCTHD.isEmpty()) return "Giỏ hàng trống!";

        // 1. Lập hóa đơn bán hàng
        if (!hdDAO.insert(hd)) {
            return "Lỗi: Không thể tạo hóa đơn!";
        }

        // 2. Xử lý chi tiết giao dịch
        for (int i = 0; i < dsCTHD.size(); i++) {
            CTHDDTO ct = dsCTHD.get(i);
            String maSP = dsMaSP.get(i); // Mã sản phẩm chung (VD: SP01)
            
            // Lấy MACTSP (IMEI) từ DTO đưa vào CTHD
            cthdDAO.insert(ct);
            
            // Cập nhật tình trạng của máy cụ thể đó thành 'Đã bán'
            ctspDAO.updateTinhTrang(ct.getMaCTSP(), "Đã bán");

            // Đồng thời cập nhật lại số lượng tồn trong bảng sanpham
            giamSoLuongTonKho(maSP); 
        }

        // 3. In hóa đơn cho khách
        try {
            // Giả sử ReportService của bạn có hàm xuất hóa đơn
            // reportService.exportInvoice(hd.getMaHD()); 
        } catch (Exception e) {
            System.out.println("Lỗi in PDF: " + e.getMessage());
        }

        return "Thanh toán thành công!";
    }
    // Tự động sinh mã hóa đơn mới (Ví dụ: HD001, HD002)
    public String tuDongTaoMaHD() {
        int soLuong = hdDAO.getRowCount();
        return "HD" + String.format("%03d", soLuong + 1);
    }
//Cập nhật sô lượng tồn kho
    private void giamSoLuongTonKho(String maSP) {
        String sql = "UPDATE sanpham SET SLton = SLton - 1 WHERE MASP = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maSP);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}