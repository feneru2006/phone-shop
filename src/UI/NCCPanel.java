package UI;

import BUS.NCCBUS;
import DTO.NCCDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;
import java.util.List;

public class NCCPanel extends JPanel {

    private NCCBUS nccBus;
    private List<NCCDTO> currentList; 
    
    private final List<String> userPermissions = Arrays.asList("CREATE_NCC", "UPDATE_NCC", "DELETE_NCC");

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JLabel lblPageInfo;
    
    private int currentPage = 1;
    private final int itemsPerPage = 10;
    private int hoveredRow = -1;

    private final Color PRIMARY_COLOR = Color.decode("#2563EB");
    private final Color BG_COLOR = Color.decode("#F8FAFF");

    public NCCPanel() {
        nccBus = new NCCBUS();
        currentList = nccBus.getListNCC();

        setLayout(new BorderLayout(20, 20));
        setBackground(BG_COLOR);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (table != null) table.clearSelection();
            }
        });

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTableSection(), BorderLayout.CENTER);
        add(buildPagination(), BorderLayout.SOUTH);

        loadTableData();
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                nccBus = new NCCBUS(); 
                search(); 
            }
        });
    }

    private JPanel buildHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        left.setOpaque(false);
        JLabel lblIcon = new JLabel("🗄️");
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        lblIcon.setForeground(PRIMARY_COLOR);
        JLabel lblTitle = new JLabel("NHÀ CUNG CẤP");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        left.add(lblIcon);
        left.add(lblTitle);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        right.setOpaque(false);

        txtSearch = new JTextField(20);
        txtSearch.putClientProperty("JTextField.placeholderText", "Tìm kiếm mã, tên, SĐT...");
        txtSearch.setPreferredSize(new Dimension(250, 38));
        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { search(); }
            public void removeUpdate(DocumentEvent e) { search(); }
            public void changedUpdate(DocumentEvent e) { search(); }
        });

        JButton btnAdd = new JButton("+ THÊM NHÀ CUNG CẤP");
        btnAdd.setBackground(PRIMARY_COLOR);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAdd.setPreferredSize(new Dimension(180, 38));
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(e -> showAddDialog());

        right.add(txtSearch);
        right.add(btnAdd);

        headerPanel.add(left, BorderLayout.WEST);
        headerPanel.add(right, BorderLayout.EAST);
        return headerPanel;
    }

    private JPanel buildTableSection() {
        RoundedPanel tableContainer = new RoundedPanel(15, Color.WHITE);
        tableContainer.setLayout(new BorderLayout());
        tableContainer.setBorder(new EmptyBorder(5, 5, 5, 5));
        tableContainer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { table.clearSelection(); }
        });

        String[] cols = {"MÃ NCC", "TÊN NHÀ CUNG CẤP", "ĐỊA CHỈ", "SĐT", "THAO TÁC"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(50);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        
        table.getTableHeader().setBackground(Color.decode("#F8FAFC")); 
        table.getTableHeader().setForeground(Color.decode("#334155"));
        table.getTableHeader().setPreferredSize(new Dimension(0, 45));
        
        table.setShowVerticalLines(true); 
        table.setShowHorizontalLines(true);
        table.setGridColor(Color.decode("#E2E8F0")); 
        table.setIntercellSpacing(new Dimension(1, 1)); 

        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(250);
        table.getColumnModel().getColumn(2).setPreferredWidth(300);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(100); 

        CustomRowRenderer rowRenderer = new CustomRowRenderer();
        for (int i = 0; i < 4; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(rowRenderer);
        }
        table.getColumnModel().getColumn(4).setCellRenderer(new ActionRenderer());

        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (hoveredRow != row) {
                    hoveredRow = row;
                    table.repaint(); 
                }
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                hoveredRow = -1;
                table.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if (row >= 0 && col == 4) { 
                    Rectangle cellRect = table.getCellRect(row, col, false);
                    int clickX = e.getX() - cellRect.x;
                    int w = cellRect.width;
                    
                    String maNCC = tableModel.getValueAt(row, 0).toString();
                    NCCDTO ncc = nccBus.timNccTheoTen(tableModel.getValueAt(row, 1).toString());
                    
                    boolean isLocked = (ncc != null && ncc.getIsDeleted() == 1);

                    if (isLocked) {
                        int lockX = w / 2 - 6;
                        if (clickX >= lockX - 10 && clickX <= lockX + 26) {
                            handleUnlock(maNCC);
                        }
                    } else {
                        int editX = w / 2 - 25;
                        int delX = w / 2 + 10;
                        if (clickX >= editX - 5 && clickX <= editX + 23) {
                            showEditDialog(ncc);
                        } else if (clickX >= delX - 5 && clickX <= delX + 23) {
                            handleDelete(maNCC);
                        }
                    }
                }
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.getViewport().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { table.clearSelection(); }
        });

        tableContainer.add(scroll, BorderLayout.CENTER);
        return tableContainer;
    }

    private JPanel buildPagination() {
        JPanel pagPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pagPanel.setOpaque(false);

        lblPageInfo = new JLabel("Trang 1 / 1");
        lblPageInfo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPageInfo.setForeground(Color.decode("#64748B"));

        JButton btnPrev = new JButton("«");
        btnPrev.setPreferredSize(new Dimension(40, 35));
        btnPrev.setBackground(Color.WHITE);
        btnPrev.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPrev.addActionListener(e -> {
            if (currentPage > 1) { currentPage--; loadTableData(); }
        });

        JButton btnNext = new JButton("»");
        btnNext.setPreferredSize(new Dimension(40, 35));
        btnNext.setBackground(Color.WHITE);
        btnNext.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNext.addActionListener(e -> {
            int maxPage = (int) Math.ceil((double) currentList.size() / itemsPerPage);
            if (currentPage < maxPage) { currentPage++; loadTableData(); }
        });

        pagPanel.add(lblPageInfo);
        pagPanel.add(btnPrev);
        pagPanel.add(btnNext);
        return pagPanel;
    }

    private void search() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            currentList = nccBus.getListNCC();
        } else {
            currentList = nccBus.timKiem(keyword, "Tất cả");
        }
        currentPage = 1; 
        loadTableData();
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
            NCCDTO ncc = currentList.get(i);
            tableModel.addRow(new Object[]{
                ncc.getMaNCC(), ncc.getTen(), ncc.getDiaChi(), ncc.getSdt(), ""
            });
        }
    }

    private void handleDelete(String maNCC) {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn khóa Nhà cung cấp " + maNCC + " này?\nSau khi khóa, bạn sẽ cần mật khẩu để mở lại.", 
            "Xác nhận khóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = nccBus.xoaNCC(maNCC, userPermissions);
            if (success) {
                search(); 
            } else {
                JOptionPane.showMessageDialog(this, "Khóa thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleUnlock(String maNCC) {
        JPasswordField pf = new JPasswordField();
        Object[] message = {
            "Nhà cung cấp này đang bị khóa.",
            "Vui lòng nhập mật mã quản trị viên để mở khóa:", pf
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Yêu cầu Mở Khóa", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (option == JOptionPane.OK_OPTION) {
            String pass = new String(pf.getPassword());
            
            if (pass.equals("admin")) { 
                if (nccBus.moKhoaNCC(maNCC)) {
                    JOptionPane.showMessageDialog(this, "Mở khóa thành công!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    search(); 
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi kết nối cơ sở dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Mật mã không chính xác!", "Từ chối", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAddDialog() {
        openModalWithDimBackground(new NCCDialog(null, nccBus, userPermissions));
    }

    private void showEditDialog(NCCDTO ncc) {
        openModalWithDimBackground(new NCCDialog(ncc, nccBus, userPermissions));
    }

    private void openModalWithDimBackground(JDialog dialog) {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        JPanel glassPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(0, 0, 0, 100)); 
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        glassPane.setOpaque(false);
        parentFrame.setGlassPane(glassPane);
        glassPane.setVisible(true);

        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true); 

        glassPane.setVisible(false); 
        search();
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

    class CustomRowRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            NCCDTO ncc = null;
            try {
                String tenNCC = tableModel.getValueAt(row, 1).toString();
                ncc = nccBus.timNccTheoTen(tenNCC);
            } catch (Exception e){}

            boolean isLocked = (ncc != null && ncc.getIsDeleted() == 1);

            if (isLocked) {
                c.setBackground(Color.decode("#F1F5F9")); 
                c.setForeground(Color.decode("#94A3B8")); 
            } else {
                c.setForeground(Color.decode("#1E293B"));
                if (isSelected) c.setBackground(Color.decode("#E2E8F0"));
                else if (row == hoveredRow) c.setBackground(Color.decode("#F1F5F9"));
                else c.setBackground(Color.WHITE);
            }
            
            if (column == 0 || column == 3) setHorizontalAlignment(JLabel.CENTER);
            else setHorizontalAlignment(JLabel.LEFT);
            
            return c;
        }
    }

    class ActionRenderer extends JPanel implements TableCellRenderer {
        private boolean isLockedRow = false;

        public ActionRenderer() { setOpaque(true); }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            try {
                String tenNCC = tableModel.getValueAt(row, 1).toString();
                NCCDTO ncc = nccBus.timNccTheoTen(tenNCC);
                isLockedRow = (ncc != null && ncc.getIsDeleted() == 1);
            } catch (Exception e){ isLockedRow = false; }

            if (isLockedRow) setBackground(Color.decode("#F1F5F9"));
            else if (isSelected) setBackground(Color.decode("#E2E8F0"));
            else if (row == hoveredRow) setBackground(Color.decode("#F1F5F9"));
            else setBackground(Color.WHITE);
            
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int iconSize = 18;

            if (isLockedRow) {
                int lockX = w / 2 - 6;
                int lockY = (h - 14) / 2;
                g2.setColor(Color.decode("#64748B")); 
                g2.setStroke(new BasicStroke(1.5f));
                
                g2.drawRoundRect(lockX, lockY + 5, 12, 9, 2, 2); 
                g2.drawArc(lockX + 2, lockY, 8, 10, 0, 180);
                g2.drawLine(lockX + 6, lockY + 8, lockX + 6, lockY + 11); 
            } else {
                int editX = w / 2 - 25; 
                int editY = (h - iconSize) / 2;
                int delX = w / 2 + 10;
                int delY = (h - iconSize) / 2;

                g2.setColor(Color.decode("#16A34A"));
                g2.setStroke(new BasicStroke(1.5f)); 
                g2.drawRoundRect(editX, editY, iconSize, iconSize, 4, 4); 
                g2.drawLine(editX + 5, editY + 13, editX + 13, editY + 5); 

                g2.setColor(Color.decode("#DC2626"));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRect(delX + 7, delY + 2, 4, 3); 
                g2.drawRoundRect(delX + 3, delY + 5, 12, 11, 2, 2); 
                g2.drawLine(delX + 1, delY + 5, delX + 17, delY + 5); 
            }
            g2.dispose();
        }
    }
}
