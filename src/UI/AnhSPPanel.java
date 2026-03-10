package UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
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
import javax.swing.table.DefaultTableModel;

import BUS.SanPhamBUS;
import BUS.anhspBUS;
import DTO.anhspDTO;

public class AnhSPPanel extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtMaAnh, txtUrl;
    private JComboBox<String> cbMaSP;
    private JLabel lblPreview;
    
    private anhspBUS imgBus = new anhspBUS();
    private SanPhamBUS spBus = new SanPhamBUS();

    public AnhSPPanel() {
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.decode("#F8FAFF"));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel("Quản lý Hình ảnh Sản phẩm");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        add(lblTitle, BorderLayout.NORTH);

        JPanel pnlContent = new JPanel(new BorderLayout(20, 0));
        pnlContent.setOpaque(false);
        pnlContent.add(taoFormBenTrai(), BorderLayout.WEST);
        pnlContent.add(taoBangBenPhai(), BorderLayout.CENTER);
        
        add(pnlContent, BorderLayout.CENTER);
        taiDuLieuLenBang();
    }

    // Đổi tên từ refreshSPCombo
    public void lamMoiComboSP() {
        String current = (String) cbMaSP.getSelectedItem();
        cbMaSP.removeAllItems();
        spBus.getAll().forEach(sp -> cbMaSP.addItem(sp.getMaSP()));
        
        if(!SanPhamPanel.selectedMaSP_Global.isEmpty()) {
            cbMaSP.setSelectedItem(SanPhamPanel.selectedMaSP_Global);
        } else if(current != null) {
            cbMaSP.setSelectedItem(current);
        }
    }
    private JPanel taoFormBenTrai() {
        JPanel pnl = new JPanel(); pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setBackground(Color.WHITE); pnl.setPreferredSize(new Dimension(320, 0));
        pnl.setBorder(new CompoundBorder(new LineBorder(Color.decode("#E2E8F0")), new EmptyBorder(20, 20, 20, 20)));

        lblPreview = new JLabel("Chưa có ảnh", SwingConstants.CENTER);
        lblPreview.setPreferredSize(new Dimension(280, 200));
        lblPreview.setMaximumSize(new Dimension(280, 200));
        lblPreview.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        lblPreview.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtMaAnh = new JTextField(); txtUrl = new JTextField();
        cbMaSP = new JComboBox<>();
        
        JButton btnBrowse = new JButton("Chọn ảnh từ máy...");
        btnBrowse.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBrowse.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if(fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                txtUrl.setText(fc.getSelectedFile().getAbsolutePath());
                capNhatXemTruoc(txtUrl.getText());
            }
        });

        JButton btnThem = taoNut("Thêm", "#10B981");
        JButton btnSua = taoNut("Sửa", "#F59E0B");
        JButton btnXoa = taoNut("Xóa", "#EF4444");
        JButton btnLuu = taoNut("Lưu DB", "#0F172A");
        JButton btnReload = taoNut("Làm mới", "#64748B");

        btnThem.addActionListener(e -> {
            if(imgBus.add(new anhspDTO(txtMaAnh.getText(), cbMaSP.getSelectedItem().toString(), txtUrl.getText(), true))) {
                taiDuLieuLenBang(); 
            } else JOptionPane.showMessageDialog(this, "Mã ảnh đã tồn tại!");
        });

        btnSua.addActionListener(e -> {
            if(imgBus.update(new anhspDTO(txtMaAnh.getText(), cbMaSP.getSelectedItem().toString(), txtUrl.getText(), true))) {
                taiDuLieuLenBang(); 
            }
        });

        btnXoa.addActionListener(e -> { 
            if(imgBus.delete(txtMaAnh.getText())) {
                taiDuLieuLenBang(); 
            }
        });
        
        btnLuu.addActionListener(e -> { 
            if(imgBus.saveToDatabase()) JOptionPane.showMessageDialog(this, "Đã lưu Ảnh vào Database!"); 
        });
        
        btnReload.addActionListener(e -> { 
            imgBus.reload();
            spBus.reload();
            lamMoiComboSP();
            taiDuLieuLenBang(); 
        });

        JPanel pnlInput = new JPanel(new GridLayout(4, 2, 5, 10));
        pnlInput.setBackground(Color.WHITE);
        pnlInput.add(new JLabel("Mã Ảnh:")); pnlInput.add(txtMaAnh);
        pnlInput.add(new JLabel("Sản phẩm:")); pnlInput.add(cbMaSP);
        pnlInput.add(new JLabel("Đường dẫn:")); pnlInput.add(txtUrl);
        pnlInput.add(new JLabel("")); pnlInput.add(btnBrowse);

        pnl.add(lblPreview); pnl.add(Box.createVerticalStrut(15));
        pnl.add(pnlInput); 
        pnl.add(Box.createVerticalStrut(15));
        
        JPanel pnlBtns = new JPanel(new GridLayout(3, 2, 5, 5));
        pnlBtns.setBackground(Color.WHITE);
        pnlBtns.add(btnThem); pnlBtns.add(btnSua); pnlBtns.add(btnXoa); pnlBtns.add(btnReload);
        pnl.add(pnlBtns);
        pnl.add(Box.createVerticalStrut(5));
        pnl.add(btnLuu); btnLuu.setMaximumSize(new Dimension(300, 40));

        lamMoiComboSP();
        return pnl;
    }
    private JPanel taoBangBenPhai() {
        model = new DefaultTableModel(new String[]{"Hình Ảnh", "Mã Ảnh", "Mã SP", "Đường dẫn"}, 0){
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? ImageIcon.class : Object.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        table.setRowHeight(70);
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = table.getSelectedRow();
                txtMaAnh.setText(model.getValueAt(r, 1).toString());
                cbMaSP.setSelectedItem(model.getValueAt(r, 2).toString());
                txtUrl.setText(model.getValueAt(r, 3).toString());
                capNhatXemTruoc(txtUrl.getText());
            }
        });
        return new JPanel(new BorderLayout()) {{ add(new JScrollPane(table)); }};
    }

    private void capNhatXemTruoc(String path) {
        try {
            ImageIcon icon = new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(280, 200, Image.SCALE_SMOOTH));
            lblPreview.setIcon(icon); lblPreview.setText("");
        } catch (Exception e) { lblPreview.setIcon(null); lblPreview.setText("Ảnh không tồn tại"); }
    }
    private void taiDuLieuLenBang() {
        model.setRowCount(0);
        for(anhspDTO a : imgBus.getAll()) {
            ImageIcon icon = null;
            try {
                icon = new ImageIcon(new ImageIcon(a.getUrl()).getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH));
            } catch (Exception e) { }
            model.addRow(new Object[]{icon, a.getMaAnh(), a.getMaSP(), a.getUrl()});
        }
    }
    private JButton taoNut(String t, String c) {
        JButton b = new JButton(t);
        b.setBackground(Color.decode(c)); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return b;
    }
}