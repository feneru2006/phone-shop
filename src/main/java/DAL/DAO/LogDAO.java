package DAL.DAO;

import DTO.logDTO;
import DAL.DAO.DBConnection; 

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LogDAO {
    
    public boolean insertLog(logDTO log) {
        String sql = "INSERT INTO log (Malog, accountid, hanhvi, thucthe, chitiethv, thoidiem) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, log.getMalog());
            ps.setString(2, log.getAccountid());
            ps.setString(3, log.getHanhvi());
            ps.setString(4, log.getThucthe());
            ps.setString(5, log.getChitiethv());
            ps.setTimestamp(6, Timestamp.valueOf(log.getThoidiem()));

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<logDTO> getAll() {
        List<logDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM log ORDER BY thoidiem DESC"; // Sắp xếp mới nhất lên đầu
        
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                list.add(new logDTO(
                        rs.getString("Malog"),
                        rs.getString("accountid"),
                        rs.getString("hanhvi"),
                        rs.getString("thucthe"),
                        rs.getString("chitiethv"),
                        rs.getTimestamp("thoidiem").toLocalDateTime()
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public String getLastMaLog() {
        String sql = "SELECT Malog FROM log ORDER BY Malog DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getString("Malog");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}