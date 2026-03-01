package BUS;

import java.util.List;

import DAL.DAO.HangSXDAO;
import DTO.hangsxDTO;

public class HangSXBUS {

    private HangSXDAO dao = new HangSXDAO();

    public List<hangsxDTO> getAll() {
        return dao.getAll();
    }

    public boolean them(hangsxDTO hang) {
        if (hang.getMaNSX().trim().isEmpty() ||
            hang.getTenTH().trim().isEmpty()) {
            System.out.println("Không được để trống!");
            return false;
        }
        return dao.insert(hang);
    }

    public boolean sua(hangsxDTO hang) {
        return dao.update(hang);
    }

    public boolean xoa(String maNSX) {
        return dao.delete(maNSX);
    }

    public List<hangsxDTO> timKiem(String key) {
        return dao.search(key);
    }
}
