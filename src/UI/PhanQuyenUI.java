package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;

public class PhanQuyenUI extends JPanel {

    // ==========================================================
    // 1. CÁC HÀM KHỞI TẠO (CONSTRUCTORS)
    // ==========================================================
    public PhanQuyenUI() {
        setLayout(new BorderLayout(0, 20));
        setBackground(Color.decode("#F3F4F6"));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initTopPanel();
        initTripleTableMatrix();
    }

    public PhanQuyenUI(String accountId) {
        this(); // Gọi lại constructor mặc định ở trên để vẽ giao diện

        // Xử lý logic khi nhận được accountId
        System.out.println("Đã chuyển sang trang phân quyền, focus vào tài khoản: " + accountId);

        // Hiển thị thông báo tạm thời để test
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, "Đang thiết lập quyền cho ID: " + accountId);
        });
    }

    // ==========================================================
    // 2. KHỞI TẠO GIAO DIỆN PHÍA TRÊN (TIÊU ĐỀ & NÚT BẤM)
    // ==========================================================
    private void initTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Ma trận phân quyền");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.decode("#1E293B"));

        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnGroup.setOpaque(false);
        btnGroup.add(createStyledButton("Tạo nhóm quyền mới", "#3B82F6"));

        topPanel.add(lblTitle, BorderLayout.WEST);
        topPanel.add(btnGroup, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
    }

    // ==========================================================
    // 3. KHỞI TẠO BẢNG CHIA 3 PHẦN (Cố định trái/phải, cuộn giữa)
    // ==========================================================
    private void initTripleTableMatrix() {
        // Khung trắng bo góc chứa bảng
        JPanel whiteBox = new JPanel(new BorderLayout());
        whiteBox.setBackground(Color.WHITE);
        whiteBox.setBorder(new LineBorder(Color.decode("#E2E8F0"), 1));

        // 1. Định nghĩa dữ liệu
        String[] allCols = {"Tên nhóm quyền", "CN01", "CN02", "CN03", "CN04", "CN05", "CN06", "CN07", "Thao tác"};
        Object[][] data = {
                {"<html><b>Administrator</b><br><font color='gray'>Q01</font></html>", true, true, true, true, true, true, true, ""},
                {"<html><b>Sales Manager</b><br><font color='gray'>Q02</font></html>", false, true, true, false, false, true, false, ""},
                {"<html><b>Inventory Specialist</b><br><font color='gray'>Q03</font></html>", true, false, false, false, false, false, true, ""},
                {"<html><b>Accountant</b><br><font color='gray'>Q04</font></html>", false, false, true, false, false, true, false, ""},
                {"<html><b>Basic Staff</b><br><font color='gray'>Q05</font></html>", false, true, false, false, false, false, false, ""}
        };

        // 2. Tạo Model dùng chung
        DefaultTableModel model = new DefaultTableModel(data, allCols) {
            @Override
            public Class<?> getColumnClass(int col) {
                if (col >= 1 && col <= 7) return Boolean.class;
                return super.getColumnClass(col);
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                // Chỉ cho phép edit cột Boolean và cột Thao tác
                return column >= 1 && column <= 8;
            }
        };

        // 3. Tạo 3 bảng: Trái (Cố định), Giữa (Cuộn), Phải (Cố định)
        JTable leftTable = createSubTable(model, new int[]{0});
        JTable centerTable = createSubTable(model, new int[]{1, 2, 3, 4, 5, 6, 7});
        JTable rightTable = createSubTable(model, new int[]{8});

        // Thiết lập kích thước các cột
        leftTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        leftTable.getColumnModel().getColumn(0).setHeaderValue(getHtmlHeader("Nhóm quyền", "Mã quyền"));

        rightTable.getColumnModel().getColumn(0).setPreferredWidth(160);
        rightTable.getColumnModel().getColumn(0).setHeaderValue("THAO TÁC");

        // --- QUAN TRỌNG: GÁN RENDERER VÀ EDITOR CHO CỘT THAO TÁC (Ở rightTable) ---
        rightTable.getColumnModel().getColumn(0).setCellRenderer(new ActionButtonsRenderer());
        rightTable.getColumnModel().getColumn(0).setCellEditor(new ActionButtonsEditor(rightTable));

        // Format bảng giữa
        centerTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for(int i = 0; i < centerTable.getColumnCount(); i++) {
            centerTable.getColumnModel().getColumn(i).setPreferredWidth(120);
            centerTable.getColumnModel().getColumn(i).setHeaderValue(getHtmlHeader("CN0" + (i+1), "Chức năng " + (i+1)));
        }

        // Đồng bộ hóa việc chọn dòng giữa 3 bảng
        ListSelectionModel selectionModel = leftTable.getSelectionModel();
        centerTable.setSelectionModel(selectionModel);
        rightTable.setSelectionModel(selectionModel);

        // Bỏ viền ScrollPane
        JScrollPane centerScroll = new JScrollPane(centerTable);
        centerScroll.setBorder(BorderFactory.createEmptyBorder());
        centerScroll.getViewport().setBackground(Color.WHITE);

        // 4. Sắp xếp vào Layout
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setOpaque(false);

        // Panel Trái
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(leftTable.getTableHeader(), BorderLayout.NORTH);
        leftPanel.add(leftTable, BorderLayout.CENTER);

        // Panel Phải
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(rightTable.getTableHeader(), BorderLayout.NORTH);
        rightPanel.add(rightTable, BorderLayout.CENTER);

        mainContent.add(leftPanel, BorderLayout.WEST);
        mainContent.add(centerScroll, BorderLayout.CENTER);
        mainContent.add(rightPanel, BorderLayout.EAST);

        whiteBox.add(mainContent, BorderLayout.CENTER);
        add(whiteBox, BorderLayout.CENTER);
    }

    // ==========================================================
    // 4. CÁC HÀM TIỆN ÍCH (HELPER METHODS)
    // ==========================================================

    // Hàm tạo bảng con từ model chính (Trích xuất các cột cần thiết)
    private JTable createSubTable(DefaultTableModel mainModel, int[] columnIndices) {
        DefaultTableModel subModel = new DefaultTableModel(mainModel.getRowCount(), columnIndices.length) {
            @Override
            public Class<?> getColumnClass(int col) {
                return mainModel.getColumnClass(columnIndices[col]);
            }
            @Override
            public boolean isCellEditable(int row, int col) {
                return mainModel.isCellEditable(row, columnIndices[col]);
            }
            @Override
            public void setValueAt(Object aValue, int row, int col) {
                mainModel.setValueAt(aValue, row, columnIndices[col]);
                fireTableCellUpdated(row, col);
            }
            @Override
            public Object getValueAt(int row, int col) {
                return mainModel.getValueAt(row, columnIndices[col]);
            }
        };

        JTable table = new JTable(subModel);
        table.setRowHeight(60);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setGridColor(Color.decode("#F1F5F9"));
        table.setSelectionBackground(Color.decode("#EFF6FF"));
        table.setSelectionForeground(Color.BLACK);

        // Format Header chung
        table.getTableHeader().setPreferredSize(new Dimension(0, 50));
        table.getTableHeader().setBackground(Color.decode("#F8FAFC"));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setForeground(Color.decode("#475569"));

        // Căn lề các ô văn bản
        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setBorder(new EmptyBorder(0, 15, 0, 0));

        for (int i = 0; i < table.getColumnCount(); i++) {
            if (table.getColumnClass(i) == String.class) {
                table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
            }
        }

        return table;
    }

    private String getHtmlHeader(String code, String name) {
        return "<html><center><b style='color:#1E293B'>" + code + "</b><br><font color='#64748B' size='3'>" + name + "</font></center></html>";
    }

    private JButton createStyledButton(String text, String hexColor) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.decode(hexColor));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(8, 15, 8, 15));
        return btn;
    }

    // ==========================================================
    // 5. LỚP VẼ NÚT BẤM CHO BẢNG (CHỈ ĐỂ HIỂN THỊ ĐẸP)
    // ==========================================================
    class ActionButtonsRenderer extends JPanel implements TableCellRenderer {
        public ActionButtonsRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 8, 15));
            setOpaque(true);
            JLabel lblDetail = new JLabel("Chi tiết");
            JLabel lblEdit = new JLabel("✏");
            JLabel lblDel = new JLabel("🗑");

            lblDetail.setForeground(Color.BLUE);
            lblDetail.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lblEdit.setForeground(Color.decode("#10B981")); // Đổi màu bút chì sang xanh lá cho đẹp
            lblEdit.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            lblDel.setForeground(Color.RED);
            lblDel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

            add(lblDetail);
            add(lblEdit);
            add(lblDel);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return this;
        }
    }

    // ==========================================================
    // 6. LỚP XỬ LÝ SỰ KIỆN CLICK (BIẾN Ô THÀNH NÚT THỰC SỰ)
    // ==========================================================
    class ActionButtonsEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton btnDetail, btnEdit, btnDel;
        private JTable table;

        public ActionButtonsEditor(JTable rightTable) {
            super(new JCheckBox());
            this.table = rightTable;

            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 15));
            panel.setOpaque(true);

            btnDetail = createIconButton("Chi tiết", Color.BLUE);
            btnEdit = createIconButton("✏", Color.decode("#10B981"));
            btnDel = createIconButton("🗑", Color.RED);

            btnEdit.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            btnDel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

            btnDetail.addActionListener(e -> {
                fireEditingStopped();
                int row = table.getSelectedRow();
                JOptionPane.showMessageDialog(panel, "Bạn đang xem Chi tiết nhóm quyền dòng: " + (row + 1));
            });

            btnEdit.addActionListener(e -> {
                fireEditingStopped();
                int row = table.getSelectedRow();
                JOptionPane.showMessageDialog(panel, "Sửa quyền dòng: " + (row + 1));
            });

            btnDel.addActionListener(e -> {
                fireEditingStopped();
                int row = table.getSelectedRow();
                int confirm = JOptionPane.showConfirmDialog(panel, "Xóa nhóm quyền dòng " + (row + 1) + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if(confirm == JOptionPane.YES_OPTION) {
                    System.out.println("Đã xóa dòng " + (row + 1));
                }
            });

            panel.add(btnDetail);
            panel.add(btnEdit);
            panel.add(btnDel);
        }

        private JButton createIconButton(String text, Color color) {
            JButton btn = new JButton(text);
            btn.setForeground(color);
            btn.setContentAreaFilled(false);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setMargin(new Insets(0, 0, 0, 0));
            return btn;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }
}
