package DAL.DAO;


import DAL.DAO.DBConnection;
import DTO.ReportProduct;
import DTO.ReportRevenueRow;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

public class ReportDAO {

    public List<ReportRevenueRow> getRevenueReport(LocalDate fromDate, LocalDate toDate) throws SQLException {
        List<ReportRevenueRow> list = new ArrayList<>();
        // SQL nối hoadon -> CTHD -> ctsp -> CTphieunhap để lấy giá nhập (cost)
        String sql = "SELECT DATE(h.ngaylap) AS report_date, " +
                     "SUM(cthd.Thanhtien) AS total_revenue, " +
                     "SUM(cpn.dongia) AS total_cost " +
                     "FROM hoadon h " +
                     "JOIN CTHD cthd ON h.MAHD = cthd.MAHD " +
                     "JOIN ctsp c ON cthd.MACTSP = c.MACTSP " +
                     "JOIN CTphieunhap cpn ON c.MACTPN = cpn.MACTPN " +
                     "WHERE h.ngaylap >= ? AND h.ngaylap < ? " +
                     "GROUP BY DATE(h.ngaylap) ORDER BY report_date";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(fromDate.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(toDate.plusDays(1).atStartOfDay()));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                BigDecimal rev = rs.getBigDecimal("total_revenue");
                BigDecimal cost = rs.getBigDecimal("total_cost");
                list.add(new ReportRevenueRow(
                    rs.getDate("report_date").toLocalDate(),
                    rev, cost, rev.subtract(cost) // profit = revenue - cost
                ));
            }
        }
        return list;
    }

    public List<ReportProduct> getTopProducts(LocalDate fromDate, LocalDate toDate, int limit) throws SQLException {
        List<ReportProduct> list = new ArrayList<>();
        String sql = "SELECT s.MASP, s.tenSP, COUNT(cthd.MACTSP) AS qty, SUM(cthd.Thanhtien) AS rev " +
                     "FROM CTHD cthd " +
                     "JOIN hoadon h ON cthd.MAHD = h.MAHD " +
                     "JOIN ctsp c ON cthd.MACTSP = c.MACTSP " +
                     "JOIN sanpham s ON c.MASP = s.MASP " +
                     "WHERE h.ngaylap >= ? AND h.ngaylap < ? " +
                     "GROUP BY s.MASP, s.tenSP ORDER BY rev DESC LIMIT ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(fromDate.atStartOfDay()));
            ps.setTimestamp(2, Timestamp.valueOf(toDate.plusDays(1).atStartOfDay()));
            ps.setInt(3, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ReportProduct(
                    rs.getString("MASP"),
                    rs.getString("tenSP"),
                    rs.getLong("qty"),
                    rs.getBigDecimal("rev")
                ));
            }
        }
        return list;
    }
}