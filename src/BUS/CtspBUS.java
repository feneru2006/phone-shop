package BUS;

import java.util.ArrayList;
import java.util.List;
import DAL.DAO.CtspDAO;
import DTO.ChitietSPDTO;

public class CtspBUS {
    private CtspDAO CtspDAO = new CtspDAO();
    private List<ChitietSPDTO> listCtsp = new ArrayList<>();

    public CtspBUS() {
        docDanhSach(); 
    }

    public void docDanhSach() {
        this.listCtsp = CtspDAO.getAll();
    }

    public List<ChitietSPDTO> getListCtsp() {
        return this.listCtsp;
    }

    // Lọc danh sách IMEI trên ArrayList (không cần gọi DB cho nhẹ máy)
    public List<ChitietSPDTO> getListByMaSP(String maSP) {
        List<ChitietSPDTO> result = new ArrayList<>();
        for (ChitietSPDTO ct : listCtsp) {
            if (ct.getMaSP().equals(maSP)) {
                result.add(ct);
            }
        }
        return result;
    }

    // --- THÊM 1 MÃ IMEI MỚI --- (Dành cho nhập lẻ, còn nhập sỉ đã có PhieuNhapBUS lo)
    public boolean themCtsp(ChitietSPDTO ctsp, List<String> permissions) {
        if (permissions == null || !permissions.contains("CREATE_CTSP")) {
            System.out.println("Lỗi: Bạn không có quyền thêm mã IMEI!");
            return false;
        }

        // 1. Thêm vào ArrayList TRƯỚC
        listCtsp.add(ctsp);

        // 2. Đẩy xuống DB
        boolean isSuccess = CtspDAO.insert(ctsp);

        // 3. Rollback nếu DB lỗi (Ví dụ trùng mã IMEI)
        if (!isSuccess) {
            listCtsp.remove(ctsp);
            System.out.println("Lỗi DB: Không thể thêm mã IMEI này (Có thể do trùng mã)!");
        }
        return isSuccess;
    }

    // --- SỬA THÔNG TIN 1 MÁY --- (Ví dụ: Đổi tình trạng từ Sẵn có -> Lỗi)
    public boolean suaCtsp(ChitietSPDTO ctspMoi, List<String> permissions) {
        if (permissions == null || !permissions.contains("UPDATE_CTSP")) {
            System.out.println("Lỗi: Bạn không có quyền cập nhật trạng thái IMEI!");
            return false;
        }

        ChitietSPDTO ctspCu = null;
        int index = -1;

        // 1. Tìm và cập nhật trên ArrayList TRƯỚC
        for (int i = 0; i < listCtsp.size(); i++) {
            if (listCtsp.get(i).getMaCTSP().equals(ctspMoi.getMaCTSP())) {
                ctspCu = listCtsp.get(i);
                index = i;
                listCtsp.set(i, ctspMoi);
                break;
            }
        }

        if (index == -1) {
            System.out.println("Lỗi: Không tìm thấy mã IMEI trong hệ thống tạm!");
            return false;
        }

        // 2. Cập nhật xuống DB
        boolean isSuccess = CtspDAO.update(ctspMoi);

        // 3. Rollback nếu lỗi
        if (!isSuccess) {
            listCtsp.set(index, ctspCu);
            System.out.println("Lỗi DB: Không thể cập nhật tình trạng máy!");
        }
        return isSuccess;
    }

    // --- XÓA 1 MÃ IMEI ---
    public boolean xoaCtsp(String maCTSP, List<String> permissions) {
        if (permissions == null || !permissions.contains("DELETE_CTSP")) {
            System.out.println("Lỗi: Bạn không có quyền xóa mã IMEI khỏi hệ thống!");
            return false;
        }

        ChitietSPDTO ctspBiXoa = null;
        int index = -1;

        // 1. Xóa khỏi ArrayList TRƯỚC
        for (int i = 0; i < listCtsp.size(); i++) {
            if (listCtsp.get(i).getMaCTSP().equals(maCTSP)) {
                ctspBiXoa = listCtsp.get(i);
                index = i;
                listCtsp.remove(i);
                break;
            }
        }

        if (ctspBiXoa == null) return false;

        // 2. Thực thi xóa DB
        boolean isSuccess = CtspDAO.delete(maCTSP);

        // 3. Rollback nếu DB cấm xóa (Ví dụ: Máy này đã được bán và nằm trong Hóa Đơn)
        if (!isSuccess) {
            listCtsp.add(index, ctspBiXoa);
            System.out.println("Lỗi DB: Không thể xóa vì máy này đã được liên kết với Hóa Đơn hoặc dữ liệu khác!");
        }
        return isSuccess;
    }
    // 1. TÌM KIẾM ĐA ĐIỀU KIỆN (Trực tiếp trên ArrayList để tăng tốc độ)
    // Giống ý tưởng FilterPBvaTT của file mẫu
    public List<ChitietSPDTO> timKiemVaLoc(String tuKhoaIMEI, String maSP, String tinhTrang) {
        List<ChitietSPDTO> result = new ArrayList<>();
        
        for (ChitietSPDTO ct : listCtsp) {
            boolean matchIMEI = tuKhoaIMEI.isEmpty() || ct.getMaCTSP().toLowerCase().contains(tuKhoaIMEI.toLowerCase());
            boolean matchMaSP = maSP.equals("ALL") || ct.getMaSP().equals(maSP);
            boolean matchTinhTrang = tinhTrang.equals("ALL") || ct.getTinhtrang().equalsIgnoreCase(tinhTrang);

            if (matchIMEI && matchMaSP && matchTinhTrang) {
                result.add(ct);
            }
        }
        return result;
    }

    // 2. LẤY DANH SÁCH IMEI THEO MÃ CHI TIẾT PHIẾU NHẬP
    // Chức năng này lấy từ DB lên vì nó thường phục vụ mục đích xem chi tiết hóa đơn cũ
    public List<ChitietSPDTO> layImeiTheoChiTietPhieu(String maCTPN) {
        return CtspDAO.getByMaCTPN(maCTPN);
    }
}