
package BUS;

import java.util.ArrayList;
import java.util.List;

import DAL.DAO.SanPhamDAO;
import DTO.SanPhamDTO;

public class SanPhamBUS {

    private SanPhamDAO spDAO = new SanPhamDAO();
    private List<SanPhamDTO> dsSanPham = new ArrayList<>();

    public SanPhamBUS() {
        dsSanPham = spDAO.getAll();
    }

    public List<SanPhamDTO> getAll() {
        return dsSanPham;
    }

    public List<SanPhamDTO> timKiem(String tieuChi, String keyword) {
        List<SanPhamDTO> result = new ArrayList<>();
        String key = keyword.toLowerCase().trim();

        for (SanPhamDTO sp : dsSanPham) {
            boolean match = false;
            switch (tieuChi) {
                case "Mã SP":
                    if (sp.getMaSP().toLowerCase().contains(key)) match = true;
                    break;
                case "Tên SP":
                    if (sp.getTenSP().toLowerCase().contains(key)) match = true;
                    break;
                case "Cấu hình":
                    if (sp.getCauHinh().toLowerCase().contains(key)) match = true;
                    break;
                default: 
                    if (sp.getMaSP().toLowerCase().contains(key) || 
                        sp.getTenSP().toLowerCase().contains(key) ||
                        sp.getCauHinh().toLowerCase().contains(key)) {
                        match = true;
                    }
                    break;
            }
            if (match) result.add(sp);
        }
        return result;
    }

   
    public boolean themSanPham(SanPhamDTO sp) {
        if (sp.getSlTon() < 0 || sp.getGia() < 0) return false;
        for (SanPhamDTO item : dsSanPham) {
            if (item.getMaSP().equalsIgnoreCase(sp.getMaSP())) {
                return false;
            }
        }
        dsSanPham.add(sp);
        return true;
    }

        public boolean suaSanPham(SanPhamDTO sp) {
        if (sp.getSlTon() < 0 || sp.getGia() < 0) return false;
        for (int i = 0; i < dsSanPham.size(); i++) {
            if (dsSanPham.get(i).getMaSP().equalsIgnoreCase(sp.getMaSP())) {
                dsSanPham.set(i, sp);
                return true;
            }
        }
        return false;
    }

    public boolean xoaSanPham(String maSP) {
        return dsSanPham.removeIf(sp -> sp.getMaSP().equalsIgnoreCase(maSP));
    }

        public boolean saveToDatabase() {
        List<SanPhamDTO> dbList = spDAO.getAll();
        for (SanPhamDTO sp : dsSanPham) {
            boolean exists = false;
            for (SanPhamDTO db : dbList) {
                if (db.getMaSP().equalsIgnoreCase(sp.getMaSP())) {
                    spDAO.update(sp);
                    exists = true;
                    break;
                }
            }
            if (!exists) spDAO.insert(sp);
        }
        for (SanPhamDTO db : dbList) {
            boolean stillExists = false;
            for (SanPhamDTO sp : dsSanPham) {
                if (sp.getMaSP().equalsIgnoreCase(db.getMaSP())) {
                    stillExists = true;
                    break;
                }
            }
            if (!stillExists) spDAO.delete(db.getMaSP());
        }
        return true;
    }

    public void reload() {
        dsSanPham = spDAO.getAll();
    }

    public SanPhamDTO getById(String maSP) {

    if (dsSanPham == null || dsSanPham.isEmpty()) {
        reload();
    }

    for (SanPhamDTO sp : dsSanPham) {
        if (sp.getMaSP().equalsIgnoreCase(maSP)) {
            return sp;
        }
    }

    return null;
}
    public double getGiaByMaSP(String maSP){
    SanPhamDTO sp = getById(maSP);

    if(sp == null){
        throw new RuntimeException("Không tìm thấy sản phẩm: " + maSP);
    }

    return sp.getGia();
}
}
