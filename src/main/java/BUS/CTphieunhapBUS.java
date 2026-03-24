package BUS;

import java.util.ArrayList;
import java.util.List;
import DAL.DAO.CTphieunhapDAO;
import DTO.CTphieunhapDTO;

public class CTphieunhapBUS {
    private CTphieunhapDAO ctpnDAO = new CTphieunhapDAO();
    private List<CTphieunhapDTO> listChiTietHienTai = new ArrayList<>();

    public CTphieunhapBUS() {}
    public List<CTphieunhapDTO> layChiTietTheoMaPhieu(String maPNH, List<String> permissions) {
        if (permissions == null || (!permissions.contains("VIEW_NHAPHANG") && !permissions.contains("ADMIN"))) {
            System.out.println("Từ chối truy cập: Bạn không có quyền xem chi tiết phiếu nhập!");
            return new ArrayList<>(); 
        }

        this.listChiTietHienTai = ctpnDAO.getByMaPNH(maPNH);
        return this.listChiTietHienTai;
    }

}