package DAL.DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import DTO.SanPhamDTO;

public class SanPhamDAO {

    public List<SanPhamDTO> getAll() {
        List<SanPhamDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM sanpham WHERE isDeleted = FALSE";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSet(rs));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

public List<SanPhamDTO> search(String keyword) {
    List<SanPhamDTO> list = new ArrayList<>();
    String sql = "SELECT * FROM sanpham WHERE isDeleted = FALSE " +
                 "AND (MASP LIKE ? OR tenSP LIKE ?)";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, "%" + keyword + "%");
        ps.setString(2, "%" + keyword + "%");

        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            list.add(mapResultSet(rs));
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
    return list;
}


public boolean insert(SanPhamDTO sp) {
    String sql = "INSERT INTO sanpham " +
                 "(MASP, tenSP, SLton, gia, trangthai, MAloai, cauhinh, NSX, isDeleted) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        setPreparedStatement(ps, sp);
        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}


public boolean update(SanPhamDTO sp) {
    String sql = "UPDATE sanpham SET tenSP=?, SLton=?, gia=?, trangthai=?, " +
                 "MAloai=?, cauhinh=?, NSX=?, isDeleted=? WHERE MASP=?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, sp.getTenSP());
        ps.setInt(2, sp.getSlTon());
        ps.setDouble(3, sp.getGia());
        ps.setString(4, sp.getTrangThai());
        ps.setString(5, sp.getMaLoai());
        ps.setString(6, sp.getCauHinh());
        ps.setString(7, sp.getNsx());
        ps.setBoolean(8, sp.isDeleted());
        ps.setString(9, sp.getMaSP());

        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}


    public boolean delete(String maSP) {
        String sql = "UPDATE sanpham SET isDeleted = TRUE WHERE MASP=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maSP);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    private SanPhamDTO mapResultSet(ResultSet rs) throws Exception {
        return new SanPhamDTO(
                rs.getString("MASP"),
                rs.getString("tenSP"),
                rs.getInt("SLton"),
                rs.getDouble("gia"),
                rs.getString("trangthai"),
                rs.getString("MAloai"),
                rs.getString("cauhinh"),
                rs.getString("NSX"),
                rs.getBoolean("isDeleted")
        );
    }

    private void setPreparedStatement(PreparedStatement ps, SanPhamDTO sp) throws Exception {
        ps.setString(1, sp.getMaSP());
        ps.setString(2, sp.getTenSP());
        ps.setInt(3, sp.getSlTon());
        ps.setDouble(4, sp.getGia());
        ps.setString(5, sp.getTrangThai());
        ps.setString(6, sp.getMaLoai());
        ps.setString(7, sp.getCauHinh());
        ps.setString(8, sp.getNsx());
        ps.setBoolean(9, sp.isDeleted());
    }
}
