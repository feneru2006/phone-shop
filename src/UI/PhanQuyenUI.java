package UI;

import DAL.DAO.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        this();

        System.out.println("Đã chuyển sang trang phân quyền, focus vào tài khoản: " + accountId);

        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, "Đang thiết lập quyền cho ID: " + accountId);
        });
    }

    // ==========================================================
    // 2. KHỞI TẠO GIAO DIỆN PHÍA TRÊN
    // ==========================================================
    private void initTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Ma trận phân quyền");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.decode("#1E293B"));

        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnGroup.setOpaque(false);

        JButton btnSave = createStyledButton("Lưu thay đổi", "#10B981"); // Thêm nút lưu
        JButton btnAdd = createStyledButton("Tạo nhóm quyền mới", "#3B82F6");

        btnGroup.add(btnSave);
        btnGroup.add(btnAdd);

        topPanel.add(lblTitle, BorderLayout.WEST);
        topPanel.add(btnGroup, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
    }

    // ==========================================================
    // 3. KHỞI TẠO BẢNG CHIA 3 PHẦN TỪ DATABASE
    // ==========================================================
    private void initTripleTableMatrix() {
        JPanel whiteBox = new JPanel(new BorderLayout());
        whiteBox.setBackground(Color.WHITE);
        whiteBox.setBorder(new LineBorder(Color.decode("#E2E8F0"), 1));

        // --- FETCH DỮ LIỆU TỪ DATABASE ---
        List<String> maCNList = new ArrayList<>();
        List<String> tenCNList = new ArrayList<>();
        List<String[]> nhomQuyenList = new ArrayList<>();
        Map<String, Set<String>> phanQuyenMap = new HashMap<>();

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // 1. Lấy danh sách chức năng (Các cột)
            ResultSet rsCN = stmt.executeQuery("SELECT MACN, tenCN FROM chucnang ORDER BY MACN");
            while (rsCN.next()) {
                maCNList.add(rsCN.getString("MACN"));
                tenCNList.add(rsCN.getString("tenCN"));
            }

            // 2. Lấy danh sách nhóm quyền (Các dòng)
            ResultSet rsNQ = stmt.executeQuery("SELECT MAQUYEN, tenQUYEN FROM nhomquyen ORDER BY MAQUYEN");
            while (rsNQ.next()) {
                nhomQuyenList.add(new String[]{rsNQ.getString("MAQUYEN"), rsNQ.getString("tenQUYEN")});
            }

            // 3. Lấy ma trận phân quyền
            ResultSet rsPQ = stmt.executeQuery("SELECT MAQUYEN, MACN FROM phanquyen");
            while (rsPQ.next()) {
                String mq = rsPQ.getString("MAQUYEN");
                String mc = rsPQ.getString("MACN");
                phanQuyenMap.computeIfAbsent(mq, k -> new HashSet<>()).add(mc);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi kết nối CSDL: " + e.getMessage());
        }

        int numCN = maCNList.size(); // Sẽ tự động là 6 cột (CN01 -> CN06) theo DB
        String[] allCols = new String[numCN + 2];
        allCols[0] = "Tên nhóm quyền";
        for (int i = 0; i < numCN; i++) {
            allCols[i + 1] = maCNList.get(i);
        }
        allCols[numCN + 1] = "Thao tác";

        // --- ĐỔ DỮ LIỆU VÀO MẢNG ---
        Object[][] data = new Object[nhomQuyenList.size()][numCN + 2];
        for (int i = 0; i < nhomQuyenList.size(); i++) {
            String maQuyen = nhomQuyenList.get(i)[0];
            String tenQuyen = nhomQuyenList.get(i)[1];

            // Cột đầu tiên: Tên nhóm quyền và Mã
            data[i][0] = "<html><b>" + tenQuyen + "</b><br><font color='gray'>" + maQuyen + "</font></html>";

            // Các cột Checkbox ở giữa
            Set<String> perms = phanQuyenMap.getOrDefault(maQuyen, new HashSet<>());
            for (int j = 0; j < numCN; j++) {
                data[i][j + 1] = perms.contains(maCNList.get(j));
            }

            // Cột Thao tác cuối cùng
            data[i][numCN + 1] = "";
        }

        // --- TẠO MODEL DÙNG CHUNG ---
        DefaultTableModel model = new DefaultTableModel(data, allCols) {
            @Override
            public Class<?> getColumnClass(int col) {
                if (col >= 1 && col <= numCN) return Boolean.class;
                return super.getColumnClass(col);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 1; // Cho phép edit checkbox và cột thao tác
            }
        };

        // --- TẠO CHỈ SỐ CỘT CHO 3 BẢNG CON ---
        int[] leftIndices = {0};
        int[] centerIndices = new int[numCN];
        for (int i = 0; i < numCN; i++) {
            centerIndices[i] = i + 1;
        }
        int[] rightIndices = {numCN + 1};

        // --- KHỞI TẠO 3 BẢNG ---
        JTable leftTable = createSubTable(model, leftIndices);
        JTable centerTable = createSubTable(model, centerIndices);
        JTable rightTable = createSubTable(model, rightIndices);

        // Format Bảng Trái
        leftTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        leftTable.getColumnModel().getColumn(0).setHeaderValue(getHtmlHeader("Nhóm quyền", "Mã quyền"));

        // Format Bảng Phải
        rightTable.getColumnModel().getColumn(0).setPreferredWidth(160);
        rightTable.getColumnModel().getColumn(0).setHeaderValue("THAO TÁC");
        rightTable.getColumnModel().getColumn(0).setCellRenderer(new ActionButtonsRenderer());
        rightTable.getColumnModel().getColumn(0).setCellEditor(new ActionButtonsEditor(rightTable, nhomQuyenList));

        // Format Bảng Giữa (Tự động lấy tên chức năng từ DB)
        centerTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < numCN; i++) {
            centerTable.getColumnModel().getColumn(i).setPreferredWidth(140);
            centerTable.getColumnModel().getColumn(i).setHeaderValue(getHtmlHeader(maCNList.get(i), tenCNList.get(i)));
        }

        ListSelectionModel selectionModel = leftTable.getSelectionModel();
        centerTable.setSelectionModel(selectionModel);
        rightTable.setSelectionModel(selectionModel);

        JScrollPane centerScroll = new JScrollPane(centerTable);
        centerScroll.setBorder(BorderFactory.createEmptyBorder());
        centerScroll.getViewport().setBackground(Color.WHITE);

        // --- BỐ TRÍ LAYOUT ---
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setOpaque(false);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(leftTable.getTableHeader(), BorderLayout.NORTH);
        leftPanel.add(leftTable, BorderLayout.CENTER);

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

        table.getTableHeader().setPreferredSize(new Dimension(0, 50));
        table.getTableHeader().setBackground(Color.decode("#F8FAFC"));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setForeground(Color.decode("#475569"));

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
    // 5. LỚP VẼ NÚT BẤM CHO BẢNG
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
            lblEdit.setForeground(Color.decode("#10B981"));
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
    // 6. LỚP XỬ LÝ SỰ KIỆN CLICK
    // ==========================================================
    class ActionButtonsEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton btnDetail, btnEdit, btnDel;
        private JTable table;
        private List<String[]> nhomQuyenList;

        public ActionButtonsEditor(JTable rightTable, List<String[]> nhomQuyenList) {
            super(new JCheckBox());
            this.table = rightTable;
            this.nhomQuyenList = nhomQuyenList;

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
                String maQuyen = nhomQuyenList.get(row)[0];
                JOptionPane.showMessageDialog(panel, "Đang xem chi tiết Mã Quyền: " + maQuyen);
            });

            btnEdit.addActionListener(e -> {
                fireEditingStopped();
                int row = table.getSelectedRow();
                String maQuyen = nhomQuyenList.get(row)[0];
                JOptionPane.showMessageDialog(panel, "Sửa quyền cho Mã: " + maQuyen);
            });

            btnDel.addActionListener(e -> {
                fireEditingStopped();
                int row = table.getSelectedRow();
                String maQuyen = nhomQuyenList.get(row)[0];
                int confirm = JOptionPane.showConfirmDialog(panel, "Xóa nhóm quyền " + maQuyen + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    System.out.println("Thực hiện lệnh xóa nhóm quyền: " + maQuyen);
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