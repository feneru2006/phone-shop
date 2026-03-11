package DAL.DAO;

import DTO.accountDTO;
import java.sql.*;

public class AccountDAO {
    public accountDTO findByUsername(String username) {
        String sql = "SELECT * FROM account WHERE ten = ? LIMIT 1";
        try (Connection conn = DAL.DAO.DBConnection.getConnection();
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

    public java.util.ArrayList<DTO.accountDTO> selectAll() {
        java.util.ArrayList<DTO.accountDTO> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM account";
        try (Connection conn = DAL.DAO.DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new DTO.accountDTO(
                        rs.getString("id"),
                        rs.getString("ten"),
                        rs.getString("pass"),
                        rs.getString("quyen")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}