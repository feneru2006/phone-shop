package UI;

import DAL.DAO.AccountDAO;
import DTO.accountDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class TaiKhoanUI extends JPanel {

    private DefaultTableModel model;
    private JTable table;
    private final AccountDAO accountDAO = new AccountDAO();

    public TaiKhoanUI() {
        // Nền tổng thể màu xám nhạt như trong hình
        setBackground(Color.decode("#F3F4F6"));
        setLayout(new BorderLayout());

        // 1. Thanh Tabs (Quản lý chính & Kỹ thuật)
        add(createTabPanel(), BorderLayout.NORTH);

        // 2. Nội dung chính bọc trong nền trắng
        JPanel mainContent = createMainContent();
        add(mainContent, BorderLayout.CENTER);

        loadDataToTable();
    }

    private JPanel createTabPanel() {
        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabPanel.setBackground(Color.decode("#F8FAFC"));
        tabPanel.setBorder(new MatteBorder(0, 0, 1, 0, Color.decode("#E2E8F0")));

        // Tab Active: Quản lý chính
        JLabel lblTab1 = new JLabel("  \uD83D\uDDC3 Quản lý chính  ", SwingConstants.CENTER);
        lblTab1.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTab1.setForeground(Color.decode("#2563EB")); // Màu xanh Blue
        lblTab1.setPreferredSize(new Dimension(140, 40));
        lblTab1.setBorder(BorderFactory.createCompoundBorder(
                new MatteBorder(0, 1, 1, 1, Color.decode("#E2E8F0")),
                new MatteBorder(0, 0, 2, 0, Color.decode("#2563EB")) // Gạch chân xanh
        ));
        lblTab1.setOpaque(true);
        lblTab1.setBackground(Color.WHITE);

        // Tab Inactive: Kỹ thuật
        JLabel lblTab2 = new JLabel("  \u2699 Kỹ thuật  ", SwingConstants.CENTER);
        lblTab2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblTab2.setForeground(Color.decode("#64748B")); // Màu xám
        lblTab2.setPreferredSize(new Dimension(100, 40));

        tabPanel.add(lblTab1);
        tabPanel.add(lblTab2);
        return tabPanel;
    }

    private JPanel createMainContent() {
        // Panel bao bọc bên ngoài để tạo khoảng cách (padding) với khung xám
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setOpaque(false);
        wrapperPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Panel trắng chứa nội dung bên trong
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

        // Phần Header (Title + Buttons)
        whiteBox.add(createBoxHeader(), BorderLayout.NORTH);

        // Phần Bảng (Table)
        whiteBox.add(createTablePanel(), BorderLayout.CENTER);

        wrapperPanel.add(whiteBox, BorderLayout.CENTER);
        return wrapperPanel;
    }

    private JPanel createBoxHeader() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        // --- CỤM BÊN TRÁI: ICON, TITLE, TRẠNG THÁI ---
        JPanel leftFlow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftFlow.setOpaque(false);

        // Icon chìa khoá có nền xanh nhạt
        JLabel lblIcon = new JLabel(" \uD83D\uDDDD ", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.decode("#EFF6FF")); // Xanh dương rất nhạt
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

        leftFlow.add(lblIcon);
        leftFlow.add(lblTitle);

        // --- CỤM BÊN PHẢI: NÚT BẤM ---
        JPanel rightFlow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightFlow.setOpaque(false);

        JButton btnThem = createStyledButton("+  THÊM MỚI BẢN GHI", "#2563EB", "#FFFFFF");

        rightFlow.add(btnThem);

        topPanel.add(leftFlow, BorderLayout.WEST);
        topPanel.add(rightFlow, BorderLayout.EAST);
        return topPanel;
    }

    private JPanel createTablePanel() {
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setOpaque(false);
        tableContainer.setBorder(new LineBorder(Color.decode("#E2E8F0"), 1, true));

        String[] cols = {"ID", "TEN", "PASS", "QUYEN", "THAO TÁC"};

        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(45);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(Color.decode("#F1F5F9"));
        table.setSelectionBackground(Color.decode("#EFF6FF"));

        // --- STYLE HEADER ---
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(Color.decode("#F8FAFC"));
                label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                label.setForeground(Color.decode("#475569"));
                label.setHorizontalAlignment(SwingConstants.LEFT);
                label.setBorder(BorderFactory.createCompoundBorder(
                        new MatteBorder(0, 0, 1, 0, Color.decode("#E2E8F0")),
                        new EmptyBorder(0, 20, 0, 0)
                ));
                return label;
            }
        });
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));

        // --- STYLE CELLS ---
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 20, 0, 0)); // Căn lề trái
                c.setBackground(Color.WHITE); // Trong hình các dòng đều là nền trắng
                c.setForeground(Color.decode("#334155"));
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount() - 1; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        // --- CỘT THAO TÁC (Sửa & Xoá) ---
        table.getColumnModel().getColumn(4).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
                actionPanel.setBackground(isSelected ? Color.decode("#EFF6FF") : Color.WHITE);

                // Icon Sửa (Xanh lá)
                JLabel lblEdit = new JLabel("<html><font size='4'>&#x270E;</font></html>");
                lblEdit.setForeground(Color.decode("#10B981"));

                // Icon Xoá (Đỏ)
                JLabel lblDel = new JLabel("<html><font size='4'>&#x1F5D1;</font></html>");
                lblDel.setForeground(Color.decode("#EF4444"));

                actionPanel.add(lblEdit);
                actionPanel.add(lblDel);
                return actionPanel;
            }
        });

        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(250);
        table.getColumnModel().getColumn(2).setPreferredWidth(200);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);

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
}