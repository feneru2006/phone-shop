package UI.Panel.Sales;

import BUS.giamgiaBUS;
import DTO.giamgiaDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

public class KhuyenMaiPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private giamgiaBUS ggBUS = new giamgiaBUS();

    private int currentPage = 1;
    private int rowsPerPage = 5;
    private int totalPages = 1;

    private JButton btnPrev;
    private JButton btnNext;
    private JLabel lblPage;

    public KhuyenMaiPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        initUI();
        loadData();
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

        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        top.setBackground(Color.WHITE);
        top.add(lblTitle, BorderLayout.WEST);
        top.add(btnAdd, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        String[] cols = {
                "MAGG", "ĐỢT GG", "BẮT ĐẦU", "KẾT THÚC", "TRẠNG THÁI", "THAO TÁC"
        };

        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return c == 5;
            }
        };

        table = new JTable(model);

        table.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent e) {

                int col = table.columnAtPoint(e.getPoint());

                if (col == 5) { // cột THAO TÁC
                    table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    table.setCursor(Cursor.getDefaultCursor());
                    table.setFocusable(false);
                    table.setRowSelectionAllowed(false);
                    table.setCellSelectionEnabled(false);
                    table.getTableHeader().setReorderingAllowed(false);
                    table.getTableHeader().setResizingAllowed(false);
                }
            }
        });
        table.setRowHeight(60);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        table.getColumn("THAO TÁC").setPreferredWidth(260);
        table.getColumn("THAO TÁC")
                .setCellRenderer(new KhuyenMaiActionRenderer());
        table.getColumn("THAO TÁC")
                .setCellEditor(new KhuyenMaiActionEditor(this, new JCheckBox()));

        JScrollPane scroll = new JScrollPane(table);

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

    public void loadData() {

        List<giamgiaDTO> list = ggBUS.getAll();

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
        String bd = model.getValueAt(row, 2).toString();
        String kt = model.getValueAt(row, 3).toString();

        JTextField txtTen = new JTextField(ten);
        JTextField txtBD = new JTextField(bd);
        JTextField txtKT = new JTextField(kt);

        Object[] message = {
                "Tên đợt:", txtTen,
                "Bắt đầu (yyyy-MM-ddTHH:mm):", txtBD,
                "Kết thúc (yyyy-MM-ddTHH:mm):", txtKT
        };

        while (true) {

            int option = JOptionPane.showConfirmDialog(
                    this,
                    message,
                    "Sửa giảm giá",
                    JOptionPane.OK_CANCEL_OPTION);

            if (option != JOptionPane.OK_OPTION) {
                break;
            }

            try {

                giamgiaDTO gg = new giamgiaDTO(
                        ma,
                        txtTen.getText(),
                        LocalDateTime.parse(txtBD.getText()),
                        LocalDateTime.parse(txtKT.getText()));

                if (ggBUS.update(gg)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                    loadData();
                    break;
                }

            }

            catch (IllegalArgumentException ex) {

                JOptionPane.showMessageDialog(this, ex.getMessage());

                String msg = ex.getMessage();

                if (msg.contains("Tên")) {
                    SwingUtilities.invokeLater(() -> {
                        txtTen.requestFocusInWindow();
                        txtTen.selectAll();
                    });
                } else if (msg.contains("Bắt đầu")) {
                    SwingUtilities.invokeLater(() -> {
                        txtBD.requestFocusInWindow();
                        txtBD.selectAll();
                    });
                } else if (msg.contains("Kết thúc")) {
                    SwingUtilities.invokeLater(() -> {
                        txtKT.requestFocusInWindow();
                        txtKT.selectAll();
                    });
                }
            } catch (Exception ex) {

                JOptionPane.showMessageDialog(this,
                        "Sai định dạng ngày!\nVí dụ đúng: 2024-06-01T07:00");

                SwingUtilities.invokeLater(() -> {
                    txtBD.requestFocusInWindow();
                    txtBD.selectAll();
                });
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
    KhuyenMaiAddDialog dialog =
        new KhuyenMaiAddDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            ggBUS);   // truyền BUS

    dialog.setVisible(true);
    loadData();
}
}