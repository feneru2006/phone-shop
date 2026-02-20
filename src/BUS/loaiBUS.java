package BUS;

import DAO.loaiDAO;
import DTO.loaiDTO;
import java.util.List;

public class loaiBUS {

    private loaiDAO dao = new loaiDAO();

    public List<loaiDTO> getAll() {
        return dao.getAll();
    }

    public boolean them(loaiDTO loai) {
        if (loai.getMaLoai().trim().isEmpty() ||
            loai.getDanhMuc().trim().isEmpty()) {
            System.out.println("Không được để trống!");
            return false;
        }
        return dao.insert(loai);
    }

    public boolean sua(loaiDTO loai) {
        return dao.update(loai);
    }

    public boolean xoa(String maLoai) {
        return dao.delete(maLoai);
    }

    public List<loaiDTO> timKiem(String key) {
        return dao.search(key);
    }
}
