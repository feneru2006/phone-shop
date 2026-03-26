package UI;

import BUS.NhanVienBUS;
import DTO.nhanvienDTO;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class GiaoDienNhanVien extends JPanel {

    private JTextField txtMaNV, txtHoTen, txtSDT, txtDiaChi, txtThamNien, txtLuong, txtTimKiem;
    private JComboBox<String> cbxGioiTinh, cbxTrangThai; 
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem;
    private JTable tbNhanVien;
    private DefaultTableModel modelNhanVien;
    
    // GỌI THẰNG BUS LÊN ĐỂ LÀM VIỆC (CHUẨN 3 LỚP)
    private NhanVienBUS nvBUS = new NhanVienBUS();
    
    private Color primaryColor = new Color(41, 128, 185);
    private Color successColor = new Color(39, 174, 96);
    private Color warningColor = new Color(243, 156, 18);
    private Color dangerColor = new Color(231, 76, 60);
    private Font mainFont = new Font("Segoe UI", Font.PLAIN, 14);
    private DecimalFormat dcf = new DecimalFormat("###,###"); 

    public GiaoDienNhanVien() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 240, 241));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel(" QUẢN LÝ NHÂN SỰ", SwingConstants.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(primaryColor);
        add(lblTitle, BorderLayout.NORTH);

        JPanel pnlBody = new JPanel(new BorderLayout(10, 10));
        pnlBody.setBackground(new Color(236, 240, 241));
        
        pnlBody.add(createTopPanel(), BorderLayout.NORTH);
        pnlBody.add(createTableNhanVien(), BorderLayout.CENTER); 

        add(pnlBody, BorderLayout.CENTER);

        setupEventTriggers();
        loadDataNhanVien(""); 
    }

    private JPanel createTopPanel() {
        JPanel pnlTop = new JPanel(new BorderLayout(10, 10));
        pnlTop.setBackground(Color.WHITE);
        pnlTop.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY, 1), "Thông Tin (Dùng để Thêm Mới)", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), primaryColor));

        JPanel pnlForm = new JPanel(new GridLayout(4, 4, 10, 15));
        pnlForm.setBackground(Color.WHITE);
        pnlForm.setBorder(new EmptyBorder(10, 10, 10, 10));

        txtMaNV = new JTextField(); 
        txtHoTen = new JTextField();
        cbxGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
        txtSDT = new JTextField();
        txtDiaChi = new JTextField();
        txtThamNien = new JTextField();
        txtLuong = new JTextField();
        cbxTrangThai = new JComboBox<>(new String[]{"1", "0"}); 

        pnlForm.add(new JLabel("Mã NV:", SwingConstants.RIGHT)); pnlForm.add(txtMaNV);
        pnlForm.add(new JLabel("Họ và Tên:", SwingConstants.RIGHT)); pnlForm.add(txtHoTen);
        pnlForm.add(new JLabel("Giới tính:", SwingConstants.RIGHT)); pnlForm.add(cbxGioiTinh);
        pnlForm.add(new JLabel("Số Điện Thoại:", SwingConstants.RIGHT)); pnlForm.add(txtSDT);
        pnlForm.add(new JLabel("Địa chỉ:", SwingConstants.RIGHT)); pnlForm.add(txtDiaChi);
        pnlForm.add(new JLabel("Thâm niên (năm):", SwingConstants.RIGHT)); pnlForm.add(txtThamNien);
        pnlForm.add(new JLabel("Mức lương (VNĐ):", SwingConstants.RIGHT)); pnlForm.add(txtLuong);
        pnlForm.add(new JLabel("Trạng thái (1-Làm, 0-Nghỉ):", SwingConstants.RIGHT)); pnlForm.add(cbxTrangThai);

        JPanel pnlAction = new JPanel(new BorderLayout(10, 10));
        pnlAction.setBackground(Color.WHITE);
        pnlAction.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlSearch.setBackground(Color.WHITE);
        txtTimKiem = new JTextField(15);
        btnTimKiem = createFlatButton("Tìm Tên/SĐT", primaryColor);
        pnlSearch.add(txtTimKiem); pnlSearch.add(btnTimKiem);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlButtons.setBackground(Color.WHITE);
        btnThem = createFlatButton("Thêm Mới", successColor);
        btnSua = createFlatButton("Cập Nhật Từ Bảng", warningColor); 
        btnXoa = createFlatButton("Xóa", dangerColor);
        btnLamMoi = createFlatButton("Làm Mới", primaryColor);

        pnlButtons.add(btnThem); pnlButtons.add(btnSua); pnlButtons.add(btnXoa); pnlButtons.add(btnLamMoi);

        pnlAction.add(pnlSearch, BorderLayout.NORTH);
        pnlAction.add(pnlButtons, BorderLayout.SOUTH);

        pnlTop.add(pnlForm, BorderLayout.CENTER);
        pnlTop.add(pnlAction, BorderLayout.EAST);
        return pnlTop;
    }

    private JPanel createTableNhanVien() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createTitledBorder(null, "Danh Sách (Có Thể Nháy Đúp Để Sửa Trực Tiếp)", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), primaryColor));

        modelNhanVien = new DefaultTableModel(new String[]{"Mã NV", "Họ Tên", "Giới Tính", "Số Điện Thoại", "Địa Chỉ", "Thâm Niên", "Lương", "Trạng Thái"}, 0){
            @Override 
            public boolean isCellEditable(int row, int column) { 
                return column != 0;
            }
        };
        tbNhanVien = new JTable(modelNhanVien);
        styleTable(tbNhanVien);
        pnl.add(new JScrollPane(tbNhanVien), BorderLayout.CENTER);
        return pnl;
    }

    private void setupEventTriggers() {
        
        tbNhanVien.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tbNhanVien.getSelectedRow() != -1) {
                int row = tbNhanVien.getSelectedRow();
                txtMaNV.setText(tbNhanVien.getValueAt(row, 0).toString());
                txtMaNV.setEditable(false); 
                txtHoTen.setText(tbNhanVien.getValueAt(row, 1).toString());
                cbxGioiTinh.setSelectedItem(tbNhanVien.getValueAt(row, 2).toString());
                txtSDT.setText(tbNhanVien.getValueAt(row, 3).toString());
                
                Object diaChiObj = tbNhanVien.getValueAt(row, 4);
                txtDiaChi.setText(diaChiObj != null ? diaChiObj.toString() : "");

                txtThamNien.setText(tbNhanVien.getValueAt(row, 5).toString());
                txtLuong.setText(tbNhanVien.getValueAt(row, 6).toString().replaceAll("[^\\d]", ""));
                
                String trangThai = tbNhanVien.getValueAt(row, 7).toString();
                if(trangThai.equals("1")) cbxTrangThai.setSelectedIndex(0);
                else cbxTrangThai.setSelectedIndex(1);
            }
        });

        btnLamMoi.addActionListener(e -> {
            txtMaNV.setText(""); txtHoTen.setText(""); txtSDT.setText(""); txtDiaChi.setText(""); 
            txtThamNien.setText(""); txtLuong.setText(""); txtTimKiem.setText("");
            cbxGioiTinh.setSelectedIndex(0); cbxTrangThai.setSelectedIndex(0);
            txtMaNV.setEditable(true); 
            
            if (tbNhanVien.isEditing()) tbNhanVien.getCellEditor().cancelCellEditing();
            tbNhanVien.clearSelection();
            loadDataNhanVien("");
        });

        btnTimKiem.addActionListener(e -> { loadDataNhanVien(txtTimKiem.getText().trim()); });

        // ==========================================
        // NÚT THÊM MỚI (Đã chuẩn hóa 100%)
        // ==========================================
        btnThem.addActionListener(e -> {
            try {
                String maNV = txtMaNV.getText().trim();
                String ten = txtHoTen.getText().trim();
                String sdt = txtSDT.getText().trim();
                String diaChi = txtDiaChi.getText().trim();
                
                if (maNV.isEmpty() || ten.isEmpty() || sdt.isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ Mã NV, Họ Tên và SĐT!"); return; }
                if (!sdt.matches("^0\\d{9}$")) { JOptionPane.showMessageDialog(this, "SĐT không hợp lệ!"); return; }

                // Xử lý Giới Tính (Combobox -> Enum)
                String gioiTinhStr = cbxGioiTinh.getSelectedItem().toString();
                nhanvienDTO.GioiTinh gtEnum = nhanvienDTO.GioiTinh.KHAC;
                if (gioiTinhStr.equalsIgnoreCase("Nam")) gtEnum = nhanvienDTO.GioiTinh.NAM;
                else if (gioiTinhStr.equalsIgnoreCase("Nữ")) gtEnum = nhanvienDTO.GioiTinh.NU;

                double thamNien = txtThamNien.getText().isEmpty() ? 0 : Double.parseDouble(txtThamNien.getText().trim());
                double luong = txtLuong.getText().isEmpty() ? 0 : Double.parseDouble(txtLuong.getText().trim());
                
                // Xử lý Trạng Thái (Combobox -> boolean)
                int trangThaiInt = Integer.parseInt(cbxTrangThai.getSelectedItem().toString());
                boolean ttBool = (trangThaiInt == 1);

                // Gọi DTO đúng chuẩn và thứ tự
                nhanvienDTO nvMoi = new nhanvienDTO(maNV, ten, gtEnum, sdt, diaChi, thamNien, luong, ttBool);
                
                if (nvBUS.themNV(nvMoi)) {
                    JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!");
                    btnLamMoi.doClick(); 
                } else {
                    JOptionPane.showMessageDialog(this, "Thêm thất bại! Mã NV có thể bị trùng."); 
                }
            } catch (Exception ex) { 
                JOptionPane.showMessageDialog(this, "Lỗi thêm mới: " + ex.getMessage()); 
            }
        });

        // ==========================================
        // NÚT CẬP NHẬT TỪ BẢNG (Đã chuẩn hóa 100%)
        // ==========================================
        btnSua.addActionListener(e -> {
            if (tbNhanVien.isEditing()) {
                tbNhanVien.getCellEditor().stopCellEditing();
            }

            int row = tbNhanVien.getSelectedRow();
            if (row == -1) { 
                JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 dòng trên bảng để cập nhật!"); 
                return; 
            }
            
            try {
                String maNV = tbNhanVien.getValueAt(row, 0).toString();
                String ten = tbNhanVien.getValueAt(row, 1).toString().trim();
                
                // Giới tính đọc từ BẢNG ép ra Enum
                String gioiTinhStr = tbNhanVien.getValueAt(row, 2).toString().trim();
                nhanvienDTO.GioiTinh gtEnum = nhanvienDTO.GioiTinh.KHAC;
                if (gioiTinhStr.equalsIgnoreCase("Nam")) gtEnum = nhanvienDTO.GioiTinh.NAM;
                else if (gioiTinhStr.equalsIgnoreCase("Nữ") || gioiTinhStr.equalsIgnoreCase("Nu")) gtEnum = nhanvienDTO.GioiTinh.NU;
                
                String sdt = tbNhanVien.getValueAt(row, 3).toString().trim();
                String diaChi = tbNhanVien.getValueAt(row, 4).toString().trim();
                
                double thamNien = Double.parseDouble(tbNhanVien.getValueAt(row, 5).toString().trim());
                double luong = Double.parseDouble(tbNhanVien.getValueAt(row, 6).toString().replaceAll("[^\\d]", ""));
                
                // Trạng thái đọc từ BẢNG ép ra boolean
                int trangThaiInt = Integer.parseInt(tbNhanVien.getValueAt(row, 7).toString().trim());
                boolean ttBool = (trangThaiInt == 1);

                if (!sdt.matches("^0\\d{9}$")) { JOptionPane.showMessageDialog(this, "SĐT không hợp lệ!"); return; }

                // Gọi DTO đúng chuẩn và thứ tự
                nhanvienDTO nvSua = new nhanvienDTO(maNV, ten, gtEnum, sdt, diaChi, thamNien, luong, ttBool);
                
                if (nvBUS.suaNV(nvSua)) {
                    JOptionPane.showMessageDialog(this, "Đã cập nhật thành công dữ liệu trực tiếp từ Bảng!");
                    loadDataNhanVien(""); 
                } else {
                    JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi ép kiểu: Thâm niên, Lương, Trạng thái (1/0) bắt buộc phải gõ là SỐ!");
            } catch (Exception ex) { 
                JOptionPane.showMessageDialog(this, "Lỗi: " + ex.getMessage()); 
            }
        });

        // ==========================================
        // NÚT XÓA 
        // ==========================================
        btnXoa.addActionListener(e -> {
            int row = tbNhanVien.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xóa!"); return; }
            
            String maNV = tbNhanVien.getValueAt(row, 0).toString();
            
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa nhân viên " + maNV + "?", "Cảnh báo", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (nvBUS.xoaNV(maNV)) {
                    JOptionPane.showMessageDialog(this, "Đã xóa nhân viên!");
                    btnLamMoi.doClick();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa! Nhân viên này đã có lịch sử lập Hóa Đơn."); 
                }
            }
        });
    }

    private void loadDataNhanVien(String keyword) {
        modelNhanVien.setRowCount(0);
        ArrayList<nhanvienDTO> list = nvBUS.timKiem(keyword);
        
        for (nhanvienDTO nv : list) {
            
            // Ép Enum ra lại chữ để hiện lên bảng
            String gt = "Khác";
            if (nv.getGioiTinh() == nhanvienDTO.GioiTinh.NAM) gt = "Nam";
            else if (nv.getGioiTinh() == nhanvienDTO.GioiTinh.NU) gt = "Nữ";
            
            // Ép boolean ra lại số 1/0 để hiện lên bảng
            int tt = nv.istrangthai() ? 1 : 0;
            
            modelNhanVien.addRow(new Object[]{
                nv.getMaNV(), 
                nv.getHoTen(), 
                gt,
                nv.getSdt(),
                nv.getDiaChi(),
                nv.getThamNien(),
                dcf.format(nv.getLuong()), 
                tt
            });
        }
    }

    private void styleTable(JTable table) {
        table.setRowHeight(30); table.setFont(mainFont);
        table.setSelectionBackground(new Color(204, 229, 255));
        table.getTableHeader().setBackground(primaryColor);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setSelectionForeground(Color.BLACK);
    }

    private JButton createFlatButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false); btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Giao Diện Quản Lý Nhân Viên");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1050, 600);
        frame.setLocationRelativeTo(null);
        frame.add(new GiaoDienNhanVien());
        frame.setVisible(true);
    }
}
