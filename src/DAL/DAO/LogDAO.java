package DAL.DAO;

import java.io.DataOutput;
import java.sql.*;
import DTO.logDTO;

public class LogDAO {
    public boolean insertLog(logDTO log) {
        String sql = "INSERT INTO log (Malog, accountid, hanhvi, thucthe, chitiethv, thoidiem) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DAL.DAO.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, log.getMalog());
            ps.setString(2, log.getAccountid());
            ps.setString(3, log.getHanhvi());
            ps.setString(4, log.getThucthe());
            ps.setString(5, log.getChitiethv());
            ps.setObject(6, log.getThoidiem());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}