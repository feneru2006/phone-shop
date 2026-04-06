package UI.Panel;

import BUS.NCCBUS;
import BUS.PhieuNhapBUS;
import BUS.SanPhamBUS;
import DTO.SanPhamDTO;
import UI.Utils.UIUtils;
import UI.Dialog.ExcelPreviewDialog;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KhoPanel extends JPanel {
    private JLayeredPane layeredPane;
    private JPanel mainContent;

    private JTable tableKho;
    private DefaultTableModel modelKho;
    private SanPhamBUS sanPhamBUS;

    private JTextField txtTimKiem;
    private JButton btnTimKiem, btnLamMoi, btnToggleCanhBao, btnXuatExcel;

    private UIUtils.RoundedPanel pnlCanhBao;
    private JTable tableCanhBao;
    private DefaultTableModel modelCanhBao;
    private boolean isCanhBaoVisible = true;

    private List<SanPhamDTO> currentList = new ArrayList<>();
    private int currentPage = 1;
    private int rowsPerPage = 20; 
    private int totalPages = 1;
    private JLabel lblPageInfo;
    private JButton btnFirst, btnPrev, btnNext, btnLast;
    
    private JScrollPane scrollKho;

    public KhoPanel() {
        initUI();
        
        modelKho.setRowCount(0);
        modelKho.addRow(new Object[]{"", "", "⏳ Đang tải dữ liệu từ máy chủ...", "", ""});
        
        loadDataAsync();
        
        new Utility.AutoRefresh(30000, () -> { 
            if (sanPhamBUS != null) {
                sanPhamBUS.reload(); 
                loadDataAsync(); 
            }
        }).start();
    }

    // THÊM HÀM NÀY ĐỂ BÊN MAINFRAME CÓ THỂ GỌI ĐƯỢC
    public void reloadData() {
        if (sanPhamBUS != null) sanPhamBUS.reload();
        if (txtTimKiem != null) txtTimKiem.setText("");
        if (modelKho != null) {
            modelKho.setRowCount(0);
            modelKho.addRow(new Object[]{"", "", "⏳ Đang cập nhật dữ liệu mới...", "", ""});
        }
        loadDataAsync();
    }

    private void loadDataAsync() {
        if (btnLamMoi != null) btnLamMoi.setEnabled(false);
        if (btnTimKiem != null) btnTimKiem.setEnabled(false);
        if (btnXuatExcel != null) btnXuatExcel.setEnabled(false);

        SwingWorker<List<SanPhamDTO>, Void> worker = new SwingWorker<List<SanPhamDTO>, Void>() {
            @Override
            protected List<SanPhamDTO> doInBackground() throws Exception {
                if (sanPhamBUS == null) {
                    sanPhamBUS = new SanPhamBUS(); 
                }
                return sanPhamBUS.getAll();
            }

            @Override
            protected void done() {
                try {
                    List<SanPhamDTO> data = get();
                    loadDataKho(data);
                    loadLowStock(data); 
                } catch (Exception e) {
                    e.printStackTrace();
                    modelKho.setRowCount(0);
                    modelKho.addRow(new Object[]{"", "", "❌ Lỗi kết nối cơ sở dữ liệu!", "", ""});
                } finally {
                    if (btnLamMoi != null) btnLamMoi.setEnabled(true);
                    if (btnTimKiem != null) btnTimKiem.setEnabled(true);
                    if (btnXuatExcel != null) btnXuatExcel.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(Color.decode("#F8FAFF"));

        layeredPane = new JLayeredPane() {
            @Override
            public void doLayout() {
                super.doLayout();
                int w = getWidth();
                int h = getHeight();
                
                if (mainContent != null) {
                    mainContent.setBounds(0, 0, w, h);
                }
                if (pnlCanhBao != null) {
                    int cbWidth = 380;
                    int cbHeight = 280;
                    pnlCanhBao.setBounds(w - cbWidth - 30, h - cbHeight - 30, cbWidth, cbHeight);
                }
            }
        };
        add(layeredPane, BorderLayout.CENTER);

        mainContent = new JPanel(new BorderLayout(15, 10));
        mainContent.setBackground(Color.decode("#F8FAFF"));
        mainContent.setBorder(new EmptyBorder(0, 15, 15, 15));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JPanel titlePanel = UIUtils.createTitlePanel("src/main/java/resources/box.png", "SẢN PHẨM TỒN KHO", 24, Color.decode("#1E293B"));
        titlePanel.setBorder(new EmptyBorder(15, 0, 10, 0)); 
        topPanel.add(titlePanel, BorderLayout.NORTH);

        JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        toolPanel.setOpaque(false);
        toolPanel.setBorder(new EmptyBorder(5, 0, 10, 0));

        txtTimKiem = new JTextField(20);
        txtTimKiem.setPreferredSize(new Dimension(250, 38));
        txtTimKiem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtTimKiem.putClientProperty("JTextField.placeholderText", "Nhập tên sản phẩm để tìm...");
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
        // SỬA: Gọi chung hàm reloadData
        btnLamMoi.addActionListener(e -> reloadData());

        btnXuatExcel = new JButton("Xuất Excel");
        btnXuatExcel.setBackground(Color.decode("#217346"));
        btnXuatExcel.setForeground(Color.WHITE);
        styleButton(btnXuatExcel);
        btnXuatExcel.addActionListener(e -> handlePreviewExcel());

        btnToggleCanhBao = new JButton("Ẩn cảnh báo >>");
        btnToggleCanhBao.setBackground(Color.decode("#F59E0B"));
        btnToggleCanhBao.setForeground(Color.WHITE);
        styleButton(btnToggleCanhBao);
        btnToggleCanhBao.addActionListener(e -> toggleCanhBaoPanel());

        toolPanel.add(txtTimKiem);
        toolPanel.add(btnTimKiem);
        toolPanel.add(btnLamMoi);
        toolPanel.add(btnXuatExcel);
        toolPanel.add(btnToggleCanhBao);
        topPanel.add(toolPanel, BorderLayout.CENTER);

        mainContent.add(topPanel, BorderLayout.NORTH);

        JPanel centerWrapper = new JPanel(new BorderLayout(0, 10));
        centerWrapper.setOpaque(false);

        String[] columnNames = {"STT", "Mã SP", "Tên Sản Phẩm", "Số Lượng Tồn", "Đơn Giá"};
        modelKho = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        tableKho = new JTable(modelKho);
        setupTableStyle(tableKho);
        
        tableKho.getTableHeader().setResizingAllowed(false);
        TableColumnModel cm = tableKho.getColumnModel();
        cm.getColumn(0).setPreferredWidth(50); cm.getColumn(0).setMaxWidth(50);
        cm.getColumn(1).setPreferredWidth(90); cm.getColumn(1).setMaxWidth(90);
        cm.getColumn(3).setPreferredWidth(120); cm.getColumn(3).setMaxWidth(120);
        cm.getColumn(2).setPreferredWidth(350); 
        cm.getColumn(4).setPreferredWidth(150);

        tableKho.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tableKho.rowAtPoint(e.getPoint());
                    if (row != -1) {
                        // Đổi thành cột 1 (chứa Mã SP) thay vì cột 2 (Tên Sản Phẩm)
                        Object valMaSP = modelKho.getValueAt(row, 1); 
                        if(valMaSP != null && !valMaSP.toString().contains("Đang tải dữ liệu") && !valMaSP.toString().contains("Đang cập nhật")) {
                            chuyenHuongSangCTSP(valMaSP.toString());
                        }
                    }
                }
            }
        });

        scrollKho = new JScrollPane(tableKho);
        scrollKho.getViewport().setBackground(Color.WHITE);
        
        scrollKho.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                calculateRowsPerPage();
            }
        });

        centerWrapper.add(scrollKho, BorderLayout.CENTER);
        centerWrapper.add(createPaginationPanel(), BorderLayout.SOUTH);
        mainContent.add(centerWrapper, BorderLayout.CENTER);

        setupCanhBaoUI();

        layeredPane.add(mainContent, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(pnlCanhBao, JLayeredPane.PALETTE_LAYER);
    }

    private void setupCanhBaoUI() {
        pnlCanhBao = new UIUtils.RoundedPanel(20, Color.WHITE) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.decode("#EF4444"));
                g2.setStroke(new BasicStroke(2f));
                g2.draw(new RoundRectangle2D.Double(1, 1, getWidth() - 3, getHeight() - 3, 20, 20));
                g2.dispose();
            }
        };
        pnlCanhBao.setLayout(new BorderLayout());
        pnlCanhBao.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel titleCanhBao = UIUtils.createTitlePanel("src/main/java/resources/warning.png", "Sắp hết hàng", 16, Color.decode("#EF4444"));
        titleCanhBao.setBorder(new EmptyBorder(0, 0, 10, 0));
        pnlCanhBao.add(titleCanhBao, BorderLayout.NORTH);

        String[] colsCanhBao = {"Mã SP", "Tên Sản Phẩm", "SL"};
        modelCanhBao = new DefaultTableModel(colsCanhBao, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tableCanhBao = new JTable(modelCanhBao);
        setupTableStyle(tableCanhBao);
        
        tableCanhBao.getTableHeader().setResizingAllowed(false);
        TableColumnModel cmCB = tableCanhBao.getColumnModel();
        cmCB.getColumn(0).setPreferredWidth(70); cmCB.getColumn(0).setMaxWidth(70);
        cmCB.getColumn(2).setPreferredWidth(50); cmCB.getColumn(2).setMaxWidth(50);

        tableCanhBao.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = tableCanhBao.rowAtPoint(e.getPoint());
                    if (row != -1) {
                        String maSP = modelCanhBao.getValueAt(row, 0).toString();
                        chuyenHuongVaHienFormNhap(maSP);
                    }
                }
            }
        });

        JScrollPane scrollCanhBao = new JScrollPane(tableCanhBao);
        scrollCanhBao.getViewport().setBackground(Color.WHITE);
        scrollCanhBao.setBorder(BorderFactory.createEmptyBorder());
        pnlCanhBao.add(scrollCanhBao, BorderLayout.CENTER);
    }

    private void calculateRowsPerPage() {
        if (scrollKho == null || tableKho == null) return;
        int viewportHeight = scrollKho.getViewport().getHeight();
        int rowHeight = tableKho.getRowHeight();
        if (viewportHeight > 0 && rowHeight > 0) {
            int newRowsPerPage = Math.max(1, (viewportHeight / rowHeight));
            if (newRowsPerPage != this.rowsPerPage) {
                this.rowsPerPage = newRowsPerPage;
                if(!currentList.isEmpty()) {
                    updateTableKho();
                }
            }
        }
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setPreferredSize(new Dimension(140, 38));
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
        table.getTableHeader().setReorderingAllowed(false);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        if(table.getColumnCount() == 5) {
            table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); 
            table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); 
            table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer); 
        } else {
            table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); 
            table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); 
        }
    }

    private JPanel createPaginationPanel() {
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        paginationPanel.setOpaque(false);

        btnFirst = new JButton("<<");
        btnPrev = new JButton("<");
        lblPageInfo = new JLabel("Trang 1 / 1");
        lblPageInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnNext = new JButton(">");
        btnLast = new JButton(">>");

        JButton[] navBtns = {btnFirst, btnPrev, btnNext, btnLast};
        for(JButton btn : navBtns) {
            btn.setBackground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        btnFirst.addActionListener(e -> { currentPage = 1; updateTableKho(); });
        btnPrev.addActionListener(e -> { if(currentPage > 1) { currentPage--; updateTableKho(); } });
        btnNext.addActionListener(e -> { if(currentPage < totalPages) { currentPage++; updateTableKho(); } });
        btnLast.addActionListener(e -> { currentPage = totalPages; updateTableKho(); });

        paginationPanel.add(btnFirst);
        paginationPanel.add(btnPrev);
        paginationPanel.add(lblPageInfo);
        paginationPanel.add(btnNext);
        paginationPanel.add(btnLast);

        return paginationPanel;
    }

    private void toggleCanhBaoPanel() {
        isCanhBaoVisible = !isCanhBaoVisible;
        pnlCanhBao.setVisible(isCanhBaoVisible);
        btnToggleCanhBao.setText(isCanhBaoVisible ? "Ẩn cảnh báo >>" : "<< Hiện cảnh báo");
        btnToggleCanhBao.setBackground(isCanhBaoVisible ? Color.decode("#F59E0B") : Color.decode("#94A3B8"));
    }

    private void loadDataKho(List<SanPhamDTO> list) {
        currentList.clear();
        if (list != null) {
            for (SanPhamDTO sp : list) {
                if (!sp.isDeleted() && sp.getSlTon() >= 1) {
                    currentList.add(sp);
                }
            }
        }
        currentPage = 1;
        updateTableKho();
    }

    private void updateTableKho() {
        modelKho.setRowCount(0);
        if (currentList == null || currentList.isEmpty()) {
            lblPageInfo.setText("Trang 0 / 0");
            totalPages = 1; currentPage = 1;
            return;
        }
        int safeRowsPerPage = Math.max(1, rowsPerPage);
        totalPages = (int) Math.ceil((double) currentList.size() / safeRowsPerPage);
        if (currentPage > totalPages) currentPage = totalPages;
        if (currentPage < 1) currentPage = 1;
        lblPageInfo.setText("Trang " + currentPage + " / " + totalPages);

        int start = (currentPage - 1) * safeRowsPerPage;
        int end = Math.min(start + safeRowsPerPage, currentList.size());

        for (int i = start; i < end; i++) {
            SanPhamDTO sp = currentList.get(i);
            modelKho.addRow(new Object[]{
                    i + 1, sp.getMaSP(), sp.getTenSP(), sp.getSlTon(),
                    String.format("%,.0f VNĐ", sp.getGia())
            });
        }
    }

    private void loadLowStock(List<SanPhamDTO> all) {
        modelCanhBao.setRowCount(0);
        if (all == null) return;
        for (SanPhamDTO sp : all) {
            if (!sp.isDeleted() && sp.getSlTon() >= 0 && sp.getSlTon() <= 10) { 
                modelCanhBao.addRow(new Object[]{ sp.getMaSP(), sp.getTenSP(), sp.getSlTon() });
            }
        }
    }

    private void handleTimKiem() {
        if (sanPhamBUS == null) {
            JOptionPane.showMessageDialog(this, "Dữ liệu đang được tải, vui lòng đợi giây lát!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String keyword = txtTimKiem.getText().trim();
        if (keyword.isEmpty()) {
            reloadData();
            return;
        }
        try {
            List<SanPhamDTO> ketQua = sanPhamBUS.timKiem("Tên SP", keyword);
            if (ketQua == null || ketQua.isEmpty()) {
                loadDataKho(new ArrayList<>());
                JOptionPane.showMessageDialog(this, "Không tìm thấy sản phẩm: " + keyword);
            } else {
                loadDataKho(ketQua);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void handlePreviewExcel() {
        if (currentList == null || currentList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu để xuất!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Window owner = SwingUtilities.getWindowAncestor(this);
        ExcelPreviewDialog previewDialog = new ExcelPreviewDialog(owner, currentList);
        previewDialog.setVisible(true);
    }

    private void chuyenHuongSangCTSP(String tenSP) {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof UI.MainFrameTest) {
            UI.MainFrameTest mainFrame = (UI.MainFrameTest) window;
            
            mainFrame.showCard("Chi tiết SP"); 

            for (Component comp : mainFrame.getContentPanel().getComponents()) {
                if (comp instanceof UI.CTSPPanel) {
                    try {
                        java.lang.reflect.Method method = comp.getClass().getMethod("timKiemSanPham", String.class);
                        method.invoke(comp, tenSP);
                    } catch (Exception e) {
                        System.out.println("Hãy thêm hàm public void timKiemSanPham(String tenSP) vào CTSPPanel.java");
                    }
                    break;
                }
            }
        }
    }

    private void chuyenHuongVaHienFormNhap(String maSP) {
        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof UI.MainFrameTest) {
            UI.MainFrameTest mainFrame = (UI.MainFrameTest) window;
            
            mainFrame.showCard("Nhập hàng");

            UI.Panel.PN.PNPanel pnPanel = null;
            for (Component comp : mainFrame.getContentPanel().getComponents()) {
                if (comp instanceof UI.Panel.PN.PNPanel) {
                    pnPanel = (UI.Panel.PN.PNPanel) comp;
                    break;
                }
            }

            BUS.PhieuNhapBUS pnBus = new BUS.PhieuNhapBUS();
            BUS.NCCBUS nccBus = new BUS.NCCBUS();
            List<String> perms = Arrays.asList("VIEW_NHAPHANG", "CREATE_NHAPHANG", "DELETE_NHAPHANG", "UPDATE_NHAPHANG");
            
            UI.Panel.PN.PNAddDialog dialog = new UI.Panel.PN.PNAddDialog(pnPanel, pnBus, nccBus, perms);
            
            try {
                java.lang.reflect.Method method = dialog.getClass().getMethod("truyenMaSanPham", String.class);
                method.invoke(dialog, maSP);
            } catch (Exception e) {
                System.out.println("Hãy thêm hàm public void truyenMaSanPham(String maSP) vào PNAddDialog.java");
            }

            dialog.setVisible(true);
            
            if(pnPanel != null) {
                pnPanel.refreshData();
            }
        }
    }
}