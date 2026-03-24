package UI.Dialog;


import BUS.ExcelBUS;
import DTO.SanPhamDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class ExcelPreviewDialog extends JDialog {
    private List<SanPhamDTO> dsSanPham;
    private JTable tablePreview;
    private DefaultTableModel modelPreview;
    private ExcelBUS excelBUS;

    public ExcelPreviewDialog(Window owner, List<SanPhamDTO> dsSanPham) {
        super(owner, "Xem trước dữ liệu xuất Excel", ModalityType.APPLICATION_MODAL);
        this.dsSanPham = dsSanPham;
        this.excelBUS = new ExcelBUS();
        khoiTaoGiaoDien();
    }

    private void khoiTaoGiaoDien() {
        setSize(850, 550);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // --- Header ---
        JLabel lblTitle = new JLabel("XEM TRƯỚC BẢNG DỮ LIỆU EXCEL", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(Color.decode("#1E293B"));
        lblTitle.setBorder(new EmptyBorder(15, 0, 15, 0));
        add(lblTitle, BorderLayout.NORTH);

        // --- Center: Table ---
        String[] columnNames = {"STT", "Mã SP", "Tên Sản Phẩm", "Số Lượng Tồn", "Đơn Giá"};
        modelPreview = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablePreview = new JTable(modelPreview);
        tablePreview.setRowHeight(35);
        tablePreview.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tablePreview.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tablePreview.getTableHeader().setBackground(Color.decode("#F1F5F9"));
        tablePreview.setGridColor(Color.decode("#E2E8F0"));

        // Căn trái dữ liệu
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        for (int i = 0; i < tablePreview.getColumnCount(); i++) {
            tablePreview.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
        }

        taiDuLieuLenBang();

        JScrollPane scrollPane = new JScrollPane(tablePreview);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.decode("#CBD5E1")));
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(new EmptyBorder(0, 15, 0, 15));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // --- Bottom: Buttons ---
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        pnlButtons.setBackground(Color.WHITE);
        
        JButton btnHuy = new JButton("Hủy bỏ");
        btnHuy.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnHuy.setPreferredSize(new Dimension(100, 38));
        btnHuy.setFocusPainted(false);
        btnHuy.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHuy.addActionListener(e -> dispose());

        JButton btnXacNhan = new JButton("Chọn nơi lưu & Xuất Excel");
        btnXacNhan.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnXacNhan.setPreferredSize(new Dimension(220, 38));
        btnXacNhan.setBackground(Color.decode("#217346"));
        btnXacNhan.setForeground(Color.WHITE);
        btnXacNhan.setFocusPainted(false);
        btnXacNhan.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnXacNhan.addActionListener(e -> xuLySuKienChonDuongDanVaXuat());

        pnlButtons.add(btnHuy);
        pnlButtons.add(btnXacNhan);
        add(pnlButtons, BorderLayout.SOUTH);
    }

    private void taiDuLieuLenBang() {
        modelPreview.setRowCount(0);
        for (int i = 0; i < dsSanPham.size(); i++) {
            SanPhamDTO sp = dsSanPham.get(i);
            modelPreview.addRow(new Object[]{
                    i + 1,
                    sp.getMaSP(),
                    sp.getTenSP(),
                    sp.getSlTon(),
                    String.format("%,.0f VNĐ", sp.getGia())
            });
        }
    }

    private void xuLySuKienChonDuongDanVaXuat() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn vị trí lưu báo cáo tồn kho");
        fileChooser.setSelectedFile(new File("BaoCaoTonKho.xlsx"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String duongDan = fileToSave.getAbsolutePath();
            if (!duongDan.endsWith(".xlsx")) {
                duongDan += ".xlsx";
            }

            // Gọi qua tầng BUS (đảm bảo file ExcelBUS và ExcelHelper vẫn giữ nguyên như cũ)
            boolean thanhCong = excelBUS.kiemTraVaXuatExcel(dsSanPham, duongDan);
            
            if (thanhCong) {
                JOptionPane.showMessageDialog(this, "Xuất file Excel thành công!\nĐã lưu tại: " + duongDan, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Tắt dialog khi xuất xong
            } else {
                JOptionPane.showMessageDialog(this, "Không thể xuất file. Dữ liệu lỗi hoặc file đang mở bởi phần mềm khác!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}