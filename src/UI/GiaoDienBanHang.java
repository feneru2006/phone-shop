package UI;

import BUS.SanPhamBUS;
import BUS.NhanVienBUS;
import DTO.nhanvienDTO;
import DTO.SanPhamDTO;
import DTO.ChitietSPDTO;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class GiaoDienBanHang extends JPanel {

    private JTable tbGioHang, tbSanPham;
    private DefaultTableModel modelSanPham, modelGioHang;
    private JTextField txtTimKiemSP, txtSdtKhach, txtTenKhach, txtKhachDua;
    private JLabel lblTongTien, lblTienThua, lblNgay, lblMaHD;
    private JComboBox<String> cbxNhanVien;

    private JButton btnXacNhan, btnHuy, btnXuatHoaDon, btnXemLichSu, btnThemSP, btnXoa, btnTimKhach, btnThemKhach,
            btnTimSP;

    private Color primaryColor = new Color(41, 128, 185);
    private Color headerColor = new Color(41, 128, 185);
    private Color successColor = new Color(39, 174, 96);
    private Color dangerColor = new Color(231, 76, 60);
    private Font mainFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 16);
    private DecimalFormat dcf = new DecimalFormat("###,###");

    public GiaoDienBanHang() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(236, 240, 241));

        JPanel pnlBody = new JPanel(new GridLayout(2, 2, 15, 15));
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(pnlBody, BorderLayout.CENTER);

        add(createHeaderPanel(), BorderLayout.NORTH);

        pnlBody.setBackground(new Color(236, 240, 241));
        pnlBody.setBorder(new EmptyBorder(10, 15, 15, 15));

        pnlBody.add(createGioHangPanel());
        pnlBody.add(createSanPhamPanel());
        pnlBody.add(createKhachHangThanhToanPanel());
        pnlBody.add(createChucNangPanel());

        add(pnlBody, BorderLayout.CENTER);

        setupEventTriggers();
        loadDataKhoSanPham(new SanPhamBUS().getAll());
        loadDataNhanVien();
    }

    private JPanel createHeaderPanel() {
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("HỆ THỐNG BÁN HÀNG TẠI QUẦY", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(primaryColor);

        JPanel pnlInfo = new JPanel(new GridLayout(1, 3));
        pnlInfo.setBackground(Color.WHITE);

        JPanel pnlNV = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlNV.setBackground(Color.WHITE);
        pnlNV.add(new JLabel("Nhân viên: "));
        cbxNhanVien = new JComboBox<>();
        cbxNhanVien.setPreferredSize(new Dimension(180, 25));
        pnlNV.add(cbxNhanVien);

        lblNgay = new JLabel("Ngày: " + java.time.LocalDate.now().toString(), SwingConstants.CENTER);
        lblMaHD = new JLabel("Trạng thái: Đang giao dịch", SwingConstants.RIGHT);

        pnlInfo.add(pnlNV);
        pnlInfo.add(lblNgay);
        pnlInfo.add(lblMaHD);
        pnlHeader.add(lblTitle, BorderLayout.CENTER);
        pnlHeader.add(pnlInfo, BorderLayout.SOUTH);
        return pnlHeader;
    }

    private void loadDataNhanVien() {
        try {
            cbxNhanVien.removeAllItems();
            List<nhanvienDTO> list = new NhanVienBUS().getAll();
            if (list == null || list.isEmpty()) {
                cbxNhanVien.addItem("DB chưa có NV");
            } else {
                for (nhanvienDTO nv : list) {
                    cbxNhanVien.addItem(nv.getHoTen());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel createGioHangPanel() {
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY, 1),
                "1. GIỎ HÀNG THANH TOÁN", TitledBorder.LEFT, TitledBorder.TOP, titleFont, primaryColor));

        String[] cols = { "STT", "Mã IMEI", "Tên SP", "Bảo hành", "Đơn Giá", "Thành Tiền" };
        modelGioHang = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tbGioHang = new JTable(modelGioHang);
        styleTable(tbGioHang);
        pnl.add(new JScrollPane(tbGioHang), BorderLayout.CENTER);

        JPanel pnlBot = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBot.setBackground(Color.WHITE);
        btnXoa = createFlatButton("Xóa Món Chọn", dangerColor, Color.WHITE);
        pnlBot.add(btnXoa);
        pnl.add(pnlBot, BorderLayout.SOUTH);
        return pnl;
    }

    private JPanel createSanPhamPanel() {
        JPanel pnl = new JPanel(new BorderLayout(5, 5));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY, 1), "2. KHO SẢN PHẨM",
                TitledBorder.LEFT, TitledBorder.TOP, titleFont, primaryColor));

        JPanel pnlTop = new JPanel(new BorderLayout(5, 0));
        pnlTop.setBackground(Color.WHITE);
        txtTimKiemSP = new JTextField();
        pnlTop.add(new JLabel(" Tên/Mã SP: "), BorderLayout.WEST);
        pnlTop.add(txtTimKiemSP, BorderLayout.CENTER);
        btnTimSP = createFlatButton("Tìm", primaryColor, Color.WHITE);
        pnlTop.add(btnTimSP, BorderLayout.EAST);

        String[] cols = { "Mã SP", "Tên SP", "Số lượng", "Cấu hình", "Giá Bán" };
        modelSanPham = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
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
        pnl.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY, 1),
                "3. THÔNG TIN THANH TOÁN", TitledBorder.LEFT, TitledBorder.TOP, titleFont, primaryColor));

        JPanel pnlKhach = new JPanel(new GridLayout(2, 1, 5, 5));
        pnlKhach.setBackground(Color.WHITE);
        JPanel pnlSDT = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlSDT.setBackground(Color.WHITE);
        txtSdtKhach = new JTextField(15);
        btnTimKhach = createFlatButton("Tìm", primaryColor, Color.WHITE);
        btnThemKhach = createFlatButton("Khách Mới", successColor, Color.WHITE);
        pnlSDT.add(new JLabel("SĐT Khách:"));
        pnlSDT.add(txtSdtKhach);
        pnlSDT.add(btnTimKhach);
        pnlSDT.add(btnThemKhach);

        JPanel pnlTen = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTen.setBackground(Color.WHITE);
        txtTenKhach = new JTextField(25);
        txtTenKhach.setEditable(false);
        pnlTen.add(new JLabel("Họ Tên:"));
        pnlTen.add(txtTenKhach);
        pnlKhach.add(pnlSDT);
        pnlKhach.add(pnlTen);

        JPanel pnlTien = new JPanel(new GridLayout(3, 2, 10, 15));
        pnlTien.setBackground(Color.WHITE);
        pnlTien.setBorder(new EmptyBorder(10, 20, 10, 20));
        lblTongTien = new JLabel("0 VNĐ");
        lblTongTien.setForeground(dangerColor);
        lblTongTien.setFont(new Font("Segoe UI", Font.BOLD, 22));
        txtKhachDua = new JTextField("");
        txtKhachDua.setFont(new Font("Segoe UI", Font.BOLD, 28)); // chữ to hơn nữa
        txtKhachDua.setPreferredSize(new Dimension(300, 55)); // ô to hơn rõ rệt
        txtKhachDua.setHorizontalAlignment(JTextField.RIGHT);
        txtKhachDua.setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 2)); // viền cho dễ nhìn
        lblTienThua = new JLabel("0 VNĐ");
        lblTienThua.setFont(new Font("Segoe UI", Font.BOLD, 18));
        pnlTien.add(new JLabel("TỔNG TIỀN:", SwingConstants.RIGHT));
        pnlTien.add(lblTongTien);
        pnlTien.add(new JLabel("Khách Đưa:", SwingConstants.RIGHT));
        pnlTien.add(txtKhachDua);
        pnlTien.add(new JLabel("Tiền Thừa:", SwingConstants.RIGHT));
        pnlTien.add(lblTienThua);

        pnl.add(pnlKhach, BorderLayout.NORTH);
        pnl.add(pnlTien, BorderLayout.CENTER);
        return pnl;
    }

    private JPanel createChucNangPanel() {
        JPanel pnl = new JPanel(new GridLayout(2, 2, 10, 10));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY, 1),
                "4. CHỨC NĂNG BÁN HÀNG", TitledBorder.LEFT, TitledBorder.TOP, titleFont, primaryColor));
        btnXacNhan = createFlatButton("XÁC NHẬN", successColor, Color.WHITE);
        btnHuy = createFlatButton("HỦY GIỎ HÀNG", dangerColor, Color.WHITE);
        btnXuatHoaDon = createFlatButton("XUẤT HÓA ĐƠN", primaryColor, Color.WHITE);
        btnXemLichSu = createFlatButton("XEM LỊCH SỬ", new Color(241, 196, 15), Color.BLACK);
        pnl.add(btnXacNhan);
        pnl.add(btnHuy);
        pnl.add(btnXuatHoaDon);
        pnl.add(btnXemLichSu);
        return pnl;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setFont(mainFont);
        table.setSelectionBackground(new Color(204, 229, 255));
        table.setShowGrid(true);
        table.setGridColor(new Color(236, 240, 241));
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(headerColor);
                setForeground(Color.WHITE);
                setFont(new Font("Segoe UI", Font.BOLD, 14));
                setHorizontalAlignment(JLabel.CENTER);
                return this;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++)
            table.getTableHeader().getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
    }

    private JButton createFlatButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void loadDataKhoSanPham(List<SanPhamDTO> listSP) {
        try {
            modelSanPham.setRowCount(0);
            DAL.DAO.CtspDAO ctspDAO = new DAL.DAO.CtspDAO();
            for (SanPhamDTO sp : listSP) {
                int soLuongMoi = 0;
                java.util.List<ChitietSPDTO> listCTSP = ctspDAO.getByMaSP(sp.getMaSP());
                if (listCTSP != null) {
                    for (ChitietSPDTO ct : listCTSP) {
                        if (ct == null)
                            continue;
                        String tt = ct.getTinhtrang();
                        if (tt != null && (tt.toLowerCase().contains("sẵn") || tt.toLowerCase().contains("mới")
                                || tt.trim().equals("1"))) {
                            soLuongMoi++;
                        }
                    }
                }
                modelSanPham.addRow(new Object[] { sp.getMaSP(), sp.getTenSP(), soLuongMoi, sp.getCauHinh(),
                        dcf.format(sp.getGia()) });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupEventTriggers() {

        // TÌM SẢN PHẨM
        btnTimSP.addActionListener(e -> {
            String keyword = txtTimKiemSP.getText().trim();
            if (keyword.isEmpty()) {
                loadDataKhoSanPham(new SanPhamBUS().getAll());
            } else {
                loadDataKhoSanPham(new SanPhamBUS().timKiem("Tất cả", keyword));
            }
        });

        // TÌM KHÁCH
        btnTimKhach.addActionListener(e -> {
            String sdt = txtSdtKhach.getText().trim();
            if (!sdt.matches("^0\\d{9}$")) {
                JOptionPane.showMessageDialog(this, "SĐT sai định dạng!");
                return;
            }
            try (java.sql.Connection conn = DAL.DAO.DBConnection.getConnection();
                    java.sql.PreparedStatement ps = conn
                            .prepareStatement("SELECT HOTEN FROM khachhang WHERE SDT = ?")) {
                ps.setString(1, sdt);
                java.sql.ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    txtTenKhach.setText(rs.getString("HOTEN"));
                } else {
                    txtTenKhach.setText("");
                    JOptionPane.showMessageDialog(this, "Không tìm thấy! Bấm Khách Mới để thêm.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // THÊM KHÁCH
        btnThemKhach.addActionListener(e -> {
            String sdt = txtSdtKhach.getText().trim();
            if (!sdt.matches("^0\\d{9}$")) {
                JOptionPane.showMessageDialog(this, "Nhập SĐT hợp lệ trước khi thêm!");
                return;
            }
            String ten = JOptionPane.showInputDialog(this, "Nhập Họ Tên khách mới:");
            if (ten == null || ten.trim().isEmpty())
                return;

            try (java.sql.Connection conn = DAL.DAO.DBConnection.getConnection();
                    java.sql.PreparedStatement ps = conn
                            .prepareStatement("INSERT INTO khachhang (MAKH, HOTEN, SDT) VALUES (?, ?, ?)")) {
                ps.setString(1, "KH" + System.currentTimeMillis());
                ps.setString(2, ten);
                ps.setString(3, sdt);
                if (ps.executeUpdate() > 0) {
                    txtTenKhach.setText(ten);
                    JOptionPane.showMessageDialog(this, "Thêm khách thành công!");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // THÊM VÀO GIỎ HÀNG
        btnThemSP.addActionListener(e -> {
            try {
                int row = tbSanPham.getSelectedRow();
                if (row == -1) {
                    JOptionPane.showMessageDialog(this, "Ông chưa click chọn máy nào bên bảng Kho Sản Phẩm kìa!");
                    return;
                }

                int ton = Integer.parseInt(tbSanPham.getValueAt(row, 2).toString());
                if (ton <= 0) {
                    JOptionPane.showMessageDialog(this, "Hết hàng rồi ông ơi!");
                    return;
                }

                String maSP = String.valueOf(tbSanPham.getValueAt(row, 0));
                String tenSP = String.valueOf(tbSanPham.getValueAt(row, 1));

                // Lọc tiền an toàn (xóa mọi chữ cái, dấu phẩy, dấu chấm)
                String giaStr = String.valueOf(tbSanPham.getValueAt(row, 4)).replaceAll("[^\\d]", "");
                double giaBan = giaStr.isEmpty() ? 0 : Double.parseDouble(giaStr);

                String realIMEI = null;

                // Chọc thẳng DB lấy IMEI
                String sql = "SELECT MACTSP FROM ctsp WHERE MASP = ? AND (TINHTRANG = '1' OR TINHTRANG LIKE '%sẵn%' OR TINHTRANG LIKE '%mới%')";
                try (java.sql.Connection conn = DAL.DAO.DBConnection.getConnection();
                        java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {

                    ps.setString(1, maSP);
                    java.sql.ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        String imei = rs.getString("MACTSP");
                        if (imei == null)
                            continue; // Né Null

                        boolean daCo = false;
                        for (int i = 0; i < modelGioHang.getRowCount(); i++) {
                            Object imeiTrongGio = modelGioHang.getValueAt(i, 1);
                            if (imeiTrongGio != null && imeiTrongGio.toString().equals(imei)) {
                                daCo = true;
                                break;
                            }
                        }
                        if (!daCo) {
                            realIMEI = imei;
                            break;
                        }
                    }
                }

                if (realIMEI != null) {
                    modelGioHang.addRow(new Object[] { modelGioHang.getRowCount() + 1, realIMEI, tenSP, 12,
                            dcf.format(giaBan), dcf.format(giaBan) });
                    capNhatTongTien();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Máy này đã bốc hết vô giỏ rồi (hoặc lỗi DB không lấy được IMEI)!");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi Thêm SP: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // XÓA 1 MÓN
        btnXoa.addActionListener(e -> {
            int row = tbGioHang.getSelectedRow();
            if (row != -1) {
                modelGioHang.removeRow(row);
                capNhatTongTien();
            } else {
                JOptionPane.showMessageDialog(this, "Chọn 1 dòng bên giỏ hàng để xóa!");
            }
        });

        // HỦY GIỎ
        btnHuy.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Hủy toàn bộ giỏ?", "Cảnh báo", JOptionPane.YES_NO_OPTION) == 0) {
                modelGioHang.setRowCount(0);
                capNhatTongTien();
            }
        });

        // XUẤT HÓA ĐƠN PDF
        btnXuatHoaDon.addActionListener(e -> {
            if (modelGioHang.getRowCount() == 0 || txtTenKhach.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Giỏ hàng trống hoặc chưa có khách, lấy gì in hả ông!");
                return;
            }
            try {
                // Tạo một tờ giấy ảo trong code
                JTextArea toGiayAo = new JTextArea();
                toGiayAo.setFont(new Font("Monospaced", Font.PLAIN, 12));

                toGiayAo.append("========================================================\n");
                toGiayAo.append("                 CỬA HÀNG ĐIỆN THOẠI                    \n");
                toGiayAo.append("                  HÓA ĐƠN BÁN HÀNG                      \n");
                toGiayAo.append("========================================================\n");
                toGiayAo.append(" Khách hàng: " + txtTenKhach.getText() + "\n");
                toGiayAo.append(" Số ĐT     : " + txtSdtKhach.getText() + "\n");
                toGiayAo.append(" Nhân viên : " + cbxNhanVien.getSelectedItem().toString() + "\n");
                toGiayAo.append(" Ngày in   : " + java.time.LocalDateTime.now() + "\n");
                toGiayAo.append("--------------------------------------------------------\n");
                toGiayAo.append(String.format(" %-20s | %-15s | %s\n", "TÊN SẢN PHẨM", "MÃ IMEI", "GIÁ BÁN"));
                toGiayAo.append("--------------------------------------------------------\n");

                // Quét giỏ hàng chép vào tờ giấy
                for (int i = 0; i < modelGioHang.getRowCount(); i++) {
                    String ten = modelGioHang.getValueAt(i, 2).toString();
                    if (ten.length() > 20)
                        ten = ten.substring(0, 17) + "..."; // Cắt bớt tên nếu quá dài
                    String imei = modelGioHang.getValueAt(i, 1).toString();
                    String gia = modelGioHang.getValueAt(i, 5).toString();
                    toGiayAo.append(String.format(" %-20s | %-15s | %s\n", ten, imei, gia));
                }

                toGiayAo.append("--------------------------------------------------------\n");
                toGiayAo.append(" TỔNG TIỀN THANH TOÁN: " + lblTongTien.getText() + "\n");
                toGiayAo.append("========================================================\n");
                toGiayAo.append("             CẢM ƠN QUÝ KHÁCH VÀ HẸN GẶP LẠI!           \n");

                // GỌI LỆNH IN CỦA JAVA
                JOptionPane.showMessageDialog(this,
                        "Hệ thống chuẩn bị mở bảng In.\n💡 MẸO CHO ÔNG:\nỞ mục 'Printer', hãy chọn 'Microsoft Print to PDF' để lưu thành file PDF nha!");

                // Hiển thị hộp thoại in của Windows
                boolean inThanhCong = toGiayAo.print();

                if (inThanhCong) {
                    JOptionPane.showMessageDialog(this, "✅ Đã lưu file PDF thành công!");
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xuất PDF: " + ex.getMessage());
            }
        });
        // XÁC NHẬN CHỐT ĐƠN (LƯU DB & TRỪ KHO)
        btnXacNhan.addActionListener(e -> {
            if (modelGioHang.getRowCount() == 0 || txtTenKhach.getText().isEmpty() || txtKhachDua.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nhập đủ thông tin (Giỏ hàng, Khách, Tiền đưa) để chốt đơn!");
                return;
            }
            try {
                double tongTien = Double.parseDouble(lblTongTien.getText().replace(" VNĐ", "").replace(",", ""));
                double khachDua = Double.parseDouble(txtKhachDua.getText().replace(",", ""));

                if (khachDua < tongTien) {
                    JOptionPane.showMessageDialog(this, "Khách đưa thiếu tiền!");
                    return;
                }

                String sdt = txtSdtKhach.getText().trim();
                String maHD = "HD" + System.currentTimeMillis();
                java.sql.Date ngayLap = new java.sql.Date(System.currentTimeMillis());

                try (java.sql.Connection conn = DAL.DAO.DBConnection.getConnection()) {
                    String maKH = "";
                    // Tìm Mã KH
                    try (java.sql.PreparedStatement psKH = conn
                            .prepareStatement("SELECT MAKH FROM khachhang WHERE SDT = ?")) {
                        psKH.setString(1, sdt);
                        java.sql.ResultSet rsKH = psKH.executeQuery();
                        if (rsKH.next())
                            maKH = rsKH.getString("MAKH");
                    }
                    if (maKH.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Lỗi: Không tìm thấy Mã KH trong CSDL!");
                        return;
                    }

                    // Lưu Hóa Đơn
                    String sqlInsertHD = "INSERT INTO hoadon (MAHD, MAKH, MANV, NGAYLAP, TONGTIEN) VALUES (?, ?, ?, ?, ?)";
                    try (java.sql.PreparedStatement psHD = conn.prepareStatement(sqlInsertHD)) {
                        psHD.setString(1, maHD);
                        psHD.setString(2, maKH);
                        psHD.setString(3, "NV01"); // Tạm mặc định NV01
                        psHD.setDate(4, ngayLap);
                        psHD.setDouble(5, tongTien);
                        psHD.executeUpdate();
                    }

                    // Cập nhật lại kho (Trừ máy)
                    String sqlUpdateKho = "UPDATE ctsp SET TINHTRANG = 'Đã bán' WHERE MACTSP = ?";
                    try (java.sql.PreparedStatement psCTSP = conn.prepareStatement(sqlUpdateKho)) {
                        for (int i = 0; i < modelGioHang.getRowCount(); i++) {
                            psCTSP.setString(1, modelGioHang.getValueAt(i, 1).toString());
                            psCTSP.executeUpdate();
                        }
                    }
                }

                lblTienThua.setText(dcf.format(khachDua - tongTien) + " VNĐ");
                JOptionPane.showMessageDialog(this, "🎉 CHỐT ĐƠN THÀNH CÔNG! Đã lưu Hóa đơn & Trừ kho!");

                // Clear giao diện sau khi chốt
                modelGioHang.setRowCount(0);
                capNhatTongTien();
                txtKhachDua.setText("");
                txtSdtKhach.setText("");
                txtTenKhach.setText("");
                loadDataKhoSanPham(new SanPhamBUS().getAll());

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi Lưu Đơn: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // XEM LỊCH SỬ HÓA ĐƠN
        btnXemLichSu.addActionListener(e -> {
            JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this), "Lịch Sử Giao Dịch",
                    Dialog.ModalityType.APPLICATION_MODAL);
            dialog.setSize(800, 450);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());

            String[] cols = { "Mã Hóa Đơn", "Mã Khách", "Mã NV", "Ngày Lập", "Tổng Tiền" };
            DefaultTableModel modelLichSu = new DefaultTableModel(cols, 0) {
                @Override
                public boolean isCellEditable(int r, int c) {
                    return false;
                }
            };
            JTable tbLichSu = new JTable(modelLichSu);
            tbLichSu.setRowHeight(30);

            try (java.sql.Connection conn = DAL.DAO.DBConnection.getConnection();
                    java.sql.PreparedStatement ps = conn.prepareStatement("SELECT * FROM hoadon ORDER BY MAHD DESC");
                    java.sql.ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    modelLichSu.addRow(new Object[] {
                            rs.getString("MAHD"),
                            rs.getString("MAKH"),
                            rs.getString("MANV"),
                            rs.getString("NGAYLAP"),
                            dcf.format(rs.getDouble("TONGTIEN")) + " VNĐ"
                    });
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi xem lịch sử: " + ex.getMessage());
            }

            dialog.add(new JScrollPane(tbLichSu), BorderLayout.CENTER);
            dialog.setVisible(true);
        });
    }

    private void capNhatTongTien() {
        double tong = 0;
        for (int i = 0; i < modelGioHang.getRowCount(); i++) {
            tong += Double.parseDouble(modelGioHang.getValueAt(i, 5).toString().replace(",", ""));
        }
        lblTongTien.setText(dcf.format(tong) + " VNĐ");
    }

}