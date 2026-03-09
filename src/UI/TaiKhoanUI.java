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
import java.util.ArrayList;

public class TaiKhoanUI extends JPanel {

    private DefaultTableModel model;
    private JTable table;
    private JLabel lblRows;
    private final AccountDAO accountDAO = new AccountDAO();

    public TaiKhoanUI() {
        setBackground(Color.decode("#F8FAFC"));
        setLayout(new BorderLayout(0, 20));
        setBorder(new EmptyBorder(25, 30, 25, 30));

        initTopPanel();
        initBottomPanel();
        initTablePanel();
    }

    private void initTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JPanel leftFlow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        leftFlow.setOpaque(false);

        JLabel lblIcon = new JLabel("🔑");
        lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 22));
        lblIcon.setForeground(Color.decode("#3B82F6"));

        JLabel lblTitle = new JLabel("TÀI KHOẢN");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.decode("#1E293B"));

        leftFlow.add(lblIcon);
        leftFlow.add(lblTitle);

        JPanel rightFlow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightFlow.setOpaque(false);

        JButton btnThem = createStyledButton("+  THÊM MỚI BẢN GHI", "#3B82F6", "#FFFFFF");
        JButton btnCauHinh = createStyledButton("⚙  CẤU HÌNH", "#F1F5F9", "#475569");
        btnCauHinh.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.decode("#E2E8F0"), 1),
                new EmptyBorder(8, 15, 8, 15)
        ));

        rightFlow.add(btnThem);
        rightFlow.add(btnCauHinh);

        topPanel.add(leftFlow, BorderLayout.WEST);
        topPanel.add(rightFlow, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
    }

    private void initBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JPanel leftFlow = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        leftFlow.setOpaque(false);

        lblRows = new JLabel("   ROWS: 0");
        lblRows.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRows.setForeground(Color.decode("#3B82F6"));

        leftFlow.add(lblRows);

        JPanel rightFlow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightFlow.setOpaque(false);

        JButton btnPrev = createPageButton("«", false);
        JButton btnPage1 = createPageButton("1", true);
        JButton btnNext = createPageButton("»", false);

        rightFlow.add(btnPrev);
        rightFlow.add(btnPage1);
        rightFlow.add(btnNext);

        bottomPanel.add(leftFlow, BorderLayout.WEST);
        bottomPanel.add(rightFlow, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void initTablePanel() {
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.decode("#E2E8F0"), 1, true),
                new EmptyBorder(5, 5, 5, 5)
        ));

        String[] cols = {"ID", "TEN", "PASS", "QUYEN", "THAO TAC"};

        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(50);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(Color.decode("#F1F5F9"));
        table.setBorder(BorderFactory.createLineBorder(Color.decode("#E2E8F0"), 1));

        // --- CĂN LỀ TIÊU ĐỀ (HEADER) CÁCH TRÁI 15PX ---
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(Color.decode("#F8FAFC"));
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                label.setForeground(Color.decode("#64748B"));
                label.setHorizontalAlignment(SwingConstants.LEFT);
                // Tạo Border: ngoài là đường kẻ, trong là padding 15px
                label.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(Color.decode("#E2E8F0"), 1),
                        new EmptyBorder(0, 15, 0, 0)
                ));
                return label;
            }
        });
        table.getTableHeader().setPreferredSize(new Dimension(0, 45));

        // --- CĂN LỀ NỘI DUNG Ô (CELLS) CÁCH TRÁI 15PX ---
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(BorderFactory.createCompoundBorder(
                        new MatteBorder(0, 0, 1, 1, Color.decode("#E2E8F0")),
                        new EmptyBorder(0, 15, 0, 15) // Cách trái 15px
                ));

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : Color.decode("#F8FAFC"));
                } else {
                    c.setBackground(Color.decode("#E0F2FE"));
                }
                c.setForeground(Color.decode("#334155"));
                setFont(new Font("Segoe UI", Font.PLAIN, 14));
                return c;
            }
        };
        cellRenderer.setHorizontalAlignment(SwingConstants.LEFT);

        for (int i = 0; i < table.getColumnCount() - 1; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        // --- CĂN LỀ CỘT THAO TÁC CÁCH TRÁI 15PX ---
        table.getColumnModel().getColumn(4).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10)); // Hgap 15px đồng bộ
                actionPanel.setOpaque(true);
                if (!isSelected) {
                    actionPanel.setBackground(row % 2 == 0 ? Color.WHITE : Color.decode("#F8FAFC"));
                } else {
                    actionPanel.setBackground(Color.decode("#E0F2FE"));
                }

                JLabel lblEdit = new JLabel("<html><font size='4'>☑</font></html>");
                lblEdit.setForeground(Color.decode("#10B981"));
                lblEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));

                JLabel lblDel = new JLabel("<html><font size='4'>🗑</font></html>");
                lblDel.setForeground(Color.decode("#EF4444"));
                lblDel.setCursor(new Cursor(Cursor.HAND_CURSOR));

                actionPanel.add(lblEdit);
                actionPanel.add(lblDel);
                return actionPanel;
            }
        });

        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(250);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        cardPanel.add(scrollPane, BorderLayout.CENTER);
        add(cardPanel, BorderLayout.CENTER);

        loadDataToTable();
    }

    private void loadDataToTable() {
        model.setRowCount(0);
        ArrayList<accountDTO> list = accountDAO.selectAll();
        if (list != null) {
            for (accountDTO acc : list) {
                model.addRow(new Object[]{acc.getId(), acc.getTen(), "********", acc.getQuyen(), ""});
            }
            lblRows.setText("   ROWS: " + list.size());
        }
    }

    private JButton createStyledButton(String text, String bgColor, String fgColor) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.decode(bgColor));
        btn.setForeground(Color.decode(fgColor));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        return btn;
    }

    private JButton createPageButton(String text, boolean isActive) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(35, 35));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (isActive) {
            btn.setBackground(Color.decode("#3B82F6"));
            btn.setForeground(Color.WHITE);
            btn.setBorder(new LineBorder(Color.decode("#3B82F6")));
        } else {
            btn.setBackground(Color.WHITE);
            btn.setForeground(Color.decode("#64748B"));
            btn.setBorder(new LineBorder(Color.decode("#CBD5E1")));
        }
        return btn;
    }

}