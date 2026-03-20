package UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import BUS.HangSXBUS;
import BUS.SanPhamBUS;
import BUS.anhspBUS;
import BUS.loaiBUS;
import DTO.SanPhamDTO;
import DTO.anhspDTO;
import DTO.hangsxDTO;
import DTO.loaiDTO;

public class SanPhamPanel extends JPanel {
    public static String selectedMaSP_Global = ""; 

    private static final Color BG_APP = Color.decode("#F8FAFF");
    private static final Color SIDEBAR_BG = Color.decode("#FFFFFF");
    private static final Color BORDER_COLOR = Color.decode("#E2E8F0"); 
    private static final Color ACCENT_BLUE = Color.decode("#2563EB");
    private static final Color TEXT_DARK = Color.decode("#1E293B");

    private SanPhamBUS spBUS = new SanPhamBUS();
    private loaiBUS lBUS = new loaiBUS();
    private HangSXBUS hBUS = new HangSXBUS();
    private anhspBUS imgBUS = new anhspBUS();
    
    private JTable table;
    private DefaultTableModel model;
    
    private JTextField txtMaSP, txtTenSP, txtCauHinh, txtSLTon, txtGiaNhap, txtLoiNhuan, txtGiaBan, txtTimKiem;
    private JComboBox<String> cbTrangThai, cbMaLoai, cbNSX, cbTieuChi;
    private JLabel lblHinhAnh;
    private DecimalFormat df = new DecimalFormat("#,###");
    private Map<String, String> mapLoai = new HashMap<>();
    private Map<String, String> mapNSX = new HashMap<>();

    public SanPhamPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(BG_APP);
        setBorder(new EmptyBorder(20, 20, 20, 20)); 

        JLabel lblTitle = new JLabel("Quản lý Sản phẩm");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(TEXT_DARK);
        add(lblTitle, BorderLayout.NORTH);

        JPanel pnlMain = new JPanel(new BorderLayout(0, 20));
        pnlMain.setOpaque(false);
        pnlMain.add(taoPhanDau(), BorderLayout.NORTH);
        pnlMain.add(taoPhanDuoi(), BorderLayout.CENTER);

        add(pnlMain, BorderLayout.CENTER);
        taiDuLieuBang(spBUS.getAll());
        thietLapSuKienBang();
    }

    public void lamMoiDuLieu() {
        spBUS.reload(); lBUS.reload(); hBUS.reload(); imgBUS.reload(); 
        lamMoiCombobox(); 
        taiDuLieuBang(spBUS.getAll());
    }

    private JLabel taoNhan(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(Color.decode("#475569")); 
        return lbl;
    }

    private JPanel taoPhanDau() {
        JPanel pnlTop = new JPanel(new BorderLayout(25, 0));
        pnlTop.setOpaque(false);

        JPanel pnlAnh = new JPanel(new BorderLayout(0, 10));
        pnlAnh.setBackground(SIDEBAR_BG);
        pnlAnh.setPreferredSize(new Dimension(280, 240));
        pnlAnh.setBorder(new LineBorder(BORDER_COLOR, 2));
        
        lblHinhAnh = new JLabel("Chọn SP để xem ảnh", SwingConstants.CENTER);
        lblHinhAnh.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblHinhAnh.setForeground(Color.GRAY);
        pnlAnh.add(lblHinhAnh, BorderLayout.CENTER);

        JPanel pnlFields = new JPanel(new GridLayout(5, 4, 10, 10));
        pnlFields.setBackground(SIDEBAR_BG);
        pnlFields.setBorder(new CompoundBorder(
            new LineBorder(BORDER_COLOR, 1),
            new CompoundBorder(
                new TitledBorder(new EmptyBorder(0,0,0,0), " Thông số chi tiết ", TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12), ACCENT_BLUE),
                new EmptyBorder(15, 20, 15, 20)
            )
        ));

        txtMaSP = new JTextField(); txtTenSP = new JTextField();
        txtCauHinh = new JTextField();
        txtSLTon = new JTextField(); txtSLTon.setEditable(false); 
        
        txtGiaNhap = new JTextField("0"); 
        txtLoiNhuan = new JTextField("0");
        txtGiaBan = new JTextField("0");
        txtGiaBan.setEditable(false); 
        txtGiaBan.setForeground(Color.RED);
        txtGiaBan.setFont(new Font("Segoe UI", Font.BOLD, 13));

        DocumentListener calcListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { tinhGiaBanTudong(); }
            public void removeUpdate(DocumentEvent e) { tinhGiaBanTudong(); }
            public void changedUpdate(DocumentEvent e) { tinhGiaBanTudong(); }
        };
        txtGiaNhap.getDocument().addDocumentListener(calcListener);
        txtLoiNhuan.getDocument().addDocumentListener(calcListener);

        cbTrangThai = new JComboBox<>(new String[]{"Bán","Ngừng bán"});
        cbMaLoai = new JComboBox<>(); cbNSX = new JComboBox<>();
        lamMoiCombobox();

        // Row 1
        pnlFields.add(taoNhan("Mã SP:")); pnlFields.add(txtMaSP);
        pnlFields.add(taoNhan("Tên SP:")); pnlFields.add(txtTenSP);
        // Row 2
        pnlFields.add(taoNhan("Danh mục:")); pnlFields.add(cbMaLoai);
        pnlFields.add(taoNhan("Nhà SX:")); pnlFields.add(cbNSX);
        // Row 3
        pnlFields.add(taoNhan("Trạng thái:")); pnlFields.add(cbTrangThai);
        pnlFields.add(taoNhan("SL tồn:")); pnlFields.add(txtSLTon);
        // Row 4
        pnlFields.add(taoNhan("Cấu hình:")); pnlFields.add(txtCauHinh);
        pnlFields.add(taoNhan("Giá nhập (VNĐ):")); pnlFields.add(txtGiaNhap);
        // Row 5
        pnlFields.add(taoNhan("% Lợi nhuận:")); pnlFields.add(txtLoiNhuan);
        pnlFields.add(taoNhan("Giá bán (VNĐ):")); pnlFields.add(txtGiaBan);

        pnlTop.add(pnlAnh, BorderLayout.WEST);
        pnlTop.add(pnlFields, BorderLayout.CENTER);
        return pnlTop;
    }

    private void tinhGiaBanTudong() {
        try {
            double giaNhap = Double.parseDouble(txtGiaNhap.getText().replace(".", "").replace(",", "").trim());
            double phanTram = Double.parseDouble(txtLoiNhuan.getText().trim());
            double giaBan = giaNhap + (giaNhap * phanTram / 100);
            txtGiaBan.setText(df.format(giaBan) + " đ");
        } catch (Exception ex) {
            txtGiaBan.setText("0 đ");
        }
    }

    private JPanel taoPhanDuoi() {
        JPanel pnlBottom = new JPanel(new BorderLayout(0, 12));
        pnlBottom.setOpaque(false);

        JPanel pnlToolbar = new JPanel(new BorderLayout());
        pnlToolbar.setOpaque(false);

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlSearch.setOpaque(false);
        cbTieuChi = new JComboBox<>(new String[]{"Lựa chọn", "Tên SP", "Mã SP", "Cấu hình", "Hãng", "Loại"});
        txtTimKiem = new JTextField(15);
        txtTimKiem.setPreferredSize(new Dimension(0, 32));
        JButton btnTim = taoNutBam("Tìm kiếm", "#2563EB", Color.WHITE);
        pnlSearch.add(cbTieuChi); pnlSearch.add(txtTimKiem); pnlSearch.add(btnTim);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlBtns.setOpaque(false);
        JButton btnThem = taoNutBam("Thêm", "#10B981", Color.WHITE);
        JButton btnSua = taoNutBam("Sửa", "#F59E0B", Color.WHITE);
        JButton btnXoa = taoNutBam("Xóa", "#EF4444", Color.WHITE);
        JButton btnReset = taoNutBam("Làm mới", "#64748B", Color.WHITE);
        JButton btnLuuDB = taoNutBam("Lưu DB", "#0F172A", Color.WHITE);
        
        pnlBtns.add(btnThem); pnlBtns.add(btnSua); pnlBtns.add(btnXoa); pnlBtns.add(btnReset); pnlBtns.add(btnLuuDB);

        pnlToolbar.add(pnlSearch, BorderLayout.WEST);
        pnlToolbar.add(pnlBtns, BorderLayout.EAST);

        model = new DefaultTableModel(new String[]{"Hình ảnh", "Mã SP", "Tên SP", "Cấu hình", "Tồn", "Giá nhập", "% Lời", "Giá bán", "Trạng thái", "Loại", "Hãng"}, 0) {
            @Override
            public Class<?> getColumnClass(int col) { return col == 0 ? ImageIcon.class : Object.class; }
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        
        table = new JTable(model);
        table.setRowHeight(40); 
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setShowGrid(false); 
        table.getTableHeader().setPreferredSize(new Dimension(0, 38)); 
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                label.setBackground(Color.decode("#0F172A")); 
                label.setForeground(Color.WHITE);             
                label.setFont(new Font("Segoe UI", Font.BOLD, 12)); 
                label.setHorizontalAlignment(JLabel.CENTER);  
                label.setBorder(new MatteBorder(0, 0, 1, 1, Color.GRAY));
                return label;
            }
        });

        TableColumnModel tcm = table.getColumnModel();
        tcm.getColumn(0).setPreferredWidth(50);  // Ảnh
        tcm.getColumn(1).setPreferredWidth(60);  // Mã
        tcm.getColumn(2).setPreferredWidth(160); // Tên
        tcm.getColumn(3).setPreferredWidth(120); // Cấu hình
        tcm.getColumn(4).setPreferredWidth(40);  // Tồn
        tcm.getColumn(5).setPreferredWidth(90);  // Giá Nhập
        tcm.getColumn(6).setPreferredWidth(50);  // % Lời
        tcm.getColumn(7).setPreferredWidth(100); // Giá Bán
        tcm.getColumn(8).setPreferredWidth(70);  // Trạng thái
        tcm.getColumn(9).setPreferredWidth(80);  // Loại
        tcm.getColumn(10).setPreferredWidth(80); // Hãng

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for(int i = 1; i < table.getColumnCount(); i++) {
            if(i != 2 && i != 3) table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer); 
        }

        pnlBottom.add(pnlToolbar, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(new LineBorder(BORDER_COLOR));
        pnlBottom.add(scroll, BorderLayout.CENTER);

        btnThem.addActionListener(e -> xuLyHanhDong("THEM"));
        btnSua.addActionListener(e -> xuLyHanhDong("SUA"));
        btnXoa.addActionListener(e -> {
            if(txtMaSP.getText().isEmpty()) return;
            if(JOptionPane.showConfirmDialog(this, "Xóa Sản Phẩm này sẽ xóa luôn các Chi Tiết SP thuộc về nó. Tiếp tục?", "Cảnh báo", JOptionPane.YES_NO_OPTION) == 0) {
                if(spBUS.xoaSanPham(txtMaSP.getText())) { 
                    taiDuLieuBang(spBUS.getAll()); 
                    lamSachForm(); 
                }
            }
        });
        btnReset.addActionListener(e -> { lamMoiDuLieu(); lamSachForm(); });
        btnLuuDB.addActionListener(e -> { if(spBUS.saveToDatabase()) JOptionPane.showMessageDialog(this, "Đã cập nhật dữ liệu xuống Database!"); });
        
        btnTim.addActionListener(e -> {
            String tieuChi = cbTieuChi.getSelectedItem().toString();
            String keyword = txtTimKiem.getText().trim();
            taiDuLieuBang(spBUS.timKiem(tieuChi, keyword));
        });

        return pnlBottom;
    }

    private void xuLyHanhDong(String mode) {
        try {
            String config = txtCauHinh.getText().trim();
            double giaNhap = Double.parseDouble(txtGiaNhap.getText().replace(".", "").replace(" đ", "").trim());
            double phanTram = Double.parseDouble(txtLoiNhuan.getText().trim());
            int tonKho = txtSLTon.getText().isEmpty() ? 0 : Integer.parseInt(txtSLTon.getText().trim());

            SanPhamDTO sp = new SanPhamDTO(
                txtMaSP.getText().trim(), txtTenSP.getText().trim(), tonKho, giaNhap, 
                cbTrangThai.getSelectedItem().toString(), mapLoai.get(cbMaLoai.getSelectedItem().toString()), 
                config, mapNSX.get(cbNSX.getSelectedItem().toString()), false, phanTram
            );

            boolean res = mode.equals("THEM") ? spBUS.themSanPham(sp) : spBUS.suaSanPham(sp);
            if(res) {
                taiDuLieuBang(spBUS.getAll());
            } else {
                JOptionPane.showMessageDialog(this, "Thao tác thất bại! Kiểm tra lại mã trùng.");
            }
        } catch (Exception ex) { 
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng số cho Giá và Lợi Nhuận!"); 
        }
    }

    private void taiDuLieuBang(List<SanPhamDTO> list) {
        model.setRowCount(0);
        for (SanPhamDTO s : list) {
            ImageIcon icon = null;
            for(anhspDTO a : imgBUS.getAll()) {
                if(a.getMaSP().equals(s.getMaSP())) {
                    try { icon = new ImageIcon(new ImageIcon(a.getUrl()).getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH)); } 
                    catch(Exception e) { icon = null; }
                    break;
                }
            }
            String tenL = "", tenH = "";
            for(Map.Entry<String, String> entry : mapLoai.entrySet()) { if(entry.getValue().equals(s.getMaLoai())) tenL = entry.getKey(); }
            for(Map.Entry<String, String> entry : mapNSX.entrySet()) { if(entry.getValue().equals(s.getNsx())) tenH = entry.getKey(); }

            model.addRow(new Object[]{ 
                icon, s.getMaSP(), s.getTenSP(), s.getCauHinh(), s.getSlTon(), 
                df.format(s.getGia()) + " đ", 
                s.getPhanTramLoiNhuan() + "%", 
                df.format(s.getGiaBan()) + " đ",
                s.getTrangThai(), tenL, tenH 
            });
        }
    }

    private void thietLapSuKienBang() {
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int r = table.getSelectedRow();
                String msp = model.getValueAt(r, 1).toString();
                txtMaSP.setText(msp);
                txtTenSP.setText(model.getValueAt(r, 2).toString());
                txtCauHinh.setText(model.getValueAt(r, 3).toString()); 
                txtSLTon.setText(model.getValueAt(r, 4).toString());
                txtGiaNhap.setText(model.getValueAt(r, 5).toString().replaceAll("[^0-9]", ""));
                txtLoiNhuan.setText(model.getValueAt(r, 6).toString().replace("%", ""));
                
                cbTrangThai.setSelectedItem(model.getValueAt(r, 8).toString());
                cbMaLoai.setSelectedItem(model.getValueAt(r, 9).toString());
                cbNSX.setSelectedItem(model.getValueAt(r, 10).toString());
                
                for(anhspDTO a : imgBUS.getAll()) {
                    if(a.getMaSP().equals(msp)) { capNhatXemTruoc(a.getUrl()); return; }
                }
                lblHinhAnh.setIcon(null); lblHinhAnh.setText("Chưa có ảnh của SP này");
            }
        });
    }

    private void capNhatXemTruoc(String path) {
        try {
            ImageIcon icon = new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(260, 220, Image.SCALE_SMOOTH));
            lblHinhAnh.setIcon(icon); lblHinhAnh.setText("");
        } catch (Exception e) { lblHinhAnh.setIcon(null); lblHinhAnh.setText("Lỗi load ảnh"); }
    }

    private void lamMoiCombobox() {
        cbMaLoai.removeAllItems(); mapLoai.clear();
        for (loaiDTO l : lBUS.getAll()) { cbMaLoai.addItem(l.getDanhMuc()); mapLoai.put(l.getDanhMuc(), l.getMaLoai()); }
        cbNSX.removeAllItems(); mapNSX.clear();
        for (hangsxDTO h : hBUS.getAll()) { cbNSX.addItem(h.getTenTH()); mapNSX.put(h.getTenTH(), h.getMaNSX()); }
    }

    private void lamSachForm() { 
        txtMaSP.setText(""); txtTenSP.setText(""); txtCauHinh.setText(""); 
        txtSLTon.setText("0"); txtGiaNhap.setText("0"); txtLoiNhuan.setText("0"); txtGiaBan.setText("0 đ"); 
        lblHinhAnh.setIcon(null); lblHinhAnh.setText("Chọn SP để xem ảnh");
    }

    private JButton taoNutBam(String t, String bg, Color fg) {
        JButton b = new JButton(t); b.setBackground(Color.decode(bg)); b.setForeground(fg);
        b.setFont(new Font("Segoe UI", Font.BOLD, 12)); b.setFocusPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }
}