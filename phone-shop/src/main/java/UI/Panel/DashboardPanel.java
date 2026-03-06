package UI.Panel;

import BUS.ReportService;
import DAL.DashboardDAL;
import DTO.DashboardDTO;
import DTO.ReportProduct;
import DTO.ReportRevenueRow;
import DTO.logDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
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
    
    private JLabel lblTotalProduct, lblTotalRevenue, lblTotalStaff;
    private JTable logTable, revenueDailyTable, revenueMonthlyTable, lowStockTable;
    private DefaultTableModel logModel, dailyModel, monthlyModel, lowStockModel;
    private JPanel chartLinePanel, chartPiePanel;
    private JScrollPane scrollPane;
    private JPanel mainContainer;

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.decode("#F8FAFF"));

        mainContainer = new JPanel();
        mainContainer.setLayout(new BorderLayout(15, 15));
        mainContainer.setBackground(Color.decode("#F8FAFF"));
        mainContainer.setBorder(new EmptyBorder(20, 20, 20, 20));

        initStatsCards();
        initAnalysisSection();
        initTableSection();

        scrollPane = new JScrollPane(mainContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getViewport().setBackground(Color.decode("#F8FAFF"));
        
        add(scrollPane, BorderLayout.CENTER);
        refreshData();
    }

    private void initStatsCards() {
        JPanel container = new JPanel(new GridLayout(1, 3, 15, 0));
        container.setOpaque(false);
        container.setPreferredSize(new Dimension(0, 100));

        lblTotalProduct = new JLabel("0");
        container.add(createCard("TỔNG SẢN PHẨM", lblTotalProduct, Color.decode("#3B82F6"), "📦"));

        lblTotalRevenue = new JLabel("0 VNĐ");
        container.add(createCard("DOANH THU", lblTotalRevenue, Color.decode("#10B981"), "💰"));

        lblTotalStaff = new JLabel("0");
        container.add(createCard("NHÂN VIÊN", lblTotalStaff, Color.decode("#8B5CF6"), "👤"));

        mainContainer.add(container, BorderLayout.NORTH);
    }

    private void initAnalysisSection() {
        JPanel mainAnalysis = new JPanel(new GridLayout(1, 2, 15, 0));
        mainAnalysis.setOpaque(false);
        mainAnalysis.setPreferredSize(new Dimension(0, 520)); 

        // Khối trái
        JPanel leftBox = new JPanel(new BorderLayout(0, 10));
        leftBox.setOpaque(false);
        chartLinePanel = new RoundedPanel(20, Color.WHITE);
        chartLinePanel.setLayout(new BorderLayout());
        chartLinePanel.setPreferredSize(new Dimension(0, 250));
        
        RoundedPanel dailyBox = new RoundedPanel(20, Color.WHITE);
        dailyBox.setBorder(new EmptyBorder(12, 15, 12, 15));
        dailyBox.setLayout(new BorderLayout());
        dailyBox.add(createTitleWithIcon("📊", "CHI TIẾT DOANH THU 30 NGÀY GẦN NHẤT"), BorderLayout.NORTH);
        
        dailyModel = createNonEditableModel(new String[]{"Ngày", "Doanh thu (VNĐ)"});
        revenueDailyTable = createStyledTable(dailyModel);
        dailyBox.add(new JScrollPane(revenueDailyTable), BorderLayout.CENTER);

        leftBox.add(chartLinePanel, BorderLayout.NORTH);
        leftBox.add(dailyBox, BorderLayout.CENTER);

        // Khối phải
        JPanel rightBox = new JPanel(new BorderLayout(0, 10));
        rightBox.setOpaque(false);
        chartPiePanel = new RoundedPanel(20, Color.WHITE);
        chartPiePanel.setLayout(new BorderLayout());
        chartPiePanel.setPreferredSize(new Dimension(0, 250));

        RoundedPanel monthlyBox = new RoundedPanel(20, Color.WHITE);
        monthlyBox.setBorder(new EmptyBorder(12, 15, 12, 15));
        monthlyBox.setLayout(new BorderLayout());
        monthlyBox.add(createTitleWithIcon("📈", "THỐNG KÊ DOANH THU THEO THÁNG"), BorderLayout.NORTH);

        monthlyModel = createNonEditableModel(new String[]{"Tháng/Năm", "Tổng doanh thu"});
        revenueMonthlyTable = createStyledTable(monthlyModel);
        monthlyBox.add(new JScrollPane(revenueMonthlyTable), BorderLayout.CENTER);

        rightBox.add(chartPiePanel, BorderLayout.NORTH);
        rightBox.add(monthlyBox, BorderLayout.CENTER);

        mainAnalysis.add(leftBox);
        mainAnalysis.add(rightBox);
        mainContainer.add(mainAnalysis, BorderLayout.CENTER);
    }

    private void initTableSection() {
        JPanel bottomPanel = new JPanel(new BorderLayout(15, 0));
        bottomPanel.setOpaque(false);
        bottomPanel.setPreferredSize(new Dimension(0, 250));

        // Nhật ký
        RoundedPanel logContainer = new RoundedPanel(20, Color.WHITE);
        logContainer.setLayout(new BorderLayout());
        logContainer.setBorder(new EmptyBorder(12, 15, 12, 15));
        logContainer.add(createTitleWithIcon("📝", "NHẬT KÝ HỆ THỐNG GẦN ĐÂY"), BorderLayout.NORTH);
        
        logModel = createNonEditableModel(new String[]{"Hành vi", "Thực thể", "Chi tiết", "Thời điểm"});
        logTable = createStyledTable(logModel);
        logContainer.add(new JScrollPane(logTable), BorderLayout.CENTER);
        bottomPanel.add(logContainer, BorderLayout.CENTER);

        // Tồn kho
        RoundedPanel stockContainer = new RoundedPanel(20, Color.WHITE);
        stockContainer.setPreferredSize(new Dimension(380, 0));
        stockContainer.setLayout(new BorderLayout());
        stockContainer.setBorder(new EmptyBorder(12, 15, 12, 15));
        JLabel stockTitle = createTitleWithIcon("⚠️", "CẢNH BÁO TỒN KHO (≤ 5)");
        stockTitle.setForeground(Color.RED);
        stockContainer.add(stockTitle, BorderLayout.NORTH);

        lowStockModel = createNonEditableModel(new String[]{"Tên Sản Phẩm", "Tồn kho"});
        lowStockTable = createStyledTable(lowStockModel);
        stockContainer.add(new JScrollPane(lowStockTable), BorderLayout.CENTER);
        
        bottomPanel.add(stockContainer, BorderLayout.EAST);
        mainContainer.add(bottomPanel, BorderLayout.SOUTH);
    }

    // --- HÀM TẠO MODEL KHÔNG CHO SỬA ---
    private DefaultTableModel createNonEditableModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    // --- HÀM TẠO TABLE "CHỈ NHÌN" ---
    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(32);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setReorderingAllowed(false); // Không cho kéo đổi cột
        table.getTableHeader().setResizingAllowed(false);   // Không cho chỉnh độ rộng cột
        
        // Cấu hình để KHÔNG THỂ TƯƠNG TÁC
        table.setFocusable(false);             // Không nhận tiêu điểm (không hiện viền ô)
        table.setRowSelectionAllowed(false);    // Không cho chọn hàng
        table.setCellSelectionEnabled(false);   // Không cho chọn ô (ngăn to đen)
        table.setIntercellSpacing(new Dimension(0, 0));
        
        table.setShowVerticalLines(false);
        table.setGridColor(Color.decode("#F1F5F9"));
        return table;
    }

    private JLabel createTitleWithIcon(String icon, String text) {
        JLabel label = new JLabel("<html><font face='Segoe UI Emoji'>" + icon + "</font> &nbsp;<font face='Segoe UI'>" + text + "</font></html>");
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return label;
    }

    // (Giữ nguyên các hàm refreshData, updateCharts, createCard, RoundedPanel từ bản cũ của bạn)
    // ... [Hàm refreshData, updateLineChart, updatePieChart, createCard, RoundedPanel] ...

    public void refreshData() {
        try {
            DashboardDTO dto = dashboardDAL.getDashboardData();
            if (dto != null) {
                lblTotalProduct.setText(String.valueOf(dto.getTongSanPham()));
                lblTotalStaff.setText(String.valueOf(dto.getTongNhanVien()));
                lblTotalRevenue.setText(String.format("%,.0f VNĐ", dto.getTongDoanhThu()));

                logModel.setRowCount(0);
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM HH:mm");
                for (logDTO log : dto.getDanhSachLog()) {
                    logModel.addRow(new Object[]{
                        log.getHanhvi(), log.getThucthe(), log.getChitiethv(),
                        log.getThoidiem() != null ? log.getThoidiem().format(dtf) : ""
                    });
                }
            }
            dailyModel.setRowCount(0);
            List<ReportRevenueRow> dayData = reportService.getRevenueList(LocalDate.now().minusDays(30), LocalDate.now());
            if (dayData != null) {
                for (ReportRevenueRow r : dayData) {
                    dailyModel.addRow(new Object[]{
                        r.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), 
                        String.format("%,.0f", r.getRevenue())
                    });
                }
            }
            monthlyModel.setRowCount(0);
            monthlyModel.addRow(new Object[]{"Tháng 02/2026", "142,500,000"});
            monthlyModel.addRow(new Object[]{"Tháng 01/2026", "128,000,000"});
            lowStockModel.setRowCount(0);
            lowStockModel.addRow(new Object[]{"iPhone 15 Pro Max 256GB", "2"});
            lowStockModel.addRow(new Object[]{"Sạc dự phòng Anker 20k", "5"});

            updateLineChart();
            updatePieChart();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void updateLineChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<ReportRevenueRow> data = reportService.getRevenueList(LocalDate.now().minusDays(30), LocalDate.now());
        if (data != null) {
            for (ReportRevenueRow r : data) {
                dataset.addValue(r.getRevenue(), "Doanh thu", String.valueOf(r.getDate().getDayOfMonth()));
            }
        }
        JFreeChart chart = ChartFactory.createLineChart("DOANH SỐ 30 NGÀY VỪA QUA", "", "", dataset, PlotOrientation.VERTICAL, false, true, false);
        chart.setBackgroundPaint(Color.WHITE);
        chartLinePanel.removeAll();
        chartLinePanel.add(new ChartPanel(chart), BorderLayout.CENTER);
        chartLinePanel.revalidate();
    }

    private void updatePieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        List<ReportProduct> data = reportService.getTopProducts(LocalDate.now().minusMonths(1), LocalDate.now(), 5);
        if (data != null) {
            for (ReportProduct p : data) {
                dataset.setValue(p.getProductName(), p.getTotalRevenue());
            }
        }
        JFreeChart chart = ChartFactory.createPieChart("TỶ TRỌNG SẢN PHẨM BÁN CHẠY", dataset, true, true, false);
        chart.setBackgroundPaint(Color.WHITE);
        chartPiePanel.removeAll();
        chartPiePanel.add(new ChartPanel(chart), BorderLayout.CENTER);
        chartPiePanel.revalidate();
    }

    private JPanel createCard(String title, JLabel valueLabel, Color color, String icon) {
        RoundedPanel card = new RoundedPanel(20, color);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(10, 15, 10, 15));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblTitle.setForeground(new Color(255, 255, 255, 180));
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(Color.WHITE);
        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        lblIcon.setForeground(new Color(255, 255, 255, 60));
        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(lblIcon, BorderLayout.EAST);
        return card;
    }

    class RoundedPanel extends JPanel {
        private int radius;
        private Color bgColor;
        public RoundedPanel(int radius, Color bgColor) {
            super(new BorderLayout());
            this.radius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
            g2.dispose();
            super.paintComponent(g);
        }
    }
}