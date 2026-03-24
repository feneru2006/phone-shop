
package BUS;

import DAL.DAO.ChitietSPDAO;
import DTO.ChitietSPDTO;
import java.util.ArrayList;

public class ChitietSPBUS {
    private ChitietSPDAO dao = new ChitietSPDAO();

    // Hàm lấy danh sách các máy (IMEI) cụ thể đang rảnh trong kho
    public ArrayList<ChitietSPDTO> layMayConTrongKho(String maSP) {
        return dao.getAvailableByMaSP(maSP);
    }

    // Hàm đổi tình trạng máy (từ 'Sẵn có' sang 'Đã bán')
    public boolean doiTinhTrangMay(String maCTSP, String tinhTrangMoi) {
        return dao.updateTinhTrang(maCTSP, tinhTrangMoi);
    }
}
