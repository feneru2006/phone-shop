package UI.Panel.Sales;

import BUS.giamgiaBUS;
import DTO.giamgiaDTO;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

public class KhuyenMaiPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private giamgiaBUS ggBUS = new giamgiaBUS();

    private JComboBox<String> cbFilter;
    private String currentFilter = "Tất cả";

    private JTextField txtSearch; // ===== SEARCH =====

    private int currentPage = 1;
    private int rowsPerPage = 8;
    private int totalPages = 1;

    private JButton btnPrev;
    private JButton btnNext;
    private JLabel lblPage;

    public KhuyenMaiPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        initUI();
        loadData();

        new Utility.AutoRefresh(30000, () -> {
            SwingUtilities.invokeLater(() -> {
                ggBUS.reload();
                loadData();
            });
        }).start();
    }

    private void initUI() {

        JLabel lblTitle = new JLabel("KHUYẾN MÃI");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JButton btnAdd = new JButton("TẠO KHUYẾN MÃI");
        btnAdd.putClientProperty("JButton.buttonType", "roundRect");
        btnAdd.setBackground(new Color(37, 99, 235));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(e -> openAddDialog());

        // ===== SEARCH =====
        txtSearch = new JTextField(15);
        txtSearch.setPreferredSize(new Dimension(180, 35));

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                onSearch();
            }

            public void removeUpdate(DocumentEvent e) {
                onSearch();
            }

            public void changedUpdate(DocumentEvent e) {
                onSearch();
            }
        });

        // ===== FILTER =====
        cbFilter = new JComboBox<>(new String[] {
                "Tất cả",
                "Sắp diễn ra",
                "Đang hoạt động",
                "Đã kết thúc"
        });
        cbFilter.setPreferredSize(new Dimension(150, 35));

        cbFilter.addActionListener(e -> {
            currentFilter = cbFilter.getSelectedItem().toString();
            currentPage = 1;
            loadData();
        });

        // ===== TOP =====
        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        top.setBackground(Color.WHITE);

        top.add(lblTitle, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setBackground(Color.WHITE);

        right.add(new JLabel("Tìm:"));
        right.add(txtSearch);

        right.add(new JLabel("Lọc:"));
        right.add(cbFilter);

        right.add(btnAdd);

        top.add(right, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        // ===== TABLE =====
        String[] cols = {
                "MAGG", "ĐỢT GG", "BẮT ĐẦU", "KẾT THÚC", "TRẠNG THÁI", "THAO TÁC"
        };

        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return c == 5;
            }
        };

        table = new JTable(model);

        table.setRowHeight(60);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(51, 65, 85));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setRowSelectionAllowed(false);
        table.getTableHeader().setReorderingAllowed(false); // ❌ kéo đổi vị trí cột
        table.getTableHeader().setResizingAllowed(false); // ❌ kéo giãn cột
        ((DefaultTableCellRenderer) table.getTableHeader()
                .getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        table.setShowVerticalLines(false);
        table.setGridColor(new Color(230, 230, 230));
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, false, false, row, column); // 👈 ép false hết

                // màu dòng xen kẽ cho đẹp
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));

                setHorizontalAlignment(JLabel.CENTER);

                return c;
            }
        });

        table.getColumn("THAO TÁC").setPreferredWidth(260);
        table.getColumn("THAO TÁC")
                .setCellRenderer(new KhuyenMaiActionRenderer());
        table.getColumn("THAO TÁC")
                .setCellEditor(new KhuyenMaiActionEditor(this, new JCheckBox()));

        JScrollPane scroll = new JScrollPane(table);

        // ===== PAGINATION =====
        btnPrev = new JButton("<<");
        btnNext = new JButton(">>");
        lblPage = new JLabel();

        btnPrev.addActionListener(e -> {
            currentPage--;
            loadData();
        });

        btnNext.addActionListener(e -> {
            currentPage++;
            loadData();
        });

        JPanel pagination = new JPanel();
        pagination.setBackground(Color.WHITE);
        pagination.add(btnPrev);
        pagination.add(lblPage);
        pagination.add(btnNext);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Color.WHITE);
        center.add(scroll, BorderLayout.CENTER);
        center.add(pagination, BorderLayout.SOUTH);

        add(center, BorderLayout.CENTER);
    }

    private void onSearch() {
        currentPage = 1;
        loadData();
    }

    public void loadData() {

        List<giamgiaDTO> list = ggBUS.getAll();

        // ===== SEARCH =====
        String keyword = txtSearch.getText().toLowerCase().trim();

        if (!keyword.isEmpty()) {
            list = list.stream()
                    .filter(gg -> gg.getMAGG().toLowerCase().contains(keyword) ||
                            gg.getdotGG().toLowerCase().contains(keyword))
                    .toList();
        }

        // ===== FILTER =====
        if (!currentFilter.equals("Tất cả")) {
            list = list.stream()
                    .filter(gg -> getStatus(gg).equals(currentFilter))
                    .toList();
        }

        int totalRows = list.size();
        totalPages = (int) Math.ceil((double) totalRows / rowsPerPage);

        if (totalPages == 0)
            totalPages = 1;
        if (currentPage > totalPages)
            currentPage = totalPages;
        if (currentPage < 1)
            currentPage = 1;

        int start = (currentPage - 1) * rowsPerPage;
        int end = Math.min(start + rowsPerPage, totalRows);

        model.setRowCount(0);

        for (int i = start; i < end; i++) {
            giamgiaDTO gg = list.get(i);

            model.addRow(new Object[] {
                    gg.getMAGG(),
                    gg.getdotGG(),
                    gg.getBatdau(),
                    gg.getKetthuc(),
                    getStatus(gg),
                    "ACTION"
            });
        }

        lblPage.setText("Trang " + currentPage + " / " + totalPages);
        btnPrev.setEnabled(currentPage > 1);
        btnNext.setEnabled(currentPage < totalPages);
    }

    private String getStatus(giamgiaDTO gg) {
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(gg.getBatdau()))
            return "Sắp diễn ra";
        if (now.isAfter(gg.getKetthuc()))
            return "Đã kết thúc";
        return "Đang hoạt động";
    }

    public void editRow(int row) {

        String ma = model.getValueAt(row, 0).toString();
        String ten = model.getValueAt(row, 1).toString();
        String bdStr = model.getValueAt(row, 2).toString();
        String ktStr = model.getValueAt(row, 3).toString();

        JTextField txtTen = new JTextField(ten);

        JSpinner spBD = new JSpinner(new SpinnerDateModel());
        JSpinner spKT = new JSpinner(new SpinnerDateModel());

        JSpinner.DateEditor edBD = new JSpinner.DateEditor(spBD, "dd-MM-yyyy");
        JSpinner.DateEditor edKT = new JSpinner.DateEditor(spKT, "dd-MM-yyyy");

        spBD.setEditor(edBD);
        spKT.setEditor(edKT);

        // ===== LẤY NGÀY TỪ TABLE =====
        try {

            LocalDateTime bdTime = LocalDateTime.parse(bdStr);
            LocalDateTime ktTime = LocalDateTime.parse(ktStr);

            java.util.Date bdDate = java.util.Date.from(
                    bdTime.atZone(java.time.ZoneId.systemDefault()).toInstant());

            java.util.Date ktDate = java.util.Date.from(
                    ktTime.atZone(java.time.ZoneId.systemDefault()).toInstant());

            spBD.setValue(bdDate);
            spKT.setValue(ktDate);

        } catch (Exception e) {

            spBD.setValue(new java.util.Date());
            spKT.setValue(new java.util.Date());
        }

        Object[] message = {
                "Tên khuyến mãi:", txtTen,
                "Ngày bắt đầu:", spBD,
                "Ngày kết thúc:", spKT
        };

        while (true) {

            int option = JOptionPane.showConfirmDialog(
                    this,
                    message,
                    "Sửa khuyến mãi",
                    JOptionPane.OK_CANCEL_OPTION);

            if (option != JOptionPane.OK_OPTION) {
                break;
            }

            try {

                java.util.Date bdDate = (java.util.Date) spBD.getValue();
                java.util.Date ktDate = (java.util.Date) spKT.getValue();

                LocalDateTime bd = bdDate.toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDateTime()
                        .toLocalDate()
                        .atStartOfDay();

                LocalDateTime kt = ktDate.toInstant()
                        .atZone(java.time.ZoneId.systemDefault())
                        .toLocalDateTime()
                        .toLocalDate()
                        .atTime(23, 59, 59);

                if (kt.isBefore(bd)) {
                    JOptionPane.showMessageDialog(this,
                            "Ngày kết thúc phải sau ngày bắt đầu!");
                    continue;
                }

                giamgiaDTO gg = new giamgiaDTO(
                        ma,
                        txtTen.getText(),
                        bd,
                        kt);

                if (ggBUS.update(gg)) {

                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                    loadData();
                    break;
                }

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(this,
                        "Dữ liệu không hợp lệ!");
            }
        }
    }

    public void viewDetail(int row) {
        String maGG = model.getValueAt(row, 0).toString();

        KhuyenMaiDetailDialog dialog = new KhuyenMaiDetailDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                maGG);

        dialog.setVisible(true);
    }

    public void deleteRow(int row) {

        String maGG = model.getValueAt(row, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa khuyến mãi " + maGG + " ?",
                "XÁC NHẬN XÓA",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            ggBUS.delete(maGG);
            loadData();
        }
    }

    private void openAddDialog() {
        KhuyenMaiAddDialog dialog = new KhuyenMaiAddDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                ggBUS); // truyền BUS

        dialog.setVisible(true);
        loadData();
    }
}