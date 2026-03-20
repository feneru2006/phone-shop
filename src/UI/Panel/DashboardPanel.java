package UI.Panel;

import BUS.ReportService;
import DAL.DashboardDAL;
import DTO.DashboardDTO;
import DTO.ReportProduct;
import DTO.ReportRevenueRow;
import DTO.logDTO;
import UI.Utils.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
    private JTable logTable, revenueDailyTable, revenueMonthlyTable, lowStockTable, productRevenueTable;
    private DefaultTableModel logModel, dailyModel, monthlyModel, lowStockModel, productModel;
    private JPanel chartLinePanel, chartPiePanel;
    private JTextField txtStartDate, txtEndDate;
    private JScrollPane scrollPane;
    private JPanel mainContainer;
    
    // Các biến hỗ trợ cho chức năng chuyển đổi Bảng doanh thu
    private JPanel revenueCards;
    private CardLayout revenueCardLayout;
    private JButton btnToggleRevenue;
    private boolean isDailyView = true;

    public static class UIDateUtils {
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        public static String format(LocalDate date) { return date != null ? date.format(FORMATTER) : ""; }
        public static LocalDate parse(String dateStr) {
            try { return LocalDate.parse(dateStr, FORMATTER); } 
            catch (DateTimeParseException e) { return null; }
        }
    }

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.decode("#F8FAFF"));

        mainContainer = new JPanel();
        mainContainer.setLayout(new BorderLayout(0, 20)); 
        mainContainer.setBackground(Color.decode("#F8FAFF"));
        mainContainer.setBorder(new EmptyBorder(5, 5, 5, 5)); 

        initStatsCards();
        initAnalysisSection(); 
        initTableSection();

        scrollPane = new JScrollPane(mainContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(Color.decode("#F8FAFF"));
        scrollPane.getViewport().setBackground(Color.decode("#F8FAFF"));
        
        add(scrollPane, BorderLayout.CENTER);

        refreshOverviewData();
        filterStatistics(); 
    }

    private void initStatsCards() {
        JPanel container = new JPanel(new GridLayout(1, 3, 18, 0)); 
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
        RoundedPanel statisticsPanel = new RoundedPanel(25, Color.decode("#FDD835"));
        statisticsPanel.setLayout(new BorderLayout(0, 15)); 
        statisticsPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); 

        // --- HEADER ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        // Gọi hàm từ UIUtils
        JPanel titlePanel = UIUtils.createTitlePanel("src/main/java/resources/fire.png", "THỐNG KÊ DOANH SỐ", 22, Color.WHITE);
        headerPanel.add(titlePanel, BorderLayout.WEST); 

        JPanel dateFilterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        dateFilterPanel.setOpaque(false);
        txtStartDate = new JTextField(UIDateUtils.format(LocalDate.now().minusDays(30)), 10);
        txtEndDate = new JTextField(UIDateUtils.format(LocalDate.now()), 10);
        JButton btnFilter = new JButton("Lọc dữ liệu");
        btnFilter.addActionListener(e -> filterStatistics());

        dateFilterPanel.add(new JLabel("Từ:"));
        dateFilterPanel.add(txtStartDate);
        dateFilterPanel.add(new JLabel("Đến:"));
        dateFilterPanel.add(txtEndDate);
        dateFilterPanel.add(btnFilter);
        headerPanel.add(dateFilterPanel, BorderLayout.EAST);
        statisticsPanel.add(headerPanel, BorderLayout.NORTH);

        // --- CONTENT ---
        JPanel contentPanel = new JPanel(new GridLayout(2, 1, 0, 20)); 
        contentPanel.setOpaque(false);

        // Hàng 1: Hai khung trắng chứa biểu đồ
        JPanel chartsRow = new JPanel(new GridLayout(1, 2, 20, 0)); 
        chartsRow.setOpaque(false);
        chartsRow.setPreferredSize(new Dimension(0, 400)); 

        RoundedPanel lineBox = new RoundedPanel(20, Color.WHITE);
        lineBox.setLayout(new BorderLayout());
        lineBox.setBorder(new EmptyBorder(15, 5, 10, 8)); 
        JPanel lineTitlePanel = UIUtils.createTitlePanel("src/main/java/resources/chart_line.png", "DOANH THU THEO NGÀY", 18, Color.BLACK);
        lineTitlePanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        chartLinePanel = new JPanel(new BorderLayout());
        chartLinePanel.setOpaque(false);
        lineBox.add(lineTitlePanel, BorderLayout.NORTH);
        lineBox.add(chartLinePanel, BorderLayout.CENTER);

        RoundedPanel pieBox = new RoundedPanel(20, Color.WHITE);
        pieBox.setLayout(new BorderLayout());
        pieBox.setBorder(new EmptyBorder(15, 15, 15, 15)); 
        JPanel pieTitlePanel = UIUtils.createTitlePanel("src/main/java/resources/chart_pie.png", "TỶ TRỌNG SẢN PHẨM", 18, Color.BLACK);
        pieTitlePanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        chartPiePanel = new JPanel(new BorderLayout());
        chartPiePanel.setOpaque(false);
        pieBox.add(pieTitlePanel, BorderLayout.NORTH);
        pieBox.add(chartPiePanel, BorderLayout.CENTER);

        chartsRow.add(lineBox);
        chartsRow.add(pieBox);

        // Hàng 2: Hai bảng chi tiết
        JPanel tablesRow = new JPanel(new GridLayout(1, 2, 20, 0)); 
        tablesRow.setOpaque(false);
        tablesRow.setPreferredSize(new Dimension(0, 280));

        // 1. Khung bên trái (Gộp doanh thu Ngày và Tháng có nút bấm)
        RoundedPanel revenueBox = new RoundedPanel(20, Color.WHITE);
        revenueBox.setLayout(new BorderLayout());
        revenueBox.setBorder(new EmptyBorder(15, 15, 15, 15)); 

        // Header bảng trái chứa Tiêu đề và Nút chuyển đổi
        JPanel revenueHeader = new JPanel(new BorderLayout());
        revenueHeader.setOpaque(false);
        JPanel revenueTitlePanel = UIUtils.createTitlePanel("src/main/java/resources/clipboard.png", "DOANH THU THEO NGÀY", 18, Color.BLACK);
        
        btnToggleRevenue = new JButton("Xem theo Tháng");
        btnToggleRevenue.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnToggleRevenue.setFocusPainted(false);
        btnToggleRevenue.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnToggleRevenue.setBackground(Color.decode("#F1F5F9")); 
        
        // Logic bấm để đổi bảng và tiêu đề
        btnToggleRevenue.addActionListener(e -> {
            isDailyView = !isDailyView;
            if (isDailyView) {
                ((JLabel) revenueTitlePanel.getComponent(1)).setText("DOANH THU THEO NGÀY");
                btnToggleRevenue.setText("Xem theo Tháng");
                revenueCardLayout.show(revenueCards, "DAILY");
            } else {
                ((JLabel) revenueTitlePanel.getComponent(1)).setText("DOANH THU THEO THÁNG");
                btnToggleRevenue.setText("Xem theo Ngày");
                revenueCardLayout.show(revenueCards, "MONTHLY");
            }
        });

        revenueHeader.add(revenueTitlePanel, BorderLayout.WEST);
        revenueHeader.add(btnToggleRevenue, BorderLayout.EAST);
        revenueHeader.setBorder(new EmptyBorder(0, 0, 10, 0));

        revenueCardLayout = new CardLayout();
        revenueCards = new JPanel(revenueCardLayout);
        revenueCards.setOpaque(false);

        dailyModel = createNonEditableModel(new String[]{"Ngày", "Doanh thu (VNĐ)"});
        revenueDailyTable = createStyledTable(dailyModel, true); 
        revenueCards.add(new JScrollPane(revenueDailyTable), "DAILY");

        monthlyModel = createNonEditableModel(new String[]{"Tháng/Năm", "Tổng doanh thu (VNĐ)"});
        revenueMonthlyTable = createStyledTable(monthlyModel, true);
        revenueCards.add(new JScrollPane(revenueMonthlyTable), "MONTHLY");

        revenueBox.add(revenueHeader, BorderLayout.NORTH);
        revenueBox.add(revenueCards, BorderLayout.CENTER);

        // 2. Khung bên phải (Doanh thu theo Sản Phẩm)
        RoundedPanel productBox = new RoundedPanel(20, Color.WHITE);
        productBox.setLayout(new BorderLayout());
        productBox.setBorder(new EmptyBorder(15, 15, 15, 15)); 
        JPanel productTitlePanel = UIUtils.createTitlePanel("src/main/java/resources/box.png", "DOANH THU TỪNG SẢN PHẨM", 18, Color.BLACK);
        productTitlePanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        productModel = createNonEditableModel(new String[]{"Tên Sản Phẩm", "Doanh thu (VNĐ)"});
        productRevenueTable = createStyledTable(productModel, true);
        
        productBox.add(productTitlePanel, BorderLayout.NORTH);
        productBox.add(new JScrollPane(productRevenueTable), BorderLayout.CENTER);

        // Thêm vào hàng tables
        tablesRow.add(revenueBox);
        tablesRow.add(productBox);

        contentPanel.add(chartsRow);
        contentPanel.add(tablesRow);
        statisticsPanel.add(contentPanel, BorderLayout.CENTER);
        mainContainer.add(statisticsPanel, BorderLayout.CENTER);
    }

    private void initTableSection() {
        JPanel bottomPanel = new JPanel(new BorderLayout(20, 0)); 
        bottomPanel.setOpaque(false);
        bottomPanel.setPreferredSize(new Dimension(0, 320)); 

        RoundedPanel logContainer = new RoundedPanel(20, Color.WHITE);
        logContainer.setLayout(new BorderLayout());
        logContainer.setBorder(new EmptyBorder(15, 15, 15, 15)); 
        JPanel logTitlePanel = UIUtils.createTitlePanel("src/main/java/resources/log.png", "NHẬT KÝ HỆ THỐNG GẦN ĐÂY", 16, Color.BLACK);
        logTitlePanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        logModel = createNonEditableModel(new String[]{"Hành vi", "Thực thể", "Chi tiết", "Thời điểm"});
        logTable = createStyledTable(logModel, false);
        logContainer.add(logTitlePanel, BorderLayout.NORTH);
        logContainer.add(new JScrollPane(logTable), BorderLayout.CENTER);
        bottomPanel.add(logContainer, BorderLayout.CENTER);

        RoundedPanel stockContainer = new RoundedPanel(20, Color.WHITE);
        stockContainer.setPreferredSize(new Dimension(480, 0));
        stockContainer.setLayout(new BorderLayout());
        stockContainer.setBorder(new EmptyBorder(15, 20, 15, 15)); 
        JPanel stockTitlePanel = UIUtils.createTitlePanel("src/main/java/resources/warning.png", "CẢNH BÁO TỒN KHO", 16, Color.RED);
        stockTitlePanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        lowStockModel = createNonEditableModel(new String[]{"Tên Sản Phẩm", "Số lượng"});
        lowStockTable = createStyledTable(lowStockModel, false);
        stockContainer.add(stockTitlePanel, BorderLayout.NORTH);
        stockContainer.add(new JScrollPane(lowStockTable), BorderLayout.CENTER);
        bottomPanel.add(stockContainer, BorderLayout.EAST);
        
        mainContainer.add(bottomPanel, BorderLayout.SOUTH);
    }

    private DefaultTableModel createNonEditableModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
    }

    private JTable createStyledTable(DefaultTableModel model, boolean showVerticalLines) {
        JTable table = new JTable(model);
        table.setRowHeight(38);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setPreferredSize(new Dimension(0, 35));
        table.setShowVerticalLines(showVerticalLines); 
        table.setGridColor(Color.decode("#E2E8F0"));
        table.getTableHeader().setReorderingAllowed(false);
        return table;
    }

    private void refreshOverviewData() {
        try {
            DashboardDTO dto = dashboardDAL.getDashboardData();
            if (dto != null) {
                lblTotalProduct.setText(String.valueOf(dto.getTongSanPham()));
                lblTotalStaff.setText(String.valueOf(dto.getTongNhanVien()));
                lblTotalRevenue.setText(String.format("%,.0f VNĐ", dto.getTongDoanhThu()));

                logModel.setRowCount(0);
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                for (logDTO log : dto.getDanhSachLog()) {
                    logModel.addRow(new Object[]{
                        log.getHanhvi(), log.getThucthe(), log.getChitiethv(),
                        log.getThoidiem() != null ? log.getThoidiem().format(dtf) : ""
                    });
                }
            }
            lowStockModel.setRowCount(0);
            List<Object[]> lowStocks = dashboardDAL.getLowStockProducts(5);
            for (Object[] row : lowStocks) { lowStockModel.addRow(row); }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void filterStatistics() {
        LocalDate startDate = UIDateUtils.parse(txtStartDate.getText().trim());
        LocalDate endDate = UIDateUtils.parse(txtEndDate.getText().trim());
        if (startDate == null || endDate == null) return;

        try {
            List<ReportRevenueRow> dayData = reportService.getRevenueList(startDate, endDate);
            dailyModel.setRowCount(0);
            if (dayData != null) {
                for (ReportRevenueRow r : dayData) {
                    dailyModel.addRow(new Object[]{ UIDateUtils.format(r.getDate()), String.format("%,.0f", r.getRevenue().doubleValue()) });
                }
            }
            
            monthlyModel.setRowCount(0);
            List<Object[]> monthData = reportService.getMonthlyRevenueList(startDate, endDate);
            if (monthData != null) {
                for (Object[] row : monthData) {
                    BigDecimal rev = (BigDecimal) row[1];
                    monthlyModel.addRow(new Object[]{ row[0], String.format("%,.0f", rev.doubleValue()) });
                }
            }
            
            productModel.setRowCount(0);
            List<ReportProduct> prodData = reportService.getTopProducts(startDate, endDate, 50); 
            if (prodData != null) {
                for (ReportProduct p : prodData) {
                    productModel.addRow(new Object[]{ 
                        p.getProductName(), 
                        String.format("%,.0f", p.getTotalRevenue().doubleValue()) 
                    });
                }
            }
            
            updateLineChart(dayData);
            updatePieChart(startDate, endDate);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void updateLineChart(List<ReportRevenueRow> dayData) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (dayData != null) {
            for (ReportRevenueRow r : dayData) {
                dataset.addValue(r.getRevenue().doubleValue(), "Doanh thu", r.getDate().format(DateTimeFormatter.ofPattern("dd/MM")));
            }
        }
        JFreeChart chart = ChartFactory.createLineChart(null, "", null, dataset, PlotOrientation.VERTICAL, false, true, false);
        chart.setBackgroundPaint(Color.WHITE); 
        chartLinePanel.removeAll();
        chartLinePanel.add(new ChartPanel(chart), BorderLayout.CENTER);
        chartLinePanel.revalidate();
    }

    private void updatePieChart(LocalDate start, LocalDate end) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        List<ReportProduct> data = reportService.getTopProducts(start, end, 5);
        if (data != null) {
            for (ReportProduct p : data) {
                dataset.setValue(p.getProductName(), p.getTotalRevenue().doubleValue());
            }
        }
        JFreeChart chart = ChartFactory.createPieChart(null, dataset, true, true, false);
        chart.setBackgroundPaint(Color.WHITE); 
        chartPiePanel.removeAll();
        chartPiePanel.add(new ChartPanel(chart), BorderLayout.CENTER);
        chartPiePanel.revalidate();
    }

    private JPanel createCard(String title, JLabel valueLabel, Color color, String icon) {
        RoundedPanel card = new RoundedPanel(22, color);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblTitle.setForeground(new Color(255, 255, 255, 200));
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(Color.WHITE);
        
        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40)); 
        lblIcon.setForeground(new Color(255, 255, 255, 70));
        
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