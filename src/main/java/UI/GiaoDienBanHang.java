package UI;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import BUS.SanPhamBUS;
import BUS.NhanVienBUS;
import BUS.BanHangBUS;
import DTO.nhanvienDTO;
import DTO.SanPhamDTO;
import Utility.SessionManager; 
import BUS.KhachHangBUS; 
import DTO.khachhangDTO; 

public class GiaoDienBanHang extends JPanel {

    private JTable tbGioHang, tbSanPham;
    private DefaultTableModel modelSanPham, modelGioHang;
    private JTextField txtTimKiemSP, txtKhachDua;
    private JLabel lblTongTien, lblTienThua, lblNgay;
    
    private JLabel lblNhanVien; 
    private JComboBox<String> cbxKhachHang; 
    
    private JButton btnXacNhan, btnHuy, btnXuatHoaDon, btnXemLichSu, btnThemSP, btnXoa, btnThemKhach, btnTimSP;

    private BanHangBUS bhBUS = new BanHangBUS();

    private Color primaryColor = new Color(41, 128, 185);   
    private Color successColor = new Color(39, 174, 96);    
    private Color dangerColor = new Color(231, 76, 60);     
    private Font mainFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 16);
    private DecimalFormat dcf = new DecimalFormat("###,###");

    public GiaoDienBanHang() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 240, 241));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        add(createHeaderPanel(), BorderLayout.NORTH);

        JPanel pnlBody = new JPanel(new GridLayout(2, 2, 15, 15));
        pnlBody.setBackground(new Color(236, 240, 241));

        pnlBody.add(createGioHangPanel());          
        pnlBody.add(createSanPhamPanel());          
        pnlBody.add(createKhachHangThanhToanPanel()); 
        pnlBody.add(createChucNangPanel());         

        add(pnlBody, BorderLayout.CENTER);
        
        setupEventTriggers();
        loadDataKhoSanPham(new SanPhamBUS().getAll()); 
        loadDataNhanVien(); 
        loadDataKhachHang(); 
    }

    private JPanel createHeaderPanel() {
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel lblTitle = new JLabel("HỆ THỐNG BÁN HÀNG TẠI QUẦY", SwingConstants.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(primaryColor);

        JPanel pnlInfo = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        pnlInfo.setBackground(Color.WHITE);

        pnlInfo.add(new JLabel("Nhân viên trực: "));
        
        lblNhanVien = new JLabel("Đang tải..."); 
        lblNhanVien.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblNhanVien.setForeground(primaryColor);
        pnlInfo.add(lblNhanVien);

        lblNgay = new JLabel("Ngày: " + java.time.LocalDate.now().toString());
        pnlInfo.add(lblNgay);

        pnlHeader.add(lblTitle, BorderLayout.WEST);
        pnlHeader.add(pnlInfo, BorderLayout.EAST);
        return pnlHeader;
    }

    private void loadDataNhanVien() {
        try {
            if (SessionManager.currentUser != null) {
                String maNV = SessionManager.currentUser.getId(); 
                String hoTenThucTe = new NhanVienBUS().layTenNhanVien(maNV);
                
                if (hoTenThucTe.equals("Không tìm thấy")) {
                    hoTenThucTe = SessionManager.currentUser.getTen(); 
                }
                lblNhanVien.setText(hoTenThucTe);
            } else {
                lblNhanVien.setText("Hệ Thống");
            }
        } catch (Exception e) { 
            e.printStackTrace(); 
        }
    }

    private void loadDataKhachHang() {
        try {
            cbxKhachHang.removeAllItems();
            cbxKhachHang.addItem("Khách Vãng Lai - 0000000000"); 
            
            KhachHangBUS khBUS = new KhachHangBUS();
            ArrayList<khachhangDTO> listKH = khBUS.getList(); 
            
            if (listKH != null) {
                for (khachhangDTO kh : listKH) {
                    cbxKhachHang.addItem(kh.getHoTen() + " - " + kh.getSdt());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel createGioHangPanel() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY, 1), "1. GIỎ HÀNG", TitledBorder.LEFT, TitledBorder.TOP, titleFont, primaryColor));
        
        modelGioHang = new DefaultTableModel(new String[]{"STT", "Mã IMEI", "Tên SP", "Bảo hành (Tháng)", "Đơn Giá", "Thành Tiền"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return column == 3; }
        };
        
        tbGioHang = new JTable(modelGioHang);
        styleTable(tbGioHang);
        pnl.add(new JScrollPane(tbGioHang), BorderLayout.CENTER);
        
        btnXoa = createFlatButton("Xóa Món Chọn", dangerColor, Color.WHITE);
        JPanel pnlB = new JPanel(new FlowLayout(FlowLayout.RIGHT)); 
        pnlB.setBackground(Color.WHITE);
        pnlB.add(btnXoa); 
        pnl.add(pnlB, BorderLayout.SOUTH);
        
        return pnl;
    }
   
    private JPanel createSanPhamPanel() {
        JPanel pnl = new JPanel(new BorderLayout(5, 5));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY, 1), "2. KHO SẢN PHẨM", TitledBorder.LEFT, TitledBorder.TOP, titleFont, primaryColor));

        JPanel pnlTop = new JPanel(new BorderLayout(5, 0));
        pnlTop.setBackground(Color.WHITE);
        txtTimKiemSP = new JTextField();
        pnlTop.add(new JLabel(" Tên/Mã SP: "), BorderLayout.WEST);
        pnlTop.add(txtTimKiemSP, BorderLayout.CENTER);
        btnTimSP = createFlatButton("Tìm", primaryColor, Color.WHITE);
        pnlTop.add(btnTimSP, BorderLayout.EAST);

        String[] cols = {"Mã SP", "Tên SP (Gồm KM)", "Số lượng", "Cấu hình", "Giá Bán (Đã Giảm)"};
        modelSanPham = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        tbSanPham = new JTable(modelSanPham);
        styleTable(tbSanPham);
        pnl.add(new JScrollPane(tbSanPham), BorderLayout.CENTER);
        
        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBot.setBackground(Color.WHITE);
        btnThemSP = createFlatButton("Thêm Vào Giỏ", successColor, Color.WHITE);
        pnlBot.add(btnThemSP);
        pnl.add(pnlBot, BorderLayout.SOUTH);
        return pnl;
    }

    private JPanel createKhachHangThanhToanPanel() {
        JPanel pnl = new JPanel(new BorderLayout(0, 15)); 
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY, 1), "3. THÔNG TIN THANH TOÁN", TitledBorder.LEFT, TitledBorder.TOP, titleFont, primaryColor));

        // --- GIAO DIỆN CHỌN KHÁCH ĐÃ ĐƯỢC CHỈNH LẠI BẰNG BORDERLAYOUT ---
        JPanel pnlKhach = new JPanel(new BorderLayout(10, 0));
        pnlKhach.setBackground(Color.WHITE);
        pnlKhach.setBorder(new EmptyBorder(5, 20, 5, 20));
        
        cbxKhachHang = new JComboBox<>();
        cbxKhachHang.setFont(mainFont);
        
        btnThemKhach = createFlatButton("Thêm Mới", successColor, Color.WHITE); 
        btnThemKhach.setPreferredSize(new Dimension(100, 35));
        
        pnlKhach.add(new JLabel("Khách Hàng: "), BorderLayout.WEST); 
        pnlKhach.add(cbxKhachHang, BorderLayout.CENTER); 
        pnlKhach.add(btnThemKhach, BorderLayout.EAST); 

        // --- TÍNH TIỀN ---
        JPanel pnlTien = new JPanel(new GridLayout(3, 2, 10, 15)); 
        pnlTien.setBackground(Color.WHITE);
        pnlTien.setBorder(new EmptyBorder(5, 20, 5, 20)); 
        
        lblTongTien = new JLabel("0 VNĐ"); lblTongTien.setForeground(dangerColor); lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 22)); 
        txtKhachDua = new JTextField(""); lblTienThua = new JLabel("0 VNĐ"); lblTienThua.setFont(new Font("Segoe UI", Font.BOLD, 18));
        
        pnlTien.add(new JLabel("TỔNG TIỀN:", SwingConstants.RIGHT)); pnlTien.add(lblTongTien);
        pnlTien.add(new JLabel("Khách Đưa:", SwingConstants.RIGHT)); pnlTien.add(txtKhachDua);
        pnlTien.add(new JLabel("Tiền Thừa:", SwingConstants.RIGHT)); pnlTien.add(lblTienThua);

        pnl.add(pnlKhach, BorderLayout.NORTH);
        pnl.add(pnlTien, BorderLayout.CENTER);
        return pnl;
    }

    private JPanel createChucNangPanel() {
        JPanel pnl = new JPanel(new GridLayout(3, 2, 10, 10));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY, 1), "4. CHỨC NĂNG", TitledBorder.LEFT, TitledBorder.TOP, titleFont, primaryColor));
        
        btnXacNhan = createFlatButton("XÁC NHẬN CHỐT ĐƠN", successColor, Color.WHITE);
        btnHuy = createFlatButton("HỦY GIỎ HÀNG", dangerColor, Color.WHITE);
        btnXuatHoaDon = createFlatButton("XUẤT HÓA ĐƠN", primaryColor, Color.WHITE);
        btnXemLichSu = createFlatButton("XEM LỊCH SỬ", new Color(241, 196, 15), Color.BLACK);
        
        JButton btnInBaoHanh = createFlatButton("IN BẢO HÀNH", new Color(142, 68, 173), Color.WHITE);
        btnInBaoHanh.addActionListener(e -> hienThiPhieuBaoHanh()); 

        pnl.add(btnXacNhan); 
        pnl.add(btnHuy); 
        pnl.add(btnXuatHoaDon); 
        pnl.add(btnInBaoHanh); 
        pnl.add(btnXemLichSu); 
        
        return pnl;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(35); table.setFont(mainFont);
        table.setSelectionBackground(new Color(204, 229, 255)); 
        table.setShowGrid(true); table.setGridColor(new Color(236, 240, 241));
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(primaryColor); setForeground(Color.WHITE);
                setFont(new Font("Segoe UI", Font.BOLD, 14)); setHorizontalAlignment(JLabel.CENTER);
                return this;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) table.getTableHeader().getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
    }

    private JButton createFlatButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg); btn.setForeground(fg);
        btn.setFocusPainted(false); btn.setOpaque(true); btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void loadDataKhoSanPham(List<SanPhamDTO> listSP) {
        try {
            modelSanPham.setRowCount(0); 
            for (SanPhamDTO sp : listSP) {
                int slHienThi = bhBUS.getSoLuongMayRanh(sp.getMaSP());
                Object[] dataKM = bhBUS.getGiaKhuyenMai(sp.getMaSP(), sp.getGia());
                
                double giaThucTe = 0;
                String textKM = "";
                if(dataKM[0] instanceof Double) giaThucTe = (Double) dataKM[0];
                if(dataKM[1] instanceof String) textKM = (String) dataKM[1];
                
                String tenKemKhuyenMai = sp.getTenSP() + textKM;

                modelSanPham.addRow(new Object[]{sp.getMaSP(), tenKemKhuyenMai, slHienThi, sp.getCauHinh(), dcf.format(giaThucTe)});
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void tinhTienThuaTuDong() {
        try {
            String tongTienStr = lblTongTien.getText().replaceAll("[^\\d]", "");
            String tienDuaStr = txtKhachDua.getText().replaceAll("[^\\d]", "");

            double tongTien = tongTienStr.isEmpty() ? 0 : Double.parseDouble(tongTienStr);
            double khachDua = tienDuaStr.isEmpty() ? 0 : Double.parseDouble(tienDuaStr);

            double tienThua = khachDua - tongTien;

            if (tienDuaStr.isEmpty()) {
                lblTienThua.setText("0 VNĐ");
                lblTienThua.setForeground(Color.BLACK);
            } else if (tienThua >= 0) {
                lblTienThua.setText(dcf.format(tienThua) + " VNĐ");
                lblTienThua.setForeground(successColor); 
            } else {
                lblTienThua.setText("Thiếu: " + dcf.format(Math.abs(tienThua)) + " VNĐ");
                lblTienThua.setForeground(dangerColor); 
            }
        } catch (Exception e) {}
    }

    private void setupEventTriggers() {
        
        txtKhachDua.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { tinhTienThuaTuDong(); }
            public void removeUpdate(DocumentEvent e) { tinhTienThuaTuDong(); }
            public void changedUpdate(DocumentEvent e) { tinhTienThuaTuDong(); }
        });

        btnXuatHoaDon.addActionListener(e -> {
            if (modelGioHang.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Giỏ hàng đang trống! Chưa mua gì sao in hóa đơn!");
                return;
            }
            double tongTien = 0, khachDua = 0;
            try { tongTien = Double.parseDouble(lblTongTien.getText().replaceAll("[^\\d]", "")); } catch (Exception ex) {}
            try { khachDua = Double.parseDouble(txtKhachDua.getText().replaceAll("[^\\d]", "")); } catch (Exception ex) {}
            
            String selectedKhach = cbxKhachHang.getSelectedItem() != null ? cbxKhachHang.getSelectedItem().toString() : "Khách Vãng Lai - 0000000000";
            String[] parts = selectedKhach.split(" - ");
            String tenKhach = parts[0];
            String sdt = parts.length > 1 ? parts[1] : "0000000000";
            
            String tenNV = lblNhanVien.getText();
            
            hienThiHoaDon("BẢN NHÁP (CHƯA LƯU)", tenKhach, sdt, tenNV, tongTien, khachDua, khachDua - tongTien, null);
        });

        btnTimSP.addActionListener(e -> {
            String keyword = txtTimKiemSP.getText().trim();
            if (keyword.isEmpty()) loadDataKhoSanPham(new SanPhamBUS().getAll());
            else loadDataKhoSanPham(new SanPhamBUS().timKiem("Tất cả", keyword));
        });

        // --- NÚT THÊM KHÁCH HÀNG MỚI ĐÃ ĐƯỢC GIỮ NGUYÊN ---
        btnThemKhach.addActionListener(e -> {
            JTextField txtTen = new JTextField();
            JTextField txtSDT = new JTextField();
            
            JComboBox<String> cbGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
            JTextField txtDiaChi = new JTextField();

            Object[] message = { "Họ tên khách hàng (*):", txtTen, "Số điện thoại (*):", txtSDT, "Giới tính:", cbGioiTinh, "Địa chỉ:", txtDiaChi };

            int option = JOptionPane.showConfirmDialog(this, message, "Thêm Khách Hàng Mới", JOptionPane.OK_CANCEL_OPTION);
            
            if (option == JOptionPane.OK_OPTION) {
                String ten = txtTen.getText().trim();
                String sdt = txtSDT.getText().trim();
                String gioiTinh = cbGioiTinh.getSelectedItem().toString();
                String diaChi = txtDiaChi.getText().trim().isEmpty() ? "Chưa cập nhật" : txtDiaChi.getText().trim();

                if (ten.isEmpty() || sdt.isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng nhập đủ Họ Tên và SĐT!"); return; }
                if (!sdt.matches("^0\\d{9}$")) { JOptionPane.showMessageDialog(this, "SĐT sai định dạng!"); return; }

                try {
                    bhBUS.themKhachHangNhanh(ten, sdt, gioiTinh, diaChi);
                    JOptionPane.showMessageDialog(this, "Đã thêm khách hàng mới thành công!");
                    
                    String khachMoi = ten + " - " + sdt;
                    cbxKhachHang.addItem(khachMoi);
                    cbxKhachHang.setSelectedItem(khachMoi);
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage());
                }
            }
        });

        btnThemSP.addActionListener(e -> {
            int row = tbSanPham.getSelectedRow();
            if (row == -1) { JOptionPane.showMessageDialog(this, "Chọn máy bên Kho Sản Phẩm đã!"); return; }
            
            String maSP = tbSanPham.getValueAt(row, 0).toString();
            String tenSP = tbSanPham.getValueAt(row, 1).toString(); 
            String gia = tbSanPham.getValueAt(row, 4).toString(); 
            
            int ton = 0;
            try { ton = Integer.parseInt(tbSanPham.getValueAt(row, 2).toString().trim()); } catch(Exception ex){}
            if (ton <= 0) { JOptionPane.showMessageDialog(this,  "Máy này đã hết hàng!"); return; }

            List<String> imeiTrongGio = new ArrayList<>();
            for (int i = 0; i < modelGioHang.getRowCount(); i++) {
                imeiTrongGio.add(modelGioHang.getValueAt(i, 1).toString().trim());
            }

            String realIMEI = bhBUS.getIMEIKhaDung(maSP, imeiTrongGio);

            if (realIMEI != null) {
                modelGioHang.addRow(new Object[]{modelGioHang.getRowCount() + 1, realIMEI, tenSP, "12", gia, gia});
                capNhatTongTien();
            } else { JOptionPane.showMessageDialog(this, "Không tìm thấy mã IMEI khả dụng hoặc bạn đã gom hết máy rảnh vô giỏ rồi!"); }
        });

        btnXoa.addActionListener(e -> {
            int row = tbGioHang.getSelectedRow();
            if (row != -1) { modelGioHang.removeRow(row); capNhatTongTien(); }
            else { JOptionPane.showMessageDialog(this, "Chọn 1 dòng bên giỏ hàng để xóa!"); }
        });

        btnHuy.addActionListener(e -> { 
            if (JOptionPane.showConfirmDialog(this, "Hủy toàn bộ giỏ hàng?", "Cảnh báo", JOptionPane.YES_NO_OPTION) == 0) {
                modelGioHang.setRowCount(0); capNhatTongTien(); txtKhachDua.setText("");
            }
        });

        btnXacNhan.addActionListener(e -> {
            if (tbGioHang.isEditing()) tbGioHang.getCellEditor().stopCellEditing();

            if (modelGioHang.getRowCount() == 0) { JOptionPane.showMessageDialog(this, " Giỏ hàng đang trống!"); return; }

            try {
                double tongTien = Double.parseDouble(lblTongTien.getText().replaceAll("[^\\d]", ""));
                String tienDuaStr = txtKhachDua.getText().replaceAll("[^\\d]", "");
                
                if (tienDuaStr.isEmpty()) { JOptionPane.showMessageDialog(this, "️ Nhập số tiền Khách Đưa!"); txtKhachDua.requestFocus(); return; }
                
                double khachDua = Double.parseDouble(tienDuaStr);
                if (khachDua < tongTien) { JOptionPane.showMessageDialog(this, "️ Khách đưa THIẾU TIỀN!"); txtKhachDua.requestFocus(); return; }
                
                double tienThua = khachDua - tongTien;
                
                String selectedKhach = cbxKhachHang.getSelectedItem() != null ? cbxKhachHang.getSelectedItem().toString() : "";
                String[] parts = selectedKhach.split(" - ");
                String tenKhach = parts[0];
                String sdt = parts.length > 1 ? parts[1] : "";
                
                // --- BẮT LỖI KHÁCH VÃNG LAI Ở ĐÂY ---
                if (sdt.isEmpty() || sdt.equals("0000000000")) { 
                    JOptionPane.showMessageDialog(this, "Bạn bắt buộc phải chọn Khách Hàng có thật hoặc Thêm Khách Mới để lưu Hóa Đơn vào CSDL!"); 
                    cbxKhachHang.requestFocus();
                    return; 
                }
                
                String tenNV = lblNhanVien.getText();

                List<Object[]> cartItems = new ArrayList<>();
                for (int i = 0; i < modelGioHang.getRowCount(); i++) {
                    String imei = modelGioHang.getValueAt(i, 1).toString();
                    int soThangBH = Integer.parseInt(modelGioHang.getValueAt(i, 3).toString());
                    double donGia = Double.parseDouble(modelGioHang.getValueAt(i, 4).toString().replaceAll("[^\\d]", ""));
                    cartItems.add(new Object[]{imei, donGia, soThangBH});
                }

                List<String> listKetQua = bhBUS.chotDonHang(sdt, tenNV, tongTien, cartItems);

                String maHDMoi = listKetQua.get(0);
                listKetQua.remove(0); 

                JOptionPane.showMessageDialog(this, "XUẤT SẮC! Đã lưu Hóa Đơn " + maHDMoi + " thành công!");
                hienThiHoaDon(maHDMoi, tenKhach, sdt, tenNV, tongTien, khachDua, tienThua, listKetQua);
                
                modelGioHang.setRowCount(0); capNhatTongTien(); 
                txtKhachDua.setText(""); 
                cbxKhachHang.setSelectedIndex(0); 
                lblTienThua.setText("0 VNĐ");
                loadDataKhoSanPham(new SanPhamBUS().getAll());
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi chốt đơn: \n" + ex.getMessage(), "LỖI TRANSACTION", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        btnXemLichSu.addActionListener(e -> {
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Lịch Sử Bán Hàng", true);
            dialog.setSize(800, 450); dialog.setLocationRelativeTo(this);
            
            String[] cols = {"Mã HĐ", "Mã KH", "Mã NV", "Thời Gian Lập", "Tổng Tiền"};
            DefaultTableModel mdlLS = new DefaultTableModel(cols, 0) {
                @Override 
                public boolean isCellEditable(int row, int column) { 
                    return false; 
                }
            };
            
            JTable tbLS = new JTable(mdlLS); 
            tbLS.setRowHeight(30);
            
            styleTable(tbLS);

            tbLS.getTableHeader().setReorderingAllowed(false);
            tbLS.getTableHeader().setResizingAllowed(false);
            
            tbLS.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            
            tbLS.getColumnModel().getColumn(0).setPreferredWidth(80);  
            tbLS.getColumnModel().getColumn(1).setPreferredWidth(100); 
            tbLS.getColumnModel().getColumn(2).setPreferredWidth(120); 
            tbLS.getColumnModel().getColumn(3).setPreferredWidth(160); 
            tbLS.getColumnModel().getColumn(4).setPreferredWidth(140); 
            
            List<Object[]> lichSu = bhBUS.getLichSuHoaDon();
            for (Object[] row : lichSu) {
                double tien = (Double) row[4];
                row[4] = dcf.format(tien) + " VNĐ";
                mdlLS.addRow(row);
            }

            dialog.add(new JScrollPane(tbLS)); dialog.setVisible(true);
        });
    }

     private void capNhatTongTien() {
        double tong = 0;
        for (int i = 0; i < modelGioHang.getRowCount(); i++) {
            try {
                String soSach = modelGioHang.getValueAt(i, 5).toString().replaceAll("[^\\d]", "");
                if (!soSach.isEmpty()) tong += Double.parseDouble(soSach);
            } catch (Exception e) { }
        }
        lblTongTien.setText(dcf.format(tong) + " VNĐ");
        tinhTienThuaTuDong(); 
    }

    private void hienThiHoaDon(String maHD, String tenKhach, String sdt, String tenNV, double tongTien, double khachDua, double tienThua, List<String> danhSachMaCTHD) {
        JDialog dialogHD = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "HÓA ĐƠN MUA HÀNG", true);
        dialogHD.setSize(600, 650); 
        dialogHD.setLocationRelativeTo(this);
        JTextArea txtBill = new JTextArea();
        
        txtBill.setFont(new Font("Courier New", Font.PLAIN, 14)); 
        txtBill.setEditable(false); 
        txtBill.setMargin(new Insets(10, 10, 10, 10));

        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String ngayGioDep = java.time.LocalDateTime.now().format(formatter);

        txtBill.append("===========================================================================\n");
        txtBill.append("                           PHONE SHOP - NHÓM 4                             \n");
        txtBill.append("===========================================================================\n");
        txtBill.append(" Mã HĐ     : " + maHD + "\n");
        txtBill.append(" Ngày      : " + ngayGioDep + "\n"); 
        txtBill.append(" Nhân viên : " + tenNV + "\n");
        txtBill.append(" Khách hàng: " + tenKhach + " - " + sdt + "\n");
        txtBill.append("---------------------------------------------------------------------------\n");
        
        txtBill.append(String.format(" %-8s | %-28s | %-10s | %s\n", "MÃ CTHD", "TÊN MÁY", "IMEI", "GIÁ BÁN"));
        txtBill.append("---------------------------------------------------------------------------\n");

        List<String> dsKhuyenMai = new ArrayList<>();

        for (int i = 0; i < modelGioHang.getRowCount(); i++) {
            String tenGoc = modelGioHang.getValueAt(i, 2).toString();
            String tenMayHienThi = tenGoc;
            String imei = modelGioHang.getValueAt(i, 1).toString();
            double giaDaGiam = Double.parseDouble(modelGioHang.getValueAt(i, 4).toString().replaceAll("[^\\d]", ""));
            
            double giaGoc = giaDaGiam;
            if (danhSachMaCTHD != null) { 
                giaGoc = bhBUS.getGiaGocByIMEI(imei);
            }

            if (tenGoc.contains("(KM")) {
                int viTri = tenGoc.indexOf("(KM");
                tenMayHienThi = tenGoc.substring(0, viTri).trim(); 
                String chuoiKM = tenGoc.substring(viTri).replace("(", "").replace(")", ""); 
                
                String thongTinKM = chuoiKM + " | Giá gốc: " + dcf.format(giaGoc) + "đ";
                
                if (danhSachMaCTHD == null) thongTinKM = chuoiKM; 
                
                if (!dsKhuyenMai.contains(thongTinKM)) {
                    dsKhuyenMai.add(thongTinKM); 
                }
            }

            if (tenMayHienThi.length() > 28) tenMayHienThi = tenMayHienThi.substring(0, 25) + "..."; 
            String gia = dcf.format(giaDaGiam);
            String maCTHD_HienThi = (danhSachMaCTHD == null || danhSachMaCTHD.size() <= i) ? "NHÁP." + (i+1) : danhSachMaCTHD.get(i);
            
            txtBill.append(String.format(" %-8s | %-28s | %-10s | %s\n", maCTHD_HienThi, tenMayHienThi, imei, gia));
        }

        txtBill.append("---------------------------------------------------------------------------\n");
        
        if (!dsKhuyenMai.isEmpty()) {
            for (String km : dsKhuyenMai) {
                txtBill.append(String.format("%73s\n", "Áp dụng: " + km));
            }
            txtBill.append("---------------------------------------------------------------------------\n");
        }

        txtBill.append(" TỔNG CỘNG  : " + dcf.format(tongTien) + " VNĐ\n");
        txtBill.append(" KHÁCH ĐƯA  : " + dcf.format(khachDua) + " VNĐ\n");
        txtBill.append(" TIỀN THỪA  : " + dcf.format(tienThua) + " VNĐ\n");
        
        txtBill.append("===========================================================================\n");
        txtBill.append("                      CẢM ƠN QUÝ KHÁCH. HẸN GẶP LẠI!                       \n");

        dialogHD.add(new JScrollPane(txtBill), BorderLayout.CENTER);
        
        JButton btnIn = createFlatButton("XUẤT FILE PDF / IN", primaryColor, Color.WHITE);
        btnIn.addActionListener(e -> { 
            try { 
                txtBill.print(); 
            } catch (Exception ex) {} 
        });
        
        JPanel pnlBot = new JPanel(); pnlBot.add(btnIn); dialogHD.add(pnlBot, BorderLayout.SOUTH);
        dialogHD.setVisible(true);
    }

    private void hienThiPhieuBaoHanh() {
        if (modelGioHang.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "⚠️ Giỏ hàng đang trống! Chưa có sản phẩm để in bảo hành.");
            return;
        }

        String selectedKhach = cbxKhachHang.getSelectedItem() != null ? cbxKhachHang.getSelectedItem().toString() : "Khách Vãng Lai - 0000000000";
        String[] parts = selectedKhach.split(" - ");
        String tenKhach = parts[0];
        String sdt = parts.length > 1 ? parts[1] : "0000000000";
        
        String tenNV = lblNhanVien.getText();

        JDialog dialogBH = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "PHIẾU BẢO HÀNH", true);
        dialogBH.setSize(650, 500);
        dialogBH.setLocationRelativeTo(this);
        JTextArea txtBH = new JTextArea();
        txtBH.setFont(new Font("Courier New", Font.PLAIN, 14));
        txtBH.setEditable(false);
        txtBH.setMargin(new Insets(10, 10, 10, 10));

        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String ngayGioDep = java.time.LocalDateTime.now().format(formatter);

        txtBH.append("===========================================================================\n");
        txtBH.append("                        PHIẾU BẢO HÀNH CHÍNH HÃNG                          \n");
        txtBH.append("===========================================================================\n");
        txtBH.append(" Ngày mua  : " + ngayGioDep + "\n");
        txtBH.append(" Nhân viên : " + tenNV + "\n");
        txtBH.append(" Khách hàng: " + tenKhach + " - " + sdt + "\n");
        txtBH.append("---------------------------------------------------------------------------\n");
        txtBH.append(String.format(" %-30s | %-15s | %s\n", "TÊN MÁY", "MÃ IMEI", "THỜI HẠN BH"));
        txtBH.append("---------------------------------------------------------------------------\n");

        boolean coBaoHanh = false;
        for (int i = 0; i < modelGioHang.getRowCount(); i++) {
            String tenMay = modelGioHang.getValueAt(i, 2).toString();
            if (tenMay.contains("(KM")) tenMay = tenMay.substring(0, tenMay.indexOf("(KM")).trim();
            if (tenMay.length() > 28) tenMay = tenMay.substring(0, 25) + "...";
            
            String imei = modelGioHang.getValueAt(i, 1).toString();
            String thoiHan = modelGioHang.getValueAt(i, 3).toString();
            
            if (!thoiHan.equals("0") && !thoiHan.isEmpty()) {
                txtBH.append(String.format(" %-30s | %-15s | %s\n", tenMay, imei, thoiHan + " Tháng"));
                coBaoHanh = true;
            }
        }

        if (!coBaoHanh) {
            txtBH.append(" Không có sản phẩm nào áp dụng bảo hành trong đơn hàng này.\n");
        }

        txtBH.append("---------------------------------------------------------------------------\n");
        txtBH.append(" ĐIỀU KIỆN BẢO HÀNH:\n");
        txtBH.append(" 1. Máy còn nguyên tem, không rơi vỡ, cấn móp, vào nước.\n");
        txtBH.append(" 2. Lỗi do nhà sản xuất (Phần cứng, màn hình, mainboard...).\n");
        txtBH.append(" 3. Quý khách vui lòng đọc SĐT hoặc giữ phiếu này khi đến bảo hành.\n");
        txtBH.append("===========================================================================\n");
        txtBH.append("                 TRUNG TÂM BẢO HÀNH PHONE SHOP - XIN CẢM ƠN!               \n");

        dialogBH.add(new JScrollPane(txtBH), BorderLayout.CENTER);

        JButton btnIn = createFlatButton("🖨️ XUẤT FILE PDF / IN BẢO HÀNH", new Color(142, 68, 173), Color.WHITE);
        btnIn.addActionListener(e -> { try { txtBH.print(); } catch (Exception ex) {} });
        JPanel pnlBot = new JPanel(); pnlBot.add(btnIn); dialogBH.add(pnlBot, BorderLayout.SOUTH);
        
        dialogBH.setVisible(true);
    }
}