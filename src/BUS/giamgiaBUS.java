package BUS;

import java.util.List;
import DAL.DAO.giamgiaDAO;
import DTO.giamgiaDTO;
import Utility.Validator;

public class giamgiaBUS {

    private giamgiaDAO ggDAO;

    public giamgiaBUS() {
        ggDAO = new giamgiaDAO();
    }

    // ==============================
    // Lấy toàn bộ danh sách giảm giá
    // ==============================
    public List<giamgiaDTO> getAll() {
        return ggDAO.getAll();
    }

    // ==============================
    // Thêm đợt giảm giá
    // ==============================
    public boolean add(giamgiaDTO gg) {

        // Kiểm tra mã giảm giá
        if (!Validator.isValidCode(gg.getMAGG())) {
            throw new IllegalArgumentException(
                Validator.invalidFormatMessage("Mã giảm giá")
            );
        }

        // Kiểm tra thời gian
        if (!Validator.isValidDateRange(
                gg.getBatdau(),
                gg.getKetthuc())) {

            throw new IllegalArgumentException(
                Validator.invalidDateRangeMessage()
            );
        }

        return ggDAO.insert(gg);
    }

    // ==============================
    // Cập nhật đợt giảm giá
    // ==============================
    public boolean update(giamgiaDTO gg) {

        if (!Validator.isValidCode(gg.getMAGG())) {
            throw new IllegalArgumentException(
                Validator.invalidFormatMessage("Mã giảm giá")
            );
        }

        if (!Validator.isValidDateRange(
                gg.getBatdau(),
                gg.getKetthuc())) {

            throw new IllegalArgumentException(
                Validator.invalidDateRangeMessage()
            );
        }

        return ggDAO.update(gg);
    }

    // ==============================
    // Xóa đợt giảm giá
    // ==============================
    public boolean delete(String maGG) {

        if (!Validator.isValidCode(maGG)) {
            throw new IllegalArgumentException(
                Validator.invalidFormatMessage("Mã giảm giá")
            );
        }

        return ggDAO.delete(maGG);
    }
}