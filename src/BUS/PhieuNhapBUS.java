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

        private List<phieunhapDTO> listPhieuNhap = new ArrayList<>();

        public PhieuNhapBUS() {
            docDanhSach(); 
        }

        public void docDanhSach() {
            this.listPhieuNhap = pnDAO.getAll();
        }

        public List<phieunhapDTO> getListPhieuNhap() {
            return this.listPhieuNhap;
        }

        public boolean nhapHangVaoKho(phieunhapDTO pn, List<CTphieunhapDTO> listCTPN, List<String> permissions) {

            if (permissions == null || !permissions.contains("CREATE_NHAPHANG")) {
                System.out.println("Từ chối truy cập: Bạn không có quyền tạo phiếu nhập hàng!");
                return false;
            }

            if (listCTPN == null || listCTPN.isEmpty()) {
                System.out.println("Lỗi: Phiếu nhập phải có ít nhất 1 sản phẩm!");
                return false;
            }

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

            boolean isSuccess = pnDAO.thucHienNhapHang(pn, listCTPN, danhSachMayMoi);

            if (isSuccess) {
                this.listPhieuNhap.add(0, pn); 
                System.out.println("Nhập hàng thành công! Đã tự động sinh " + danhSachMayMoi.size() + " mã IMEI.");
                return true;
            } else {
                System.out.println("Lỗi hệ thống: Nhập hàng thất bại!");
                return false;
            }
        }

        public phieunhapDTO timPhieuNhapTheoId(String maPNH) {
            for (phieunhapDTO pn : listPhieuNhap) {
                if (pn.getMaPNH().equals(maPNH)) {
                    return pn;
                }
            }
            return null; 
        }

        public boolean suaPhieuNhap(phieunhapDTO pnMoi, List<String> permissions) {
            if (permissions == null || !permissions.contains("UPDATE_NHAPHANG")) {
                System.out.println("Lỗi: Bạn không có quyền sửa phiếu nhập!");
                return false;
            }

            phieunhapDTO pnCu = null;
            int index = -1;

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

            boolean isSuccess = pnDAO.update(pnMoi);

            if (!isSuccess) {
                listPhieuNhap.set(index, pnCu);
                System.out.println("Lỗi Database: Không thể cập nhật phiếu nhập!");
            }

            return isSuccess;
        }

        public boolean xoaPhieuNhap(String maPNH, List<String> permissions) {
            if (permissions == null || !permissions.contains("DELETE_NHAPHANG")) {
                System.out.println("Lỗi: Bạn không có quyền xóa phiếu nhập!");
                return false;
            }

            phieunhapDTO pnBiXoa = null;
            int index = -1;

            for (int i = 0; i < listPhieuNhap.size(); i++) {
                if (listPhieuNhap.get(i).getMaPNH().equals(maPNH)) {
                    pnBiXoa = listPhieuNhap.get(i);
                    index = i;
                    listPhieuNhap.remove(i);
                    break;
                }
            }

            if (pnBiXoa == null) return false;

            boolean isSuccess = pnDAO.delete(maPNH);

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

        public double thongKeTongTienThang(int month, int year, List<String> permissions) {
            if (permissions == null || (!permissions.contains("VIEW_THONGKE") && !permissions.contains("VIEW_NHAPHANG"))) {
                System.out.println("Từ chối truy cập: Bạn không có quyền xem thống kê!");
                return -1;
            }
            return pnDAO.getTongTienNhapTrongThang(month, year);
        }

        public List<phieunhapDTO> locTheoNhanVien(String maNV) {
            List<phieunhapDTO> ketQua = new ArrayList<>();
            for (phieunhapDTO pn : listPhieuNhap) {
                if (pn.getMaNV().equalsIgnoreCase(maNV)) {
                    ketQua.add(pn);
                }
            }
            return ketQua;
        }

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