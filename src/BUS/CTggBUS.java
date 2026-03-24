package BUS;

import java.util.ArrayList;
import java.util.List;

import DAL.DAO.CTggDAO;
import DTO.CTggDTO;
import Utility.Validator;

public class CTggBUS {

    private CTggDAO ctggDAO = new CTggDAO();
    private List<CTggDTO> dsCTGG = new ArrayList<>();
    private LogBUS logBUS = new LogBUS();
    
    public CTggBUS() {
        dsCTGG = ctggDAO.getAll();
    }

    public List<CTggDTO> getAll() {
        return dsCTGG;
    }

    public void reload() {
        dsCTGG = ctggDAO.getAll();
    }

    // ==============================
    // ADD
    // ==============================
    public boolean add(CTggDTO ctgg) {

        if (!Validator.isValidCode(ctgg.getMAGG())) {
            throw new IllegalArgumentException(
                    Validator.invalidFormatMessage("Mã giảm giá"));
        }

        if (!Validator.isValidCode(ctgg.getMaSP())) {
            throw new IllegalArgumentException(
                    Validator.invalidFormatMessage("Mã sản phẩm"));
        }

        if (!Validator.isValidPercent(ctgg.getPhantramgg())) {
            throw new IllegalArgumentException(
                    Validator.invalidPercentMessage());
        }

        // thêm vào list trước
        dsCTGG.add(ctgg);

        // insert DB
        if (!ctggDAO.insert(ctgg)) {
            dsCTGG.remove(ctgg); // rollback
            return false;
        }
        logBUS.ghiNhatKy("Thêm", "Sản phẩm", "Sửa GG mã: " + ctgg.getMAGG());
        return true;
    }

    // ==============================
    // UPDATE
    // ==============================
    public boolean update(CTggDTO ctgg) {

        if (!Validator.isValidCode(ctgg.getMAGG()) ||
            !Validator.isValidCode(ctgg.getMaSP())) {

            throw new IllegalArgumentException(
                    "Thiếu hoặc sai thông tin mã cập nhật!");
        }

        if (!Validator.isValidPercent(ctgg.getPhantramgg())) {
            throw new IllegalArgumentException(
                    Validator.invalidPercentMessage());
        }

        for (int i = 0; i < dsCTGG.size(); i++) {

            CTggDTO old = dsCTGG.get(i);

            if (old.getMAGG().equals(ctgg.getMAGG()) &&
                old.getMaSP().equals(ctgg.getMaSP())) {
                dsCTGG.set(i, ctgg);

                // update DB
                if (!ctggDAO.update(ctgg)) {
                    dsCTGG.set(i, old); // rollback
                    return false;
                }
                logBUS.ghiNhatKy("Sữa", "Giảm giá", "Sửa GG mã: " + ctgg.getMAGG());
                return true;
            }
        }

        return false;
    }

    // ==============================
    // DELETE 1 PRODUCT
    // ==============================
    public boolean delete(String maGG, String maSP) {

        if (!Validator.isValidCode(maGG) ||
            !Validator.isValidCode(maSP)) {

            throw new IllegalArgumentException(
                    "Thông tin mã không hợp lệ!");
        }

        for (int i = 0; i < dsCTGG.size(); i++) {

            CTggDTO ct = dsCTGG.get(i);

            if (ct.getMAGG().equals(maGG) &&
                ct.getMaSP().equals(maSP)) {
                logBUS.ghiNhatKy("Xóa", "CTGG", "Sửa GG mã: " + maGG);
                dsCTGG.remove(i);
                
                if (!ctggDAO.delete(maGG, maSP)) {
                    dsCTGG.add(i, ct); // rollback
                    return false;
                }

                return true;
            }
        }

        return false;
    }

    // ==============================
    // DELETE ALL BY MAGG
    // ==============================
    public boolean deleteByMAGG(String maGG) {

        if (!Validator.isValidCode(maGG)) {
            throw new IllegalArgumentException(
                    Validator.invalidFormatMessage("Mã giảm giá"));
        }

        List<CTggDTO> removed = new ArrayList<>();

        for (int i = dsCTGG.size() - 1; i >= 0; i--) {

            if (dsCTGG.get(i).getMAGG().equals(maGG)) {
                removed.add(dsCTGG.get(i));
                logBUS.ghiNhatKy("Xóa", "Giảm giá", "Sửa GG mã: " + maGG);
                dsCTGG.remove(i);
                
            }
        }

        if (!ctggDAO.deleteByMAGG(maGG)) {
            dsCTGG.addAll(removed); // rollback
            return false;
        }

        return true;
    }

    // ==============================
    // GET BY MAGG
    // ==============================
    public List<CTggDTO> getByMaGG(String maGG) {

        if (!Validator.isValidCode(maGG)) {
            throw new IllegalArgumentException(
                    Validator.invalidFormatMessage("Mã giảm giá"));
        }

        List<CTggDTO> result = new ArrayList<>();

        if (dsCTGG == null) {
            reload();
        }

        for (CTggDTO ct : dsCTGG) {
            if (ct.getMAGG().equals(maGG)) {
                result.add(ct);
            }
        }

        return result;
    }
}