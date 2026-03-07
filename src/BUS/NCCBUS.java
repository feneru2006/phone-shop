
package BUS;

import java.util.ArrayList;
import java.util.List;
import DAL.DAO.NCCDAO;
import DTO.NCCDTO;

public class NCCBUS {
    private NCCDAO nccDAO = new NCCDAO();
    private List<NCCDTO> listNCC = new ArrayList<>();

    public NCCBUS() {
        docDanhSach(); 
    }

    // Lấy dữ liệu từ DB về ArrayList
    public void docDanhSach() {
        this.listNCC = nccDAO.getAll();
    }

    public List<NCCDTO> getListNCC() {
        return this.listNCC;
    }

    // --- THÊM NHÀ CUNG CẤP ---
    public boolean themNCC(NCCDTO ncc, List<String> permissions) {
        if (permissions == null || !permissions.contains("CREATE_NCC")) {
            System.out.println("Lỗi: Bạn không có quyền thêm Nhà Cung Cấp!");
            return false;
        }

        // 1. Cập nhật trên ArrayList TRƯỚC theo yêu cầu
        listNCC.add(ncc);

        // 2. Đẩy dữ liệu xuống Database
        boolean isSuccess = nccDAO.insert(ncc);

        // 3. Nếu Database báo lỗi (Ví dụ trùng mã MANCC), thì xóa phần tử vừa thêm ra khỏi ArrayList
        if (!isSuccess) {
            listNCC.remove(ncc);
            System.out.println("Lỗi Database: Không thể thêm Nhà cung cấp!");
        }
        
        return isSuccess;
    }

    // --- SỬA NHÀ CUNG CẤP ---
    public boolean suaNCC(NCCDTO nccMoi, List<String> permissions) {
        if (permissions == null || !permissions.contains("UPDATE_NCC")) {
            System.out.println("Lỗi: Bạn không có quyền sửa Nhà Cung Cấp!");
            return false;
        }

        // Lưu tạm thông tin cũ để phòng hờ rollback
        NCCDTO nccCu = null;
        int index = -1;

        // 1. Cập nhật trên ArrayList TRƯỚC
        for (int i = 0; i < listNCC.size(); i++) {
            if (listNCC.get(i).getMaNCC().equals(nccMoi.getMaNCC())) {
                nccCu = listNCC.get(i);
                index = i;
                listNCC.set(i, nccMoi); // Thay thế trên list
                break;
            }
        }

        if (index == -1) {
            System.out.println("Không tìm thấy Nhà cung cấp trong danh sách tạm!");
            return false;
        }

        // 2. Đẩy xuống DB
        boolean isSuccess = nccDAO.update(nccMoi);

        // 3. Rollback nếu DB lỗi
        if (!isSuccess) {
            listNCC.set(index, nccCu); // Trả lại thông tin cũ vào ArrayList
            System.out.println("Lỗi Database: Không thể cập nhật!");
        }

        return isSuccess;
    }

    // --- XÓA NHÀ CUNG CẤP ---
    public boolean xoaNCC(String maNCC, List<String> permissions) {
        if (permissions == null || !permissions.contains("DELETE_NCC")) {
            System.out.println("Lỗi: Bạn không có quyền xóa Nhà Cung Cấp!");
            return false;
        }

        NCCDTO nccBiXoa = null;
        int index = -1;

        // 1. Xóa trên ArrayList TRƯỚC
        for (int i = 0; i < listNCC.size(); i++) {
            if (listNCC.get(i).getMaNCC().equals(maNCC)) {
                nccBiXoa = listNCC.get(i);
                index = i;
                listNCC.remove(i);
                break;
            }
        }

        if (nccBiXoa == null) return false;

        // 2. Thực thi xóa dưới DB
        boolean isSuccess = nccDAO.delete(maNCC);

        // 3. Rollback nếu DB lỗi (Ví dụ: NCC đang dính khóa ngoại với Phiếu nhập)
        if (!isSuccess) {
            listNCC.add(index, nccBiXoa); // Nhét lại vào vị trí cũ
            System.out.println("Lỗi Database: Không thể xóa vì Nhà cung cấp này đã có giao dịch Nhập hàng!");
        }

        return isSuccess;
    }
    // ==========================================================
    // CÁC HÀM BỔ SUNG HỖ TRỢ GIAO DIỆN (Tham khảo từ code mẫu)
    // ==========================================================

    // 1. TÌM KIẾM NHÀ CUNG CẤP (Trực tiếp trên ArrayList)
    public List<NCCDTO> timKiem(String tuKhoa, String tieuChi) {
        List<NCCDTO> result = new ArrayList<>();
        String txt = tuKhoa.toLowerCase().trim();

        for (NCCDTO ncc : listNCC) {
            switch (tieuChi) {
                case "Tất cả":
                    if (ncc.getMaNCC().toLowerCase().contains(txt) || 
                        ncc.getTen().toLowerCase().contains(txt) || 
                        ncc.getDiaChi().toLowerCase().contains(txt) || 
                        ncc.getSdt().toLowerCase().contains(txt)) {
                        result.add(ncc);
                    }
                    break;
                case "Mã nhà cung cấp":
                    if (ncc.getMaNCC().toLowerCase().contains(txt)) result.add(ncc);
                    break;
                case "Tên nhà cung cấp":
                    if (ncc.getTen().toLowerCase().contains(txt)) result.add(ncc);
                    break;
                case "Số điện thoại":
                    if (ncc.getSdt().toLowerCase().contains(txt)) result.add(ncc);
                    break;
            }
        }
        return result;
    }

    // 2. LẤY MẢNG TÊN NHÀ CUNG CẤP (Hỗ trợ đưa vào JComboBox)
    public String[] layMangTenNCC() {
        String[] result = new String[listNCC.size()];
        for (int i = 0; i < listNCC.size(); i++) {
            result[i] = listNCC.get(i).getTen();
        }
        return result;
    }

    // 3. TÌM ĐỐI TƯỢNG NCC THEO TÊN (Phục vụ khi người dùng chọn tên trên ComboBox)
    public NCCDTO timNccTheoTen(String tenNCC) {
        for (NCCDTO ncc : listNCC) {
            if (ncc.getTen().equalsIgnoreCase(tenNCC.trim())) {
                return ncc;
            }
        }
        return null;
    }

    // 4. LẤY TÊN NCC DỰA VÀO MÃ (Dùng để hiển thị lên bảng Phiếu Nhập thay vì hiện Mã khó nhớ)
    public String layTenNccTheoMa(String maNCC) {
        for (NCCDTO ncc : listNCC) {
            if (ncc.getMaNCC().equals(maNCC)) {
                return ncc.getTen();
            }
        }
        return "Không xác định";
    }
}
