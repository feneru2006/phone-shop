
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
                list.add(new NCCDTO(
                    rs.getString("MANCC"),
                    rs.getString("ten"),
                    rs.getString("diachi"),
                    rs.getString("SDT")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(NCCDTO ncc) {
        String sql = "INSERT INTO NCC(MANCC, ten, diachi, SDT) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setString(1, ncc.getMaNCC());
            ps.setString(2, ncc.getTen());
            ps.setString(3, ncc.getDiaChi());
            ps.setString(4, ncc.getSdt());
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(String maNCC) {
        String sql = "DELETE FROM NCC WHERE MANCC=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setString(1, maNCC);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
