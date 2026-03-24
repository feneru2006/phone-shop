package BUS;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import DAL.DAO.SanPhamDAO;
import DTO.SanPhamDTO;

public class SanPhamBUS {
    private SanPhamDAO spDAO = new SanPhamDAO();
    private List<SanPhamDTO> dsSanPham = new ArrayList<>();
    
    // Thêm đối tượng LogBUS
    private LogBUS logBUS = new LogBUS();

    public SanPhamBUS() {
        dsSanPham = spDAO.getAll();
    }

    public List<SanPhamDTO> getAll() {
        return dsSanPham.stream()
                .filter(sp -> !sp.isDeleted())
                .collect(Collectors.toList());
    }

    public List<SanPhamDTO> timKiem(String tieuChi, String keyword) {
        List<SanPhamDTO> result = new ArrayList<>();
        String key = keyword.toLowerCase().trim();

        for (SanPhamDTO sp : getAll()) {
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
        if (sp.getSlTon() < 0 || sp.getGia() < 0 || sp.getPhanTramLoiNhuan() < 0) return false;
        for (SanPhamDTO item : dsSanPham) {
            if (item.getMaSP().equalsIgnoreCase(sp.getMaSP())) {
                return false;
            }
        }
        dsSanPham.add(sp);
        // Ghi log khi thêm thành công
        logBUS.ghiNhatKy("Thêm", "Sản Phẩm", "Thêm sản phẩm mã: " + sp.getMaSP());
        return true;
    }

    public boolean suaSanPham(SanPhamDTO sp) {
        if (sp.getSlTon() < 0 || sp.getGia() < 0 || sp.getPhanTramLoiNhuan() < 0) return false;
        for (int i = 0; i < dsSanPham.size(); i++) {
            if (dsSanPham.get(i).getMaSP().equalsIgnoreCase(sp.getMaSP())) {
                dsSanPham.set(i, sp);
                // Ghi log khi sửa thành công
                logBUS.ghiNhatKy("Sửa", "Sản Phẩm", "Sửa sản phẩm mã: " + sp.getMaSP());
                return true;
            }
        }
        return false;
    }

    public boolean xoaSanPham(String maSP) {
        for (SanPhamDTO sp : dsSanPham) {
            if (sp.getMaSP().equalsIgnoreCase(maSP)) {
                sp.setDeleted(true); 
                // Ghi log khi xóa thành công
                logBUS.ghiNhatKy("Xóa", "Sản Phẩm", "Đánh dấu xóa sản phẩm mã: " + maSP);
                return true;
            }
        }
        return false;
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
            if (!exists && !sp.isDeleted()) {
                spDAO.insert(sp);
            }
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