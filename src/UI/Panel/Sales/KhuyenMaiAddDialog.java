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

    private JTextField txtMa, txtTen, txtBD, txtKT, txtPercent;
    private JTable tableSP;
    private DefaultTableModel modelSP;

    private giamgiaBUS ggBUS = new giamgiaBUS();
    private CTggBUS ctggBUS = new CTggBUS();
    private SanPhamBUS spBUS = new SanPhamBUS();

    public KhuyenMaiAddDialog(Frame parent) {
        super(parent, "TẠO KHUYẾN MÃI", true);
        setSize(700, 500);
        setLocationRelativeTo(parent);
        initUI();
    }

    private void initUI() {

        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        txtMa = new JTextField(ggBUS.generateNewMaGG());
        txtMa.setEditable(false);
        txtTen = new JTextField();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        LocalDateTime now = LocalDateTime.now();

        txtBD = new JTextField(now.format(formatter));
        txtKT = new JTextField(now.plusDays(7).format(formatter));
        txtPercent = new JTextField("10");

        form.add(new JLabel("Mã GG:"));
        form.add(txtMa);
        form.add(new JLabel("Tên đợt:"));
        form.add(txtTen);
        form.add(new JLabel("Bắt đầu:"));
        form.add(txtBD);
        form.add(new JLabel("Kết thúc:"));
        form.add(txtKT);
        form.add(new JLabel("% Giảm:"));
        form.add(txtPercent);

        add(form, BorderLayout.NORTH);

        modelSP = new DefaultTableModel(
                new String[] { "Chọn", "MASP", "Tên SP" }, 0) {

            public Class<?> getColumnClass(int col) {
                return col == 0 ? Boolean.class : String.class;
            }
        };

        tableSP = new JTable(modelSP);
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
            giamgiaDTO gg = new giamgiaDTO(
                    txtMa.getText(),
                    txtTen.getText(),
                    LocalDateTime.parse(txtBD.getText()),
                    LocalDateTime.parse(txtKT.getText()));

            ggBUS.add(gg);

            int percent = Integer.parseInt(txtPercent.getText());

            for (int i = 0; i < tableSP.getRowCount(); i++) {
                Boolean checked = (Boolean) tableSP.getValueAt(i, 0);

                if (checked != null && checked) {
                    String masp = tableSP.getValueAt(i, 1).toString();

                    CTggDTO ct = new CTggDTO(
                            txtMa.getText(),
                            masp,
                            percent);

                    ctggBUS.add(ct);
                }
            }

            JOptionPane.showMessageDialog(this, "Tạo thành công!");
            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi dữ liệu!");
        }
    }
}