package BUS;

import DAL.DAO.HoaDonDAO;
import DAL.DAO.CTHDDAO; 
import DAL.DAO.CtspDAO;
import DTO.hoadonDTO;
import DTO.CTHDDTO;
import java.util.ArrayList;

public class HoaDonBUS {

    // Gọi các DAO lên để làm việc
    private HoaDonDAO hdDAO = new HoaDonDAO();
    private CTHDDAO cthdDAO = new CTHDDAO();
    private CtspDAO ctspDAO = new CtspDAO();

    // Lấy danh sách hóa đơn (Dùng cho nút Xem Lịch Sử sau này)
    public ArrayList<hoadonDTO> getAll() {
        return hdDAO.getAll();
    }

    public boolean xuLyThanhToan(hoadonDTO hd, ArrayList<CTHDDTO> dsCTHD) {
        
        // BƯỚC 1: Lưu Hóa Đơn vào Database trước (Để lấy cái Mã Hóa Đơn)
        if (!hdDAO.insert(hd)) {
            return false; // Rớt ngay từ bước 1 thì báo lỗi luôn
        }

        // BƯỚC 2 & 3: Duyệt qua từng cái điện thoại trong Giỏ Hàng
        for (CTHDDTO cthd : dsCTHD) {
            
            // Bước 2: Lưu vào bảng Chi Tiết Hóa Đơn (Mã HD, Mã IMEI, Giá...)
            cthdDAO.insert(cthd);

            // Bước 3: Cập nhật tình trạng của cái máy IMEI đó thành "Đã bán"
            ctspDAO.updateTinhTrang(cthd.getMaCTSP(), "Đã bán");
        }

        // Nếu chạy mượt mà qua hết vòng lặp thì trả về Thành công!
        return true; 
    }
}