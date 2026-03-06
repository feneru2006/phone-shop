package UI.Panel.Sales;

import BUS.CTggBUS;
import BUS.SanPhamBUS;
import DTO.CTggDTO;
import DTO.SanPhamDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class KhuyenMaiDetailDialog extends JDialog {

    private JTable table;
    private DefaultTableModel model;

    private CTggBUS ctggBUS = new CTggBUS();
    private SanPhamBUS sanPhamBUS = new SanPhamBUS();

    private String maGG;

    public KhuyenMaiDetailDialog(Frame parent, String maGG) {
        super(parent, "Chi tiết khuyến mãi", true);
        this.maGG = maGG;

        initUI();
        loadData();

        setSize(700, 450);
        setLocationRelativeTo(parent);
    }

    private void initUI() {

        setLayout(new BorderLayout());
        setResizable(false);

        JLabel lblTitle = new JLabel("Chi tiết khuyến mãi: " + maGG);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(15,15,10,15));

        add(lblTitle, BorderLayout.NORTH);

        model = new DefaultTableModel(
                new String[]{"MÃ SP","TÊN SẢN PHẨM","% GIẢM"},0){

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));

        JScrollPane scroll = new JScrollPane(table);
        add(scroll, BorderLayout.CENTER);

        JButton btnAdd = new JButton("Thêm SP");
        JButton btnDelete = new JButton("Xóa SP");
        JButton btnClose = new JButton("Đóng");

        btnAdd.addActionListener(e -> addProduct());
        btnDelete.addActionListener(e -> deleteProduct());
        btnClose.addActionListener(e -> dispose());

        JPanel bottom = new JPanel();
        bottom.add(btnAdd);
        bottom.add(btnDelete);
        bottom.add(btnClose);

        add(bottom, BorderLayout.SOUTH);
    }

    private void loadData() {

        model.setRowCount(0);

        List<CTggDTO> list = ctggBUS.getByMaGG(maGG);

        for(CTggDTO ct : list){

            SanPhamDTO sp = sanPhamBUS.getById(ct.getMaSP());

            String tenSP = (sp != null) ? sp.getTenSP() : "Không tìm thấy";

            model.addRow(new Object[]{
                    ct.getMaSP(),
                    tenSP,
                    ct.getPhantramgg()
            });
        }
    }

    // ================= THÊM SẢN PHẨM =================
    private void addProduct(){

        JTextField txtMaSP = new JTextField();
        JTextField txtPercent = new JTextField();

        Object[] message = {
                "Mã sản phẩm:", txtMaSP,
                "Phần trăm giảm:", txtPercent
        };

        int option = JOptionPane.showConfirmDialog(
                this,
                message,
                "Thêm sản phẩm giảm giá",
                JOptionPane.OK_CANCEL_OPTION
        );

        if(option == JOptionPane.OK_OPTION){
            try {

                CTggDTO ct = new CTggDTO(
                        maGG,
                        txtMaSP.getText(),
                        Integer.parseInt(txtPercent.getText())
                );

                if(ctggBUS.add(ct)){
                    JOptionPane.showMessageDialog(this,"Thêm thành công!");
                    loadData();
                }else{
                    JOptionPane.showMessageDialog(this,"Thêm thất bại!");
                }

            }catch(Exception ex){
                JOptionPane.showMessageDialog(this,
                        "Dữ liệu không hợp lệ!");
            }
        }
    }

    // ================= XÓA SẢN PHẨM =================
    private void deleteProduct(){

        int selectedRow = table.getSelectedRow();

        if(selectedRow == -1){
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn sản phẩm để xóa!");
            return;
        }

        String maSP = model.getValueAt(selectedRow,0).toString();

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa sản phẩm " + maSP + " khỏi khuyến mãi?",
                "XÁC NHẬN XÓA",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if(confirm == JOptionPane.YES_OPTION){

            if(ctggBUS.delete(maGG, maSP)){
                JOptionPane.showMessageDialog(this,"Xóa thành công!");
                loadData();
            }else{
                JOptionPane.showMessageDialog(this,"Xóa thất bại!");
            }
        }
    }
}