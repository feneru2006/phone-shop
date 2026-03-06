package BUS;

import DTO.PhieubaohanhDTO;
import DAL.DAO.PhieubaohanhDAO;
import Utility.Validator;

import java.time.LocalDate;
import java.util.List;

public class PhieubaohanhBUS {

    private PhieubaohanhDAO bhDAO;

    public PhieubaohanhBUS() {
        bhDAO = new PhieubaohanhDAO();
    }

    // ===============================
    // Lấy tất cả
    // ===============================
    public List<PhieubaohanhDTO> getAll() {
        return bhDAO.getAll();
    }

    // ===============================
    // Thêm phiếu bảo hành
    // ===============================
    public boolean add(PhieubaohanhDTO bh) {

        // Kiểm tra mã bảo hành
        if (!Validator.isValidCode(bh.getMaBH())) {
            throw new IllegalArgumentException(
                    Validator.invalidFormatMessage("Mã bảo hành"));
        }

        // Kiểm tra mã CTHD
        if (!Validator.isValidCode(bh.getMaCTHD())) {
            throw new IllegalArgumentException(
                    Validator.invalidFormatMessage("Mã chi tiết hóa đơn"));
        }

        // Kiểm tra mã khách hàng
        if (!Validator.isValidCode(bh.getMaKH())) {
            throw new IllegalArgumentException(
                    Validator.invalidFormatMessage("Mã khách hàng"));
        }

        // Kiểm tra ngày bắt đầu
        if (bh.getNgayBD() == null) {
            throw new IllegalArgumentException(
                    Validator.requiredMessage("Ngày bắt đầu"));
        }

        // Kiểm tra thời hạn
        if (!Validator.isPositiveInteger(bh.getThoiHan())) {
            throw new IllegalArgumentException(
                    Validator.positiveNumberMessage("Thời hạn bảo hành"));
        }

        // Tự động set trạng thái
        updateTrangThaiLogic(bh);

        return bhDAO.insert(bh);
    }

    // ===============================
    // Cập nhật phiếu bảo hành
    // ===============================
    public boolean update(PhieubaohanhDTO bh) {

        if (!Validator.isValidCode(bh.getMaBH())) {
            throw new IllegalArgumentException(
                    Validator.invalidFormatMessage("Mã bảo hành"));
        }

        updateTrangThaiLogic(bh);

        return bhDAO.update(bh);
    }

    // ===============================
    // Xóa
    // ===============================
    public boolean delete(String maBH) {

        if (!Validator.isValidCode(maBH)) {
            throw new IllegalArgumentException(
                    Validator.invalidFormatMessage("Mã bảo hành"));
        }

        return bhDAO.delete(maBH);
    }

    // ===============================
    // Logic tự động cập nhật trạng thái
    // ===============================
    private void updateTrangThaiLogic(PhieubaohanhDTO bh) {

        LocalDate ngayHetHan = bh.getNgayBD().plusMonths(bh.getThoiHan());

        if (LocalDate.now().isAfter(ngayHetHan)) {
            bh.setTrangthai("Hết hạn");
        } else {
            bh.setTrangthai("Còn hiệu lực");
        }
    }

    // ===============================
    // Kiểm tra còn bảo hành không
    // ===============================
    public boolean isStillValid(PhieubaohanhDTO bh) {

        LocalDate ngayHetHan = bh.getNgayBD().plusMonths(bh.getThoiHan());

        return LocalDate.now().isBefore(ngayHetHan);
    }
}