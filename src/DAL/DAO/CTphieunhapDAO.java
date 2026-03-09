package DAL.DAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import DTO.CTphieunhapDTO;

public class CTphieunhapDAO {
    
        public List<CTphieunhapDTO> getByMaPNH(String maPNH) {
        List<CTphieunhapDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM CTphieunhap WHERE MAPNH = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, maPNH);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                list.add(new CTphieunhapDTO(
                    rs.getString("MACTPN"),
                    rs.getString("MAPNH"),
                    rs.getString("MASP"),
                    rs.getInt("SL"),
                    rs.getDouble("dongia"),
                    rs.getDouble("thanhtien") 
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}