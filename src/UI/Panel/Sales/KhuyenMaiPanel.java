package UI.Panel.Sales;

import BUS.giamgiaBUS;
import BUS.CTggBUS;
import DTO.giamgiaDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;
import java.time.format.DateTimeFormatter;

public class KhuyenMaiPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private giamgiaBUS ggBUS = new giamgiaBUS();
    private CTggBUS ctggBUS = new CTggBUS();

    // ===== PHÂN TRANG =====
    private int currentPage = 1;
    private int rowsPerPage = 5;
    private int totalPages = 1;

    private JButton btnPrev;
    private JButton btnNext;
    private JLabel lblPage;

    public KhuyenMaiPanel() {
        setLayout(new BorderLayout());
        setOpaque(false);
        initUI();
        loadData();
    }

    private void initUI() {

        // ================= TITLE =================
        JLabel lblTitle = new JLabel("KHUYẾN MÃI");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        // ================= BUTTON =================
        JButton btnAdd = new JButton("TẠO ĐỢT KHUYẾN MÃI");
        btnAdd.putClientProperty("JButton.buttonType", "roundRect");
        btnAdd.setBackground(new Color(37, 99, 235));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(e -> openAddDialog());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        topPanel.setOpaque(false);
        topPanel.add(lblTitle, BorderLayout.WEST);
        topPanel.add(btnAdd, BorderLayout.EAST);

        // ================= TABLE =================
        String[] columnNames = {
                "MAGG", "ĐỢT GG", "BẮT ĐẦU", "KẾT THÚC", "THAO TÁC"
        };

        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(37, 99, 235));
        table.setSelectionForeground(Color.WHITE);

        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setReorderingAllowed(false);

        table.getColumn("THAO TÁC")
                .setCellRenderer(new ActionRenderer());
        table.getColumn("THAO TÁC")
                .setCellEditor(new ActionEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 20, 10, 20));
        scrollPane.getViewport().setOpaque(false);

        // ================= PAGINATION =================
        btnPrev = new JButton("<< Trước");
        btnNext = new JButton("Sau >>");
        lblPage = new JLabel("Trang 1");

        btnPrev.addActionListener(e -> {
            currentPage--;
            loadData();
        });

        btnNext.addActionListener(e -> {
            currentPage++;
            loadData();
        });

        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        paginationPanel.setOpaque(false);
        paginationPanel.add(btnPrev);
        paginationPanel.add(lblPage);
        paginationPanel.add(btnNext);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        centerPanel.add(paginationPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    // ================= LOAD DATA (PHÂN TRANG) =================
    private void loadData() {

        List<giamgiaDTO> list = ggBUS.getAll();

        int totalRows = list.size();
        totalPages = (int) Math.ceil((double) totalRows / rowsPerPage);

        if (totalPages == 0) totalPages = 1;
        if (currentPage > totalPages) currentPage = totalPages;
        if (currentPage < 1) currentPage = 1;

        int start = (currentPage - 1) * rowsPerPage;
        int end = Math.min(start + rowsPerPage, totalRows);

        model.setRowCount(0);

        for (int i = start; i < end; i++) {
            giamgiaDTO gg = list.get(i);
            model.addRow(new Object[]{
                    gg.getMAGG(),
                    gg.getdotGG(),
                    gg.getBatdau(),
                    gg.getKetthuc(),
                    "ACTION"
            });
        }

        lblPage.setText("Trang " + currentPage + " / " + totalPages);

        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);
    }

    // ================= ADD =================
    private void openAddDialog() {

        JTextField txtMa = new JTextField();
        JTextField txtTen = new JTextField();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        LocalDateTime end = now.plusDays(7);

        JTextField txtBatDau = new JTextField(now.format(formatter));
        JTextField txtKetThuc = new JTextField(end.format(formatter));

        Object[] message = {
                "Mã GG:", txtMa,
                "Tên đợt:", txtTen,
                "Bắt đầu (yyyy-MM-ddTHH:mm):", txtBatDau,
                "Kết thúc:", txtKetThuc
        };

        int option = JOptionPane.showConfirmDialog(
                this, message, "Thêm giảm giá",
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                giamgiaDTO gg = new giamgiaDTO(
                        txtMa.getText(),
                        txtTen.getText(),
                        LocalDateTime.parse(txtBatDau.getText()),
                        LocalDateTime.parse(txtKetThuc.getText()));

                if (ggBUS.add(gg)) {
                    JOptionPane.showMessageDialog(this,
                            "Thêm đợt giảm giá thành công!");
                    loadData();
                }

            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Sai định dạng ngày giờ!");
            }
        }
    }

    // ================= EDIT =================
    private void editRow(int row) {

        String maGG = model.getValueAt(row, 0).toString();
        String ten = model.getValueAt(row, 1).toString();
        String bd = model.getValueAt(row, 2).toString();
        String kt = model.getValueAt(row, 3).toString();

        JTextField txtTen = new JTextField(ten);
        JTextField txtBatDau = new JTextField(bd);
        JTextField txtKetThuc = new JTextField(kt);

        Object[] message = {
                "Tên đợt:", txtTen,
                "Bắt đầu:", txtBatDau,
                "Kết thúc:", txtKetThuc
        };

        int option = JOptionPane.showConfirmDialog(
                this, message, "Sửa giảm giá",
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                giamgiaDTO gg = new giamgiaDTO(
                        maGG,
                        txtTen.getText(),
                        LocalDateTime.parse(txtBatDau.getText()),
                        LocalDateTime.parse(txtKetThuc.getText()));

                if (ggBUS.update(gg)) {
                    JOptionPane.showMessageDialog(this,
                            "Cập nhật thành công!");
                    loadData();
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Sai định dạng!");
            }
        }
    }

    // ================= DELETE =================
    private void deleteRow(int row) {

        String maGG = model.getValueAt(row, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Xóa đợt giảm giá này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (ggBUS.delete(maGG)) {
                JOptionPane.showMessageDialog(this,
                        "Xóa thành công!");
                loadData();
            }
        }
    }

    // ================= RENDERER =================
    class ActionRenderer extends JPanel
            implements javax.swing.table.TableCellRenderer {

        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");

        public ActionRenderer() {
            setOpaque(false);
            setLayout(new FlowLayout(FlowLayout.CENTER, 15, 5));
            add(btnEdit);
            add(btnDelete);
        }

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value,
                boolean isSelected, boolean hasFocus,
                int row, int column) {
            return this;
        }
    }

    // ================= EDITOR =================
    class ActionEditor extends DefaultCellEditor {

        private JPanel panel;
        private JButton btnEdit;
        private JButton btnDelete;
        private int currentRow;

        public ActionEditor(JCheckBox checkBox) {
            super(checkBox);

            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
            panel.setOpaque(false);

            btnEdit = new JButton("Sửa");
            btnDelete = new JButton("Xóa");

            panel.add(btnEdit);
            panel.add(btnDelete);

            btnEdit.addActionListener(e -> {
                fireEditingStopped();
                editRow(currentRow);
            });

            btnDelete.addActionListener(e -> {
                fireEditingStopped();
                deleteRow(currentRow);
            });
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value,
                boolean isSelected,
                int row, int column) {

            currentRow = row;
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "ACTION";
        }
    }
}