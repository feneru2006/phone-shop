package BUS;


import DAL.DAO.ReportDAO;
import DTO.ReportProduct;
import DTO.ReportRevenueRow;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class ReportService {
    private final ReportDAO reportDAO = new ReportDAO();

    public List<ReportRevenueRow> getRevenueList(LocalDate from, LocalDate to) {
        try {
            return reportDAO.getRevenueReport(from, to);
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải báo cáo doanh thu", e);
        }
    }

    public List<ReportProduct> getTopProducts(LocalDate from, LocalDate to, int limit) {
        try {
            return reportDAO.getTopProducts(from, to, limit);
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi tải top sản phẩm", e);
        }
    }
}