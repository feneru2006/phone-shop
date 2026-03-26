package UI;

import BUS.CtspBUS;
import BUS.SanPhamBUS;
import DTO.ChitietSPDTO;
import DTO.SanPhamDTO;

// Thư viện Apache POI dùng để thao tác với Excel
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CTSPPanel extends JPanel {

    private CtspBUS ctspBus;
    private SanPhamBUS spBus;
    private List<ChitietSPDTO> currentList;
    private final List<String> perms = Arrays.asList("VIEW_CTSP", "UPDATE_CTSP", "DELETE_CTSP");

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JComboBox<String> cbSanPham, cbTinhTrang;
    private JLabel lblPageInfo, lblTotalItems;
    
    private int hoveredRow = -1;
    private int currentPage = 1;
    private final int itemsPerPage = 10; 

    private final Color PRIMARY_COLOR = Color.decode("#3B82F6"); 
    private final Color BG_COLOR = Color.decode("#F8FAFF");

    public CTSPPanel() {
        ctspBus = new CtspBUS();
        spBus = new SanPhamBUS();
        currentList = new ArrayList<>();

        setLayout(new BorderLayout(15, 15));
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { if (table != null) table.clearSelection(); }
        });

        add(buildHeader(), BorderLayout.NORTH);
        add(buildFilterPanel(), BorderLayout.WEST);
        add(buildTableSection(), BorderLayout.CENTER);

        refreshData();
        setupAutoFilter();
        
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                ctspBus = new BUS.CtspBUS(); 
                spBus = new BUS.SanPhamBUS();
                refreshData(); 
            }
        });
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        left.setOpaque(false);
        JLabel lblIcon = new JLabel("📱"); 
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        JLabel lblTitle = new JLabel("CHI TIẾT SẢN PHẨM");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.decode("#1E293B"));
        left.add(lblIcon); left.add(lblTitle);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        right.setOpaque(false);
        txtSearch = new JTextField(25);
        txtSearch.putClientProperty("JTextField.placeholderText", "Nhập mã IMEI,...");
        txtSearch.setPreferredSize(new Dimension(250, 38));
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        JButton btnExport = new JButton("EXPORT EXCEL");
        btnExport.setBackground(Color.decode("#10B981")); 
        btnExport.setForeground(Color.WHITE);
        btnExport.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnExport.setPreferredSize(new Dimension(160, 38));
        btnExport.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExport.addActionListener(e -> exportToExcel());

        right.add(txtSearch);
        right.add(btnExport);

        header.add(left, BorderLayout.WEST); header.add(right, BorderLayout.EAST);
        return header;
    }

    private JPanel buildFilterPanel() {
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setPreferredSize(new Dimension(250, 0));
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#E2E8F0"), 1, true),
            new EmptyBorder(20, 15, 20, 15)
        ));

        Dimension fieldSize = new Dimension(Integer.MAX_VALUE, 38);

        filterPanel.add(createLabel("Lọc theo Sản phẩm:"));
        cbSanPham = new JComboBox<>();
        cbSanPham.setMaximumSize(fieldSize);
        cbSanPham.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbSanPham.addItem("Tất cả Sản phẩm");
        for (SanPhamDTO sp : spBus.getAll()) {
            cbSanPham.addItem(sp.getMaSP() + " - " + sp.getTenSP());
        }
        filterPanel.add(cbSanPham); filterPanel.add(Box.createVerticalStrut(20));

        filterPanel.add(createLabel("Lọc theo Tình trạng:"));
        cbTinhTrang = new JComboBox<>();
        cbTinhTrang.setMaximumSize(fieldSize);
        cbTinhTrang.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbTinhTrang.addItem("Tất cả Tình trạng");
        
        java.util.Set<String> stSet = new java.util.LinkedHashSet<>();
        stSet.add("Sẵn có");
        stSet.add("Đã bán");
        for(ChitietSPDTO ct : ctspBus.getListCtsp()) {
            if (ct.getTinhtrang() != null && !ct.getTinhtrang().isEmpty()) {
                stSet.add(ct.getTinhtrang());
            }
        }
        for (String st : stSet) cbTinhTrang.addItem(st);
        
        filterPanel.add(cbTinhTrang); filterPanel.add(Box.createVerticalStrut(30));
        
        lblTotalItems = new JLabel("Tổng số máy: 0");
        lblTotalItems.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalItems.setForeground(PRIMARY_COLOR);
        lblTotalItems.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterPanel.add(lblTotalItems);
        return filterPanel;
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(Color.decode("#64748B"));
        lbl.setBorder(new EmptyBorder(0, 0, 8, 0));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JPanel buildTableSection() {
        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setOpaque(false);

        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setBorder(BorderFactory.createLineBorder(Color.decode("#E2E8F0"), 1, true));

        String[] cols = {"STT", "MÃ IMEI / MÃ VẠCH", "TÊN SẢN PHẨM", "TÌNH TRẠNG", "THUỘC PHIẾU NHẬP"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(Color.decode("#F8FAFC"));
        table.getTableHeader().setPreferredSize(new Dimension(0, 45));
        
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setGridColor(Color.decode("#E2E8F0"));
        table.setIntercellSpacing(new Dimension(1, 1)); 

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(280);
        table.getColumnModel().getColumn(3).setPreferredWidth(120); 
        table.getColumnModel().getColumn(4).setPreferredWidth(150);
        
        CustomRowRenderer rowRenderer = new CustomRowRenderer();
        for (int i = 0; i < 5; i++) table.getColumnModel().getColumn(i).setCellRenderer(rowRenderer);

        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (hoveredRow != row) { hoveredRow = row; table.repaint(); }
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseExited(MouseEvent e) { hoveredRow = -1; table.repaint(); }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getViewport().addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { table.clearSelection(); }
        });
        
        tableContainer.add(scroll, BorderLayout.CENTER);
        tableWrapper.add(tableContainer, BorderLayout.CENTER);
        tableWrapper.add(buildPagination(), BorderLayout.SOUTH);
        return tableWrapper;
    }

    private JPanel buildPagination() {
        JPanel pagPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pagPanel.setOpaque(false);

        lblPageInfo = new JLabel("Trang 1 / 1");
        lblPageInfo.setFont(new Font("Segoe UI", Font.BOLD, 13));

        JButton btnPrev = new JButton("«");
        btnPrev.setBackground(Color.WHITE); btnPrev.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPrev.addActionListener(e -> { if (currentPage > 1) { currentPage--; loadTableData(); }});

        JButton btnNext = new JButton("»");
        btnNext.setBackground(Color.WHITE); btnNext.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNext.addActionListener(e -> {
            int maxPage = (int) Math.ceil((double) currentList.size() / itemsPerPage);
            if (currentPage < maxPage) { currentPage++; loadTableData(); }
        });

        pagPanel.add(lblPageInfo); pagPanel.add(btnPrev); pagPanel.add(btnNext);
        return pagPanel;
    }

    private void setupAutoFilter() {
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterData(); }
            public void removeUpdate(DocumentEvent e) { filterData(); }
            public void changedUpdate(DocumentEvent e) { filterData(); }
        });
        cbSanPham.addItemListener(e -> { if (e.getStateChange() == ItemEvent.SELECTED) filterData(); });
        cbTinhTrang.addItemListener(e -> { if (e.getStateChange() == ItemEvent.SELECTED) filterData(); });
    }

    private void filterData() {
        String search = txtSearch.getText().toLowerCase();
        String spFull = cbSanPham.getSelectedItem().toString();
        String maSPFilter = spFull.equals("Tất cả Sản phẩm") ? "ALL" : spFull.split(" - ")[0];
        String tinhTrangFilter = cbTinhTrang.getSelectedItem().toString();
        if (tinhTrangFilter.equals("Tất cả Tình trạng")) tinhTrangFilter = "ALL";

        currentList = new ArrayList<>();
        
        for (ChitietSPDTO ct : ctspBus.getListCtsp()) {
            if (ct.getIsDeleted() == 0) { 
                boolean matchIMEI = search.isEmpty() || ct.getMaCTSP().toLowerCase().contains(search);
                boolean matchMaSP = maSPFilter.equals("ALL") || ct.getMaSP().equals(maSPFilter);
                boolean matchTT = tinhTrangFilter.equals("ALL") || ct.getTinhtrang().equalsIgnoreCase(tinhTrangFilter);

                if (matchIMEI && matchMaSP && matchTT) {
                    currentList.add(ct);
                }
            }
        }
        
        currentList.sort((ct1, ct2) -> {
            return Integer.compare(getPriority(ct1.getTinhtrang()), getPriority(ct2.getTinhtrang()));
        });

        currentPage = 1; 
        lblTotalItems.setText("Tổng số máy: " + currentList.size());
        loadTableData();
    }

    private int getPriority(String status) {
        if (status == null) return 5;
        switch (status) {
            case "Sẵn có": return 1;
            case "Lỗi": 
            case "Đang bảo hành":
            case "Trưng bày": return 2;
            case "Đã bán": return 3;
            case "Thất lạc":
            case "Đã hủy": return 4;
            default: return 5;
        }
    }

    public void refreshData() {
        ctspBus.docDanhSach(); 
        filterData();
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        int totalItems = currentList.size();
        int maxPage = (int) Math.ceil((double) totalItems / itemsPerPage);
        if (maxPage == 0) maxPage = 1;
        if (currentPage > maxPage) currentPage = maxPage;

        int start = (currentPage - 1) * itemsPerPage;
        int end = Math.min(start + itemsPerPage, totalItems);
        lblPageInfo.setText("Trang " + currentPage + " / " + maxPage);

        for (int i = start; i < end; i++) {
            ChitietSPDTO ct = currentList.get(i);
            SanPhamDTO sp = spBus.getById(ct.getMaSP());
            String tenSP = (sp != null) ? sp.getTenSP() : "Sản phẩm không xác định";
            
            tableModel.addRow(new Object[]{
                (i + 1), 
                ct.getMaCTSP(), 
                tenSP, 
                ct.getTinhtrang(), 
                ct.getMaCTPN() 
            });
        }
    }

    private void exportToExcel() {
        if (currentList == null || currentList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn vị trí lưu file Excel");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.endsWith(".xlsx")) filePath += ".xlsx";

            try (Workbook workbook = new XSSFWorkbook(); FileOutputStream out = new FileOutputStream(filePath)) {
                Sheet sheet = workbook.createSheet("Chi Tiet San Pham");
                
                Row headerRow = sheet.createRow(0);
                String[] columns = {"STT", "Mã IMEI", "Mã Sản Phẩm", "Tình trạng", "Mã NCC", "Mã CTPN"};
                
                CellStyle headerStyle = workbook.createCellStyle();
                org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);
                headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                for (int i = 0; i < columns.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(columns[i]);
                    cell.setCellStyle(headerStyle);
                }

                int rowNum = 1;
                for (ChitietSPDTO ct : currentList) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(rowNum - 1);
                    row.createCell(1).setCellValue(ct.getMaCTSP());
                    row.createCell(2).setCellValue(ct.getMaSP());
                    row.createCell(3).setCellValue(ct.getTinhtrang());
                    row.createCell(4).setCellValue(ct.getMaNCC() != null ? ct.getMaNCC() : "");
                    row.createCell(5).setCellValue(ct.getMaCTPN());
                }

                for (int i = 0; i < columns.length; i++) sheet.autoSizeColumn(i);
                
                workbook.write(out);
                JOptionPane.showMessageDialog(this, "Xuất file Excel thành công!\nĐã lưu tại: " + filePath, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xuất file Excel: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    class CustomRowRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            String tt = table.getValueAt(row, 3).toString();
            boolean isLocked = tt.equals("Đã bán") || tt.equals("Đã hủy") || tt.equals("Thất lạc");

            if (isSelected) c.setBackground(Color.decode("#E2E8F0")); 
            else if (row == hoveredRow && !isLocked) c.setBackground(Color.decode("#F1F5F9")); 
            else if (isLocked) c.setBackground(Color.decode("#F8FAFC")); 
            else c.setBackground(Color.WHITE);

            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.decode("#E2E8F0")));
            
            if (isLocked) {
                c.setForeground(Color.decode("#94A3B8")); 
                setFont(new Font("Segoe UI", Font.ITALIC, 13));
            } else {
                c.setForeground(Color.decode("#1E293B"));
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
            }

            if (column == 3) {
                if (!isLocked) {
                    if (tt.equals("Sẵn có")) c.setForeground(Color.decode("#10B981")); 
                    else if (tt.equals("Lỗi")) c.setForeground(Color.decode("#EF4444")); 
                    else c.setForeground(Color.decode("#F59E0B")); 
                }
                setFont(new Font("Segoe UI", Font.BOLD, 12));
            }
            
            setHorizontalAlignment(column == 0 ? JLabel.CENTER : JLabel.LEFT);
            return c;
        }
    }
    public void timKiemSanPham(String maSP) {
    if (txtSearch != null) {
        txtSearch.setText(maSP);
    }
}
}