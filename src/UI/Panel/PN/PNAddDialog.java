package UI.Panel.PN;

import BUS.CtspBUS;
import BUS.NCCBUS;
import BUS.PhieuNhapBUS;
import BUS.SanPhamBUS;
import DTO.CTphieunhapDTO;
import DTO.ChitietSPDTO;
import DTO.NCCDTO;
import DTO.SanPhamDTO;
import DTO.phieunhapDTO;
import DTO.nhanvienDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PNAddDialog extends JDialog {

    private PhieuNhapBUS pnBus;
    private NCCBUS nccBus;
    private SanPhamBUS spBus = new SanPhamBUS();
    private BUS.NhanVienBUS nvBus = new BUS.NhanVienBUS(); 
    private CtspBUS ctspBus = new CtspBUS(); 
    private List<String> perms;
    
    private List<CTphieunhapDTO> cartList = new ArrayList<>();
    private List<ChitietSPDTO> imeiListToSave = new ArrayList<>();
    
    private DefaultTableModel cartModel;
    private JComboBox<String> cbMaSP, cbNV, cbNCC;
    private JTextField txtTenSP, txtGiaNhap, txtImeiStart, txtSoLuong, txtMaPhieu;
    private JLabel lblTotal;
    private double tongTienPhieu = 0;
    
    private int currentMaxCTPN = 0;

    public PNAddDialog(JPanel parent, PhieuNhapBUS pnBus, NCCBUS nccBus, List<String> perms) {
        this.pnBus = pnBus;
        this.nccBus = nccBus;
        this.perms = perms;

        setTitle("TẠO PHIẾU NHẬP HÀNG MỚI");
        setSize(1150, 700);
        setLocationRelativeTo(parent);
        setModal(true);
        setLayout(new BorderLayout(10, 10)); 

        for(ChitietSPDTO ct : ctspBus.getListCtsp()) {
            if(ct.getMaCTPN() != null && ct.getMaCTPN().startsWith("CTPN")) {
                try {
                    int num = Integer.parseInt(ct.getMaCTPN().replace("CTPN", ""));
                    if (num > currentMaxCTPN) currentMaxCTPN = num;
                } catch(Exception ignored){}
            }
        }

        JPanel mainWrapper = new JPanel(new BorderLayout(15, 0));
        mainWrapper.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainWrapper.setBackground(Color.decode("#F8FAFF"));

        mainWrapper.add(buildLeftProducts(), BorderLayout.WEST);
        mainWrapper.add(buildCenterForm(), BorderLayout.CENTER);
        mainWrapper.add(buildRightInfo(), BorderLayout.EAST);
        
        setContentPane(mainWrapper);
        autoGenMaPhieu();
    }

    private void autoGenMaPhieu() {
        int max = 0;
        for (phieunhapDTO pn : pnBus.getListPhieuNhap()) {
            try {
                int num = Integer.parseInt(pn.getMaPNH().replace("PN", ""));
                if (num > max) max = num;
            } catch (Exception ignored) {}
        }
        txtMaPhieu.setText(String.format("PN%02d", max + 1));
    }

    private JPanel buildLeftProducts() {
        JPanel p = new JPanel(new BorderLayout());
        p.setPreferredSize(new Dimension(300, 0));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.decode("#94A3B8")), 
            " Danh sách Sản Phẩm kho ", TitledBorder.LEFT, TitledBorder.TOP, 
            new Font("Segoe UI", Font.BOLD, 13), Color.decode("#1E293B")
        ));
        
        DefaultTableModel m = new DefaultTableModel(new String[]{"Mã SP", "Tên SP"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; } 
        };
        for (SanPhamDTO sp : spBus.getAll()) m.addRow(new Object[]{sp.getMaSP(), sp.getTenSP()});
        
        JTable t = new JTable(m);
        t.setRowHeight(30);
        t.getTableHeader().setBackground(Color.decode("#F1F5F9"));
        t.setDefaultEditor(Object.class, null); 
        
        p.add(new JScrollPane(t), BorderLayout.CENTER);
        return p;
    }

    private JPanel buildCenterForm() {
        JPanel p = new JPanel(new BorderLayout(0, 15));
        p.setOpaque(false);

        JPanel formBox = new JPanel(new BorderLayout());
        formBox.setBackground(Color.WHITE);
        formBox.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.decode("#94A3B8")), 
            " Thông tin chi tiết sản phẩm nhập ", TitledBorder.LEFT, TitledBorder.TOP, 
            new Font("Segoe UI", Font.BOLD, 13), Color.decode("#1E293B")
        ));

        JPanel form = new JPanel(new GridLayout(3, 4, 15, 15));
        form.setBorder(new EmptyBorder(15, 15, 15, 15));
        form.setBackground(Color.WHITE);
        
        cbMaSP = new JComboBox<>();
        cbMaSP.addItem("-- Chọn SP --");
        for (SanPhamDTO sp : spBus.getAll()) cbMaSP.addItem(sp.getMaSP());
        
        txtTenSP = new JTextField(); txtTenSP.setEditable(false);
        txtGiaNhap = new JTextField(); txtGiaNhap.setEditable(false);
        txtImeiStart = new JTextField();
        txtSoLuong = new JTextField();

        cbMaSP.addItemListener(e -> {
            if (cbMaSP.getSelectedIndex() > 0) {
                SanPhamDTO sp = spBus.getById(cbMaSP.getSelectedItem().toString());
                if (sp != null) {
                    txtTenSP.setText(sp.getTenSP());
                    txtGiaNhap.setText(String.format("%.0f", sp.getGia())); 
                    
                    txtImeiStart.setEnabled(true);
                    txtImeiStart.setBackground(Color.WHITE);

                    long maxImei = 0;
                    for(ChitietSPDTO ct : ctspBus.getListCtsp()) {
                        if(ct.getMaCTSP().toUpperCase().startsWith("IMEI")) {
                            try {
                                long num = Long.parseLong(ct.getMaCTSP().substring(4));
                                if(num > maxImei) maxImei = num;
                            } catch (Exception ignored) {}
                        }
                    }
                    for(ChitietSPDTO ct : imeiListToSave) {
                        if(ct.getMaCTSP().toUpperCase().startsWith("IMEI")) {
                            try {
                                long num = Long.parseLong(ct.getMaCTSP().substring(4));
                                if(num > maxImei) maxImei = num;
                            } catch (Exception ignored) {}
                        }
                    }
                    txtImeiStart.setText("IMEI" + (maxImei + 1));
                }
            }
        });

        form.add(new JLabel("Mã Sản Phẩm:")); form.add(cbMaSP);
        form.add(new JLabel("Tên Sản Phẩm:")); form.add(txtTenSP);
        form.add(new JLabel("Giá nhập (Lấy từ DB):")); form.add(txtGiaNhap);
        form.add(new JLabel("Mã IMEI bắt đầu:")); form.add(txtImeiStart);
        form.add(new JLabel("Số lượng:")); form.add(txtSoLuong);

        JButton btnAdd = new JButton("THÊM VÀO PHIẾU");
        btnAdd.setBackground(Color.decode("#3B82F6"));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(e -> addToCart());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(btnAdd);

        formBox.add(form, BorderLayout.CENTER);
        formBox.add(btnPanel, BorderLayout.SOUTH);

        cartModel = new DefaultTableModel(new String[]{"STT", "Mã SP", "Tên SP", "Đơn giá", "Số lượng"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable cartTable = new JTable(cartModel);
        cartTable.setRowHeight(30);
        cartTable.getTableHeader().setBackground(Color.decode("#F1F5F9"));
        cartTable.setDefaultEditor(Object.class, null); 

        JPanel cartBox = new JPanel(new BorderLayout());
        cartBox.setBackground(Color.WHITE);
        cartBox.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.decode("#94A3B8")), 
            " Danh sách hàng hóa trong phiếu ", TitledBorder.LEFT, TitledBorder.TOP, 
            new Font("Segoe UI", Font.BOLD, 13), Color.decode("#1E293B")
        ));
        cartBox.add(new JScrollPane(cartTable), BorderLayout.CENTER);
        
        p.add(formBox, BorderLayout.NORTH);
        p.add(cartBox, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildRightInfo() {
        JPanel p = new JPanel(new BorderLayout());
        p.setPreferredSize(new Dimension(300, 0));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.decode("#94A3B8")), 
            " Thông tin phiếu nhập ", TitledBorder.LEFT, TitledBorder.TOP, 
            new Font("Segoe UI", Font.BOLD, 13), Color.decode("#1E293B")
        ));

        JPanel form = new JPanel(new GridLayout(6, 1, 0, 5));
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(15, 15, 15, 15));

        txtMaPhieu = new JTextField(); txtMaPhieu.setEditable(false);
        
        cbNV = new JComboBox<>(); 
        for (nhanvienDTO nv : nvBus.getAll()) {
            cbNV.addItem(nv.getMaNV() + " - " + nv.getHoTen());
        }

        cbNCC = new JComboBox<>();
        for (NCCDTO ncc : nccBus.getListNCC()) {
            if (ncc.getIsDeleted() == 0) {
                cbNCC.addItem(ncc.getMaNCC() + " - " + ncc.getTen());
            }
        }

        form.add(new JLabel("Mã phiếu nhập:")); form.add(txtMaPhieu);
        form.add(new JLabel("Nhân viên nhập:")); form.add(cbNV);
        form.add(new JLabel("Nhà cung cấp:")); form.add(cbNCC);

        JPanel bottomBox = new JPanel(new BorderLayout(0, 20));
        bottomBox.setBackground(Color.WHITE);
        bottomBox.setBorder(new EmptyBorder(15, 15, 15, 15));

        lblTotal = new JLabel("TỔNG TIỀN: 0 đ", SwingConstants.CENTER);
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotal.setForeground(Color.decode("#DC2626"));

        JPanel btns = new JPanel(new GridLayout(1, 2, 10, 0));
        btns.setOpaque(false);
        JButton btnCancel = new JButton("THOÁT");
        btnCancel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnCancel.addActionListener(e -> dispose());
        
        JButton btnSave = new JButton("NHẬP HÀNG");
        btnSave.setBackground(Color.decode("#10B981")); 
        btnSave.setForeground(Color.WHITE);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSave.addActionListener(e -> savePhieuNhap());

        btns.add(btnCancel); btns.add(btnSave);
        bottomBox.add(lblTotal, BorderLayout.CENTER);
        bottomBox.add(btns, BorderLayout.SOUTH);

        p.add(form, BorderLayout.NORTH);
        p.add(bottomBox, BorderLayout.SOUTH); 
        return p;
    }

    private void addToCart() {
        if (cbMaSP.getSelectedIndex() <= 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm!"); return;
        }

        try {
            String maSP = cbMaSP.getSelectedItem().toString();
            String tenSP = txtTenSP.getText();
            double gia = Double.parseDouble(txtGiaNhap.getText());
            int sl = Integer.parseInt(txtSoLuong.getText());

            if (sl <= 0) {
                JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0!"); return;
            }

            String imeiStr = txtImeiStart.getText().trim();
            if (!imeiStr.toUpperCase().startsWith("IMEI")) {
                JOptionPane.showMessageDialog(this, "Lỗi: Mã IMEI phải bắt đầu bằng chữ 'IMEI'!"); return;
            }
            long startImeiNum = 0;
            try { startImeiNum = Long.parseLong(imeiStr.substring(4)); } catch (Exception e) { return; }

            // Validate IMEI trùng
            for (int i = 0; i < sl; i++) {
                String checkImei = "IMEI" + (startImeiNum + i);
                boolean isClashDB = ctspBus.getListCtsp().stream().anyMatch(ct -> ct.getMaCTSP().equalsIgnoreCase(checkImei));
                boolean isClashCart = imeiListToSave.stream().anyMatch(ct -> ct.getMaCTSP().equalsIgnoreCase(checkImei));
                if (isClashDB || isClashCart) {
                    JOptionPane.showMessageDialog(this, "Phát hiện trùng lặp! Mã [" + checkImei + "] đã tồn tại.", "Lỗi Validate", JOptionPane.ERROR_MESSAGE);
                    return; 
                }
            }

            boolean foundCTPN = false;
            String assignedCTPN = "";
            for (CTphieunhapDTO ctpn : cartList) {
                if (ctpn.getMaSP().equals(maSP)) {
                    ctpn.setSl(ctpn.getSl() + sl);
                    ctpn.setThanhTien(ctpn.getThanhTien() + (gia * sl));
                    assignedCTPN = ctpn.getMaCTPN();
                    foundCTPN = true;
                    break;
                }
            }

            if (!foundCTPN) {
                currentMaxCTPN++;
                assignedCTPN = String.format("CTPN%02d", currentMaxCTPN);
                cartList.add(new CTphieunhapDTO(assignedCTPN, txtMaPhieu.getText(), maSP, sl, gia, gia * sl));
                cartModel.addRow(new Object[]{cartModel.getRowCount() + 1, maSP, tenSP, String.format("%,.0f", gia), sl});
            } else {
                for (int i = 0; i < cartModel.getRowCount(); i++) {
                    if (cartModel.getValueAt(i, 1).equals(maSP)) {
                        int oldSl = Integer.parseInt(cartModel.getValueAt(i, 4).toString());
                        cartModel.setValueAt(oldSl + sl, i, 4);
                        break;
                    }
                }
            }

            for (int i = 0; i < sl; i++) {
                ChitietSPDTO ctsp = new ChitietSPDTO();
                ctsp.setMaCTSP("IMEI" + (startImeiNum + i));
                ctsp.setMaSP(maSP);
                ctsp.setMaCTPN(assignedCTPN); 
                ctsp.setTinhtrang("Sẵn có");
                imeiListToSave.add(ctsp);
            }

            tongTienPhieu += (sl * gia);
            lblTotal.setText(String.format("TỔNG TIỀN: %,.0f đ", tongTienPhieu));
            cbMaSP.setSelectedIndex(0);
            txtImeiStart.setText(""); txtSoLuong.setText("");

        } catch (NumberFormatException ex) { 
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số!"); 
        }
    }

    private void savePhieuNhap() {
        if (cartList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chưa có sản phẩm nào trong phiếu!"); return;
        }
        String maNCC = cbNCC.getSelectedItem().toString().split(" - ")[0];
        String maNV = cbNV.getSelectedItem().toString().split(" - ")[0];

        phieunhapDTO pn = new phieunhapDTO(txtMaPhieu.getText(), maNV, LocalDateTime.now(), tongTienPhieu, maNCC);
        for (ChitietSPDTO imei : imeiListToSave) imei.setMaNCC(maNCC); 

        if (pnBus.nhapHangVaoKho(pn, cartList, imeiListToSave, perms)) {
            JOptionPane.showMessageDialog(this, "Nhập hàng thành công!");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu phiếu nhập!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}