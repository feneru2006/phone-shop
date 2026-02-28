package UI;

import BUS.ReportService;
import DAL.DashboardDAL;
import DTO.DashboardDTO;
import DTO.ReportProduct;
import DTO.ReportRevenueRow;
import DTO.logDTO;
import com.formdev.flatlaf.extras.FlatSVGIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class DashboardPanel extends JPanel {

    private final DashboardDAL dashboardDAL = new DashboardDAL();
    private final ReportService reportService = new ReportService();
    
    // Các UI Components cần cập nhật dữ liệu
    private JLabel lblTotalProduct, lblTotalRevenue, lblTotalStaff;
    private JTable logTable;
    private DefaultTableModel logModel;
    private JPanel chartLinePanel, chartPiePanel;

    public DashboardPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(Color.decode("#F8FAFF"));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 1. Top Section: Stats Cards
        initStatsCards();

        // 2. Middle Section: Charts (Line & Pie)
        initChartsSection();

        // 3. Bottom Section: Recent Logs Table
        initLogsTable();

        // Load dữ liệu từ Database
        refreshData();
    }

    private void initStatsCards() {
        JPanel container = new JPanel(new GridLayout(1, 3, 20, 0));
        container.setOpaque(false);

        // Card 1: Tổng sản phẩm (Xanh dương)
        lblTotalProduct = new JLabel("0");
        container.add(createCard("TỔNG SẢN PHẨM", lblTotalProduct, Color.decode("#3B82F6"), "📦"));

        // Card 2: Doanh thu (Xanh lá)
        lblTotalRevenue = new JLabel("0đ");
        container.add(createCard("DOANH THU", lblTotalRevenue, Color.decode("#10B981"), "💰"));

        // Card 3: Nhân viên (Tím)
        lblTotalStaff = new JLabel("0");
        container.add(createCard("NHÂN VIÊN", lblTotalStaff, Color.decode("#8B5CF6"), "👤"));

        add(container, BorderLayout.NORTH);
    }

    private void initChartsSection() {
        JPanel container = new JPanel(new GridLayout(1, 2, 20, 0));
        container.setOpaque(false);
        container.setPreferredSize(new Dimension(0, 350));

        chartLinePanel = new JPanel(new BorderLayout());
        chartLinePanel.setBackground(Color.WHITE);
        chartLinePanel.setBorder(BorderFactory.createLineBorder(Color.decode("#E2E8F0")));

        chartPiePanel = new JPanel(new BorderLayout());
        chartPiePanel.setBackground(Color.WHITE);
        chartPiePanel.setBorder(BorderFactory.createLineBorder(Color.decode("#E2E8F0")));

        container.add(chartLinePanel);
        container.add(chartPiePanel);

        add(container, BorderLayout.CENTER);
    }

    private void initLogsTable() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.decode("#E2E8F0")), 
                " NHẬT KÝ HỆ THỐNG MỚI NHẤT ", 0, 0, 
                new Font("Segoe UI", Font.BOLD, 14), Color.decode("#1E293B")));
        container.setPreferredSize(new Dimension(0, 250));

        String[] cols = {"Hành vi", "Thực thể", "Chi tiết", "Thời điểm"};
        logModel = new DefaultTableModel(cols, 0);
        logTable = new JTable(logModel);
        logTable.setRowHeight(35);
        logTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        logTable.setShowGrid(false);
        logTable.setIntercellSpacing(new Dimension(0, 0));
        
        JScrollPane scroll = new JScrollPane(logTable);
        scroll.setBorder(new EmptyBorder(10, 10, 10, 10));
        scroll.getViewport().setBackground(Color.WHITE);
        
        container.add(scroll, BorderLayout.CENTER);
        add(container, BorderLayout.SOUTH);
    }

    private void refreshData() {
        // Lấy dữ liệu từ DAL
        DashboardDTO dto = dashboardDAL.getDashboardData();
        lblTotalProduct.setText(String.valueOf(dto.getTongSanPham()));
        lblTotalStaff.setText(String.valueOf(dto.getTongNhanVien()));
        lblTotalRevenue.setText(String.format("%,.0f VNĐ", dto.getTongDoanhThu()));

        // Cập nhật Nhật ký
        logModel.setRowCount(0);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (logDTO log : dto.getDanhSachLog()) {
            logModel.addRow(new Object[]{
                log.getHanhvi(), log.getThucthe(), log.getChitiethv(), 
                log.getThoidiem() != null ? log.getThoidiem().format(dtf) : ""
            });
        }

        // Cập nhật Biểu đồ Đường (30 ngày gần nhất)
        updateLineChart();
        
        // Cập nhật Biểu đồ Tròn (Doanh thu theo sản phẩm)
        updatePieChart();
    }

    private void updateLineChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        LocalDate now = LocalDate.now();
        List<ReportRevenueRow> data = reportService.getRevenueList(now.minusDays(30), now);
        
        for (ReportRevenueRow row : data) {
            dataset.addValue(row.getRevenue(), "Doanh thu", row.getDate().getDayOfMonth() + "");
        }

        JFreeChart lineChart = ChartFactory.createLineChart(
                "Biểu đồ doanh thu 30 ngày gần nhất", "Ngày", "VNĐ",
                dataset, PlotOrientation.VERTICAL, false, true, false);
        
        lineChart.setBackgroundPaint(Color.WHITE);
        chartLinePanel.removeAll();
        chartLinePanel.add(new ChartPanel(lineChart), BorderLayout.CENTER);
        chartLinePanel.revalidate();
    }

    private void updatePieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        List<ReportProduct> data = reportService.getTopProducts(LocalDate.now().minusMonths(1), LocalDate.now(), 5);
        
        for (ReportProduct p : data) {
            dataset.setValue(p.getProductName(), p.getTotalRevenue());
        }

        JFreeChart pieChart = ChartFactory.createPieChart("Tỷ lệ doanh thu Top 5 sản phẩm", dataset, true, true, false);
        pieChart.setBackgroundPaint(Color.WHITE);
        chartPiePanel.removeAll();
        chartPiePanel.add(new ChartPanel(pieChart), BorderLayout.CENTER);
        chartPiePanel.revalidate();
    }

    private JPanel createCard(String title, JLabel valueLabel, Color color, String icon) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(new Color(255, 255, 255, 200));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(Color.WHITE);

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        lblIcon.setForeground(new Color(255, 255, 255, 100));

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(lblIcon, BorderLayout.EAST);

        return card;
    }
}