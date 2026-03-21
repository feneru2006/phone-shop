package UI;

import DAL.DAO.AccountDAO;
import DAL.DAO.DBConnection;
import DTO.accountDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PhanQuyenUI extends JPanel {

    private DefaultTableModel mainModel;
    private List<String> maCNList;
    private List<String> tenCNList;
    private List<String[]> nhomQuyenList;
    private JPanel tableContainer;

    public PhanQuyenUI() {
        setLayout(new BorderLayout(0, 20));
        setBackground(Color.decode("#F3F4F6"));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        tableContainer = new JPanel(new BorderLayout());
        tableContainer.setOpaque(false);

        initTopPanel();
        add(tableContainer, BorderLayout.CENTER);

        initTripleTableMatrix();
    }

    public PhanQuyenUI(String accountId) {
        this();
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, "Đang thiết lập quyền cho ID: " + accountId);
        });
    }

    private void initTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Ma trận phân quyền");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.decode("#1E293B"));

        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnGroup.setOpaque(false);

        JButton btnSave = createStyledButton("Lưu thay đổi", "#10B981");
        JButton btnAdd = createStyledButton("Tạo nhóm quyền mới", "#3B82F6");

        btnSave.addActionListener(e -> savePermissions());
        btnAdd.addActionListener(e -> showAddRoleDialog());

        btnGroup.add(btnSave);
        btnGroup.add(btnAdd);

        topPanel.add(lblTitle, BorderLayout.WEST);
        topPanel.add(btnGroup, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
    }

    private void initTripleTableMatrix() {
        tableContainer.removeAll();

        JPanel whiteBox = new JPanel(new BorderLayout());
        whiteBox.setBackground(Color.WHITE);
        whiteBox.setBorder(new LineBorder(Color.decode("#E2E8F0"), 1));

        maCNList = new ArrayList<>();
        tenCNList = new ArrayList<>();
        nhomQuyenList = new ArrayList<>();
        Map<String, Set<String>> phanQuyenMap = new HashMap<>();

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            ResultSet rsCN = stmt.executeQuery("SELECT MACN, tenCN FROM chucnang ORDER BY MACN");
            while (rsCN.next()) {
                maCNList.add(rsCN.getString("MACN"));
                tenCNList.add(rsCN.getString("tenCN"));
            }

            ResultSet rsNQ = stmt.executeQuery("SELECT MAQUYEN, tenQUYEN FROM nhomquyen ORDER BY MAQUYEN");
            while (rsNQ.next()) {
                nhomQuyenList.add(new String[]{rsNQ.getString("MAQUYEN"), rsNQ.getString("tenQUYEN")});
            }

            ResultSet rsPQ = stmt.executeQuery("SELECT MAQUYEN, MACN FROM phanquyen");
            while (rsPQ.next()) {
                String mq = rsPQ.getString("MAQUYEN");
                String mc = rsPQ.getString("MACN");
                phanQuyenMap.computeIfAbsent(mq, k -> new HashSet<>()).add(mc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int numCN = maCNList.size();
        String[] allCols = new String[numCN + 2];
        allCols[0] = "Tên nhóm quyền";
        for (int i = 0; i < numCN; i++) {
            allCols[i + 1] = maCNList.get(i);
        }
        allCols[numCN + 1] = "Thao tác";

        Object[][] data = new Object[nhomQuyenList.size()][numCN + 2];
        for (int i = 0; i < nhomQuyenList.size(); i++) {
            String maQuyen = nhomQuyenList.get(i)[0];
            String tenQuyen = nhomQuyenList.get(i)[1];

            // Đánh dấu đỏ cho Admin để phân biệt cấp bậc
            if (maQuyen.equals("AD")) {
                data[i][0] = "<html><b style='color:red'>" + tenQuyen + " 👑</b><br><font color='gray'>" + maQuyen + "</font></html>";
            } else {
                data[i][0] = "<html><b>" + tenQuyen + "</b><br><font color='gray'>" + maQuyen + "</font></html>";
            }

            Set<String> perms = phanQuyenMap.getOrDefault(maQuyen, new HashSet<>());
            for (int j = 0; j < numCN; j++) {
                // [RULE 1]: Nếu là Admin (AD), luôn ép full quyền = true (tích xanh hết)
                if (maQuyen.equals("AD")) {
                    data[i][j + 1] = true;
                } else {
                    data[i][j + 1] = perms.contains(maCNList.get(j));
                }
            }
            data[i][numCN + 1] = "";
        }

        mainModel = new DefaultTableModel(data, allCols) {
            @Override
            public Class<?> getColumnClass(int col) {
                if (col >= 1 && col <= numCN) return Boolean.class;
                return super.getColumnClass(col);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                // [RULE 2]: KHÓA HOÀN TOÀN TẤT CẢ CHECKBOX CỦA DÒNG "AD"
                String maQuyen = nhomQuyenList.get(row)[0];
                if (maQuyen.equals("AD") && column >= 1 && column <= numCN) {
                    return false;
                }
                return column >= 1;
            }
        };

        int[] leftIndices = {0};
        int[] centerIndices = new int[numCN];
        for (int i = 0; i < numCN; i++) centerIndices[i] = i + 1;
        int[] rightIndices = {numCN + 1};

        JTable leftTable = createSubTable(mainModel, leftIndices);
        JTable centerTable = createSubTable(mainModel, centerIndices);
        JTable rightTable = createSubTable(mainModel, rightIndices);

        leftTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        leftTable.getColumnModel().getColumn(0).setHeaderValue(getHtmlHeader("Nhóm quyền", "Mã quyền"));

        rightTable.getColumnModel().getColumn(0).setPreferredWidth(180);
        rightTable.getColumnModel().getColumn(0).setHeaderValue("THAO TÁC");
        rightTable.getColumnModel().getColumn(0).setCellRenderer(new ActionButtonsRenderer());
        rightTable.getColumnModel().getColumn(0).setCellEditor(new ActionButtonsEditor(rightTable, nhomQuyenList));

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

        tableContainer.add(whiteBox, BorderLayout.CENTER);
        tableContainer.revalidate();
        tableContainer.repaint();
    }

    private void savePermissions() {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn cập nhật tất cả phân quyền?", "Xác nhận lưu", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            String sqlDelete = "DELETE FROM phanquyen";
            conn.prepareStatement(sqlDelete).executeUpdate();

            String sqlInsert = "INSERT INTO phanquyen (MAQUYEN, MACN) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(sqlInsert);

            for (int i = 0; i < mainModel.getRowCount(); i++) {
                String maQuyen = nhomQuyenList.get(i)[0];
                for (int j = 0; j < maCNList.size(); j++) {
                    Boolean isChecked = (Boolean) mainModel.getValueAt(i, j + 1);
                    if (isChecked != null && isChecked) {
                        ps.setString(1, maQuyen);
                        ps.setString(2, maCNList.get(j));
                        ps.addBatch();
                    }
                }
            }
            ps.executeBatch();
            conn.commit();
            JOptionPane.showMessageDialog(this, "Đã lưu ma trận phân quyền thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAddRoleDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Tạo nhóm quyền mới", true);
        dialog.setSize(420, 320);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        header.setBackground(Color.decode("#3B82F6"));
        JLabel title = new JLabel("THÊM NHÓM QUYỀN MỚI");
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(Color.WHITE);
        header.add(title);
        dialog.add(header, BorderLayout.NORTH);

        JPanel body = new JPanel(new GridLayout(4, 1, 5, 5));
        body.setBackground(Color.WHITE);
        body.setBorder(new EmptyBorder(20, 30, 20, 30));

        JTextField txtMa = new JTextField();
        JTextField txtTen = new JTextField();

        body.add(new JLabel("Mã nhóm quyền (VD: AD, WM):"));
        body.add(txtMa);
        body.add(new JLabel("Tên nhóm quyền:"));
        body.add(txtTen);
        dialog.add(body, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        footer.setBackground(Color.decode("#F8FAFC"));
        footer.setBorder(new LineBorder(Color.decode("#E2E8F0"), 1));

        JButton btnSubmit = createStyledButton("Lưu nhóm quyền", "#3B82F6");
        btnSubmit.addActionListener(e -> {
            String ma = txtMa.getText().trim().toUpperCase();
            String ten = txtTen.getText().trim();

            if (ma.isEmpty() || ten.isEmpty()) return;
            if (ma.equals("AD")) {
                JOptionPane.showMessageDialog(dialog, "Mã AD đã tồn tại và là mã hệ thống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                String sql = "INSERT INTO nhomquyen (MAQUYEN, tenQUYEN) VALUES (?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, ma);
                ps.setString(2, ten);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(dialog, "Thêm nhóm quyền thành công!");
                dialog.dispose();
                initTripleTableMatrix();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage());
            }
        });

        JButton btnClose = createStyledButton("Hủy", "#94A3B8");
        btnClose.addActionListener(e -> dialog.dispose());

        footer.add(btnClose);
        footer.add(btnSubmit);
        dialog.add(footer, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showUsersInRoleDialog(String maQuyen, String tenQuyen) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Danh sách nhân viên", true);
        dialog.setSize(550, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        dialog.getContentPane().setBackground(Color.WHITE);

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        header.setBackground(Color.decode("#6366F1"));
        JLabel title = new JLabel("Nhân viên thuộc nhóm: " + tenQuyen.toUpperCase());
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(Color.WHITE);
        header.add(title);
        dialog.add(header, BorderLayout.NORTH);

        String[] cols = {"Mã Nhân Viên", "Tên Đăng Nhập", "Mã Quyền"};
        DefaultTableModel modelUsers = new DefaultTableModel(cols, 0);
        JTable tblUsers = new JTable(modelUsers);
        tblUsers.setRowHeight(35);

        AccountDAO accDAO = new AccountDAO();
        List<accountDTO> list = accDAO.selectByRole(maQuyen);
        for (accountDTO acc : list) modelUsers.addRow(new Object[]{acc.getId(), acc.getTen(), acc.getQuyen()});
        if (list.isEmpty()) modelUsers.addRow(new Object[]{"", "Chưa có nhân viên nào", ""});

        JScrollPane scroll = new JScrollPane(tblUsers);
        scroll.setBorder(new EmptyBorder(10, 10, 10, 10));
        dialog.add(scroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        JButton btnClose = createStyledButton("Đóng", "#94A3B8");
        btnClose.addActionListener(e -> dialog.dispose());
        footer.add(btnClose);
        dialog.add(footer, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private JTable createSubTable(DefaultTableModel mainModel, int[] columnIndices) {
        DefaultTableModel subModel = new DefaultTableModel(mainModel.getRowCount(), columnIndices.length) {
            @Override public Class<?> getColumnClass(int col) { return mainModel.getColumnClass(columnIndices[col]); }
            @Override public boolean isCellEditable(int row, int col) { return mainModel.isCellEditable(row, columnIndices[col]); }
            @Override public void setValueAt(Object aValue, int row, int col) { mainModel.setValueAt(aValue, row, columnIndices[col]); fireTableCellUpdated(row, col); }
            @Override public Object getValueAt(int row, int col) { return mainModel.getValueAt(row, col >= 0 ? columnIndices[col] : 0); }
        };

        JTable table = new JTable(subModel);
        table.setRowHeight(60);
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.setGridColor(Color.decode("#F1F5F9"));
        table.setSelectionBackground(Color.decode("#EFF6FF"));

        table.getTableHeader().setPreferredSize(new Dimension(0, 50));
        table.getTableHeader().setBackground(Color.decode("#F8FAFC"));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setBorder(new EmptyBorder(0, 15, 0, 0));
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (table.getColumnClass(i) == String.class) table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
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

    class ActionButtonsRenderer extends JPanel implements TableCellRenderer {
        public ActionButtonsRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 8, 15));
            setOpaque(true);
            JLabel lblUsers = new JLabel("👥"); lblUsers.setForeground(Color.decode("#6366F1")); lblUsers.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
            JLabel lblEdit = new JLabel("✏"); lblEdit.setForeground(Color.decode("#10B981")); lblEdit.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            JLabel lblDel = new JLabel("🗑"); lblDel.setForeground(Color.RED); lblDel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            add(lblUsers); add(lblEdit); add(lblDel);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return this;
        }
    }

    class ActionButtonsEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton btnUsers, btnEdit, btnDel;

        public ActionButtonsEditor(JTable rightTable, List<String[]> nhomQuyenList) {
            super(new JCheckBox());
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 15));

            btnUsers = createIconButton("👥", Color.decode("#6366F1"));
            btnEdit = createIconButton("✏", Color.decode("#10B981"));
            btnDel = createIconButton("🗑", Color.RED);

            btnUsers.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
            btnEdit.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            btnDel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));

            btnUsers.addActionListener(e -> {
                fireEditingStopped();
                int row = rightTable.getSelectedRow();
                showUsersInRoleDialog(nhomQuyenList.get(row)[0], nhomQuyenList.get(row)[1]);
            });

            btnEdit.addActionListener(e -> {
                fireEditingStopped();
                int row = rightTable.getSelectedRow();
                String maQuyen = nhomQuyenList.get(row)[0];

                // [RULE 3]: KHÔNG CHO SỬA ADMIN
                if (maQuyen.equals("AD")) {
                    JOptionPane.showMessageDialog(panel, "Quyền Admin (AD) là tối thượng, không thể sửa!", "Từ chối truy cập", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                JOptionPane.showMessageDialog(panel, "Sửa quyền cho Mã: " + maQuyen);
            });

            btnDel.addActionListener(e -> {
                fireEditingStopped();
                int row = rightTable.getSelectedRow();
                String maQuyen = nhomQuyenList.get(row)[0];

                // [RULE 4]: KHÔNG CHO XÓA ADMIN
                if (maQuyen.equals("AD")) {
                    JOptionPane.showMessageDialog(panel, "Quyền Admin (AD) là tối thượng, không thể xóa!", "Từ chối truy cập", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(panel, "Xóa nhóm quyền " + maQuyen + " sẽ xóa luôn toàn bộ quyền hạn liên quan.\nTiếp tục?", "Cảnh báo", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    try (Connection conn = DBConnection.getConnection()) {
                        String sql = "DELETE FROM nhomquyen WHERE MAQUYEN = ?";
                        PreparedStatement ps = conn.prepareStatement(sql);
                        ps.setString(1, maQuyen);
                        ps.executeUpdate();
                        JOptionPane.showMessageDialog(panel, "Đã xóa nhóm quyền: " + maQuyen);
                        initTripleTableMatrix();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(panel, "Lỗi khi xóa: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            panel.add(btnUsers); panel.add(btnEdit); panel.add(btnDel);
        }

        private JButton createIconButton(String text, Color color) {
            JButton btn = new JButton(text); btn.setForeground(color); btn.setContentAreaFilled(false); btn.setBorderPainted(false); return btn;
        }

        @Override public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            panel.setBackground(table.getSelectionBackground()); return panel;
        }
        @Override public Object getCellEditorValue() { return ""; }
    }
}
