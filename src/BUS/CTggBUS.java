package BUS;

import java.util.List;
import DAL.DAO.CTggDAO;
import DTO.CTggDTO;
import Utility.Validator;

public class CTggBUS {

    private CTggDAO ctggDAO;

    public CTggBUS() {
        ctggDAO = new CTggDAO();
    }

    // ==============================
    // Lấy toàn bộ danh sách chi tiết giảm giá
    // ==============================
    public List<CTggDTO> getAll() {
        return ctggDAO.getAll();
    }

    // ==============================
    // Thêm sản phẩm vào đợt giảm giá
    // ==============================
    public boolean add(CTggDTO ctgg) {

        // Kiểm tra mã giảm giá
        if (!Validator.isValidCode(ctgg.getMAGG())) {
            throw new IllegalArgumentException(
                Validator.invalidFormatMessage("Mã giảm giá")
            );
        }

        // Kiểm tra mã sản phẩm
        if (!Validator.isValidCode(ctgg.getMaSP())) {
            throw new IllegalArgumentException(
                Validator.invalidFormatMessage("Mã sản phẩm")
            );
        }

        // Kiểm tra phần trăm giảm giá (0-100)
        if (!Validator.isValidPercent(ctgg.getPhantramgg())) {
            throw new IllegalArgumentException(
                Validator.invalidPercentMessage()
            );
        }

        return ctggDAO.insert(ctgg);
    }

    // ==============================
    // Cập nhật phần trăm giảm giá
    // ==============================
    public boolean update(CTggDTO ctgg) {

        if (!Validator.isValidCode(ctgg.getMAGG()) ||
            !Validator.isValidCode(ctgg.getMaSP())) {

            throw new IllegalArgumentException(
                "Thiếu hoặc sai thông tin mã cập nhật!"
            );
        }

        if (!Validator.isValidPercent(ctgg.getPhantramgg())) {
            throw new IllegalArgumentException(
                Validator.invalidPercentMessage()
            );
        }

        return ctggDAO.update(ctgg);
    }

    // ==============================
    // Xóa 1 sản phẩm khỏi đợt giảm giá
    // ==============================
    public boolean delete(String maGG, String maSP) {

        if (!Validator.isValidCode(maGG) ||
            !Validator.isValidCode(maSP)) {

            throw new IllegalArgumentException(
                "Thông tin mã không hợp lệ!"
            );
        }

        return ctggDAO.delete(maGG, maSP);
    }

    // ==============================
    // Xóa toàn bộ sản phẩm của 1 đợt
    // ==============================
    public boolean deleteByMAGG(String maGG) {

        if (!Validator.isValidCode(maGG)) {
            throw new IllegalArgumentException(
                Validator.invalidFormatMessage("Mã giảm giá")
            );
        }

        return ctggDAO.deleteByMAGG(maGG);
    }
}