package DAL.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import DTO.NCCDTO;

public class NCCDAO {
    
    public List<NCCDTO> getAll() {
        List<NCCDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM NCC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                NCCDTO ncc = new NCCDTO(
                    rs.getString("MANCC"),
                    rs.getString("ten"),
                    rs.getString("diachi"),
                    rs.getString("SDT")
                );
                ncc.setIsDeleted(rs.getInt("isDeleted")); 
                list.add(ncc);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean insert(NCCDTO ncc) {
        String sql = "INSERT INTO NCC(MANCC, ten, diachi, SDT, isDeleted) VALUES (?, ?, ?, ?, 0)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ncc.getMaNCC());
            ps.setString(2, ncc.getTen());
            ps.setString(3, ncc.getDiaChi());
            ps.setString(4, ncc.getSdt());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean update(NCCDTO ncc) {
        String sql = "UPDATE NCC SET ten=?, diachi=?, SDT=? WHERE MANCC=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ncc.getTen());
            ps.setString(2, ncc.getDiaChi());
            ps.setString(3, ncc.getSdt());
            ps.setString(4, ncc.getMaNCC());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean delete(String maNCC) {
        String sql = "UPDATE NCC SET isDeleted = 1 WHERE MANCC=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNCC);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    public boolean unlock(String maNCC) {
        String sql = "UPDATE NCC SET isDeleted = 0 WHERE MANCC=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNCC);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }
}
