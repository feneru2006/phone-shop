package DAL.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import DTO.anhspDTO;

public class anhspDAO {

    public ArrayList<anhspDTO> getAll() {

        ArrayList<anhspDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM anhsp";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new anhspDTO(
                        rs.getString("maAnh"),
                        rs.getString("maSP"),
                        rs.getString("url"),
                        rs.getBoolean("isPrimary")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean insert(anhspDTO a) {

        String sql = "INSERT INTO anhsp VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, a.getMaAnh());
            ps.setString(2, a.getMaSP());
            ps.setString(3, a.getUrl());
            ps.setBoolean(4, a.isPrimary());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(anhspDTO a) {

        String sql = "UPDATE anhsp SET maSP=?, url=?, isPrimary=? WHERE maAnh=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, a.getMaSP());
            ps.setString(2, a.getUrl());
            ps.setBoolean(3, a.isPrimary());
            ps.setString(4, a.getMaAnh());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(String maAnh) {

        String sql = "DELETE FROM anhsp WHERE maAnh=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maAnh);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
