package DAL.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import DTO.loaiDTO;

public class loaiDAO {

    public List<loaiDTO> getAll() {

        List<loaiDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM loai";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new loaiDTO(
                        rs.getString("MAloai"),
                        rs.getString("danhmuc")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean insert(loaiDTO loai) {

        String sql = "INSERT INTO loai (MAloai, danhmuc) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, loai.getMaLoai());
            ps.setString(2, loai.getDanhMuc());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean update(loaiDTO loai) {

        String sql = "UPDATE loai SET danhmuc=? WHERE MAloai=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, loai.getDanhMuc());
            ps.setString(2, loai.getMaLoai());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean delete(String maLoai) {

        String sql = "DELETE FROM loai WHERE MAloai=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maLoai);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
