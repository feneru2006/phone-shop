package UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import BUS.HangSXBUS;
import DTO.hangsxDTO;

public class HangSXPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtMaNSX, txtTenNSX, txtTimKiem;
    private JComboBox<String> cbTieuChi;
    private HangSXBUS bus = new HangSXBUS();

    public HangSXPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.decode("#F8FAFF"));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Quản lý Hãng Sản Xuất");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        add(lblTitle, BorderLayout.NORTH);

        JPanel pnlMain = new JPanel(new BorderLayout(0, 20));
        pnlMain.setOpaque(false);
        pnlMain.add(taoPhanDau(), BorderLayout.NORTH);
        pnlMain.add(taoPhanDuoi(), BorderLayout.CENTER);

        add(pnlMain, BorderLayout.CENTER);
        taiDuLieuBang(bus.getAll());
    }

    private JLabel taoNhan(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(Color.BLACK);
        return lbl;
    }

    private JPanel taoPhanDau() {
        JPanel pnlFields = new JPanel(new GridLayout(1, 4, 15, 15));
        pnlFields.setBackground(Color.WHITE);
        pnlFields.setBorder(new CompoundBorder(
            BorderFactory.createTitledBorder(new LineBorder(Color.GRAY, 1), "Thông Tin Hãng"),
            new EmptyBorder(15, 20, 15, 20)
        ));

        txtMaNSX = new JTextField();
        txtTenNSX = new JTextField();

        pnlFields.add(taoNhan("Mã Hãng SX:")); pnlFields.add(txtMaNSX);
        pnlFields.add(taoNhan("Tên Thương Hiệu:")); pnlFields.add(txtTenNSX);

        return pnlFields;
    }

    private JPanel taoPhanDuoi() {
        JPanel pnlBottom = new JPanel(new BorderLayout(0, 10));
        pnlBottom.setOpaque(false);
        
        JPanel pnlToolbar = new JPanel(new BorderLayout());
        pnlToolbar.setOpaque(false);

        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        pnlSearch.setOpaque(false);
        cbTieuChi = new JComboBox<>(new String[]{"Tất cả", "Mã NSX", "Tên Thương Hiệu"});
        txtTimKiem = new JTextField(15);
        JButton btnTim = taoNutBam("Tìm kiếm", "#2563EB");
        pnlSearch.add(cbTieuChi); pnlSearch.add(txtTimKiem); pnlSearch.add(btnTim);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlBtns.setOpaque(false);
        JButton btnThem = taoNutBam("Thêm", "#10B981");
        JButton btnSua = taoNutBam("Sửa", "#F59E0B");
        JButton btnXoa = taoNutBam("Xóa", "#EF4444");
        JButton btnMoi = taoNutBam("Làm mới", "#64748B");
        JButton btnLuu = taoNutBam("Lưu DB", "#0F172A");
        pnlBtns.add(btnThem); pnlBtns.add(btnSua); pnlBtns.add(btnXoa); pnlBtns.add(btnMoi); pnlBtns.add(btnLuu);

        pnlToolbar.add(pnlSearch, BorderLayout.WEST);
        pnlToolbar.add(pnlBtns, BorderLayout.EAST);

        model = new DefaultTableModel(new String[]{"Mã Hãng SX", "Tên Thương Hiệu"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(35);
        
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        table.getTableHeader().setResizingAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(Color.decode("#0F172A"));
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 0, 1, Color.WHITE),
                    new EmptyBorder(5, 5, 5, 5)
                ));
                return label;
            }
        };
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        leftRenderer.setBorder(new EmptyBorder(0, 10, 0, 0));

        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); 
        table.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);  

        pnlBottom.add(pnlToolbar, BorderLayout.NORTH);
        pnlBottom.add(new JScrollPane(table), BorderLayout.CENTER);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = table.getSelectedRow();
                if(r != -1) {
                    txtMaNSX.setText(model.getValueAt(r, 0).toString());
                    txtTenNSX.setText(model.getValueAt(r, 1).toString());
                }
            }
        });

        btnThem.addActionListener(e -> {
            if(bus.them(new hangsxDTO(txtMaNSX.getText(), txtTenNSX.getText()))) {
                taiDuLieuBang(bus.getAll());
            } else JOptionPane.showMessageDialog(this, "Thêm thất bại (Trùng mã hoặc rỗng)!");
        });

        btnSua.addActionListener(e -> {
            if(bus.sua(new hangsxDTO(txtMaNSX.getText(), txtTenNSX.getText()))) {
                taiDuLieuBang(bus.getAll());
            } else JOptionPane.showMessageDialog(this, "Sửa thất bại!");
        });

        btnXoa.addActionListener(e -> {
            if(txtMaNSX.getText().isEmpty()) return;
            if(JOptionPane.showConfirmDialog(this, "Xóa NSX này?") == JOptionPane.YES_OPTION) {
                if(bus.xoa(txtMaNSX.getText())) {
                    taiDuLieuBang(bus.getAll());
                    txtMaNSX.setText(""); txtTenNSX.setText("");
                } else JOptionPane.showMessageDialog(this, "Xóa thất bại!");
            }
        });

        btnTim.addActionListener(e -> {
            taiDuLieuBang(bus.timKiem(cbTieuChi.getSelectedItem().toString(), txtTimKiem.getText()));
        });

        btnMoi.addActionListener(e -> { 
            bus.reload(); 
            taiDuLieuBang(bus.getAll());
            txtMaNSX.setText(""); txtTenNSX.setText(""); 
            txtTimKiem.setText("");
            cbTieuChi.setSelectedIndex(0);
        });

        btnLuu.addActionListener(e -> {
            if(bus.saveToDatabase()) JOptionPane.showMessageDialog(this, "Đã lưu thay đổi xuống Database!");
            else JOptionPane.showMessageDialog(this, "Lưu thất bại!");
        });

        return pnlBottom;
    }

    private void taiDuLieuBang(List<hangsxDTO> list) {
        model.setRowCount(0);
        for (hangsxDTO h : list) model.addRow(new Object[]{h.getMaNSX(), h.getTenTH()});
    }

    private JButton taoNutBam(String text, String bg) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.decode(bg));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setFocusPainted(false);
        return btn;
    }
}