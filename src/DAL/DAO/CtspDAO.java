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

    public boolean insert(ChitietSPDTO ctsp) {
        String sql = "INSERT INTO ctsp(MACTSP, MASP, MANCC, tinhtrang, MACTPN) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ctsp.getMaCTSP());
            ps.setString(2, ctsp.getMaSP());
            ps.setString(3, ctsp.getMaNCC());
            ps.setString(4, ctsp.getTinhtrang());
            ps.setString(5, ctsp.getMaCTPN());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(ChitietSPDTO ctsp) {
        String sql = "UPDATE ctsp SET MASP=?, MANCC=?, tinhtrang=?, MACTPN=? WHERE MACTSP=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ctsp.getMaSP());
            ps.setString(2, ctsp.getMaNCC());
            ps.setString(3, ctsp.getTinhtrang()); 
            ps.setString(4, ctsp.getMaCTPN());
            ps.setString(5, ctsp.getMaCTSP());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String maCTSP) {
        String sql = "DELETE FROM ctsp WHERE MACTSP=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maCTSP);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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