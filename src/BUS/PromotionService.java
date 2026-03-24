package BUS;

import DAL.DAO.CTggDAO;
import DAL.DAO.SanPhamDAO;
import DAL.DAO.giamgiaDAO;
import DTO.CTggDTO;
import DTO.SanPhamDTO;
import DTO.giamgiaDTO;

import java.time.LocalDateTime;
import java.util.List;

public class PromotionService {

    private giamgiaDAO ggDAO = new giamgiaDAO();
    private CTggDAO ctggDAO = new CTggDAO();
    private SanPhamDAO spDAO = new SanPhamDAO();

    // =====================================================
    // 1. TẠO CHIẾN DỊCH KHUYẾN MÃI
    // =====================================================

    public boolean createPromotion(giamgiaDTO gg, List<CTggDTO> details) {

        // Validate thời gian
        if (gg.getBatdau().isAfter(gg.getKetthuc())) {
            throw new IllegalArgumentException("Ngày bắt đầu phải trước ngày kết thúc!");
        }

        // Insert bảng giamgia
        boolean inserted = ggDAO.insert(gg);

        if (!inserted) return false;

        // Insert bảng ctgg
        for (CTggDTO ct : details) {
            ctggDAO.insert(ct);
        }

        return true;
    }

    // =====================================================
    // 2. LẤY KHUYẾN MÃI ĐANG HOẠT ĐỘNG
    // (Vì DAO chưa có findByDate nên ta xử lý ở Service)
    // =====================================================

    public giamgiaDTO getActivePromotion() {

        List<giamgiaDTO> list = ggDAO.getAll();

        LocalDateTime now = LocalDateTime.now();

        for (giamgiaDTO gg : list) {
            if ((now.isEqual(gg.getBatdau()) || now.isAfter(gg.getBatdau()))
                    && (now.isBefore(gg.getKetthuc()) || now.isEqual(gg.getKetthuc()))) {

                return gg;
            }
        }

        return null;
    }

    // =====================================================
    // 3. LẤY % GIẢM CỦA 1 SẢN PHẨM (nếu có)
    // =====================================================

    public int getDiscountPercent(String maSP) {

        giamgiaDTO active = getActivePromotion();
        if (active == null) return 0;

        List<CTggDTO> ctList = ctggDAO.getAll();

        for (CTggDTO ct : ctList) {
            if (ct.getMAGG().equals(active.getMAGG())
                    && ct.getMaSP().equals(maSP)) {

                return ct.getPhantramgg();
            }
        }

        return 0;
    }

    // =====================================================
    // 4. TÍNH GIÁ SAU GIẢM (QUAN TRỌNG NHẤT)
    // =====================================================

    public double getFinalPrice(String maSP) {

        // Lấy giá gốc
        double originalPrice = 0;

        List<SanPhamDTO> spList = spDAO.getAll();
        for (SanPhamDTO sp : spList) {
            if (sp.getMaSP().equals(maSP)) {
                originalPrice = sp.getGia();
                break;
            }
        }

        int discountPercent = getDiscountPercent(maSP);

        if (discountPercent == 0) {
            return originalPrice;
        }

        double finalPrice = originalPrice * (1 - discountPercent / 100.0);

        return Math.max(finalPrice, 0);
    }
}