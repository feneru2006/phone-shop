package UI;

import DAL.DAO.AccountDAO;
import DTO.accountDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10); // Bo góc icon 10px
                super.paintComponent(g);
            }
        };
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        lblIcon.setForeground(Color.decode("#2563EB"));
        lblIcon.setPreferredSize(new Dimension(36, 36));

        JLabel lblTitle = new JLabel("TÀI KHOẢN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.decode("#0F172A"));

        // =======================================================
        // Bo góc tối đa cho Badge "SYSTEM LIVE" thành hình viên thuốc (pill)
        // =======================================================
        leftFlow.add(lblIcon);
        leftFlow.add(lblTitle);

        JPanel rightFlow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightFlow.setOpaque(false);

        JButton btnThem = createStyledButton("+  THÊM MỚI", "#2563EB", "#FFFFFF");
        btnThem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Nút thêm mới được click!");
        });

        rightFlow.add(btnThem);

        topPanel.add(leftFlow, BorderLayout.WEST);
        topPanel.add(rightFlow, BorderLayout.EAST);
        return topPanel;
    }

    private JPanel createTablePanel() {
        // =======================================================
        // Bo góc cho khung chứa bảng dữ liệu (12px)
        // =======================================================
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

        // Chỉ chọn 1 hàng tại một thời điểm
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // =======================================================
        // CĂN CHỈNH STYLE VÀ KHÓA VỊ TRÍ CỘT CHO HEADER
        // =======================================================
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(Color.decode("#F8FAFC"));
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                label.setForeground(Color.decode("#475569"));

                // Nếu là cột "THAO TÁC" (cột số 4) -> Căn giữa
                if (column == 4) {
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    label.setBorder(new MatteBorder(0, 0, 1, 0, Color.decode("#E2E8F0")));
                }
                // Các cột khác -> Căn trái & lùi vào 20px (Padding)
                else {
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

        // =======================================================
        // XỬ LÝ HIGHLIGHT 2 MÀU (Ô ĐƯỢC CHỌN VÀ HÀNG ĐƯỢC CHỌN)
        // =======================================================
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Set padding rỗng để ghi đè làm mất đi viền focus chấm chấm mặc định của Java
                setBorder(new EmptyBorder(0, 20, 0, 0));

                // Tô màu
                if (hasFocus) {
                    // Màu cho ô chính xác đang được click (Xanh lam pastel rất nhạt)
                    c.setBackground(Color.decode("#DBEAFE"));
                } else if (isSelected) {
                    // Màu cho các ô còn lại trên cùng hàng được chọn (Xám xanh siêu nhạt)
                    c.setBackground(Color.decode("#F1F5F9"));
                } else {
                    // Màu mặc định
                    c.setBackground(Color.WHITE);
                }

                c.setForeground(Color.decode("#334155"));
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                return c;
            }
        };

        for (int i = 0; i < 4; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        // GÁN BỘ RENDERER VÀ EDITOR ĐỂ TẠO NÚT BẤM VÀ HIGHLIGHT CHO CỘT 4
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

    // =======================================================
    // Tùy chỉnh vẽ bo góc cho Button
    // =======================================================
    private JButton createStyledButton(String text, String bgColor, String fgColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10); // Bo góc nút bấm
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setBackground(Color.decode(bgColor));
        btn.setForeground(Color.decode(fgColor));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false); // Quan trọng để hiển thị màu bo góc nền dưới
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        return btn;
    }

    // =========================================================================
    // CÁC LỚP BỔ TRỢ ĐỂ TẠO NÚT BẤM TRONG JTABLE (SỬA, PHÂN QUYỀN, XÓA)
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

            // Xử lý màu cho cột thao tác đồng bộ với các cột khác
            if (hasFocus) {
                actionPanel.setBackground(Color.decode("#DBEAFE")); // Ô đang click
            } else if (isSelected) {
                actionPanel.setBackground(Color.decode("#F1F5F9")); // Hàng đang chọn
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
                // CHUYỂN TRANG
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
            currentAccountId = table.getValueAt(row, 0).toString(); // Lấy ID
            // Khi ô Thao tác đang trong trạng thái Editor (bị click vào) thì tô màu highlight của cell
            actionPanel.setBackground(Color.decode("#DBEAFE"));
            return actionPanel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }
}
