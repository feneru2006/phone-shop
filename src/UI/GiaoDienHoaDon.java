package UI;

import BUS.ReportService;
import DTO.CTHDDTO;
import DTO.hoadonDTO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class GiaoDienHoaDon extends JDialog {

    private hoadonDTO hoaDon;
    private ArrayList<CTHDDTO> dsChiTiet;
    private String tenKhachHang;
    private String tenNhanVien;
    private ReportService reportService = new ReportService();

    // Constructor nhận dữ liệu từ form Bán Hàng truyền sang
    public GiaoDienHoaDon(Frame parent, boolean modal, hoadonDTO hd, ArrayList<CTHDDTO> dsCTHD, String tenKH, String tenNV) {
        super(parent, modal);
        this.hoaDon = hd;
        this.dsChiTiet = dsCTHD;
        this.tenKhachHang = tenKH;
        this.tenNhanVien = tenNV;
        
        initComponents();
        loadDataLenGiaoDien();
        setLocationRelativeTo(parent); // Hiển thị ở giữa màn hình
    }

    private void initComponents() {
        setTitle("Hóa Đơn Mua Hàng - Phone Shop");
        setSize(450, 600);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);

        // 1. PHẦN ĐẦU: THÔNG TIN CỬA HÀNG & HÓA ĐƠ
        JPanel pnlHeader = new JPanel();
        pnlHeader.setLayout(new BoxLayout(pnlHeader, BoxLayout.Y_AXIS));
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel lblTitle = new JLabel("PHONE SHOP");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSubTitle = new JLabel("HÓA ĐƠN THANH TOÁN");
        lblSubTitle.setFont(new Font("Arial", Font.BOLD, 16));
        lblSubTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlHeader.add(lblTitle);
        pnlHeader.add(Box.createVerticalStrut(10));
        pnlHeader.add(lblSubTitle);
        pnlHeader.add(Box.createVerticalStrut(15));

        // Panel chứa thông tin chữ
        JPanel pnlInfo = new JPanel(new GridLayout(4, 1, 5, 5));
        pnlInfo.setBackground(Color.WHITE);
        pnlInfo.add(new JLabel("Mã Hóa Đơn: " + hoaDon.getMaHD()));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        pnlInfo.add(new JLabel("Ngày lập: " + hoaDon.getNgayLap().format(formatter)));
        pnlInfo.add(new JLabel("Thu ngân: " + tenNhanVien));
        pnlInfo.add(new JLabel("Khách hàng: " + tenKhachHang));

        pnlHeader.add(pnlInfo);
        add(pnlHeader, BorderLayout.NORTH);

        // 2. PHẦN GIỮA: BẢNG CHI TIẾT SẢN PHẨM (IMEI)
        String[] columnNames = {"STT", "Mã Máy (IMEI)", "Đơn Giá", "Thành Tiền"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);
        
        // Đổ dữ liệu vào bảng
        int stt = 1;
        for (CTHDDTO ct : dsChiTiet) {
            model.addRow(new Object[]{
                stt++, 
                ct.getMaCTSP(), // Mã IMEI
                String.format("%,.0f", ct.getDonGia()), 
                String.format("%,.0f", ct.getThanhTien())
            });
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        // 3. PHẦN CUỐI: TỔNG TIỀN & NÚT XUẤT PDF
        JPanel pnlFooter = new JPanel(new BorderLayout());
        pnlFooter.setBackground(Color.WHITE);
        pnlFooter.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        JLabel lblTongTien = new JLabel("TỔNG CỘNG: " + String.format("%,.0f VNĐ", hoaDon.getTongTien()));
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 16));
        lblTongTien.setForeground(Color.RED);
        lblTongTien.setHorizontalAlignment(SwingConstants.RIGHT);
        pnlFooter.add(lblTongTien, BorderLayout.NORTH);

        // Các nút bấm
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        pnlButtons.setBackground(Color.WHITE);
        
        JButton btnXuatPDF = new JButton("Xuất Hóa Đơn PDF");
        btnXuatPDF.setBackground(new Color(40, 167, 69)); 
        btnXuatPDF.setForeground(Color.WHITE);
        btnXuatPDF.setFont(new Font("Arial", Font.BOLD, 14));
        
        JButton btnDong = new JButton("Đóng");
        btnDong.setFont(new Font("Arial", Font.PLAIN, 14));

    // xuất PDF
        btnXuatPDF.addActionListener(e -> {
            reportService.xuatHoaDonPDF(hoaDon, dsChiTiet, tenKhachHang, tenNhanVien);
            JOptionPane.showMessageDialog(this, "Đã xuất PDF và mở file thành công!");
        });

        // Sự kiện đóng
        btnDong.addActionListener(e -> dispose()); // Tắt popup

        pnlButtons.add(btnXuatPDF);
        pnlButtons.add(btnDong);
        pnlFooter.add(pnlButtons, BorderLayout.SOUTH);

        add(pnlFooter, BorderLayout.SOUTH);
    }
    // ==========================================
    // HÀM MAIN ĐỂ TEST KHUNG GIAO DIỆN TRỐNG
    // ==========================================
    public static void main(String args[]) {
        // Chỉnh giao diện cho mượt
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) { 
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) { }

        // 1. Khởi tạo đối tượng rỗng (Để tránh lỗi sập phần mềm)
        hoadonDTO hdRong = new hoadonDTO();
        hdRong.setMaHD("..."); // Mã rỗng
        hdRong.setNgayLap(java.time.LocalDateTime.now());
        hdRong.setTongTien(0); // Tiền 0đ

        // 2. Bảng sản phẩm rỗng không có gì cả
        ArrayList<CTHDDTO> dsRong = new ArrayList<>(); 

        // 3. Hiện form
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                // Chỗ này thêm chữ "dialog" vào để tạo biến nhé
                GiaoDienHoaDon dialog = new GiaoDienHoaDon(new javax.swing.JFrame(), true, hdRong, dsRong, "...", "...");
                
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                
                dialog.setVisible(true);
            }
        });
    }
    private void loadDataLenGiaoDien() {
        // Hàm phụ trợ nếu muốn update UI thêm sau này
    }
    
}