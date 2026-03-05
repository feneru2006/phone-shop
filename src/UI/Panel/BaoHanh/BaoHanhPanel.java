package UI.Panel.BaoHanh;

import BUS.PhieubaohanhBUS;
import DTO.PhieubaohanhDTO;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BaoHanhPanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private JTextField txtSearch;

    private JButton btnPrev;
    private JButton btnNext;

    private JLabel lblPage;

    private PhieubaohanhBUS bhBUS = new PhieubaohanhBUS();

    private List<PhieubaohanhDTO> fullList = new ArrayList<>();

    private int currentPage = 1;
    private int pageSize = 10;

    public BaoHanhPanel() {

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        initUI();
        loadData();
    }

    private void initUI() {

        /* ===== TITLE ===== */

        JLabel lblTitle = new JLabel("QUẢN LÝ BẢO HÀNH");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));

        /* ===== SEARCH ===== */

        txtSearch = new JTextField(20);

        txtSearch.setPreferredSize(new Dimension(200, 35));

        txtSearch.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                search();
            }

            public void removeUpdate(DocumentEvent e) {
                search();
            }

            public void changedUpdate(DocumentEvent e) {
                search();
            }

        });

        /* ===== BUTTON ADD ===== */

        JButton btnAdd = new JButton("+ THÊM");

        btnAdd.setBackground(new Color(37, 99, 235));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnAdd.addActionListener(e -> openAddDialog());

        /* ===== TOP PANEL ===== */

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setBackground(Color.WHITE);

        left.add(lblTitle);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        right.setBackground(Color.WHITE);

        right.add(new JLabel("Tìm MABH:"));
        right.add(txtSearch);
        right.add(btnAdd);

        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        top.setBackground(Color.WHITE);

        top.add(left, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        /* ===== TABLE ===== */

        String[] cols = {
                "MABH",
                "MACTHD",
                "MAKH",
                "NGÀY BD",
                "THỜI HẠN",
                "TRẠNG THÁI"
        };

        model = new DefaultTableModel(cols, 0) {

            public boolean isCellEditable(int r, int c) {
                return false;
            }

        };

        table = new JTable(model);

        table.setRowHeight(45);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        table.setSelectionBackground(new Color(229, 231, 235));

        // JScrollPane scroll = new JScrollPane(table);
        // scroll.setBorder(BorderFactory.createEmptyBorder(10,20,10,20));   Đây là khi tôi bỏ khung

        // add(scroll,BorderLayout.CENTER);
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);

        // tạo khung
        tablePanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(null);

        tablePanel.add(scroll, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);

        /* ===== PAGINATION ===== */

        btnPrev = new JButton("<<");
        btnNext = new JButton(">>");

        lblPage = new JLabel("Page 1");

        btnPrev.addActionListener(e -> {

            if (currentPage > 1) {

                currentPage--;

                showPage();
            }

        });

        btnNext.addActionListener(e -> {

            if (currentPage * pageSize < fullList.size()) {

                currentPage++;

                showPage();
            }

        });

        JPanel bottom = new JPanel();
        bottom.setBackground(Color.WHITE);
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));

        bottom.add(btnPrev);
        bottom.add(lblPage);
        bottom.add(btnNext);

        add(bottom, BorderLayout.SOUTH);
    }

    /* ================= LOAD DATA ================= */

    private void loadData() {

        fullList = bhBUS.getAll();

        currentPage = 1;

        showPage();
    }

    /* ================= SHOW PAGE ================= */

    private void showPage() {

        model.setRowCount(0);

        int start = (currentPage - 1) * pageSize;

        int end = Math.min(start + pageSize, fullList.size());

        for (int i = start; i < end; i++) {

            PhieubaohanhDTO bh = fullList.get(i);

            model.addRow(new Object[] {
                    bh.getMaBH(),
                    bh.getMaCTHD(),
                    bh.getMaKH(),
                    bh.getNgayBD(),
                    bh.getThoiHan(),
                    bh.getTrangthai()
            });

        }

        int totalPage = (int) Math.ceil((double) fullList.size() / pageSize);

        lblPage.setText("Page " + currentPage + " / " + totalPage);
    }

    /* ================= SEARCH ================= */

    private void search() {

        String keyword = txtSearch.getText();

        if (keyword.isEmpty()) {

            fullList = bhBUS.getAll();

        } else {

            fullList = bhBUS.searchByMaBH(keyword);

        }

        currentPage = 1;

        showPage();
    }

    /* ================= ADD ================= */

    private void openAddDialog() {

        JTextField txtMaBH = new JTextField();
        JTextField txtCTHD = new JTextField();
        JTextField txtKH = new JTextField();
        JTextField txtNgay = new JTextField("2026-01-01");
        JTextField txtThoiHan = new JTextField("12");

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));

        panel.add(new JLabel("Mã BH"));
        panel.add(txtMaBH);

        panel.add(new JLabel("Mã CTHD"));
        panel.add(txtCTHD);

        panel.add(new JLabel("Mã KH"));
        panel.add(txtKH);

        panel.add(new JLabel("Ngày BD"));
        panel.add(txtNgay);

        panel.add(new JLabel("Thời hạn"));
        panel.add(txtThoiHan);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "THÊM BẢO HÀNH",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {

            try {

                PhieubaohanhDTO bh = new PhieubaohanhDTO();

                bh.setMaBH(txtMaBH.getText());
                bh.setMaCTHD(txtCTHD.getText());
                bh.setMaKH(txtKH.getText());
                bh.setNgayBD(java.time.LocalDate.parse(txtNgay.getText()));
                bh.setThoiHan(Integer.parseInt(txtThoiHan.getText()));

                if (bhBUS.add(bh)) {

                    JOptionPane.showMessageDialog(this, "Thêm thành công");

                    loadData();

                }

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(this, ex.getMessage());

            }

        }

    }
}