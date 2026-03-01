package DAL.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import DTO.CTggDTO;

public class CTggDAO {

    // ================= INSERT =================
    public boolean insert(CTggDTO ctgg) {
        String sql = "INSERT INTO ctgg (MAGG, MASP, phantramgg) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ctgg.getMAGG());
            ps.setString(2, ctgg.getMaSP());
            ps.setInt(3, ctgg.getPhantramgg());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ================= GET ALL =================
    public List<CTggDTO> getAll() {
        List<CTggDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM ctgg";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                CTggDTO ctgg = new CTggDTO(
                        rs.getString("MAGG"),
                        rs.getString("MASP"),
                        rs.getInt("phantramgg")
                );
                list.add(ctgg);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // ================= UPDATE =================
    public boolean update(CTggDTO ctgg) {
        String sql = "UPDATE ctgg SET phantramgg=? WHERE MAGG=? AND MASP=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ctgg.getPhantramgg());
            ps.setString(2, ctgg.getMAGG());
            ps.setString(3, ctgg.getMaSP());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ================= DELETE =================
    public boolean delete(String maGG, String maSP) {
        String sql = "DELETE FROM ctgg WHERE MAGG=? AND MASP=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maGG);
            ps.setString(2, maSP);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ================= DELETE BY MAGG =================
    public boolean deleteByMAGG(String maGG) {
        String sql = "DELETE FROM ctgg WHERE MAGG=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maGG);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}