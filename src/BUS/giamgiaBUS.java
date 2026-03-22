package BUS;

import java.util.ArrayList;
import java.util.List;
import DAL.DAO.giamgiaDAO;
import DTO.giamgiaDTO;
import Utility.Validator;

public class giamgiaBUS {

    private giamgiaDAO ggDAO = new giamgiaDAO();
    private List<giamgiaDTO> dsGG = new ArrayList<>();

    public giamgiaBUS() {
        dsGG = ggDAO.getAll();
    }

    // ==============================
    // Lấy toàn bộ danh sách giảm giá
    // ==============================
    public List<giamgiaDTO> getAll() {
        return dsGG;
    }

    public void reload() {
        dsGG = ggDAO.getAll();
    }

    public giamgiaDTO getById(String maGG) {
        if (dsGG == null || maGG == null) {
        }
        for (giamgiaDTO gg : dsGG) {
            if (gg.getMAGG().equalsIgnoreCase(maGG)) {
                return gg;
            }
        }
        return null;
    }

    // ==============================
    // Thêm đợt giảm giá
    // ==============================
    public boolean add(giamgiaDTO gg) {

    if (!Validator.isValidCode(gg.getMAGG())) {
        throw new IllegalArgumentException(
                Validator.invalidFormatMessage("Mã giảm giá"));
    }

    if (!Validator.isValidDateRange(
            gg.getBatdau(),
            gg.getKetthuc())) {

        throw new IllegalArgumentException(
                Validator.invalidDateRangeMessage());
    }

    // 1️⃣ Thêm vào list trước
    dsGG.add(gg);

    // 2️⃣ Sau đó thêm vào database
    if (ggDAO.insert(gg)) {
        return true;
    } else {
        // 3️⃣ Nếu DB lỗi thì rollback list
        dsGG.remove(gg);
        return false;
    }
}

    // ==============================
    // Cập nhật đợt giảm giá
    // ==============================
    public boolean update(giamgiaDTO gg) {

    if (!Validator.isValidCode(gg.getMAGG())) {
        throw new IllegalArgumentException(
                Validator.invalidFormatMessage("Mã giảm giá"));
    }

    if (Validator.isNullOrEmpty(gg.getdotGG())) {
        throw new IllegalArgumentException(
                Validator.invalidFormatMessage("Tên đợt giảm giá"));
    }

    if (!Validator.isValidDateRange(
            gg.getBatdau(),
            gg.getKetthuc())) {

        throw new IllegalArgumentException(
                Validator.invalidDateRangeMessage());
    }

    for (int i = 0; i < dsGG.size(); i++) {

        if (dsGG.get(i).getMAGG().equals(gg.getMAGG())) {

            // backup dữ liệu cũ
            giamgiaDTO old = dsGG.get(i);

            // update list trước
            dsGG.set(i, gg);

            // update DB
            if (ggDAO.update(gg)) {
                return true;
            } else {
                // rollback nếu DB lỗi
                dsGG.set(i, old);
                return false;
            }
        }
    }

    return false;
}

    // ==============================
    // Xóa đợt giảm giá
    // ==============================
    public boolean delete(String maGG) {

    if (!Validator.isValidCode(maGG)) {
        throw new IllegalArgumentException(
                Validator.invalidFormatMessage("Mã giảm giá"));
    }

    for (int i = 0; i < dsGG.size(); i++) {

        if (dsGG.get(i).getMAGG().equals(maGG)) {

            // backup
            giamgiaDTO removed = dsGG.get(i);

            // xóa khỏi list trước
            dsGG.remove(i);

            // xóa DB
            if (ggDAO.delete(maGG)) {
                return true;
            } else {
                // rollback nếu DB lỗi
                dsGG.add(i, removed);
                return false;
            }
        }
    }

    return false;
}

    public String getLastMaGG() {

        if (dsGG == null || dsGG.isEmpty()) {
            reload();
        }

        int max = 0;

        for (giamgiaDTO gg : dsGG) {

            int num = Integer.parseInt(gg.getMAGG().substring(2));

            if (num > max) {
                max = num;
            }
        }

        return String.format("GG%02d", max);
    }

    public String generateMaGG() {

        String last = getLastMaGG();

        if (last == null) {
            return "GG01";
        }

        int num = Integer.parseInt(last.substring(2));
        num++;

        return String.format("GG%02d", num);
    }
}