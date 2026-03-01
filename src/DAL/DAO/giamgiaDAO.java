package DAL.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import DTO.giamgiaDTO;

public class giamgiaDAO {

    // ================= INSERT =================
    public boolean insert(giamgiaDTO gg) {
        String sql = "INSERT INTO ctgg (MAGG, dotGG, batdau, ketthuc) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, gg.getMAGG());
            ps.setString(2, gg.getdotGG());

            // LocalDateTime -> Timestamp
            ps.setTimestamp(3, Timestamp.valueOf(gg.getBatdau()));
            ps.setTimestamp(4, Timestamp.valueOf(gg.getKetthuc()));

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ================= GET ALL =================
    public List<giamgiaDTO> getAll() {
        List<giamgiaDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM ctgg";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                giamgiaDTO gg = new giamgiaDTO(
                        rs.getString("MAGG"),
                        rs.getString("dotGG"),
                        rs.getTimestamp("batdau").toLocalDateTime(),
                        rs.getTimestamp("ketthuc").toLocalDateTime()
                );
                list.add(gg);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // ================= UPDATE =================
    public boolean update(giamgiaDTO gg) {
        String sql = "UPDATE ctgg SET dotGG=?, batdau=?, ketthuc=? WHERE MAGG=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, gg.getdotGG());
            ps.setTimestamp(2, Timestamp.valueOf(gg.getBatdau()));
            ps.setTimestamp(3, Timestamp.valueOf(gg.getKetthuc()));
            ps.setString(4, gg.getMAGG());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ================= DELETE =================
    public boolean delete(String maGG) {
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

    // ================= FIND BY ID =================
    public giamgiaDTO findById(String maGG) {
        String sql = "SELECT * FROM ctgg WHERE MAGG = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maGG);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new giamgiaDTO(
                        rs.getString("MAGG"),
                        rs.getString("dotGG"),
                        rs.getTimestamp("batdau").toLocalDateTime(),
                        rs.getTimestamp("ketthuc").toLocalDateTime()
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}