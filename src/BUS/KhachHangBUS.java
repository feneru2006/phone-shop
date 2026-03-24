
package BUS;

import DAL.DAO.KhachHangDAO;
import DTO.khachhangDTO;
import java.util.ArrayList;

public class KhachHangBUS {
    private KhachHangDAO dao = new KhachHangDAO();

    public ArrayList<khachhangDTO> getList() {
        return dao.getAll();
    }

    // Phục vụ "Chăm sóc khách hàng": Tìm khách theo SĐT khi họ đến mua hàng
    public khachhangDTO timKhachHangTheoSDT(String sdt) {
        for (khachhangDTO kh : getList()) {
            if (kh.getSdt().equals(sdt)) return kh;
        }
        return null;
    }

    public boolean themKhachHangMoi(khachhangDTO kh) {
        return dao.insert(kh);
    }
}
