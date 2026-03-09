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
                boolean add = list.add(new nhanvienDTO(
                        rs.getString("MANV"), rs.getString("hoten"),
                        nhanvienDTO.GioiTinh.valueOf(rs.getString("gioitinh").toUpperCase()),
                        rs.getString("SDT"), rs.getString("diachi"),
                        rs.getDouble("thamnien"), rs.getDouble("luong"), rs.getBoolean("trangthai")
                ));
            }
        } catch (SQLException e) {}
        return list;
    }
}