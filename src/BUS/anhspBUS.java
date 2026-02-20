package BUS;

import DAO.anhspDAO;
import DTO.anhspDTO;
import java.util.List;

public class anhspBUS {

    private anhspDAO dao = new anhspDAO();

    public List<anhspDTO> getAll() {
        return dao.getAll();
    }

    public List<anhspDTO> getByMaSP(String maSP) {
        return dao.getByMaSP(maSP);
    }

    public boolean add(anhspDTO anh) {

        if (anh.getMaAnh() == null || anh.getMaAnh().trim().isEmpty())
            return false;

        if (anh.getMaSP() == null || anh.getMaSP().trim().isEmpty())
            return false;

        if (anh.getUrl() == null || anh.getUrl().trim().isEmpty())
            return false;

        if (anh.isPrimary()) {
            List<anhspDTO> list = dao.getByMaSP(anh.getMaSP());
            for (anhspDTO a : list) {
                if (a.isPrimary()) {
                    a.setPrimary(false);
                    dao.update(a);
                }
            }
        }

        return dao.insert(anh);
    }

    public boolean update(anhspDTO anh) {

        if (anh.isPrimary()) {
            List<anhspDTO> list = dao.getByMaSP(anh.getMaSP());
            for (anhspDTO a : list) {
                if (!a.getMaAnh().equals(anh.getMaAnh()) && a.isPrimary()) {
                    a.setPrimary(false);
                    dao.update(a);
                }
            }
        }

        return dao.update(anh);
    }

    public boolean delete(String maAnh) {
        return dao.delete(maAnh);
    }
}
