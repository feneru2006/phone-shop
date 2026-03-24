package BUS;

import DAL.DAO.CTHDDAO;
import DTO.CTHDDTO;
import java.util.ArrayList;

public class CTHDBUS {
    private CTHDDAO dao = new CTHDDAO();

    // Lấy toàn bộ chi tiết của 1 hóa đơn cụ thể (Dùng khi in lại hóa đơn hoặc xem lịch sử)
    public ArrayList<CTHDDTO> layChiTietCuaHoaDon(String maHD) {
        // Hàm này gọi xuống CTHDDAO để query: SELECT * FROM CTHD WHERE MAHD = ?
        return dao.getListByMaHD(maHD);
    }
    
    // Tính tổng tiền của các chi tiết (Dùng để kiểm tra chéo với tổng tiền của bảng Hóa đơn)
    public double tinhTongTienChiTiet(String maHD) {
        double tong = 0;
        ArrayList<CTHDDTO> list = layChiTietCuaHoaDon(maHD);
        for (CTHDDTO ct : list) {
            tong += ct.getThanhTien();
        }
        return tong;
    }
}