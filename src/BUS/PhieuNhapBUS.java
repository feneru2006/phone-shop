package BUS;

import java.util.ArrayList;
import java.util.List;

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

    public boolean nhapHangVaoKho(phieunhapDTO pn, List<CTphieunhapDTO> listCTPN, List<ChitietSPDTO> listIMEI, List<String> permissions) {
        
        if (permissions == null || !permissions.contains("CREATE_NHAPHANG")) {
            System.out.println("Từ chối truy cập: Bạn không có quyền tạo phiếu nhập hàng!");
            return false;
        }
        
        if (listCTPN == null || listCTPN.isEmpty()) {
            System.out.println("Lỗi: Phiếu nhập phải có ít nhất 1 sản phẩm!");
            return false;
        }
        boolean isSuccess = pnDAO.thucHienNhapHang(pn, listCTPN, listIMEI);
        if (isSuccess) {
            this.listPhieuNhap.add(0, pn); 
            System.out.println("Nhập hàng thành công! Đã thêm " + listIMEI.size() + " mã IMEI vào kho.");
            return true;
        } else {
            System.out.println("Lỗi hệ thống: Nhập hàng thất bại do Database từ chối!");
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

    public boolean huyPhieuNhap(String maPNH, List<String> permissions) {
        if (permissions == null || !permissions.contains("DELETE_NHAPHANG")) {
            System.out.println("Lỗi: Bạn không có quyền hủy phiếu nhập!");
            return false;
        }

        phieunhapDTO pnBiHuy = null;
        int index = -1;

        for (int i = 0; i < listPhieuNhap.size(); i++) {
            if (listPhieuNhap.get(i).getMaPNH().equals(maPNH)) {
                pnBiHuy = listPhieuNhap.get(i);
                index = i;
                listPhieuNhap.remove(i);
                break;
            }
        }

        if (pnBiHuy == null) {
            System.out.println("Không tìm thấy phiếu nhập trên hệ thống!");
            return false;
        }

        boolean isSuccess = pnDAO.huyPhieuNhap(maPNH);

        if (!isSuccess) {
            listPhieuNhap.add(index, pnBiHuy);
            System.out.println("Lỗi Database: Không thể hủy phiếu nhập do lỗi kết nối hoặc vi phạm dữ liệu!");
        } else {
            System.out.println("Hủy phiếu nhập thành công. Tồn kho đã được khôi phục!");
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