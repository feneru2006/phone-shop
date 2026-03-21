package DAL.DAO;
import DAL.DBConnection;
import DTO.nhanvienDTO;
import java.sql.*;
import java.util.ArrayList;

public class NhanVienDAO {
    private final Connection conn = DBConnection.getConnection();

    public ArrayList<nhanvienDTO> getAll() {
        ArrayList<nhanvienDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM nhanvien";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                
                String gtDB = rs.getString("gioitinh");
                nhanvienDTO.GioiTinh enumGT = nhanvienDTO.GioiTinh.NAM; 
                
                if (gtDB != null) {
                    // Nếu DB ghi là "Nữ" hoặc "Nu" thì chuyển thành enum NU
                    if (gtDB.equalsIgnoreCase("Nữ") || gtDB.equalsIgnoreCase("Nu") || gtDB.equalsIgnoreCase("NỮ")) {
                        enumGT = nhanvienDTO.GioiTinh.NU; 
                    }
                }

                // Bơm dữ liệu vào DTO
                boolean add = list.add(new nhanvienDTO(
                    rs.getString("MANV"),
                    rs.getString("hoten"),
                    enumGT, 
                    rs.getString("SDT"),
                    rs.getString("diachi"),
                    rs.getDouble("thamnien"),
                    rs.getDouble("luong"),
                    rs.getBoolean("trangthai")
                ));
            }
        } catch (Exception e) { 
            System.out.println("====== LỖI Ở NHÂN VIÊN DAO ======");
            e.printStackTrace(); 
        }
        return list;
    }
}