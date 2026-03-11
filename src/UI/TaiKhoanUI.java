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
    // 2. TẠO GIAO DIỆN CHÍNH
    // ==========================================================
    private JPanel createTabPanel() {
        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabPanel.setBackground(Color.decode("#F8FAFC"));
        tabPanel.setBorder(new MatteBorder(0, 0, 1, 0, Color.decode("#E2E8F0")));
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        lblIcon.setForeground(Color.decode("#2563EB"));
        lblIcon.setPreferredSize(new Dimension(36, 36));

        JLabel lblTitle = new JLabel("TÀI KHOẢN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.decode("#0F172A"));

        leftFlow.add(lblIcon);
        leftFlow.add(lblTitle);

        JPanel rightFlow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightFlow.setOpaque(false);

        JButton btnThem = createStyledButton("+  THÊM MỚI", "#2563EB", "#FFFFFF");

        // --- GỌI FORM THÊM MỚI KHI ẤN NÚT ---
        btnThem.addActionListener(e -> showAddAccountDialog());

        rightFlow.add(btnThem);

        topPanel.add(leftFlow, BorderLayout.WEST);
        topPanel.add(rightFlow, BorderLayout.EAST);
        return topPanel;
    }

    private JPanel createTablePanel() {
        JPanel tableContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(Color.decode("#E2E8F0"));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                g2.dispose();
            }
        };
        tableContainer.setOpaque(false);
        tableContainer.setBorder(new EmptyBorder(1, 1, 1, 1));

        String[] cols = {"ID", "TÊN", "MẬT KHẨU", "QUYỀN", "THAO TÁC"};

        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        table = new JTable(model);
        table.setRowHeight(45);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(Color.decode("#F1F5F9"));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(Color.decode("#F8FAFC"));
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                label.setForeground(Color.decode("#475569"));

                if (column == 4) {
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    label.setBorder(new MatteBorder(0, 0, 1, 0, Color.decode("#E2E8F0")));
                } else {
                    label.setHorizontalAlignment(SwingConstants.LEFT);
                    label.setBorder(BorderFactory.createCompoundBorder(
                            new MatteBorder(0, 0, 1, 0, Color.decode("#E2E8F0")),
                            new EmptyBorder(0, 20, 0, 0)
                    ));
                }
                return label;
            }
        });
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 20, 0, 0));
                if (hasFocus) c.setBackground(Color.decode("#DBEAFE"));
                else if (isSelected) c.setBackground(Color.decode("#F1F5F9"));
                else c.setBackground(Color.WHITE);

                c.setForeground(Color.decode("#334155"));
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                return c;
            }
        };

        for (int i = 0; i < 4; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        table.getColumnModel().getColumn(4).setCellRenderer(new TableActionCellRender());
        table.getColumnModel().getColumn(4).setCellEditor(new TableActionCellEditor(table));

        table.getColumnModel().getColumn(0).setMinWidth(80);
        table.getColumnModel().getColumn(0).setMaxWidth(80);
        table.getColumnModel().getColumn(0).setPreferredWidth(80);

        table.getColumnModel().getColumn(1).setMinWidth(250);
        table.getColumnModel().getColumn(1).setPreferredWidth(250);

        table.getColumnModel().getColumn(2).setMinWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);

        table.getColumnModel().getColumn(3).setMinWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);

        table.getColumnModel().getColumn(4).setMinWidth(180);
        table.getColumnModel().getColumn(4).setMaxWidth(180);
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

    // ==========================================================
    // 3. FORM THÊM TÀI KHOẢN MỚI VÀ CÁC THÀNH PHẦN BỔ TRỢ
    // ==========================================================
    private void showAddAccountDialog() {
        // Tạo cửa sổ Dialog hiển thị đè lên trên
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm tài khoản mới", true);
        dialog.setSize(420, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        // Header Form
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.decode("#2563EB"));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel lblTitle = new JLabel("THÊM TÀI KHOẢN MỚI");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.CENTER);
        dialog.add(headerPanel, BorderLayout.NORTH);

        // Phần Body chứa các trường nhập liệu
        JPanel bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setBackground(Color.WHITE);
        bodyPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        JTextField txtId = createModernTextField();
        JTextField txtTen = createModernTextField();

        JPasswordField txtPass = new JPasswordField();
        txtPass.setBorder(txtId.getBorder());
        txtPass.setPreferredSize(new Dimension(200, 38));
        txtPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Combo Box chọn nhóm quyền hạn
        JComboBox<String> cbQuyen = new JComboBox<>(new String[]{"AD", "QL", "NV","ST"});
        cbQuyen.setBackground(Color.WHITE);
        cbQuyen.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbQuyen.setPreferredSize(new Dimension(200, 38));

        bodyPanel.add(createInputGroup("Mã tài khoản (ID):", txtId));
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        bodyPanel.add(createInputGroup("Tên đăng nhập:", txtTen));
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        bodyPanel.add(createInputGroup("Mật khẩu:", txtPass));
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        bodyPanel.add(createInputGroup("Quyền hạn:", cbQuyen));

        dialog.add(bodyPanel, BorderLayout.CENTER);

        // Footer Form (Chứa nút Hủy và Lưu)
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        footerPanel.setBackground(Color.decode("#F8FAFC"));
        footerPanel.setBorder(new MatteBorder(1, 0, 0, 0, Color.decode("#E2E8F0")));

        JButton btnHuy = createStyledButton("Hủy bỏ", "#94A3B8", "#FFFFFF");
        JButton btnLuu = createStyledButton("Lưu tài khoản", "#10B981", "#FFFFFF");

        btnHuy.addActionListener(e -> dialog.dispose());

        // Logic xử lý khi bấm LƯU
        btnLuu.addActionListener(e -> {
            String id = txtId.getText().trim();
            String ten = txtTen.getText().trim();
            String pass = new String(txtPass.getPassword());
            String quyen = cbQuyen.getSelectedItem().toString();

            if (id.isEmpty() || ten.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // TODO: Gọi AccountDAO để lưu vào database
                // accountDTO newAcc = new accountDTO();
                // newAcc.setId(id);
                // newAcc.setTen(ten);
                // newAcc.setPass(hashPassword(pass));
                // newAcc.setQuyen(quyen);
                // accountDAO.insert(newAcc);

                // Tạm thời hiển thị lên bảng ngay lập tức
                model.addRow(new Object[]{id, ten, "********", quyen, ""});

                JOptionPane.showMessageDialog(dialog, "Thêm tài khoản thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Đã có lỗi xảy ra khi lưu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        footerPanel.add(btnHuy);
        footerPanel.add(btnLuu);
        dialog.add(footerPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private JPanel createInputGroup(String labelText, JComponent inputComponent) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(Color.decode("#475569"));

        panel.add(label, BorderLayout.NORTH);
        panel.add(inputComponent, BorderLayout.CENTER);
        return panel;
    }

    private JTextField createModernTextField() {
        JTextField tf = new JTextField();
        tf.setPreferredSize(new Dimension(200, 38));
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tf.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.decode("#CBD5E1"), 1, true),
                new EmptyBorder(5, 10, 5, 10)
        ));
        return tf;
    }

    private JButton createStyledButton(String text, String bgColor, String fgColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setBackground(Color.decode(bgColor));
        btn.setForeground(Color.decode(fgColor));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        return btn;
    }

    // =========================================================================
    // 4. CÁC LỚP RENDERER VÀ EDITOR CHO BẢNG
    // =========================================================================
    private class ActionPanel extends JPanel {
        JButton btnRole, btnDel;

        public ActionPanel() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 15, 5));
            setOpaque(true);

            btnRole = createIconButton("✏", "#10B981");
            btnRole.setToolTipText("Chỉnh sửa phân quyền");

            btnDel = createIconButton("🗑", "#EF4444");

            add(btnRole);
            add(btnDel);
        }

        private JButton createIconButton(String iconText, String hexColor) {
            JButton btn = new JButton(iconText);
            btn.setForeground(Color.decode(hexColor));
            btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
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

            if (hasFocus) {
                actionPanel.setBackground(Color.decode("#DBEAFE"));
            } else if (isSelected) {
                actionPanel.setBackground(Color.decode("#F1F5F9"));
            } else {
                actionPanel.setBackground(Color.WHITE);
            }

            return actionPanel;
        }
    }

    private class TableActionCellEditor extends DefaultCellEditor {
        private final ActionPanel actionPanel;
        private String currentAccountId;

        public TableActionCellEditor(JTable table) {
            super(new JCheckBox());
            actionPanel = new ActionPanel();

            actionPanel.btnRole.addActionListener(e -> {
                fireEditingStopped();
                if (navListener != null) {
                    navListener.goToPhanQuyen(currentAccountId);
                } else {
                    JOptionPane.showMessageDialog(null, "Bạn đã click Phân quyền cho ID: " + currentAccountId);
                }
            });

            actionPanel.btnDel.addActionListener(e -> {
                fireEditingStopped();
                int confirm = JOptionPane.showConfirmDialog(null, "Xóa tài khoản " + currentAccountId + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    System.out.println("Đã xóa " + currentAccountId);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentAccountId = table.getValueAt(row, 0).toString();
            actionPanel.setBackground(Color.decode("#DBEAFE"));
            return actionPanel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }
}
