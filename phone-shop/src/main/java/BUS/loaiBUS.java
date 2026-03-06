package BUS;

import java.util.ArrayList;
import java.util.List;

import DAL.DAO.loaiDAO;
import DTO.loaiDTO;

public class loaiBUS {

    private loaiDAO dao = new loaiDAO();
    private List<loaiDTO> dsLoai = new ArrayList<>();

    public loaiBUS() {
        dsLoai = dao.getAll();
    }

    public List<loaiDTO> getAll() {
        return dsLoai;
    }

    public List<loaiDTO> timKiem(String key) {

        List<loaiDTO> result = new ArrayList<>();

        for (loaiDTO loai : dsLoai) {
            if (loai.getMaLoai().toLowerCase().contains(key.toLowerCase()) ||
                loai.getDanhMuc().toLowerCase().contains(key.toLowerCase())) {

                result.add(loai);
            }
        }

        return result;
    }

    public boolean them(loaiDTO loai) {

        if (loai.getMaLoai().trim().isEmpty() ||
            loai.getDanhMuc().trim().isEmpty()) {
            return false;
        }

        for (loaiDTO l : dsLoai) {
            if (l.getMaLoai().equalsIgnoreCase(loai.getMaLoai())) {
                return false;
            }
        }

        dsLoai.add(loai);
        return true;
    }

    public boolean sua(loaiDTO loai) {

        for (int i = 0; i < dsLoai.size(); i++) {
            if (dsLoai.get(i).getMaLoai().equalsIgnoreCase(loai.getMaLoai())) {
                dsLoai.set(i, loai);
                return true;
            }
        }

        return false;
    }

    public boolean xoa(String maLoai) {
        return dsLoai.removeIf(l ->
                l.getMaLoai().equalsIgnoreCase(maLoai));
    }

    public boolean saveToDatabase() {

        List<loaiDTO> dbList = dao.getAll();

        for (loaiDTO ram : dsLoai) {

            boolean exists = false;

            for (loaiDTO db : dbList) {
                if (db.getMaLoai().equalsIgnoreCase(ram.getMaLoai())) {
                    dao.update(ram);
                    exists = true;
                    break;
                }
            }

            if (!exists) {
                dao.insert(ram);
            }
        }

        for (loaiDTO db : dbList) {

            boolean stillExists = false;

            for (loaiDTO ram : dsLoai) {
                if (ram.getMaLoai().equalsIgnoreCase(db.getMaLoai())) {
                    stillExists = true;
                    break;
                }
            }

            if (!stillExists) {
                dao.delete(db.getMaLoai());
            }
        }

        return true;
    }

    public void reload() {
        dsLoai = dao.getAll();
    }
}