    package BUS;

    import java.util.ArrayList;
    import java.util.List;
    import java.util.UUID;

    import DAL.DAO.PhieuNhapDAO;
    import DTO.phieunhapDTO;
    import DTO.CTphieunhapDTO;
    import DTO.ChitietSPDTO;

    public class PhieuNhapBUS {
        private PhieuNhapDAO pnDAO = new PhieuNhapDAO();

        // YÊU CẦU: Lưu trên ArrayList nội bộ
        private List<phieunhapDTO> listPhieuNhap = new ArrayList<>();

        public PhieuNhapBUS() {
            docDanhSach(); // Khởi tạo BUS là tự động kéo DB lên ArrayList
        }

        public void docDanhSach() {
            this.listPhieuNhap = pnDAO.getAll();
        }

        public List<phieunhapDTO> getListPhieuNhap() {
            return this.listPhieuNhap;
        }

        // Nghiệp vụ Nhập hàng siêu to khổng lồ
        public boolean nhapHangVaoKho(phieunhapDTO pn, List<CTphieunhapDTO> listCTPN, List<String> permissions) {

            // 1. Phân quyền linh động
            if (permissions == null || !permissions.contains("CREATE_NHAPHANG")) {
                System.out.println("Từ chối truy cập: Bạn không có quyền tạo phiếu nhập hàng!");
                return false;
            }

            // 2. Validate dữ liệu cơ bản
            if (listCTPN == null || listCTPN.isEmpty()) {
                System.out.println("Lỗi: Phiếu nhập phải có ít nhất 1 sản phẩm!");
                return false;
            }

            // 3. Tự động sinh danh sách IMEI cho từng cái điện thoại được nhập vào
            List<ChitietSPDTO> danhSachMayMoi = new ArrayList<>();

            for (CTphieunhapDTO ctpn : listCTPN) {
                for (int i = 0; i < ctpn.getSl(); i++) {
                    String randomIMEI = "IMEI-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

                    ChitietSPDTO may = new ChitietSPDTO();
                    may.setMaCTSP(randomIMEI);
                    may.setMaSP(ctpn.getMaSP());
                    may.setMaNCC(pn.getMaNCC());
                    may.setTinhtrang("Sẵn có");
                    may.setMaCTPN(ctpn.getMaCTPN());

                    danhSachMayMoi.add(may);
                }
            }

            // 4. GỌI DATABASE DAO ĐỂ LƯU XUỐNG
            boolean isSuccess = pnDAO.thucHienNhapHang(pn, listCTPN, danhSachMayMoi);

            // 5. YÊU CẦU: Nếu Database thành công -> Cập nhật vào ArrayList
            if (isSuccess) {
                this.listPhieuNhap.add(0, pn); // Thêm phiếu mới lên đầu danh sách (vị trí 0)
                System.out.println("Nhập hàng thành công! Đã tự động sinh " + danhSachMayMoi.size() + " mã IMEI.");
                return true;
            } else {
                System.out.println("Lỗi hệ thống: Nhập hàng thất bại!");
                return false;
            }
        }
        // --- 1. TÌM PHIẾU NHẬP THEO ID ---
        // Ưu tiên tìm trong ArrayList nội bộ để tăng tốc độ, không cần gọi Database
        public phieunhapDTO timPhieuNhapTheoId(String maPNH) {
            for (phieunhapDTO pn : listPhieuNhap) {
                if (pn.getMaPNH().equals(maPNH)) {
                    return pn;
                }
            }
            return null; // Không tìm thấy
        }

        // --- 2. SỬA PHIẾU NHẬP ---
        public boolean suaPhieuNhap(phieunhapDTO pnMoi, List<String> permissions) {
            if (permissions == null || !permissions.contains("UPDATE_NHAPHANG")) {
                System.out.println("Lỗi: Bạn không có quyền sửa phiếu nhập!");
                return false;
            }

            phieunhapDTO pnCu = null;
            int index = -1;

            // 1. Cập nhật trên ArrayList TRƯỚC
            for (int i = 0; i < listPhieuNhap.size(); i++) {
                if (listPhieuNhap.get(i).getMaPNH().equals(pnMoi.getMaPNH())) {
                    pnCu = listPhieuNhap.get(i);
                    index = i;
                    listPhieuNhap.set(i, pnMoi);
                    break;
                }
            }

            if (index == -1) {
                System.out.println("Lỗi: Không tìm thấy phiếu nhập trong hệ thống tạm!");
                return false;
            }

            // 2. Đẩy xuống Database
            boolean isSuccess = pnDAO.update(pnMoi);

            // 3. Rollback nếu Database báo lỗi
            if (!isSuccess) {
                listPhieuNhap.set(index, pnCu);
                System.out.println("Lỗi Database: Không thể cập nhật phiếu nhập!");
            }

            return isSuccess;
        }

        // --- 3. XÓA PHIẾU NHẬP ---
        public boolean xoaPhieuNhap(String maPNH, List<String> permissions) {
            if (permissions == null || !permissions.contains("DELETE_NHAPHANG")) {
                System.out.println("Lỗi: Bạn không có quyền xóa phiếu nhập!");
                return false;
            }

            phieunhapDTO pnBiXoa = null;
            int index = -1;

            // 1. Xóa trên ArrayList TRƯỚC
            for (int i = 0; i < listPhieuNhap.size(); i++) {
                if (listPhieuNhap.get(i).getMaPNH().equals(maPNH)) {
                    pnBiXoa = listPhieuNhap.get(i);
                    index = i;
                    listPhieuNhap.remove(i);
                    break;
                }
            }

            if (pnBiXoa == null) return false;

            // 2. Đẩy lệnh xóa xuống Database
            boolean isSuccess = pnDAO.delete(maPNH);

            // 3. Rollback (Khôi phục lại ArrayList) nếu Database cấm xóa
            if (!isSuccess) {
                listPhieuNhap.add(index, pnBiXoa);
                System.out.println("Lỗi Database: Cấm xóa phiếu nhập này vì đang vướng các thiết bị đã nhập vào kho!");
            }

            return isSuccess;
        }

        public List<CTphieunhapDTO> xemChiTietPhieuNhap(String maPNH) {
            if (maPNH == null || maPNH.isEmpty()) return new ArrayList<>();
            return pnDAO.getChiTietPhieuNhap(maPNH);
        }

        // --- 2. Thống kê Tổng Tiền Theo Tháng ---
        public double thongKeTongTienThang(int month, int year, List<String> permissions) {
            if (permissions == null || (!permissions.contains("VIEW_THONGKE") && !permissions.contains("VIEW_NHAPHANG"))) {
                System.out.println("Từ chối truy cập: Bạn không có quyền xem thống kê!");
                return -1;
            }
            return pnDAO.getTongTienNhapTrongThang(month, year);
        }

        // --- 4. LỌC: Theo Nhân Viên Lập Phiếu ---
        public List<phieunhapDTO> locTheoNhanVien(String maNV) {
            List<phieunhapDTO> ketQua = new ArrayList<>();
            for (phieunhapDTO pn : listPhieuNhap) {
                if (pn.getMaNV().equalsIgnoreCase(maNV)) {
                    ketQua.add(pn);
                }
            }
            return ketQua;
        }

        // --- 5. LỌC: Theo Nhà Cung Cấp ---
        public List<phieunhapDTO> locTheoNCC(String maNCC) {
            List<phieunhapDTO> ketQua = new ArrayList<>();
            for (phieunhapDTO pn : listPhieuNhap) {
                if (pn.getMaNCC().equalsIgnoreCase(maNCC)) {
                    ketQua.add(pn);
                }
            }
            return ketQua;
        }
    }