package UI.Panel.Sales;

import BUS.CTggBUS;
import BUS.SanPhamBUS;
import DTO.CTggDTO;
import DTO.SanPhamDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;

public class KhuyenMaiDetailDialog extends JDialog {

    private JTable table;
    private DefaultTableModel model;

    private CTggBUS ctggBUS = new CTggBUS();
    private SanPhamBUS sanPhamBUS = new SanPhamBUS();

    private String maGG;

    public KhuyenMaiDetailDialog(Frame parent, String maGG) {
        super(parent, "Chi tiết khuyến mãi", true);
        this.maGG = maGG;

        initUI();
        loadData();

        setSize(700, 450);
        setLocationRelativeTo(parent);
    }

    private void initUI() {

        setLayout(new BorderLayout());
        setResizable(false);

        JLabel lblTitle = new JLabel("Chi tiết khuyến mãi: " + maGG);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));

        add(lblTitle, BorderLayout.NORTH);

        // THÊM CỘT GIÁ SAU GIẢM
        model = new DefaultTableModel(
                new String[] { "MÃ SP", "TÊN SẢN PHẨM", "% GIẢM", "GIÁ SAU GIẢM" }, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);

        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(220, 240, 255));
        table.setSelectionForeground(Color.BLACK);

        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setBackground(new Color(30,30,30));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);

        // căn phải cột giá
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(3).setCellRenderer(right);
        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        JButton btnAdd = new JButton("Thêm SP");
        JButton btnDelete = new JButton("Xóa SP");
        JButton btnEdit = new JButton("Sửa");

        btnAdd.addActionListener(e -> addProduct());
        btnDelete.addActionListener(e -> deleteProduct());
        btnEdit.addActionListener(e -> editProduct());

        JPanel bottom = new JPanel();
        bottom.add(btnAdd);
        bottom.add(btnDelete);
        bottom.add(btnEdit);

        add(bottom, BorderLayout.SOUTH);
    }

    private void loadData() {

        model.setRowCount(0);

        List<CTggDTO> list = ctggBUS.getByMaGG(maGG);

        for (CTggDTO ct : list) {

            SanPhamDTO sp = sanPhamBUS.getById(ct.getMaSP());

            String tenSP = (sp != null) ? sp.getTenSP() : "Không tìm thấy";

            NumberFormat vnd = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

            model.addRow(new Object[] {
                    ct.getMaSP(),
                    tenSP,
                    ct.getPhantramgg(),
                    vnd.format(ct.getGiasaugiam())
            });
        }
    }

    // ================= THÊM SẢN PHẨM =================
    private void addProduct() {

        List<SanPhamDTO> listSP = sanPhamBUS.getAll();

        JComboBox<String> cbSanPham = new JComboBox<>();
        JTextField txtPercent = new JTextField();

        for (SanPhamDTO sp : listSP) {
            cbSanPham.addItem(sp.getMaSP() + " - " + sp.getTenSP());
        }

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2, 10, 10));

        panel.add(new JLabel("Chọn sản phẩm:"));
        panel.add(cbSanPham);

        panel.add(new JLabel("Phần trăm giảm:"));
        panel.add(txtPercent);

        int option = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Thêm sản phẩm giảm giá",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {

            try {

                String selected = cbSanPham.getSelectedItem().toString();
                String maSP = selected.split(" - ")[0];

                int percent = Integer.parseInt(txtPercent.getText());

                double gia = sanPhamBUS.getGiaByMaSP(maSP);
                double giasaugiam = gia * (1 - percent / 100.0);

                CTggDTO ct = new CTggDTO(maGG, maSP, percent, giasaugiam);

                if (ctggBUS.add(ct)) {
                    JOptionPane.showMessageDialog(this, "Thêm thành công!");
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm thất bại!");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ!");
            }
        }
    }

    // ================= XÓA SẢN PHẨM =================
    private void deleteProduct() {

        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn sản phẩm để xóa!");
            return;
        }

        String maSP = model.getValueAt(selectedRow, 0).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa sản phẩm " + maSP + " khỏi khuyến mãi?",
                "XÁC NHẬN XÓA",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {

            if (ctggBUS.delete(maGG, maSP)) {
                JOptionPane.showMessageDialog(this, "Xóa thành công!");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa thất bại!");
            }
        }
    }

    // ================= SỬA % GIẢM =================
    private void editProduct() {

        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn sản phẩm để sửa!");
            return;
        }

        String maSP = model.getValueAt(selectedRow, 0).toString();
        String currentPercent = model.getValueAt(selectedRow, 2).toString();

        JTextField txtPercent = new JTextField(currentPercent);

        Object[] message = {
                "Phần trăm giảm mới:", txtPercent
        };

        int option = JOptionPane.showConfirmDialog(
                this,
                message,
                "Sửa phần trăm giảm",
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {

                int percent = Integer.parseInt(txtPercent.getText());

                // lấy giá sản phẩm
                double price = sanPhamBUS.getGiaByMaSP(maSP);

                // tính giá sau giảm
                double giasaugiam = price * (1 - percent / 100.0);

                CTggDTO ct = new CTggDTO(maGG, maSP, percent, giasaugiam);

                if (ctggBUS.update(ct)) {
                    JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                    loadData();
                } else {
                    JOptionPane.showMessageDialog(this, "Không tồn tại mã SP!");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Dữ liệu không hợp lệ!");
            }
        }
    }
}