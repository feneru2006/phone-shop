package DAL.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import DTO.anhspDTO;

public class anhspDAO {

    public List<anhspDTO> getAll() {
        List<anhspDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM anhsp";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new anhspDTO(
                        rs.getString("MAANH"),
                        rs.getString("MASP"),
                        rs.getString("URL"),
                        rs.getBoolean("isPrimary")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean insert(anhspDTO anh) {
        String sql = "INSERT INTO anhsp(MAANH, MASP, URL, isPrimary) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, anh.getMaAnh());
            ps.setString(2, anh.getMaSP());
            ps.setString(3, anh.getUrl());
            ps.setBoolean(4, anh.isPrimary());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(anhspDTO anh) {
        String sql = "UPDATE anhsp SET MASP=?, URL=?, isPrimary=? WHERE MAANH=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, anh.getMaSP());
            ps.setString(2, anh.getUrl());
            ps.setBoolean(3, anh.isPrimary());
            ps.setString(4, anh.getMaAnh());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(String maAnh) {
        String sql = "DELETE FROM anhsp WHERE MAANH=?";

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
