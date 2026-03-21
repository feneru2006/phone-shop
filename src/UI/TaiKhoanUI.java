package UI;

import DAL.DAO.AccountDAO;
import DTO.accountDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
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
                return column == 3 || column == 4;
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

                if (column == 3 || column == 4) {
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

        for (int i = 0; i < 3; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        table.getColumnModel().getColumn(3).setCellRenderer(new RoleCellRenderer());
        table.getColumnModel().getColumn(3).setCellEditor(new RoleCellEditor(table));

        table.getColumnModel().getColumn(4).setCellRenderer(new TableActionCellRender());
        table.getColumnModel().getColumn(4).setCellEditor(new TableActionCellEditor(table));

        table.getColumnModel().getColumn(0).setMinWidth(80);
        table.getColumnModel().getColumn(0).setMaxWidth(80);
        table.getColumnModel().getColumn(0).setPreferredWidth(80);

        table.getColumnModel().getColumn(1).setMinWidth(250);
        table.getColumnModel().getColumn(1).setPreferredWidth(250);

        table.getColumnModel().getColumn(2).setMinWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);

        table.getColumnModel().getColumn(3).setMinWidth(120);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);

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
    // 3. CÁC FORM: THÊM, SỬA TÀI KHOẢN, SỬA QUYỀN
    // ==========================================================
    private void showAddAccountDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm tài khoản mới", true);
        dialog.setSize(420, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.decode("#2563EB"));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel lblTitle = new JLabel("THÊM TÀI KHOẢN MỚI");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.CENTER);
        dialog.add(headerPanel, BorderLayout.NORTH);

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

        String[] danhSachQuyen = {
                "AD - Admin",
                "CC - Chăm sóc khách hàng",
                "M - Quản lý",
                "PM - Quản lý sản phẩm",
                "SA - Nhân viên bán hàng",
                "WM - Quản lý kho"
        };
        JComboBox<String> cbQuyen = new JComboBox<>(danhSachQuyen);
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

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        footerPanel.setBackground(Color.decode("#F8FAFC"));
        footerPanel.setBorder(new MatteBorder(1, 0, 0, 0, Color.decode("#E2E8F0")));

        JButton btnHuy = createStyledButton("Hủy bỏ", "#94A3B8", "#FFFFFF");
        JButton btnLuu = createStyledButton("Lưu tài khoản", "#10B981", "#FFFFFF");

        btnHuy.addActionListener(e -> dialog.dispose());

        btnLuu.addActionListener(e -> {
            String id = txtId.getText().trim();
            String ten = txtTen.getText().trim();
            String pass = new String(txtPass.getPassword());

            String quyenFull = cbQuyen.getSelectedItem().toString();
            String quyen = quyenFull.split(" - ")[0].trim();

            if (id.isEmpty() || ten.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                accountDTO newAcc = new accountDTO();
                newAcc.setId(id);
                newAcc.setTen(ten);
                newAcc.setPass(hashPassword(pass));
                newAcc.setQuyen(quyen);
                boolean isSuccess = accountDAO.insert(newAcc);

                if (isSuccess) {
                    loadDataToTable();
                    JOptionPane.showMessageDialog(dialog, "Thêm tài khoản thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Thêm thất bại! Kiểm tra lại mã ID (không được trùng).", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Đã có lỗi xảy ra khi lưu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        footerPanel.add(btnHuy);
        footerPanel.add(btnLuu);
        dialog.add(footerPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void showEditAccountDialog(String oldId, String oldTen, String oldQuyen) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chỉnh sửa tài khoản", true);
        dialog.setSize(420, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.decode("#10B981"));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel lblTitle = new JLabel("CHỈNH SỬA TÀI KHOẢN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.CENTER);
        dialog.add(headerPanel, BorderLayout.NORTH);

        JPanel bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setBackground(Color.WHITE);
        bodyPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        JTextField txtId = createModernTextField();
        txtId.setText(oldId);
        txtId.setEditable(false);
        txtId.setBackground(Color.decode("#F1F5F9"));

        JTextField txtTen = createModernTextField();
        txtTen.setText(oldTen);

        JPasswordField txtPass = new JPasswordField();
        txtPass.setBorder(txtId.getBorder());
        txtPass.setPreferredSize(new Dimension(200, 38));
        txtPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        bodyPanel.add(createInputGroup("Mã tài khoản (ID) - Không thể sửa:", txtId));
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        bodyPanel.add(createInputGroup("Tên đăng nhập:", txtTen));
        bodyPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        bodyPanel.add(createInputGroup("Mật khẩu (Nhập MK mới để đổi):", txtPass));

        dialog.add(bodyPanel, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        footerPanel.setBackground(Color.decode("#F8FAFC"));
        footerPanel.setBorder(new MatteBorder(1, 0, 0, 0, Color.decode("#E2E8F0")));

        JButton btnHuy = createStyledButton("Hủy bỏ", "#94A3B8", "#FFFFFF");
        JButton btnLuu = createStyledButton("Cập nhật", "#10B981", "#FFFFFF");

        btnHuy.addActionListener(e -> dialog.dispose());

        btnLuu.addActionListener(e -> {
            String id = txtId.getText().trim();
            String ten = txtTen.getText().trim();
            String pass = new String(txtPass.getPassword());

            if (ten.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đầy đủ thông tin và mật khẩu xác nhận!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                accountDTO upAcc = new accountDTO();
                upAcc.setId(id);
                upAcc.setTen(ten);
                upAcc.setPass(hashPassword(pass));
                upAcc.setQuyen(oldQuyen);

                boolean isSuccess = accountDAO.update(upAcc);

                if (isSuccess) {
                    loadDataToTable();
                    JOptionPane.showMessageDialog(dialog, "Cập nhật tài khoản thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Đã có lỗi xảy ra khi cập nhật!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        footerPanel.add(btnHuy);
        footerPanel.add(btnLuu);
        dialog.add(footerPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void showEditRoleDialog(String id, String ten, String oldQuyen) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thay đổi quyền", true);
        dialog.setSize(350, 240);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.decode("#F59E0B"));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        JLabel lblTitle = new JLabel("THAY ĐỔI QUYỀN HẠN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);
        headerPanel.add(lblTitle, BorderLayout.CENTER);
        dialog.add(headerPanel, BorderLayout.NORTH);

        JPanel bodyPanel = new JPanel();
        bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.Y_AXIS));
        bodyPanel.setBackground(Color.WHITE);
        bodyPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        String[] danhSachQuyen = {
                "AD - Admin",
                "CC - Chăm sóc khách hàng",
                "M - Quản lý",
                "PM - Quản lý sản phẩm",
                "SA - Nhân viên bán hàng",
                "WM - Quản lý kho"
        };
        JComboBox<String> cbQuyen = new JComboBox<>(danhSachQuyen);
        cbQuyen.setBackground(Color.WHITE);
        cbQuyen.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cbQuyen.setPreferredSize(new Dimension(200, 38));

        for (int i = 0; i < cbQuyen.getItemCount(); i++) {
            if (cbQuyen.getItemAt(i).startsWith(oldQuyen)) {
                cbQuyen.setSelectedIndex(i);
                break;
            }
        }

        bodyPanel.add(createInputGroup("Chọn quyền mới cho ID: " + id, cbQuyen));
        dialog.add(bodyPanel, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        footerPanel.setBackground(Color.decode("#F8FAFC"));
        footerPanel.setBorder(new MatteBorder(1, 0, 0, 0, Color.decode("#E2E8F0")));

        JButton btnHuy = createStyledButton("Hủy", "#94A3B8", "#FFFFFF");
        JButton btnLuu = createStyledButton("Lưu quyền", "#F59E0B", "#FFFFFF");

        btnHuy.addActionListener(e -> dialog.dispose());

        btnLuu.addActionListener(e -> {
            String newQuyen = cbQuyen.getSelectedItem().toString().split(" - ")[0].trim();
            try {
                accountDTO acc = accountDAO.findByUsername(ten);
                if (acc != null) {
                    acc.setQuyen(newQuyen);
                    boolean isSuccess = accountDAO.update(acc);
                    if (isSuccess) {
                        loadDataToTable();
                        JOptionPane.showMessageDialog(dialog, "Thay đổi quyền thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Cập nhật thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog, "Không tìm thấy tài khoản trong Database!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi kết nối khi cập nhật quyền!", "Lỗi", JOptionPane.ERROR_MESSAGE);
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

    private class RolePanel extends JPanel {
        JLabel lblRole;
        JButton btnEditRole;

        public RolePanel() {
            setLayout(new BorderLayout());
            setOpaque(true);

            lblRole = new JLabel();
            lblRole.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lblRole.setForeground(Color.decode("#3B82F6"));
            lblRole.setBorder(new EmptyBorder(0, 20, 0, 0));

            btnEditRole = new JButton("⚙️");
            btnEditRole.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            btnEditRole.setForeground(Color.decode("#64748B"));
            btnEditRole.setContentAreaFilled(false);
            btnEditRole.setBorderPainted(false);
            btnEditRole.setFocusPainted(false);
            btnEditRole.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnEditRole.setMargin(new Insets(0, 0, 0, 10));

            add(lblRole, BorderLayout.CENTER);
            add(btnEditRole, BorderLayout.EAST);
        }
    }

    private class RoleCellRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            RolePanel rolePanel = new RolePanel();
            rolePanel.lblRole.setText(value != null ? value.toString() : "");

            if (hasFocus) {
                rolePanel.setBackground(Color.decode("#DBEAFE"));
            } else if (isSelected) {
                rolePanel.setBackground(Color.decode("#F1F5F9"));
            } else {
                rolePanel.setBackground(Color.WHITE);
            }
            return rolePanel;
        }
    }

    private class RoleCellEditor extends DefaultCellEditor {
        private final RolePanel rolePanel;
        private String currentId, currentTen, currentQuyen;

        public RoleCellEditor(JTable table) {
            super(new JCheckBox());
            rolePanel = new RolePanel();

            rolePanel.btnEditRole.addActionListener(e -> {
                fireEditingStopped();
                // [CHẶN: KHÔNG CHO PHÉP ĐỔI QUYỀN ADMIN]
                if (currentQuyen.equals("AD")) {
                    JOptionPane.showMessageDialog(null, "Tài khoản Admin (AD) là tối thượng, không thể thay đổi quyền!", "Từ chối truy cập", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                showEditRoleDialog(currentId, currentTen, currentQuyen);
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentQuyen = value != null ? value.toString() : "";
            currentId = table.getValueAt(row, 0).toString();
            currentTen = table.getValueAt(row, 1).toString();

            rolePanel.lblRole.setText(currentQuyen);
            rolePanel.setBackground(Color.decode("#DBEAFE"));
            return rolePanel;
        }

        @Override
        public Object getCellEditorValue() {
            return currentQuyen;
        }
    }

    private class ActionPanel extends JPanel {
        JButton btnRole, btnDel;

        public ActionPanel() {
            setLayout(new GridBagLayout());
            setOpaque(true);

            btnRole = createIconButton("✏", "#10B981");
            btnRole.setToolTipText("Chỉnh sửa thông tin tài khoản");

            btnDel = createIconButton("🗑", "#EF4444");
            btnDel.setToolTipText("Xóa tài khoản");

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, 7, 0, 7);

            add(btnRole, gbc);
            add(btnDel, gbc);
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

    private class TableActionCellRender implements TableCellRenderer {
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
                int row = table.getSelectedRow();
                if (row != -1) {
                    String id = table.getValueAt(row, 0).toString();
                    String ten = table.getValueAt(row, 1).toString();
                    String quyen = table.getValueAt(row, 3).toString();

                    // [CHẶN: KHÔNG CHO PHÉP SỬA THÔNG TIN ADMIN]
                    if (quyen.equals("AD")) {
                        JOptionPane.showMessageDialog(null, "Tài khoản Admin (AD) là tối thượng, không thể sửa!", "Từ chối truy cập", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    showEditAccountDialog(id, ten, quyen);
                }
            });

            actionPanel.btnDel.addActionListener(e -> {
                fireEditingStopped();
                int row = table.getSelectedRow();
                if (row != -1) {
                    String quyen = table.getValueAt(row, 3).toString();

                    // [CHẶN: KHÔNG CHO PHÉP XÓA ADMIN]
                    if (quyen.equals("AD")) {
                        JOptionPane.showMessageDialog(null, "Tài khoản Admin (AD) là tối thượng, không thể xóa!", "Từ chối truy cập", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    int confirm = JOptionPane.showConfirmDialog(null, "Xóa tài khoản " + currentAccountId + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean isDeleted = accountDAO.delete(currentAccountId);
                        if (isDeleted){
                            loadDataToTable();
                            JOptionPane.showMessageDialog(null,"Đã xóa thành công");
                        }else {
                            JOptionPane.showMessageDialog(null,"Xóa thất bại!","Lỗi",JOptionPane.ERROR_MESSAGE);
                        }
                    }
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
