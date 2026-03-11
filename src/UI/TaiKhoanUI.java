package UI;

import DAL.DAO.AccountDAO;
import DTO.accountDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.security.MessageDigest;
import java.util.ArrayList;

public class TaiKhoanUI extends JPanel {

    private DefaultTableModel model;
    private JTable table;
    private final AccountDAO accountDAO = new AccountDAO();

    // Interface dùng để báo cho MainFrame biết khi nào cần chuyển trang Phân Quyền
    public interface INavigationListener {
        void goToPhanQuyen(String accountId);
    }
    private INavigationListener navListener;

    public TaiKhoanUI() {
        setBackground(Color.decode("#F3F4F6"));
        setLayout(new BorderLayout());

        add(createTabPanel(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);

        loadDataToTable();
    }

    // Thiết lập listener từ giao diện chính (MainFrame)
    public void setNavigationListener(INavigationListener listener) {
        this.navListener = listener;
    }

    // ==========================================================
    // 1. HÀM BĂM MẬT KHẨU (Sử dụng chuẩn SHA-256)
    // ==========================================================
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Lỗi mã hóa mật khẩu", ex);
        }
    }

    // ==========================================================
    // 2. TẠO GIAO DIỆN
    // ==========================================================
    private JPanel createTabPanel() {
        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabPanel.setBackground(Color.decode("#F8FAFC"));
        tabPanel.setBorder(new MatteBorder(0, 0, 1, 0, Color.decode("#E2E8F0")));

        JLabel lblTab1 = new JLabel("  \uD83D\uDDC3 Quản lý chính  ", SwingConstants.CENTER);
        lblTab1.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTab1.setForeground(Color.decode("#2563EB"));
        lblTab1.setPreferredSize(new Dimension(140, 40));
        lblTab1.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 1, 1, 1, Color.decode("#E2E8F0")),
                new MatteBorder(0, 0, 2, 0, Color.decode("#2563EB"))
        ));
        lblTab1.setOpaque(true);
        lblTab1.setBackground(Color.WHITE);

        JLabel lblTab2 = new JLabel("  \u2699 Kỹ thuật  ", SwingConstants.CENTER);
        lblTab2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTab2.setForeground(Color.decode("#64748B"));
        lblTab2.setPreferredSize(new Dimension(100, 40));

        tabPanel.add(lblTab1);
        tabPanel.add(lblTab2);
        return tabPanel;
    }

    private JPanel createMainContent() {
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel whiteBox = new JPanel(new BorderLayout(0, 20)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(Color.decode("#E2E8F0"));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            }
        };
        whiteBox.setOpaque(false);
        whiteBox.setBorder(new EmptyBorder(20, 25, 25, 25));

        whiteBox.add(createBoxHeader(), BorderLayout.NORTH);
        whiteBox.add(createTablePanel(), BorderLayout.CENTER);

        wrapperPanel.add(whiteBox, BorderLayout.CENTER);
        return wrapperPanel;
    }

    private JPanel createBoxHeader() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JPanel leftFlow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftFlow.setOpaque(false);

        JLabel lblIcon = new JLabel(" \uD83D\uDDDD ", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.decode("#EFF6FF"));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        lblIcon.setForeground(Color.decode("#2563EB"));
        lblIcon.setPreferredSize(new Dimension(36, 36));

        JLabel lblTitle = new JLabel("TÀI KHOẢN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.decode("#0F172A"));

        JLabel lblStatus = new JLabel(" \u25CF SYSTEM LIVE ");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lblStatus.setForeground(Color.decode("#10B981"));
        lblStatus.setBackground(Color.decode("#D1FAE5"));
        lblStatus.setOpaque(true);
        lblStatus.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.decode("#D1FAE5"), 4, true),
                new EmptyBorder(2, 6, 2, 6)
        ));

        leftFlow.add(lblIcon);
        leftFlow.add(lblTitle);
        leftFlow.add(lblStatus);

        JPanel rightFlow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightFlow.setOpaque(false);

        JButton btnThem = createStyledButton("+  THÊM MỚI", "#2563EB", "#FFFFFF");
        btnThem.addActionListener(e -> {
            // VÍ DỤ CÁCH DÙNG HÀM BĂM MẬT KHẨU KHI THÊM
            // String plainPassword = txtPassword.getText();
            // String hashedPassword = hashPassword(plainPassword);
            // account.setPass(hashedPassword);
            // accountDAO.insert(account);
            JOptionPane.showMessageDialog(this, "Nút thêm mới được click!");
        });

        JButton btnCauHinh = createStyledButton("⚙  CẤU HÌNH", "#FFFFFF", "#475569");
        btnCauHinh.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.decode("#CBD5E1"), 1),
                new EmptyBorder(8, 15, 8, 15)
        ));

        rightFlow.add(btnThem);
        rightFlow.add(btnCauHinh);

        topPanel.add(leftFlow, BorderLayout.WEST);
        topPanel.add(rightFlow, BorderLayout.EAST);
        return topPanel;
    }

    private JPanel createTablePanel() {
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setOpaque(false);
        tableContainer.setBorder(new LineBorder(Color.decode("#E2E8F0"), 1, true));

        String[] cols = {"ID", "TÊN", "MẬT KHẨU", "QUYỀN", "THAO TÁC"};

        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // QUAN TRỌNG: Cho phép edit cột 4 để click được nút
            }
        };

        table = new JTable(model);
        table.setRowHeight(45);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(Color.decode("#F1F5F9"));
        table.setSelectionBackground(Color.decode("#EFF6FF"));

        // Style Header
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(Color.decode("#F8FAFC"));
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                label.setForeground(Color.decode("#475569"));
                label.setBorder(BorderFactory.createCompoundBorder(
                        new MatteBorder(0, 0, 1, 0, Color.decode("#E2E8F0")),
                        new EmptyBorder(0, 20, 0, 0)
                ));
                return label;
            }
        });
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));

        // Style Cells thông thường
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 20, 0, 0));
                c.setBackground(Color.WHITE);
                c.setForeground(Color.decode("#334155"));
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                return c;
            }
        };

        for (int i = 0; i < 4; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        // =======================================================
        // 3. GÁN BỘ RENDERER VÀ EDITOR ĐỂ TẠO NÚT BẤM CHO BẢNG
        // =======================================================
        table.getColumnModel().getColumn(4).setCellRenderer(new TableActionCellRender());
        table.getColumnModel().getColumn(4).setCellEditor(new TableActionCellEditor(table));

        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(250);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(180);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        tableContainer.add(scrollPane, BorderLayout.CENTER);
        return tableContainer;
    }

    private void loadDataToTable() {
        model.setRowCount(0);
        ArrayList<accountDTO> list = accountDAO.selectAll();
        if (list != null) {
            for (accountDTO acc : list) {
                model.addRow(new Object[]{acc.getId(), acc.getTen(), "********", acc.getQuyen(), ""});
            }
        }
    }

    private JButton createStyledButton(String text, String bgColor, String fgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.decode(bgColor));
        btn.setForeground(Color.decode(fgColor));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        return btn;
    }

    // =========================================================================
    // CÁC LỚP BỔ TRỢ ĐỂ TẠO NÚT BẤM TRONG JTABLE (SỬA, PHÂN QUYỀN, XÓA)
    // =========================================================================
    private class ActionPanel extends JPanel {
        JButton btnEdit, btnRole, btnDel;

        public ActionPanel() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 15, 5));
            setOpaque(true);

            // Icon Bút chì (Sửa) - Xanh lá
            btnEdit = createIconButton("<html><font size='5'>&#x270E;</font></html>", "#10B981");

            // Icon Khiên (Phân quyền) - Cam vàng
            btnRole = createIconButton("<html><font size='5'>&#x1F6E1;</font></html>", "#F59E0B");
            btnRole.setToolTipText("Chỉnh sửa phân quyền");

            // Icon Thùng rác (Xóa) - Đỏ
            btnDel = createIconButton("<html><font size='5'>&#x1F5D1;</font></html>", "#EF4444");

            add(btnEdit);
            add(btnRole);
            add(btnDel);
        }

        private JButton createIconButton(String iconHtml, String hexColor) {
            JButton btn = new JButton(iconHtml);
            btn.setForeground(Color.decode(hexColor));
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setMargin(new Insets(0, 0, 0, 0));
            return btn;
        }
    }

    private class TableActionCellRender extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            ActionPanel actionPanel = new ActionPanel();
            actionPanel.setBackground(isSelected ? Color.decode("#EFF6FF") : Color.WHITE);
            return actionPanel;
        }
    }

    private class TableActionCellEditor extends DefaultCellEditor {
        private final ActionPanel actionPanel;
        private String currentAccountId;

        public TableActionCellEditor(JTable table) {
            super(new JCheckBox());
            actionPanel = new ActionPanel();

            actionPanel.btnEdit.addActionListener(e -> {
                fireEditingStopped();
                JOptionPane.showMessageDialog(null, "Mở form Sửa cho tài khoản: " + currentAccountId);
            });

            actionPanel.btnRole.addActionListener(e -> {
                fireEditingStopped();
                // NẾU CÓ LISTENER THÌ GỌI QUA MAIN FRAME CHUYỂN TRANG
                if (navListener != null) {
                    navListener.goToPhanQuyen(currentAccountId);
                } else {
                    JOptionPane.showMessageDialog(null, "Bạn đã click Phân quyền cho ID: " + currentAccountId + "\n(Cần cấu hình hàm chuyển trang tại MainFrame)");
                }
            });

            actionPanel.btnDel.addActionListener(e -> {
                fireEditingStopped();
                int confirm = JOptionPane.showConfirmDialog(null, "Xóa tài khoản " + currentAccountId + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    // Xóa logic
                    System.out.println("Đã xóa " + currentAccountId);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentAccountId = table.getValueAt(row, 0).toString(); // Lấy ID tài khoản ở cột 0
            actionPanel.setBackground(Color.decode("#EFF6FF"));
            return actionPanel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }
}