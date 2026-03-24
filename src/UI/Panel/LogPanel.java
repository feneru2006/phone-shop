package UI.Panel;

import BUS.LogBUS;
import DTO.logDTO;
import UI.Utils.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class LogPanel extends JPanel {
    private JTable tableLog;
    private DefaultTableModel modelLog;
    private LogBUS logBUS;
    private JTextField txtTimKiem;
    private JButton btnTimKiem, btnLamMoi;

    public LogPanel(String iconPath) {
        logBUS = new LogBUS();
        initUI(iconPath);
        loadDataToTable(logBUS.getAll());
        new Utility.AutoRefresh(30000, () -> { 
        loadDataToTable(logBUS.getAll()); 
        }).start();
    }

    private void initUI(String iconPath) {
        setLayout(new BorderLayout(15, 10));
        setBackground(Color.decode("#F8FAFF"));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        // Sử dụng UIUtils với icon truyền từ bên ngoài vào
        JPanel titlePanel = UIUtils.createTitlePanel(iconPath, "NHẬT KÝ HỆ THỐNG", 24, Color.decode("#1E293B"));
        topPanel.add(titlePanel, BorderLayout.NORTH);

        JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        toolPanel.setOpaque(false);

        txtTimKiem = new JTextField(25);
        txtTimKiem.setPreferredSize(new Dimension(300, 38));
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTimKiem.putClientProperty("JTextField.placeholderText", "Tìm kiếm theo tài khoản/Hành vi");
        txtTimKiem.addActionListener(e -> handleTimKiem());

        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setBackground(Color.decode("#3B82F6"));
        btnTimKiem.setForeground(Color.WHITE);
        styleButton(btnTimKiem);
        btnTimKiem.addActionListener(e -> handleTimKiem());

        btnLamMoi = new JButton("Làm mới");
        btnLamMoi.setBackground(Color.decode("#10B981"));
        btnLamMoi.setForeground(Color.WHITE);
        styleButton(btnLamMoi);
        btnLamMoi.addActionListener(e -> {
            txtTimKiem.setText("");
            loadDataToTable(logBUS.getAll());
        });

        toolPanel.add(txtTimKiem);
        toolPanel.add(btnTimKiem);
        toolPanel.add(btnLamMoi);
        topPanel.add(toolPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        String[] columns = {"Mã Log", "Tài Khoản", "Hành Vi", "Thực Thể", "Chi Tiết", "Thời Gian"};
        modelLog = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        tableLog = new JTable(modelLog);
        setupTableStyle(tableLog);

        JScrollPane scrollPane = new JScrollPane(tableLog);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.decode("#E2E8F0")));
        
        add(scrollPane, BorderLayout.CENTER);
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(120, 38));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void setupTableStyle(JTable table) {
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setPreferredSize(new Dimension(0, 45));
        table.getTableHeader().setBackground(Color.decode("#F1F5F9"));
        table.setShowVerticalLines(true);
        table.setGridColor(Color.decode("#E2E8F0"));
        
        // YÊU CẦU: Khóa tính năng kéo (đổi chỗ) và thay đổi kích thước cột
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);

        TableColumnModel cm = table.getColumnModel();
        cm.getColumn(0).setPreferredWidth(80); 
        cm.getColumn(1).setPreferredWidth(100); 
        cm.getColumn(2).setPreferredWidth(120); 
        cm.getColumn(3).setPreferredWidth(120); 
        cm.getColumn(4).setPreferredWidth(400); 
        cm.getColumn(5).setPreferredWidth(150); 
        
        cm.getColumn(0).setCellRenderer(centerRenderer);
        cm.getColumn(1).setCellRenderer(centerRenderer);
        cm.getColumn(5).setCellRenderer(centerRenderer);
    }

    private void loadDataToTable(List<logDTO> list) {
        modelLog.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        for (logDTO log : list) {
            modelLog.addRow(new Object[]{
                    log.getMalog(),
                    log.getAccountid(),
                    log.getHanhvi(),
                    log.getThucthe(),
                    log.getChitiethv(),
                    log.getThoidiem().format(formatter)
            });
        }
    }

    private void handleTimKiem() {
        String keyword = txtTimKiem.getText().trim().toLowerCase();
        if (keyword.isEmpty()) {
            loadDataToTable(logBUS.getAll());
            return;
        }

        List<logDTO> allLogs = logBUS.getAll();
        List<logDTO> filteredLogs = new java.util.ArrayList<>();

        for (logDTO log : allLogs) {
            if (log.getAccountid().toLowerCase().contains(keyword) ||
                log.getHanhvi().toLowerCase().contains(keyword) ||
                log.getThucthe().toLowerCase().contains(keyword) ||
                log.getChitiethv().toLowerCase().contains(keyword)) {
                filteredLogs.add(log);
            }
        }
        loadDataToTable(filteredLogs);
    }
}