package BUS;

import DAL.DAO.ReportDAO;
import DTO.ReportProduct;
import DTO.ReportRevenueRow;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ReportService {
    private final ReportDAO reportDAO = new ReportDAO();

    public List<ReportRevenueRow> getRevenueList(LocalDate from, LocalDate to) {
        try {
            return reportDAO.getRevenueReport(from, to);
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải báo cáo doanh thu", e);
        }
    }

    public List<ReportProduct> getTopProducts(LocalDate from, LocalDate to, int limit) {
        try {
            return reportDAO.getTopProducts(from, to, limit);
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải top sản phẩm", e);
        }
    }
    public List<Object[]> getMonthlyRevenueList(LocalDate from, LocalDate to) {
        try {
            return reportDAO.getMonthlyRevenueReport(from, to);
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải báo cáo doanh thu tháng", e);
        }
    }
}

    public void xuatHoaDonPDF(DTO.hoadonDTO hd, java.util.ArrayList<DTO.CTHDDTO> dsCTHD, String tenKH, String tenNV) {
        String path = "HoaDon_" + hd.getMaHD() + ".pdf"; 
        
        
        com.itextpdf.text.Document document = new com.itextpdf.text.Document(com.itextpdf.text.PageSize.A5); 

        try {
            com.itextpdf.text.pdf.PdfWriter.getInstance(document, new java.io.FileOutputStream(path));
            document.open();

            // 1. In Tiêu đề
            com.itextpdf.text.Font fontTitle = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 18, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph("PHONE SHOP", fontTitle);
            title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(title);
            document.add(new com.itextpdf.text.Paragraph("------------------------------------------------------"));

            // 2. In Thông tin chung (Mã HD, Nhân viên, Khách hàng)
            com.itextpdf.text.Font fontNormal = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 12, com.itextpdf.text.Font.NORMAL);
            document.add(new com.itextpdf.text.Paragraph("Ma Hoa Don: " + hd.getMaHD(), fontNormal));
            document.add(new com.itextpdf.text.Paragraph("Nhan vien: " + tenNV, fontNormal));
            document.add(new com.itextpdf.text.Paragraph("Khach hang: " + tenKH, fontNormal));
            document.add(new com.itextpdf.text.Paragraph(" ")); // Dòng trống

            // 3. Kẻ Bảng Chi tiết sản phẩm (4 cột)
            com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(4); 
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1f, 3f, 3f, 3f}); // Tỷ lệ độ rộng các cột

            table.addCell(new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase("STT", fontNormal)));
            table.addCell(new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase("Ma IMEI", fontNormal)));
            table.addCell(new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase("Don Gia", fontNormal)));
            table.addCell(new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase("Thanh Tien", fontNormal)));

            // Đổ dữ liệu các máy đã mua vào bảng
            int stt = 1;
            for (DTO.CTHDDTO ct : dsCTHD) {
                table.addCell(String.valueOf(stt++));
                table.addCell(ct.getMaCTSP()); 
                table.addCell(String.format("%,.0f", ct.getDonGia()));
                table.addCell(String.format("%,.0f", ct.getThanhTien()));
            }
            document.add(table);
            document.add(new com.itextpdf.text.Paragraph("------------------------------------------------------"));

            // 4. In Tổng tiền
            com.itextpdf.text.Font fontTotal = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 14, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Paragraph total = new com.itextpdf.text.Paragraph("TONG CONG: " + String.format("%,.0f VND", hd.getTongTien()), fontTotal);
            total.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
            document.add(total);

            document.close();

            // 5. Tự động bật file PDF lên màn hình
            java.io.File file = new java.io.File(path);
            if (file.exists()) {
                java.awt.Desktop.getDesktop().open(file);
            }

        } catch (Exception e) {
            System.err.println("Lỗi xuất PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
