package UI.Panel.Sales;

import BUS.giamgiaBUS;
import BUS.CTggBUS;
import BUS.SanPhamBUS;
import DTO.giamgiaDTO;
import DTO.SanPhamDTO;
import DTO.CTggDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class KhuyenMaiAddDialog extends JDialog {

    private JTextField txtMa, txtTen, txtBD, txtDays, txtPercent;
    private JTable tableSP;
    private DefaultTableModel modelSP;

    private CTggBUS ctggBUS = new CTggBUS();
    private SanPhamBUS spBUS = new SanPhamBUS();
    private giamgiaBUS ggBUS;

    public KhuyenMaiAddDialog(Frame parent, giamgiaBUS ggBUS) {
        super(parent, "TẠO KHUYẾN MÃI", true);
        this.ggBUS = ggBUS;
        setSize(700, 500);
        setLocationRelativeTo(parent);
        initUI();
    }

    private void initUI() {

        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtMa = new JTextField(ggBUS.generateMaGG());
        txtMa.setEditable(true);

        txtTen = new JTextField();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime now = LocalDateTime.now();

        txtBD = new JTextField(now.format(formatter));

        txtDays = new JTextField("7");

        txtPercent = new JTextField("10");

        form.add(new JLabel("Mã GG:"));
        form.add(txtMa);

        form.add(new JLabel("Tên đợt:"));
        form.add(txtTen);

        form.add(new JLabel("Bắt đầu:"));
        form.add(txtBD);

        form.add(new JLabel("Thời hạn (ngày):"));
        form.add(txtDays);

        form.add(new JLabel("% Giảm:"));
        form.add(txtPercent);

        add(form, BorderLayout.NORTH);

        modelSP = new DefaultTableModel(
                new String[] { "Chọn", "MASP", "Tên SP" }, 0) {

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0)
                    return Boolean.class;
                return String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // CHỈ cho phép tick checkbox
            }
        };

        tableSP = new JTable(modelSP);
        tableSP.setRowHeight(40);
        tableSP.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableSP.setSelectionBackground(new Color(220, 240, 255));
        tableSP.setSelectionForeground(Color.BLACK);

        tableSP.setShowGrid(false);
        tableSP.setIntercellSpacing(new Dimension(0, 0));

        tableSP.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        tableSP.getTableHeader().setBackground(new Color(30, 30, 30));
        tableSP.getTableHeader().setForeground(Color.WHITE);
        tableSP.getTableHeader().setReorderingAllowed(false);

        tableSP.setRowSelectionAllowed(false);
        tableSP.setColumnSelectionAllowed(false);
        tableSP.setCellSelectionEnabled(false);

        loadProducts();

        add(new JScrollPane(tableSP), BorderLayout.CENTER);

        JButton btnSave = new JButton("Lưu");
        btnSave.addActionListener(e -> savePromotion());

        JPanel bottom = new JPanel();
        bottom.add(btnSave);

        add(bottom, BorderLayout.SOUTH);
    }

    private void loadProducts() {

        List<SanPhamDTO> list = spBUS.getAll();

        for (SanPhamDTO sp : list) {
            modelSP.addRow(new Object[] {
                    false,
                    sp.getMaSP(),
                    sp.getTenSP()
            });
        }
    }

    private void savePromotion() {

        try {

            LocalDateTime bd = LocalDateTime.parse(txtBD.getText());

            int days = Integer.parseInt(txtDays.getText());

            LocalDateTime kt = bd.plusDays(days);

            giamgiaDTO gg = new giamgiaDTO(
                    txtMa.getText(),
                    txtTen.getText(),
                    bd,
                    kt);

            ggBUS.add(gg);

            int percent = Integer.parseInt(txtPercent.getText());

            for (int i = 0; i < tableSP.getRowCount(); i++) {

                Boolean checked = (Boolean) tableSP.getValueAt(i, 0);

                if (checked != null && checked) {

                    String masp = tableSP.getValueAt(i, 1).toString();

                    double price = spBUS.getGiaByMaSP(masp);

                    double giasaugiam = price * (1 - percent / 100.0);

                    CTggDTO ct = new CTggDTO(
                            txtMa.getText(),
                            masp,
                            percent,
                            giasaugiam);

                    ctggBUS.add(ct);
                }
            }

            JOptionPane.showMessageDialog(this, "Tạo thành công!");
            dispose();

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ!");
        }
    }
}