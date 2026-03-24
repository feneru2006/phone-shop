package UI.Panel.PN;

import BUS.CtspBUS;
import BUS.NCCBUS;
import BUS.PhieuNhapBUS;
import BUS.SanPhamBUS;
import DTO.CTphieunhapDTO;
import DTO.ChitietSPDTO;
import DTO.SanPhamDTO;
import DTO.phieunhapDTO;
import DTO.NCCDTO;
import DTO.nhanvienDTO; 

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.*;
import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PNPanel extends JPanel {

    private PhieuNhapBUS pnBus;
    private NCCBUS nccBus;
    private BUS.NhanVienBUS nvBus; 
    private BUS.SanPhamBUS spBus;
    private BUS.CtspBUS ctspBus;
    private List<phieunhapDTO> currentList;
    private final List<String> perms = Arrays.asList("VIEW_NHAPHANG", "CREATE_NHAPHANG", "DELETE_NHAPHANG", "UPDATE_NHAPHANG");

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch, txtTuNgay, txtDenNgay, txtTuTien, txtDenTien;
    private JComboBox<String> cbNCC, cbNV;
    private JLabel lblPageInfo;
    
    private int hoveredRow = -1;
    private int currentPage = 1;
    private final int itemsPerPage = 10;

    private final Color PRIMARY_COLOR = Color.decode("#10B981");
    private final Color BG_COLOR = Color.decode("#F8FAFF");

    public PNPanel() {
        pnBus = new PhieuNhapBUS();
        nccBus = new NCCBUS();
        nvBus = new BUS.NhanVienBUS();
        spBus = new SanPhamBUS();
        ctspBus = new CtspBUS();
        currentList = pnBus.getListPhieuNhap();

        setLayout(new BorderLayout(15, 15));
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildFilterPanel(), BorderLayout.WEST);
        add(buildTableSection(), BorderLayout.CENTER);

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                refreshData(); 
            }
        });

        loadTableData();
        setupAutoFilter();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        left.setOpaque(false);
        JLabel lblIcon = new JLabel("📥");
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        lblIcon.setForeground(PRIMARY_COLOR);
        JLabel lblTitle = new JLabel("PHIẾU NHẬP HÀNG");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        left.add(lblIcon); left.add(lblTitle);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setOpaque(false);
        txtSearch = new JTextField(20);
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm mã phiếu, nhân viên...");
        txtSearch.setPreferredSize(new Dimension(200, 38));
        
        JButton btnImport = new JButton("📤 IMPORT EXCEL");
        btnImport.setBackground(Color.decode("#10B981"));
        btnImport.setForeground(Color.WHITE);
        btnImport.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnImport.setPreferredSize(new Dimension(140, 38));
        btnImport.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnImport.addActionListener(e -> importExcel());

        JButton btnAdd = new JButton("+ THÊM THỦ CÔNG");
        btnAdd.setBackground(Color.decode("#3B82F6"));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAdd.setPreferredSize(new Dimension(150, 38));
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(e -> {
            nccBus = new NCCBUS(); 
            pnBus = new PhieuNhapBUS();
            new PNAddDialog(this, pnBus, nccBus, perms).setVisible(true);
            refreshData();
        });

        right.add(txtSearch); 
        right.add(btnImport); 
        right.add(btnAdd);
        header.add(left, BorderLayout.WEST); header.add(right, BorderLayout.EAST);
        return header;
    }

    private void importExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file Excel Phiếu Nhập Hàng");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Excel Files (*.xlsx)", "xlsx"));
        if (fileChooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = fileChooser.getSelectedFile();
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            
            List<CTphieunhapDTO> listCTPN = new ArrayList<>();
            List<ChitietSPDTO> listIMEI = new ArrayList<>();
            double totalTien = 0;
            String maNV = "", maNCC = "";

            int maxPN = 0;
            for (phieunhapDTO pn : pnBus.getListPhieuNhap()) {
                try {
                    int num = Integer.parseInt(pn.getMaPNH().replace("PN", ""));
                    if (num > maxPN) maxPN = num;
                } catch (Exception ignored) {}
            }
            String newMaPN = String.format("PN%02d", maxPN + 1);

            int maxCTPN = 0;
            for(ChitietSPDTO ct : ctspBus.getListCtsp()) {
                if(ct.getMaCTPN() != null && ct.getMaCTPN().startsWith("CTPN")) {
                    try {
                        int num = Integer.parseInt(ct.getMaCTPN().replace("CTPN", ""));
                        if (num > maxCTPN) maxCTPN = num;
                    } catch(Exception ignored){}
                }
            }

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String maSP = formatter.formatCellValue(row.getCell(1)).trim();
                String tenSPExcel = formatter.formatCellValue(row.getCell(2)).trim();
                String donGiaStr = formatter.formatCellValue(row.getCell(3)).trim();
                String soLuongStr = formatter.formatCellValue(row.getCell(4)).trim();
                String imei = formatter.formatCellValue(row.getCell(5)).trim();
                
                String currentRowNV = formatter.formatCellValue(row.getCell(6)).trim();
                String currentRowNCC = formatter.formatCellValue(row.getCell(7)).trim();

                if (maSP.isEmpty() || imei.isEmpty() || soLuongStr.isEmpty()) continue; 

                if (maNV.isEmpty()) {
                    maNV = currentRowNV;
                } else if (!maNV.equals(currentRowNV)) {
                    JOptionPane.showMessageDialog(this, "Lỗi Dòng " + (i+1) + ": Mã Nhân viên không đồng nhất! Một phiếu nhập chỉ được tạo bởi 1 Nhân viên duy nhất.", "Lỗi Validate", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (maNCC.isEmpty()) {
                    maNCC = currentRowNCC;
                } else if (!maNCC.equals(currentRowNCC)) {
                    JOptionPane.showMessageDialog(this, "Lỗi Dòng " + (i+1) + ": Mã Nhà cung cấp không đồng nhất! Một phiếu nhập chỉ được nhập từ 1 Nhà cung cấp duy nhất.", "Lỗi Validate", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int sl = 0;
                try { sl = (int) Double.parseDouble(soLuongStr); } 
                catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Lỗi Dòng " + (i+1) + ": Cột Số lượng không hợp lệ!", "Lỗi Validate", JOptionPane.ERROR_MESSAGE); return;
                }
                if (sl <= 0) continue;

                SanPhamDTO spDB = spBus.getById(maSP);
                if (spDB == null || !spDB.getTenSP().equalsIgnoreCase(tenSPExcel)) {
                    JOptionPane.showMessageDialog(this, "Lỗi Dòng " + (i+1) + ": Mã SP [" + maSP + "] không tồn tại hoặc sai Tên!", "Lỗi Validate", JOptionPane.ERROR_MESSAGE); return; 
                }

                double giaExcel = Double.parseDouble(donGiaStr);
                if (giaExcel != spDB.getGia()) {
                    JOptionPane.showMessageDialog(this, "Lỗi Dòng " + (i+1) + ": Đơn giá KHÔNG KHỚP giá CSDL (" + spDB.getGia() + ")!", "Lỗi Validate", JOptionPane.ERROR_MESSAGE); return;
                }

                NCCDTO nccDB = nccBus.timNccTheoTen(nccBus.layTenNccTheoMa(maNCC));
                if(nccDB == null || nccDB.getIsDeleted() == 1) {
                     JOptionPane.showMessageDialog(this, "Lỗi Dòng " + (i+1) + ": Nhà cung cấp không hợp lệ hoặc đang bị khóa!", "Lỗi Validate", JOptionPane.ERROR_MESSAGE); return; 
                }

                if (!imei.toUpperCase().startsWith("IMEI")) {
                    JOptionPane.showMessageDialog(this, "Lỗi Dòng " + (i+1) + ": IMEI phải bắt đầu bằng 'IMEI'!", "Lỗi Validate", JOptionPane.ERROR_MESSAGE); return;
                }
                
                long startImeiNum = 0;
                try { startImeiNum = Long.parseLong(imei.substring(4)); } 
                catch (Exception e) { JOptionPane.showMessageDialog(this, "Lỗi Dòng " + (i+1) + ": Phần sau 'IMEI' phải là số!", "Lỗi Validate", JOptionPane.ERROR_MESSAGE); return; }

                boolean foundCTPN = false;
                String assignedCTPN = "";
                for (CTphieunhapDTO ctpn : listCTPN) {
                    if (ctpn.getMaSP().equals(maSP)) {
                        ctpn.setSl(ctpn.getSl() + sl); 
                        ctpn.setThanhTien(ctpn.getThanhTien() + (giaExcel * sl));
                        assignedCTPN = ctpn.getMaCTPN();
                        foundCTPN = true;
                        break;
                    }
                }
                
                if (!foundCTPN) {
                    maxCTPN++;
                    assignedCTPN = String.format("CTPN%02d", maxCTPN);
                    listCTPN.add(new CTphieunhapDTO(assignedCTPN, newMaPN, maSP, sl, giaExcel, giaExcel * sl));
                }

                for (int k = 0; k < sl; k++) {
                    String currentImei = "IMEI" + (startImeiNum + k);
                    boolean isImeiExistDB = ctspBus.getListCtsp().stream().anyMatch(ct -> ct.getMaCTSP().equalsIgnoreCase(currentImei));
                    boolean isImeiExistInFile = listIMEI.stream().anyMatch(ct -> ct.getMaCTSP().equalsIgnoreCase(currentImei));
                    if (isImeiExistDB || isImeiExistInFile) {
                        JOptionPane.showMessageDialog(this, "Lỗi Dòng " + (i+1) + ": Mã [" + currentImei + "] đã bị trùng!", "Lỗi Validate", JOptionPane.ERROR_MESSAGE); return; 
                    }

                    ChitietSPDTO ctsp = new ChitietSPDTO();
                    ctsp.setMaCTSP(currentImei);
                    ctsp.setMaSP(maSP);
                    ctsp.setMaCTPN(assignedCTPN); 
                    ctsp.setMaNCC(maNCC);
                    ctsp.setTinhtrang("Sẵn có");
                    listIMEI.add(ctsp);
                }
                totalTien += (giaExcel * sl);
            }

            if (listCTPN.isEmpty()) {
                JOptionPane.showMessageDialog(this, "File Excel rỗng!", "Lỗi", JOptionPane.WARNING_MESSAGE); return;
            }

            phieunhapDTO pn = new phieunhapDTO(newMaPN, maNV, LocalDateTime.now(), totalTien, maNCC);
            if (pnBus.nhapHangVaoKho(pn, listCTPN, listIMEI, perms)) {
                JOptionPane.showMessageDialog(this, "Import File Excel thành công! Đã tạo " + newMaPN);
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi Database!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi đọc file: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel buildFilterPanel() {
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setPreferredSize(new Dimension(240, 0));
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#E2E8F0"), 1, true),
            new EmptyBorder(15, 15, 15, 15)
        ));

        Dimension fieldSize = new Dimension(Integer.MAX_VALUE, 35);

        filterPanel.add(createLabel("Nhà cung cấp:"));
        cbNCC = new JComboBox<>();
        cbNCC.setMaximumSize(fieldSize);
        cbNCC.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbNCC.addItem("Tất cả");
        for (NCCDTO ncc : nccBus.getListNCC()) cbNCC.addItem(ncc.getTen());
        filterPanel.add(cbNCC); filterPanel.add(Box.createVerticalStrut(15));

        filterPanel.add(createLabel("Nhân viên nhập:"));
        cbNV = new JComboBox<>(); 
        cbNV.setMaximumSize(fieldSize);
        cbNV.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbNV.addItem("Tất cả");
        for (nhanvienDTO nv : nvBus.getAll()) cbNV.addItem(nv.getHoTen());
        filterPanel.add(cbNV); filterPanel.add(Box.createVerticalStrut(15));

        filterPanel.add(createLabel("Từ ngày (dd/MM/yyyy):"));
        JPanel pTuNgay = new JPanel(new BorderLayout()); pTuNgay.setOpaque(false);
        txtTuNgay = new JTextField();
        JButton btnCal1 = new JButton("📅"); btnCal1.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCal1.addActionListener(e -> showDatePicker(txtTuNgay)); 
        pTuNgay.add(txtTuNgay, BorderLayout.CENTER); pTuNgay.add(btnCal1, BorderLayout.EAST);
        pTuNgay.setMaximumSize(fieldSize);
        pTuNgay.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterPanel.add(pTuNgay); filterPanel.add(Box.createVerticalStrut(15));

        filterPanel.add(createLabel("Đến ngày (dd/MM/yyyy):"));
        JPanel pDenNgay = new JPanel(new BorderLayout()); pDenNgay.setOpaque(false);
        txtDenNgay = new JTextField();
        JButton btnCal2 = new JButton("📅"); btnCal2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCal2.addActionListener(e -> showDatePicker(txtDenNgay));
        pDenNgay.add(txtDenNgay, BorderLayout.CENTER); pDenNgay.add(btnCal2, BorderLayout.EAST);
        pDenNgay.setMaximumSize(fieldSize);
        pDenNgay.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterPanel.add(pDenNgay); filterPanel.add(Box.createVerticalStrut(15));

        filterPanel.add(createLabel("Từ số tiền (VNĐ):"));
        txtTuTien = new JTextField(); 
        txtTuTien.setMaximumSize(fieldSize);
        txtTuTien.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterPanel.add(txtTuTien); filterPanel.add(Box.createVerticalStrut(15));

        filterPanel.add(createLabel("Đến số tiền (VNĐ):"));
        txtDenTien = new JTextField(); 
        txtDenTien.setMaximumSize(fieldSize);
        txtDenTien.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterPanel.add(txtDenTien);

        return filterPanel;
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(Color.decode("#64748B"));
        lbl.setBorder(new EmptyBorder(0, 0, 5, 0));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JPanel buildTableSection() {
        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setOpaque(false);

        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setBackground(Color.WHITE);
        tableContainer.setBorder(BorderFactory.createLineBorder(Color.decode("#E2E8F0"), 1, true));

        String[] cols = {"MÃ PHIẾU", "NHÀ CUNG CẤP", "NHÂN VIÊN NHẬP", "THỜI GIAN", "TỔNG TIỀN", "THAO TÁC"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(45);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(Color.decode("#F8FAFC"));
        table.getTableHeader().setPreferredSize(new Dimension(0, 45));
        
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setGridColor(Color.decode("#E2E8F0"));
        table.setIntercellSpacing(new Dimension(1, 1)); 

        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(160); 
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        
        CustomRowRenderer rowRenderer = new CustomRowRenderer();
        for (int i = 0; i < 5; i++) table.getColumnModel().getColumn(i).setCellRenderer(rowRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(new ActionRenderer());

        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (hoveredRow != row) { hoveredRow = row; table.repaint(); }
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override public void mouseExited(MouseEvent e) { hoveredRow = -1; table.repaint(); }
            @Override public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row >= 0 && col == 5) {
                    String maPN = tableModel.getValueAt(row, 0).toString();
                    String tenNV = tableModel.getValueAt(row, 2).toString(); 
                    Rectangle rect = table.getCellRect(row, col, false);
                    int clickX = e.getX() - rect.x;
                    int w = rect.width;
                    
                    if (clickX >= w/2 - 25 && clickX <= w/2 - 5) {
                        new PNDetailDialog(maPN, pnBus, nccBus, tenNV).setVisible(true); 
                    } else if (clickX >= w/2 + 5 && clickX <= w/2 + 25) {
                        handleDelete(maPN);
                    }
                }
            }
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
        DocumentListener docListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterData(); }
            public void removeUpdate(DocumentEvent e) { filterData(); }
            public void changedUpdate(DocumentEvent e) { filterData(); }
        };
        txtSearch.getDocument().addDocumentListener(docListener);
        txtTuTien.getDocument().addDocumentListener(docListener);
        txtDenTien.getDocument().addDocumentListener(docListener);
        txtTuNgay.getDocument().addDocumentListener(docListener);
        txtDenNgay.getDocument().addDocumentListener(docListener);

        cbNCC.addItemListener(e -> { if (e.getStateChange() == ItemEvent.SELECTED) filterData(); });
        cbNV.addItemListener(e -> { if (e.getStateChange() == ItemEvent.SELECTED) filterData(); });
    }

    private void filterData() {
        String search = txtSearch.getText().toLowerCase();
        String ncc = cbNCC.getSelectedItem().toString();
        String nv = cbNV.getSelectedItem().toString();
        
        double tuTien = 0, denTien = Double.MAX_VALUE;
        try { if (!txtTuTien.getText().isEmpty()) tuTien = Double.parseDouble(txtTuTien.getText()); } catch (Exception ignored) {}
        try { if (!txtDenTien.getText().isEmpty()) denTien = Double.parseDouble(txtDenTien.getText()); } catch (Exception ignored) {}

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate tuNgayDate = null, denNgayDate = null;
        try { if (!txtTuNgay.getText().isEmpty()) tuNgayDate = LocalDate.parse(txtTuNgay.getText(), dtf); } catch (Exception ignored) {}
        try { if (!txtDenNgay.getText().isEmpty()) denNgayDate = LocalDate.parse(txtDenNgay.getText(), dtf); } catch (Exception ignored) {}

        currentList = new ArrayList<>();
        for (phieunhapDTO pn : pnBus.getListPhieuNhap()) {
            String tenNCC = nccBus.layTenNccTheoMa(pn.getMaNCC());
            String tenNV = nvBus.layTenNhanVien(pn.getMaNV()); 
            LocalDate ngayPN = pn.getNgayNhap().toLocalDate();

            boolean matchSearch = pn.getMaPNH().toLowerCase().contains(search) || tenNCC.toLowerCase().contains(search) || tenNV.toLowerCase().contains(search);
            boolean matchNCC = ncc.equals("Tất cả") || tenNCC.equals(ncc);
            boolean matchNV = nv.equals("Tất cả") || tenNV.equals(nv);
            boolean matchTien = pn.getTongTien() >= tuTien && pn.getTongTien() <= denTien;
            boolean matchDate = (tuNgayDate == null || !ngayPN.isBefore(tuNgayDate)) && (denNgayDate == null || !ngayPN.isAfter(denNgayDate));

            if (matchSearch && matchNCC && matchNV && matchTien && matchDate) {
                currentList.add(pn);
            }
        }
        currentPage = 1; 
        loadTableData();
    }

    public void refreshData() {
        pnBus.docDanhSach(); 
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

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (int i = start; i < end; i++) {
            phieunhapDTO pn = currentList.get(i);
            tableModel.addRow(new Object[]{
                pn.getMaPNH(), nccBus.layTenNccTheoMa(pn.getMaNCC()), nvBus.layTenNhanVien(pn.getMaNV()), 
                pn.getNgayNhap().format(dtf), pn.getTongTien(), ""
            });
        }
    }

    private void handleDelete(String maPNH) {
        List<CTphieunhapDTO> ctList = pnBus.xemChiTietPhieuNhap(maPNH);
        List<String> validCTPNs = new ArrayList<>();
        for(CTphieunhapDTO ct : ctList) validCTPNs.add(ct.getMaCTPN());
        
        BUS.CtspBUS ctspBusCheck = new BUS.CtspBUS();
        boolean canDelete = true;
        for (ChitietSPDTO ct : ctspBusCheck.getListCtsp()) {
            if (validCTPNs.contains(ct.getMaCTPN())) {
                if (ct.getTinhtrang().equalsIgnoreCase("Đã bán")) {
                    canDelete = false; break;
                }
            }
        }
        
        if (!canDelete) {
            JOptionPane.showMessageDialog(this, "Không thể hủy! Đã có sản phẩm trong phiếu này được xuất kho (Bán ra).", "Lỗi Dữ Liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Hủy phiếu nhập " + maPNH + " sẽ xóa toàn bộ IMEI đã sinh và trừ Tồn kho. Tiếp tục?", "Cảnh báo", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            if (pnBus.huyPhieuNhap(maPNH, perms)) refreshData();
        }
    }

    private void showDatePicker(JTextField targetField) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chọn ngày", true);
        dialog.setSize(300, 280);
        dialog.setLocationRelativeTo(targetField);
        dialog.setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(10, 10, 10, 10));
        JButton btnPrev = new JButton("<"); btnPrev.setBackground(Color.decode("#F1F5F9"));
        JButton btnNext = new JButton(">"); btnNext.setBackground(Color.decode("#F1F5F9"));
        JLabel lblMonthYear = new JLabel("", SwingConstants.CENTER);
        lblMonthYear.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.add(btnPrev, BorderLayout.WEST);
        header.add(lblMonthYear, BorderLayout.CENTER);
        header.add(btnNext, BorderLayout.EAST);

        JPanel body = new JPanel(new GridLayout(7, 7, 2, 2));
        body.setBackground(Color.WHITE);
        body.setBorder(new EmptyBorder(0, 10, 10, 10));
        String[] days = {"CN", "T2", "T3", "T4", "T5", "T6", "T7"};
        
        final LocalDate[] currentView = {LocalDate.now()};
        
        Runnable updateCalendar = () -> {
            body.removeAll();
            for (String d : days) {
                JLabel l = new JLabel(d, SwingConstants.CENTER);
                l.setFont(new Font("Segoe UI", Font.BOLD, 11));
                l.setForeground(Color.decode("#64748B"));
                body.add(l);
            }
            LocalDate firstDayOfMonth = currentView[0].withDayOfMonth(1);
            int dayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7; 
            int daysInMonth = currentView[0].lengthOfMonth();

            lblMonthYear.setText("Tháng " + currentView[0].getMonthValue() + " / " + currentView[0].getYear());

            for (int i = 0; i < dayOfWeek; i++) body.add(new JLabel(""));

            for (int i = 1; i <= daysInMonth; i++) {
                int day = i;
                JButton btnDay = new JButton(String.valueOf(day));
                btnDay.setMargin(new Insets(0, 0, 0, 0));
                btnDay.setBackground(Color.WHITE);
                btnDay.setFocusPainted(false);
                btnDay.setCursor(new Cursor(Cursor.HAND_CURSOR));
                
                if (day == LocalDate.now().getDayOfMonth() && currentView[0].getMonthValue() == LocalDate.now().getMonthValue() && currentView[0].getYear() == LocalDate.now().getYear()) {
                    btnDay.setBackground(PRIMARY_COLOR);
                    btnDay.setForeground(Color.WHITE);
                    btnDay.setFont(new Font("Segoe UI", Font.BOLD, 12));
                }

                btnDay.addActionListener(e -> {
                    targetField.setText(String.format("%02d/%02d/%04d", day, currentView[0].getMonthValue(), currentView[0].getYear()));
                    dialog.dispose();
                });
                body.add(btnDay);
            }
            
            int remaining = 42 - (dayOfWeek + daysInMonth);
            for (int i = 0; i < remaining; i++) body.add(new JLabel(""));
            
            body.revalidate();
            body.repaint();
        };

        btnPrev.addActionListener(e -> { currentView[0] = currentView[0].minusMonths(1); updateCalendar.run(); });
        btnNext.addActionListener(e -> { currentView[0] = currentView[0].plusMonths(1); updateCalendar.run(); });

        updateCalendar.run();
        dialog.add(header, BorderLayout.NORTH);
        dialog.add(body, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    class CustomRowRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            c.setForeground(Color.decode("#1E293B"));
            if (isSelected) c.setBackground(Color.decode("#E2E8F0")); 
            else if (row == hoveredRow) c.setBackground(Color.decode("#F1F5F9")); 
            else c.setBackground(Color.WHITE);

            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.decode("#E2E8F0")));

            if (column == 4 && value != null) {
                try {
                    double val = Double.parseDouble(value.toString());
                    setText(String.format("%,.0f đ", val));
                } catch (Exception e) { setText(value.toString()); }
                setHorizontalAlignment(JLabel.RIGHT);
            } 
            else if (column == 0 || column == 3) {
                setHorizontalAlignment(JLabel.CENTER);
            } else {
                setHorizontalAlignment(JLabel.LEFT);
            }
            return c;
        }
    }

    class ActionRenderer extends JPanel implements TableCellRenderer {
        public ActionRenderer() { 
            setOpaque(true); 
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.decode("#E2E8F0")));
        }
        @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) setBackground(Color.decode("#E2E8F0"));
            else if (row == hoveredRow) setBackground(Color.decode("#F1F5F9"));
            else setBackground(Color.WHITE);
            return this;
        }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight(), size = 16, y = (h - size) / 2;
            
            int eyeX = w/2 - 20;
            g2.setColor(Color.decode("#3B82F6")); g2.setStroke(new BasicStroke(1.5f));
            g2.drawArc(eyeX, y + 4, size, size - 8, 0, 360); g2.fillOval(eyeX + 6, y + 6, 4, 4);

            int delX = w/2 + 10;
            g2.setColor(Color.decode("#EF4444"));
            g2.drawRect(delX + 4, y, size - 8, 3); g2.drawRoundRect(delX + 2, y + 3, size - 4, size - 3, 2, 2);

            g2.dispose();
        }
    }
}