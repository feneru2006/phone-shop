package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import DTO.SanPhamDTO;

public class SanPhamDAO {

    public boolean insert(SanPhamDTO sp) {
        String sql = "INSERT INTO sanpham(maSP, tenSP, slTon, gia, trangThai, maLoai, cauHinh, nsx) "
           + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";


        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sp.getMaSP());
            ps.setString(2, sp.getTenSP());
            ps.setInt(3, sp.getSlTon());
            ps.setDouble(4, sp.getGia());
            ps.setString(5, sp.getTrangThai());
            ps.setString(6, sp.getMaLoai());
            ps.setString(7, sp.getCauHinh());
            ps.setString(8, sp.getNsx());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<SanPhamDTO> getAll() {
        List<SanPhamDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM sanpham";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                SanPhamDTO sp = new SanPhamDTO(
                        rs.getString("maSP"),
                        rs.getString("tenSP"),
                        rs.getInt("slTon"),
                        rs.getDouble("gia"),
                        rs.getString("trangThai"),
                        rs.getString("maLoai"),
                        rs.getString("cauHinh"),
                        rs.getString("nsx")
                );
                list.add(sp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public boolean update(SanPhamDTO sp) {
    String sql = "UPDATE sanpham SET tenSP=?, slTon=?, gia=?, trangThai=?, maLoai=?, cauHinh=?, nsx=? WHERE maSP=?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, sp.getTenSP());
        ps.setInt(2, sp.getSlTon());
        ps.setDouble(3, sp.getGia());
        ps.setString(4, sp.getTrangThai());
        ps.setString(5, sp.getMaLoai());
        ps.setString(6, sp.getCauHinh());
        ps.setString(7, sp.getNsx());
        ps.setString(8, sp.getMaSP());

        return ps.executeUpdate() > 0;

    } catch (Exception e) {
        e.printStackTrace();
    }
    return false;
}

    public boolean delete(String maSP) {
        String sql = "UPDATE sanpham SET trangThai = 'INACTIVE' WHERE maSP = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, maSP);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<SanPhamDTO> search(String keyword) {
        List<SanPhamDTO> list = new ArrayList<>();
        String sql = "SELECT * FROM sanpham WHERE tenSP LIKE ? OR maSP LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                SanPhamDTO sp = new SanPhamDTO(
                        rs.getString("maSP"),
                        rs.getString("tenSP"),
                        rs.getInt("slTon"),
                        rs.getDouble("gia"),
                        rs.getString("trangThai"),
                        rs.getString("maLoai"),
                        rs.getString("cauHinh"),
                        rs.getString("nsx")
                );
                list.add(sp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    public List<SanPhamDTO> getSanPhamSapHetHang(int minStock) {
    List<SanPhamDTO> list = new ArrayList<>();
    String sql = "SELECT * FROM sanpham WHERE slTon <= ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, minStock);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            SanPhamDTO sp = new SanPhamDTO(
                    rs.getString("maSP"),
                    rs.getString("tenSP"),
                    rs.getInt("slTon"),
                    rs.getDouble("gia"),
                    rs.getString("trangThai"),
                    rs.getString("maLoai"),
                    rs.getString("cauHinh"),
                    rs.getString("nsx")
            );
            list.add(sp);
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return list;
}


}
