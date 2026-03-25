package BUS;

import DAL.DAO.NhanVienDAO;
import DTO.nhanvienDTO;
import java.util.ArrayList;

public class NhanVienBUS {
    private NhanVienDAO nvDAO = new NhanVienDAO();

    // Dùng chung cho Load Data và Tìm Kiếm
    public ArrayList<nhanvienDTO> timKiem(String keyword) {
        return nvDAO.timKiem(keyword);
    }
    
    // Đảm bảo có hàm getList() để mấy form khác gọi (như cái PNAddDialog, GiaoDienBanHang hồi trưa)
    public ArrayList<nhanvienDTO> getList() {
        return nvDAO.timKiem("");
    }

    public boolean themNV(nhanvienDTO nv) {
        return nvDAO.insert(nv);
    }

    public boolean suaNV(nhanvienDTO nv) {
        return nvDAO.update(nv);
    }

    public boolean xoaNV(String maNV) {
        return nvDAO.delete(maNV);
    }

    public String layTenNhanVien(String maNV) {

    ArrayList<nhanvienDTO> list = getList();

    for (nhanvienDTO nv : list) {
        if (nv.getMaNV().equals(maNV)) {
            return nv.getHoTen();
        }
    }

    return "Không tìm thấy";
}
}
