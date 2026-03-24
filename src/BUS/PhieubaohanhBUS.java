package BUS;

import DTO.PhieubaohanhDTO;
import DAL.DAO.PhieubaohanhDAO;
import Utility.Validator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PhieubaohanhBUS {

    private PhieubaohanhDAO bhDAO;
    private LogBUS logBUS = new LogBUS();
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
        if (!Validator.isValidCode(bh.getMaCTHD())) {
            throw new IllegalArgumentException(
                    Validator.invalidFormatMessage("Mã chi tiết hóa đơn"));
        }
        if (!Validator.isValidCode(bh.getMaKH())) {
            throw new IllegalArgumentException(
                    Validator.invalidFormatMessage("Mã khách hàng"));
        }
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
        logBUS.ghiNhatKy("Thêm", "Phiếu bảo hành", "Thêm phiếu bảo hành mã: " + bh.getMaBH());
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
        logBUS.ghiNhatKy("Thêm", "Phiếu bảo hành", "Thêm phiếu bảo hành mã: " + bh.getMaBH());
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
        logBUS.ghiNhatKy("Thêm", "Phiếu bảo hành", "Thêm phiếu bảo hành mã: " + maBH);
        return bhDAO.delete(maBH);
    }

    // ===============================
    // Logic tự động cập nhật trạng thái
    // ===============================
    public void updateTrangThaiLogic(PhieubaohanhDTO bh) {

        LocalDate ngayHetHan = bh.getNgayBD().plusMonths(bh.getThoiHan());

        if (LocalDate.now().isAfter(ngayHetHan)) {
            bh.setTrangthai("Hết hạn");
        } else {
            bh.setTrangthai("Đang bao hành");
        }
    }

    // ===============================
    // Kiểm tra còn bảo hành không
    // ===============================
    public List<PhieubaohanhDTO> searchByMaBH(String keyword) {

        List<PhieubaohanhDTO> result = new ArrayList<>();

        List<PhieubaohanhDTO> list = bhDAO.getAll();

        for (PhieubaohanhDTO bh : list) {

            if (bh.getMaBH().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(bh);
            }

        }

        return result;
    }

    public void updateAllTrangThai() {

    List<PhieubaohanhDTO> list = bhDAO.getAll();

    for (PhieubaohanhDTO bh : list) {

        String oldStatus = bh.getTrangthai();

        updateTrangThaiLogic(bh); // tính lại trạng thái

        // nếu trạng thái thay đổi thì update DB
        if (!bh.getTrangthai().equals(oldStatus)) {
            bhDAO.update(bh);
        }
    }
}
}