package BUS;

import java.util.List;

import DAO.SanPhamDAO;
import DTO.SanPhamDTO;

public class SanPhamBUS {

    private SanPhamDAO spDAO = new SanPhamDAO();

    public List<SanPhamDTO> getAll() {
        return spDAO.getAll();
    }

    public boolean themSanPham(SanPhamDTO sp) {

        if (sp.getMaSP().trim().isEmpty()) {
            System.out.println("Mã sản phẩm không được rỗng!");
            return false;
        }

        if (sp.getTenSP().trim().isEmpty()) {
            System.out.println("Tên sản phẩm không được rỗng!");
            return false;
        }

        if (sp.getGia() <= 0) {
            System.out.println("Giá phải lớn hơn 0!");
            return false;
        }

        if (sp.getSlTon() < 0) {
            System.out.println("Số lượng tồn không hợp lệ!");
            return false;
        }

        if (sp.getCauHinh().trim().isEmpty()) {
            System.out.println("Cấu hình không được rỗng!");
            return false;
        }

        return spDAO.insert(sp);
    }

    public boolean suaSanPham(SanPhamDTO sp) {

        if (sp.getGia() <= 0) return false;
        if (sp.getSlTon() < 0) return false;

        return spDAO.update(sp);
    }

    public boolean xoaSanPham(String maSP) {
        return spDAO.delete(maSP);
    }

    public List<SanPhamDTO> timKiem(String keyword) {
        return spDAO.search(keyword);
    }

    public List<SanPhamDTO> getSanPhamSapHetHang(int minStock) {
        return spDAO.getSanPhamSapHetHang(minStock);
    }

    public boolean isSapHetHang(SanPhamDTO sp, int minStock) {
        return sp.getSlTon() <= minStock;
    }
}
