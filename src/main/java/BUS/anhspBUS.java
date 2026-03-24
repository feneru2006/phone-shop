package BUS;

import java.util.ArrayList;

import DAL.DAO.anhspDAO;
import DTO.anhspDTO;

public class anhspBUS {

    private anhspDAO dao = new anhspDAO();
    private ArrayList<anhspDTO> list;
    
    // Thêm đối tượng LogBUS
    private LogBUS logBUS = new LogBUS();

    public anhspBUS() {
        list = dao.getAll();
    }

    public ArrayList<anhspDTO> getAll() {
        return list;
    }

    public boolean add(anhspDTO a) {

        for (anhspDTO x : list) {
            if (x.getMaAnh().equals(a.getMaAnh()))
                return false;
        }

        list.add(a);
        // Ghi log khi thêm thành công
        logBUS.ghiNhatKy("Thêm", "Ảnh Sản Phẩm", "Thêm ảnh mã: " + a.getMaAnh());
        return true;
    }

    public boolean update(anhspDTO a) {

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getMaAnh().equals(a.getMaAnh())) {
                list.set(i, a);
                // Ghi log khi sửa thành công
                logBUS.ghiNhatKy("Sửa", "Ảnh Sản Phẩm", "Sửa ảnh mã: " + a.getMaAnh());
                return true;
            }
        }
        return false;
    }

    public boolean delete(String maAnh) {

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getMaAnh().equals(maAnh)) {
                list.remove(i);
                // Ghi log khi xóa thành công
                logBUS.ghiNhatKy("Xóa", "Ảnh Sản Phẩm", "Xóa ảnh mã: " + maAnh);
                return true;
            }
        }
        return false;
    }

    public boolean saveToDatabase() {

        try {

            ArrayList<anhspDTO> dbList = dao.getAll();

            for (anhspDTO ram : list) {

                boolean exists = false;

                for (anhspDTO db : dbList) {
                    if (db.getMaAnh().equals(ram.getMaAnh())) {
                        dao.update(ram);
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    dao.insert(ram);
                }
            }

            for (anhspDTO db : dbList) {

                boolean stillExists = false;

                for (anhspDTO ram : list) {
                    if (ram.getMaAnh().equals(db.getMaAnh())) {
                        stillExists = true;
                        break;
                    }
                }

                if (!stillExists) {
                    dao.delete(db.getMaAnh());
                }
            }

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public void reload() {
        list = dao.getAll();
    }
}