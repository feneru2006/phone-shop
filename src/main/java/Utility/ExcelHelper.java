package Utility;


import DTO.SanPhamDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.util.List;

public class ExcelHelper {
    
    // Tên hàm mô tả đúng hành động vật lý: ghi dữ liệu ra file Excel
    public static boolean ghiFileExcelTonKho(List<SanPhamDTO> dsSanPham, String duongDanLuuFile) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("TonKho");

            // Style cho dòng Header
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            // Tạo header
            Row headerRow = sheet.createRow(0);
            String[] columns = {"STT", "Mã SP", "Tên Sản Phẩm", "Số Lượng Tồn", "Đơn Giá"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Đổ dữ liệu
            int rowNum = 1;
            for (int i = 0; i < dsSanPham.size(); i++) {
                SanPhamDTO sp = dsSanPham.get(i);
                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(sp.getMaSP());
                row.createCell(2).setCellValue(sp.getTenSP());
                row.createCell(3).setCellValue(sp.getSlTon());
                row.createCell(4).setCellValue(String.format("%,.0f VNĐ", sp.getGia()));
            }

            // Căn chỉnh độ rộng cột tự động
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Ghi file ra ổ cứng
            try (FileOutputStream fileOut = new FileOutputStream(duongDanLuuFile)) {
                workbook.write(fileOut);
            }
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi khi ghi file Excel: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}