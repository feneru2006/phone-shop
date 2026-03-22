package UI;

import BUS.NCCBUS;
import BUS.PhieuNhapBUS;
import BUS.SanPhamBUS;
import DTO.CTphieunhapDTO;
import DTO.SanPhamDTO;
import DTO.phieunhapDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PNDetailDialog extends JDialog {

    private SanPhamBUS spBus = new SanPhamBUS(); 

    public PNDetailDialog(String maPNH, PhieuNhapBUS pnBus, NCCBUS nccBus, String tenNhanVien) {
        setTitle("CHI TIẾT PHIẾU NHẬP");
        setSize(850, 550); 
        setLocationRelativeTo(null);
        setModal(true);

        phieunhapDTO pn = pnBus.timPhieuNhapTheoId(maPNH);
        List<CTphieunhapDTO> chiTietList = pnBus.xemChiTietPhieuNhap(maPNH);

        JPanel main = new JPanel(new BorderLayout(10, 20));
        main.setBackground(Color.WHITE);
        main.setBorder(new EmptyBorder(20, 25, 20, 25));

        JPanel infoPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#E2E8F0")));
        
        infoPanel.add(createInfoBlock("MÃ SỐ", pn.getMaPNH()));
        infoPanel.add(createInfoBlock("ĐỐI TÁC (NCC)", nccBus.layTenNccTheoMa(pn.getMaNCC())));
        infoPanel.add(createInfoBlock("NHÂN VIÊN LẬP", tenNhanVien)); 
        main.add(infoPanel, BorderLayout.NORTH);

        String[] cols = {"MÃ SP", "TÊN SẢN PHẨM", "SỐ LƯỢNG", "ĐƠN GIÁ NHẬP", "THÀNH TIỀN"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        
        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(Color.decode("#F8FAFC"));
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        table.setShowVerticalLines(true);
        table.setShowHorizontalLines(true);
        table.setGridColor(Color.decode("#E2E8F0"));
        
        DefaultTableCellRenderer rightRender = new DefaultTableCellRenderer();
        rightRender.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(2).setCellRenderer(rightRender);
        table.getColumnModel().getColumn(3).setCellRenderer(rightRender);
        table.getColumnModel().getColumn(4).setCellRenderer(rightRender);

        for (CTphieunhapDTO ct : chiTietList) {
            SanPhamDTO sp = spBus.getById(ct.getMaSP());
            String tenSP = (sp != null) ? sp.getTenSP() : "Sản phẩm không tồn tại";

            model.addRow(new Object[]{
                ct.getMaSP(), 
                tenSP, 
                ct.getSl(), 
                String.format("%,.0f đ", ct.getDonGia()), 
                String.format("%,.0f đ", ct.getThanhTien())
            });
        }
        
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(250);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createLineBorder(Color.decode("#E2E8F0")));
        main.add(scroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(Color.WHITE);
        JLabel lblTotal = new JLabel(String.format("TỔNG CỘNG: %,.0f VNĐ", pn.getTongTien()));
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTotal.setForeground(Color.decode("#DC2626")); // Màu đỏ
        bottom.add(lblTotal);
        
        main.add(bottom, BorderLayout.SOUTH);
        setContentPane(main);
    }

    private JPanel createInfoBlock(String title, String value) {
        JPanel p = new JPanel(new GridLayout(2, 1, 0, 5));
        p.setBackground(Color.WHITE);
        p.setBorder(new EmptyBorder(0, 0, 15, 0)); 

        JLabel lblT = new JLabel(title);
        lblT.setForeground(Color.decode("#64748B"));
        lblT.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JLabel lblV = new JLabel(value);
        lblV.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblV.setForeground(Color.decode("#1E293B")); 

        p.add(lblT); 
        p.add(lblV);
        return p;
    }
}
