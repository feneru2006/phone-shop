package DTO;


import java.math.BigDecimal;
import java.time.LocalDate;

public class ReportRevenueRow {
    private LocalDate date;
    private BigDecimal revenue;
    private BigDecimal cost;
    private BigDecimal profit;

    public ReportRevenueRow(LocalDate date, BigDecimal revenue, BigDecimal cost, BigDecimal profit) {
        this.date = date;
        this.revenue = revenue != null ? revenue : BigDecimal.ZERO;
        this.cost = cost != null ? cost : BigDecimal.ZERO;
        this.profit = profit != null ? profit : BigDecimal.ZERO;
    }

    // Getters/Setters giữ nguyên như file bạn đã upload
    public LocalDate getDate() { return date; }
    public BigDecimal getRevenue() { return revenue; }
    public BigDecimal getCost() { return cost; }
    public BigDecimal getProfit() { return profit; }
}