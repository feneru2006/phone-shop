package DAL.DAO;

import DTO.accountDTO;
import java.sql.*;

public class AccountDAO {
    public accountDTO findByUsername(String username) {
        String sql = "SELECT * FROM account WHERE ten = ? LIMIT 1";
        try (Connection conn = DAO.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new accountDTO(
                            rs.getString("id"),
                            rs.getString("ten"),
                            rs.getString("pass"),
                            rs.getString("quyen")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}