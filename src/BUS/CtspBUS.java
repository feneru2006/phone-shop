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

    public List<ChitietSPDTO> getListByMaSP(String maSP) {
        List<ChitietSPDTO> result = new ArrayList<>();
        for (ChitietSPDTO ct : listCtsp) {
            if (ct.getMaSP().equals(maSP)) {
                result.add(ct);
            }
        }
        return result;
    }

    public List<ChitietSPDTO> timKiemVaLoc(String tuKhoaIMEI, String maSP, String tinhTrang) {
        List<ChitietSPDTO> result = new ArrayList<>();
        
        for (ChitietSPDTO ct : listCtsp) {
            if (ct.getIsDeleted() == 0) {
            boolean matchIMEI = tuKhoaIMEI.isEmpty() || ct.getMaCTSP().toLowerCase().contains(tuKhoaIMEI.toLowerCase());
            boolean matchMaSP = maSP.equals("ALL") || ct.getMaSP().equals(maSP);
            boolean matchTinhTrang = tinhTrang.equals("ALL") || ct.getTinhtrang().equalsIgnoreCase(tinhTrang);

            if (matchIMEI && matchMaSP && matchTinhTrang) {
                result.add(ct);
            }
        }
    }
        return result;
    }

    public List<ChitietSPDTO> layImeiTheoChiTietPhieu(String maCTPN) {
        return CtspDAO.getByMaCTPN(maCTPN);
    }
}
