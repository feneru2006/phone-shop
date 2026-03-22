package BUS;

import java.util.ArrayList;
import java.util.List;

import DAL.DAO.HangSXDAO;
import DTO.hangsxDTO;

public class HangSXBUS {

    private HangSXDAO dao = new HangSXDAO();
    private List<hangsxDTO> dsHang = new ArrayList<>();
    
    // Thêm đối tượng LogBUS
    private LogBUS logBUS = new LogBUS();

    public HangSXBUS() {
        dsHang = dao.getAll(); 
    }

    public List<hangsxDTO> getAll() {
        return dsHang;
    }
    
    public List<hangsxDTO> timKiem(String tieuChi, String keyword) {
        List<hangsxDTO> result = new ArrayList<>();
        String key = keyword.toLowerCase().trim();

        for (hangsxDTO h : dsHang) {
            boolean match = false;
            switch (tieuChi) {
                case "Mã NSX":
                    if (h.getMaNSX().toLowerCase().contains(key)) match = true;
                    break;
                case "Tên Thương Hiệu":
                    if (h.getTenTH().toLowerCase().contains(key)) match = true;
                    break;
                default: 
                    if (h.getMaNSX().toLowerCase().contains(key) ||
                        h.getTenTH().toLowerCase().contains(key)) {
                        match = true;
                    }
                    break;
            }
            if (match) result.add(h);
        }
        return result;
    }

    public boolean them(hangsxDTO hang) {
        if (hang.getMaNSX().trim().isEmpty() || hang.getTenTH().trim().isEmpty()) {
            return false;
        }
        for (hangsxDTO h : dsHang) {
            if (h.getMaNSX().equalsIgnoreCase(hang.getMaNSX())) {
                return false;
            }
        }
        dsHang.add(hang);
        // Ghi log khi thêm thành công
        logBUS.ghiNhatKy("Thêm", "Hãng Sản Xuất", "Thêm hãng sản xuất mã: " + hang.getMaNSX());
        return true;
    }

    public boolean sua(hangsxDTO hang) {
        for (int i = 0; i < dsHang.size(); i++) {
            if (dsHang.get(i).getMaNSX().equals(hang.getMaNSX())) {
                dsHang.set(i, hang);
                // Ghi log khi sửa thành công
                logBUS.ghiNhatKy("Sửa", "Hãng Sản Xuất", "Sửa hãng sản xuất mã: " + hang.getMaNSX());
                return true;
            }
        }
        return false;
    }

    public boolean xoa(String maNSX) {
        // Tách ra để kiểm tra kết quả xóa thành công hay không trước khi return
        boolean ketQua = dsHang.removeIf(h -> h.getMaNSX().equals(maNSX));
        if (ketQua) {
            // Ghi log khi xóa thành công
            logBUS.ghiNhatKy("Xóa", "Hãng Sản Xuất", "Xóa hãng sản xuất mã: " + maNSX);
        }
        return ketQua;
    }

    public boolean saveToDatabase() {
        List<hangsxDTO> dbList = dao.getAll();

        for (hangsxDTO ram : dsHang) {
            boolean exists = false;
            for (hangsxDTO db : dbList) {
                if (db.getMaNSX().equals(ram.getMaNSX())) {
                    dao.update(ram);
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                dao.insert(ram);
            }
        }

        for (hangsxDTO db : dbList) {
            boolean stillExists = false;
            for (hangsxDTO ram : dsHang) {
                if (ram.getMaNSX().equals(db.getMaNSX())) {
                    stillExists = true;
                    break;
                }
            }
            if (!stillExists) {
                dao.delete(db.getMaNSX());
            }
        }
        return true;
    }

    public void reload() {
        dsHang = dao.getAll();
    }
}