package BUS;


import DTO.SanPhamDTO;
import Utility.ExcelHelper;
import java.util.List;
import java.io.File;

public class ExcelBUS {
    public boolean kiemTraVaXuatExcel(List<SanPhamDTO> dsSanPham, String duongDanFile) {
        if (dsSanPham == null || dsSanPham.isEmpty()) return false;
        
        File file = new File(duongDanFile);
        // Kiểm tra nếu file đang mở (không thể ghi)
        if (file.exists() && !file.canWrite()) {
            return false;
        }

        return ExcelHelper.ghiFileExcelTonKho(dsSanPham, duongDanFile);
    }
}