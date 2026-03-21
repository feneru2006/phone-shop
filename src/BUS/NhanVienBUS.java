
package BUS;
import DAL.DAO.NhanVienDAO;
import DTO.nhanvienDTO;
import java.util.ArrayList; 

public class NhanVienBUS {
    private NhanVienDAO dao = new NhanVienDAO();

    
    public String layTenNhanVien(String maNV) {
        for (nhanvienDTO nv : dao.getAll()) {
            if (nv.getMaNV().equals(maNV)) return nv.getHoTen();
        }
        return "Unknown";
    }

    // Để lấy danh sách đổ vào ComboBox cho bên bán hàng UI
    public java.util.List<nhanvienDTO> getAll() {
        return dao.getAll();
    }
}