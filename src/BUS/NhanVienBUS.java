
package BUS;
import DAL.DAO.NhanVienDAO;
import DTO.nhanvienDTO;

public class NhanVienBUS {
    private NhanVienDAO dao = new NhanVienDAO();

    public String layTenNhanVien(String maNV) {
        for (nhanvienDTO nv : dao.getAll()) {
            if (nv.getMaNV().equals(maNV)) return nv.getHoTen();
        }
        return "Unknown";
    }
}