package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import DTO.hangsxDTO;

public class HangSXDAO {

    public List<hangsxDTO> getAll() {
        List<hangsxDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM hangsx";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                hangsxDTO hang = new hangsxDTO(
                        rs.getString("MANSX"),
                        rs.getString("TENTH")
                );
                list.add(hang);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean insert(hangsxDTO hang) {
        String sql = "INSERT INTO hangsx VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, hang.getMaNSX());
            ps.setString(2, hang.getTenTH());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(hangsxDTO hang) {
        String sql = "UPDATE hangsx SET TENTH=? WHERE MANSX=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, hang.getTenTH());
            ps.setString(2, hang.getMaNSX());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(String maNSX) {
        String sql = "DELETE FROM hangsx WHERE MANSX=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maNSX);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<hangsxDTO> search(String keyword) {
        List<hangsxDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM hangsx WHERE MANSX LIKE ? OR TENTH LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new hangsxDTO(
                        rs.getString("MANSX"),
                        rs.getString("TENTH")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}
