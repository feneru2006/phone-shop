package DAL.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import DTO.ChitietSPDTO;

public class CtspDAO {
    public List<ChitietSPDTO> getAll() {
        List<ChitietSPDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM ctsp";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new ChitietSPDTO(
                        rs.getString("MACTSP"),
                        rs.getString("MASP"),
                        rs.getString("MANCC"),
                        rs.getString("tinhtrang"),
                        rs.getString("MACTPN")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ChitietSPDTO> getByMaSP(String maSP) {
        List<ChitietSPDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM ctsp WHERE MASP = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maSP);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new ChitietSPDTO(
                        rs.getString("MACTSP"),
                        rs.getString("MASP"),
                        rs.getString("MANCC"),
                        rs.getString("tinhtrang"),
                        rs.getString("MACTPN")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<ChitietSPDTO> getByMaCTPN(String maCTPN) {
        List<ChitietSPDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM ctsp WHERE MACTPN = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maCTPN);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new ChitietSPDTO(
                        rs.getString("MACTSP"),
                        rs.getString("MASP"),
                        rs.getString("MANCC"),
                        rs.getString("tinhtrang"),
                        rs.getString("MACTPN")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
