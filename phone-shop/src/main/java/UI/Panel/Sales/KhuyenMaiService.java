package UI.Panel.Sales;

import BUS.giamgiaBUS;
import DTO.giamgiaDTO;

import java.time.LocalDateTime;
import java.util.List;

public class KhuyenMaiService {

    private giamgiaBUS ggBUS = new giamgiaBUS();

    public List<giamgiaDTO> getAll() {
        return ggBUS.getAll();
    }

    public boolean delete(String ma) {
        return ggBUS.delete(ma);
    }

    public boolean update(String ma, String ten,
                          LocalDateTime bd,
                          LocalDateTime kt) {
        giamgiaDTO gg = new giamgiaDTO(ma, ten, bd, kt);
        return ggBUS.update(gg);
    }

    public boolean add(String ma, String ten,
                       LocalDateTime bd,
                       LocalDateTime kt) {
        giamgiaDTO gg = new giamgiaDTO(ma, ten, bd, kt);
        return ggBUS.add(gg);
    }
}