package UI;

import BUS.KhachHangBUS;
import DTO.khachhangDTO;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class GiaoDienKhachHang extends JPanel {

    private JTextField txtMaKH, txtHoTen, txtSDT, txtDiaChi, txtTimKiem;
    private JComboBox<String> cbxGioiTinh; 
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTimKiem;
    private JTable tbKhachHang, tbLichSu;
    private DefaultTableModel modelKhachHang, modelLichSu;
    
    // GỌI THẰNG BUS LÊN LÀM QUẢN LÝ (CHUẨN 3 LỚP)
    private KhachHangBUS khBUS = new KhachHangBUS();
    
    private Color primaryColor = new Color(41, 128, 185);
    private Color successColor = new Color(39, 174, 96);
    private Color warningColor = new Color(243, 156, 18);
    private Color dangerColor = new Color(231, 76, 60);
    private Font mainFont = new Font("Segoe UI", Font.PLAIN, 14);
    private DecimalFormat dcf = new DecimalFormat("###,### VNĐ");

    public GiaoDienKhachHang() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 240, 241));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel lblTitle = new JLabel(" QUẢN LÝ KHÁCH HÀNG & CHĂM SÓC", SwingConstants.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(primaryColor);
        add(lblTitle, BorderLayout.NORTH);

        JPanel pnlBody = new JPanel(new BorderLayout(10, 10));
        pnlBody.setBackground(new Color(236, 240, 241));
        
        pnlBody.add(createTopPanel(), BorderLayout.NORTH);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, createTableKhachHang(), createTableLichSu());
        splitPane.setDividerLocation(250); 
        splitPane.setDividerSize(8);
        pnlBody.add(splitPane, BorderLayout.CENTER);

        add(pnlBody, BorderLayout.CENTER);

        setupEventTriggers();
        loadDataKhachHang(""); 
    }

    private JPanel createTopPanel() {
        JPanel pnlTop = new JPanel(new BorderLayout(10, 10));
        pnlTop.setBackground(Color.WHITE);
        pnlTop.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY, 1), "Thông Tin Khách Hàng", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), primaryColor));

        JPanel pnlForm = new JPanel(new GridLayout(3, 4, 10, 15));
        pnlForm.setBackground(Color.WHITE);
        pnlForm.setBorder(new EmptyBorder(10, 10, 10, 10));

        txtMaKH = new JTextField(); 
        txtHoTen = new JTextField();
        cbxGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"}); 
        txtSDT = new JTextField();
        txtDiaChi = new JTextField();

        pnlForm.add(new JLabel("Mã KH:", SwingConstants.RIGHT)); pnlForm.add(txtMaKH);
        pnlForm.add(new JLabel("Họ và Tên:", SwingConstants.RIGHT)); pnlForm.add(txtHoTen);
        
        pnlForm.add(new JLabel("Giới tính:", SwingConstants.RIGHT)); pnlForm.add(cbxGioiTinh);
        pnlForm.add(new JLabel("Số Điện Thoại:", SwingConstants.RIGHT)); pnlForm.add(txtSDT);
        
        pnlForm.add(new JLabel("Địa chỉ:", SwingConstants.RIGHT)); pnlForm.add(txtDiaChi);
        pnlForm.add(new JLabel("")); pnlForm.add(new JLabel("")); 

        JPanel pnlAction = new JPanel(new BorderLayout(10, 10));
        pnlAction.setBackground(Color.WHITE);
        pnlAction.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlSearch.setBackground(Color.WHITE);
        txtTimKiem = new JTextField(15);
        btnTimKiem = createFlatButton(" Tìm Tên/SĐT", primaryColor);
        pnlSearch.add(txtTimKiem); pnlSearch.add(btnTimKiem);

        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlButtons.setBackground(Color.WHITE);
        btnThem = createFlatButton(" Thêm", successColor);
        btnSua = createFlatButton(" Cập Nhật", warningColor);
        btnXoa = createFlatButton(" Xóa", dangerColor);
        btnLamMoi = createFlatButton(" Làm Mới", primaryColor);

        pnlButtons.add(btnThem); pnlButtons.add(btnSua); pnlButtons.add(btnXoa); pnlButtons.add(btnLamMoi);

        pnlAction.add(pnlSearch, BorderLayout.NORTH);
        pnlAction.add(pnlButtons, BorderLayout.SOUTH);

        pnlTop.add(pnlForm, BorderLayout.CENTER);
        pnlTop.add(pnlAction, BorderLayout.EAST);
        return pnlTop;
    }

    private JPanel createTableKhachHang() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createTitledBorder(null, "Danh Sách Khách Hàng", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), primaryColor));

        modelKhachHang = new DefaultTableModel(new String[]{"Mã KH", "Họ Tên", "Giới Tính", "Số Điện Thoại", "Địa Chỉ"}, 0){
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tbKhachHang = new JTable(modelKhachHang);
        styleTable(tbKhachHang);
        pnl.add(new JScrollPane(tbKhachHang), BorderLayout.CENTER);
        return pnl;
    }

    private JPanel createTableLichSu() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createTitledBorder(null, "Lịch Sử Giao Dịch Của Khách Này", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 14), new Color(142, 68, 173)));

        modelLichSu = new DefaultTableModel(new String[]{"Mã Hóa Đơn", "Nhân Viên Phục Vụ", "Ngày Mua", "Tổng Tiền"}, 0){
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tbLichSu = new JTable(modelLichSu);
        styleTable(tbLichSu);
        pnl.add(new JScrollPane(tbLichSu), BorderLayout.CENTER);
        return pnl;
    }

    private void setupEventTriggers() {
        
        tbKhachHang.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tbKhachHang.getSelectedRow() != -1) {
                int row = tbKhachHang.getSelectedRow();
                String maKH = tbKhachHang.getValueAt(row, 0).toString();
                
                txtMaKH.setText(maKH);
                txtMaKH.setEditable(false); 
                txtHoTen.setText(tbKhachHang.getValueAt(row, 1).toString());
                cbxGioiTinh.setSelectedItem(tbKhachHang.getValueAt(row, 2).toString());
                txtSDT.setText(tbKhachHang.getValueAt(row, 3).toString());
                
                Object diaChiObj = tbKhachHang.getValueAt(row, 4);
                txtDiaChi.setText(diaChiObj != null ? diaChiObj.toString() : "");
                
                loadLichSuGiaoDich(maKH);
            }
        });

        btnLamMoi.addActionListener(e -> {
            txtMaKH.setText(""); txtHoTen.setText(""); txtSDT.setText(""); txtDiaChi.setText(""); txtTimKiem.setText("");
            cbxGioiTinh.setSelectedIndex(0);
            txtMaKH.setEditable(true); 
            tbKhachHang.clearSelection();
            modelLichSu.setRowCount(0); 
            loadDataKhachHang("");
        });

        btnTimKiem.addActionListener(e -> { loadDataKhachHang(txtTimKiem.getText().trim()); });

        btnThem.addActionListener(e -> {
            String maKH = txtMaKH.getText().trim();
            String ten = txtHoTen.getText().trim();
            String sdt = txtSDT.getText().trim();
            String diaChi = txtDiaChi.getText().trim();
            
            // XỬ LÝ ENUM GIỚI TÍNH
            String gioiTinhStr = cbxGioiTinh.getSelectedItem().toString();
            khachhangDTO.GioiTinh gtEnum = khachhangDTO.GioiTinh.KHAC;
            if (gioiTinhStr.equalsIgnoreCase("Nam")) gtEnum = khachhangDTO.GioiTinh.NAM;
            else if (gioiTinhStr.equalsIgnoreCase("Nữ")) gtEnum = khachhangDTO.GioiTinh.NU;
            
            if (maKH.isEmpty() || ten.isEmpty() || sdt.isEmpty()) { 
                JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ Mã KH, Họ Tên và SĐT!"); 
                return; 
            }
            if (!sdt.matches("^0\\d{9}$")) { 
                JOptionPane.showMessageDialog(this, "SĐT không hợp lệ (Bắt đầu bằng 0, gồm 10 số)!"); 
                return; 
            }

            // GÓI DATA VÀO DTO VÀ ĐẨY XUỐNG BUS (Truyền gtEnum vào đây)
            khachhangDTO khMoi = new khachhangDTO(maKH, ten, gtEnum, diaChi, sdt);
            
            if (khBUS.themKH(khMoi)) {
                JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!");
                btnLamMoi.doClick(); 
            } else {
                JOptionPane.showMessageDialog(this, "Thêm thất bại! Mã Khách Hàng hoặc SĐT đã bị trùng.");
            }
        });

        btnSua.addActionListener(e -> {
            String maKH = txtMaKH.getText();
            if (maKH.isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng chọn 1 khách hàng trên bảng để sửa!"); return; }
            
            String ten = txtHoTen.getText().trim();
            String sdt = txtSDT.getText().trim();
            String diaChi = txtDiaChi.getText().trim();
            
  
            String gioiTinhStr = cbxGioiTinh.getSelectedItem().toString();
            khachhangDTO.GioiTinh gtEnum = khachhangDTO.GioiTinh.KHAC;
            if (gioiTinhStr.equalsIgnoreCase("Nam")) gtEnum = khachhangDTO.GioiTinh.NAM;
            else if (gioiTinhStr.equalsIgnoreCase("Nữ") || gioiTinhStr.equalsIgnoreCase("Nu")) gtEnum = khachhangDTO.GioiTinh.NU;
            
            if (!sdt.matches("^0\\d{9}$")) { JOptionPane.showMessageDialog(this, "SĐT không hợp lệ!"); return; }

            // GÓI DATA VÀO DTO VÀ ĐẨY XUỐNG BUS (Truyền gtEnum vào đây)
            khachhangDTO khSua = new khachhangDTO(maKH, ten, gtEnum, diaChi, sdt);
            
            if (khBUS.suaKH(khSua)) {
                JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
                loadDataKhachHang("");
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại!");
            }
        });
                
        btnXoa.addActionListener(e -> {
            String maKH = txtMaKH.getText();
            if (maKH.isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng chọn khách cần xóa!"); return; }
            
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa khách hàng này?", "Cảnh báo", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (khBUS.xoaKH(maKH)) {
                    JOptionPane.showMessageDialog(this, "Đã xóa khách hàng!");
                    btnLamMoi.doClick();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa! Khách này đã có lịch sử mua hàng trong Database."); 
                }
            }
        });
    }

    private void loadDataKhachHang(String keyword) {
        modelKhachHang.setRowCount(0);
        
        // GỌI BUS ĐỂ LẤY DANH SÁCH
        ArrayList<khachhangDTO> list = khBUS.timKiem(keyword);
        
        for (khachhangDTO kh : list) {
            modelKhachHang.addRow(new Object[]{
                kh.getMaKH(), 
                kh.getHoTen(), 
                kh.getGioiTinh(),
                kh.getSdt(),
                kh.getDiaChi()
            });
        }
    }

    private void loadLichSuGiaoDich(String maKH) {
        modelLichSu.setRowCount(0);
        
        // GỌI BUS ĐỂ LẤY DANH SÁCH LỊCH SỬ (Dạng Object[])
        ArrayList<Object[]> listLichSu = khBUS.getLichSuMuaHang(maKH);
        
        for (Object[] row : listLichSu) {
            // Định dạng lại cột Tiền (nằm ở vị trí index số 3 trong mảng)
            double tien = (double) row[3];
            row[3] = dcf.format(tien);
            
            modelLichSu.addRow(row);
        }
    }

    private void styleTable(JTable table) {
        table.setRowHeight(30); table.setFont(mainFont);
        table.setSelectionBackground(new Color(204, 229, 255));
        table.getTableHeader().setBackground(primaryColor);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
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
        JFrame frame = new JFrame("Giao Diện Quản Lý Khách Hàng");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);
        frame.setLocationRelativeTo(null);
        frame.add(new GiaoDienKhachHang());
        frame.setVisible(true);
    }
}
