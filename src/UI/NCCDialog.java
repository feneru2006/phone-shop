package UI.Panel.NCC;

import BUS.NCCBUS;
import DTO.NCCDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class NCCDialog extends JDialog {

    private JTextField txtMaNCC, txtTen, txtSDT, txtDiaChi;
    private JButton btnSave, btnCancel;
    
    private NCCBUS nccBus;
    private NCCDTO currentNcc; 
    private List<String> permissions;
    private boolean isEditMode;

    public NCCDialog(NCCDTO ncc, NCCBUS bus, List<String> perms) {
        this.nccBus = bus;
        this.currentNcc = ncc;
        this.permissions = perms;
        this.isEditMode = (ncc != null);

        setTitle(isEditMode ? "CẬP NHẬT NHÀ CUNG CẤP" : "THÊM NHÀ CUNG CẤP MỚI");
        // Tăng chiều rộng và chiều cao Form
        setSize(500, 480);
        setModal(true); 
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initUI();
        loadDataIfEdit();
    }

    private void initUI() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        // Tăng padding xung quanh để form nhìn thoáng hơn
        panel.setBorder(new EmptyBorder(30, 40, 30, 40));

        txtMaNCC = createTextField();
        // 👉 LUÔN KHÓA Ô MÃ NCC (Dù là Thêm mới hay Cập nhật để tránh nhập trùng)
        txtMaNCC.setEditable(false);
        txtMaNCC.setBackground(Color.decode("#F1F5F9")); 
        
        txtTen = createTextField();
        txtSDT = createTextField();
        txtDiaChi = createTextField();

        // 1. Dòng Mã NCC
        panel.add(createLabel("Mã Nhà Cung Cấp (*Tự sinh):"));
        panel.add(Box.createVerticalStrut(8)); // Tăng khoảng cách
        panel.add(txtMaNCC);
        panel.add(Box.createVerticalStrut(20));

        // 2. Dòng Tên NCC
        panel.add(createLabel("Tên Nhà Cung Cấp (*):"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(txtTen);
        panel.add(Box.createVerticalStrut(20));

        // 3. Dòng SĐT
        panel.add(createLabel("Số Điện Thoại (*):"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(txtSDT);
        panel.add(Box.createVerticalStrut(20));

        // 4. Dòng Địa chỉ
        panel.add(createLabel("Địa Chỉ:"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(txtDiaChi);
        panel.add(Box.createVerticalStrut(35)); // Cách xa nút bấm ra một chút

        // 5. Buttons
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        btnPanel.setOpaque(false);
        btnPanel.setAlignmentX(Component.LEFT_ALIGNMENT); // Căn trái cho Panel chứa nút

        btnCancel = new JButton("Hủy Bỏ");
        btnCancel.setBackground(Color.decode("#F1F5F9"));
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnCancel.setPreferredSize(new Dimension(0, 40)); // Nút to hơn
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(e -> dispose());

        btnSave = new JButton("Lưu Thay Đổi");
        btnSave.setBackground(Color.decode("#2563EB"));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnSave.setPreferredSize(new Dimension(0, 40)); // Nút to hơn
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(e -> saveNCC());

        btnPanel.add(btnCancel);
        btnPanel.add(btnSave);

        panel.add(btnPanel);
        setContentPane(panel);
    }

    // Hàm hỗ trợ tạo Label và ép nó căn lề trái
    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(Color.decode("#334155"));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT); // ÉP CĂN TRÁI
        return lbl;
    }

    // Hàm hỗ trợ tạo TextField và ép căn lề trái, kéo giãn hết cỡ
    private JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setAlignmentX(Component.LEFT_ALIGNMENT); // ÉP CĂN TRÁI
        tf.setPreferredSize(new Dimension(400, 40)); 
        tf.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); // Chiếm full chiều ngang
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return tf;
    }

    // 👉 THUẬT TOÁN TỰ SINH MÃ NCC
    private String autoGenMaNCC(NCCBUS nccBus) {
        long maxNum = 0;
        for (NCCDTO ncc : nccBus.getListNCC()) {
            if (ncc.getMaNCC().toUpperCase().startsWith("NCC")) {
                try {
                    long num = Long.parseLong(ncc.getMaNCC().substring(3));
                    if (num > maxNum) maxNum = num;
                } catch (Exception ignored) {}
            }
        }
        return "NCC" + (maxNum + 1);
    }

    private void loadDataIfEdit() {
        if (isEditMode) {
            txtMaNCC.setText(currentNcc.getMaNCC());
            txtTen.setText(currentNcc.getTen());
            txtSDT.setText(currentNcc.getSdt());
            txtDiaChi.setText(currentNcc.getDiaChi());
        } else {
            // 👉 NẾU LÀ THÊM MỚI, GỌI HÀM TỰ SINH MÃ
            txtMaNCC.setText(autoGenMaNCC(nccBus));
        }
    }

    private void saveNCC() {
        String ma = txtMaNCC.getText().trim();
        String ten = txtTen.getText().trim();
        String sdt = txtSDT.getText().trim();
        String diachi = txtDiaChi.getText().trim();

        if (ma.isEmpty() || ten.isEmpty() || sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ các trường có dấu (*)", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        NCCDTO ncc = new NCCDTO(ma, ten, diachi, sdt);
        boolean success;

        if (isEditMode) {
            success = nccBus.suaNCC(ncc, permissions);
            if (success) {
                JOptionPane.showMessageDialog(this, "Cập nhật Nhà cung cấp thành công!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi cập nhật! Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            if (nccBus.getListNCC().stream().anyMatch(n -> n.getMaNCC().equalsIgnoreCase(ma))) {
                JOptionPane.showMessageDialog(this, "Mã Nhà cung cấp đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            success = nccBus.themNCC(ncc, permissions);
            if (success) {
                JOptionPane.showMessageDialog(this, "Thêm Nhà cung cấp thành công!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi thêm vào CSDL!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}