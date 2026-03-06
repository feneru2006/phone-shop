package UI.Panel.Sales;

import DTO.giamgiaDTO;

import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class KhuyenMaiController {

    private KhuyenMaiView view;
    private KhuyenMaiService service;

    public KhuyenMaiController(KhuyenMaiView view) {
        this.view = view;
        this.service = new KhuyenMaiService();
    }

    public void loadData() {

        List<giamgiaDTO> list = service.getAll();
        view.preparePagination(list.size());

        view.clearTable();

        int start = view.getStartIndex();
        int end = view.getEndIndex(list.size());

        for (int i = start; i < end; i++) {
            giamgiaDTO gg = list.get(i);

            view.addRow(new Object[]{
                    gg.getMAGG(),
                    gg.getdotGG(),
                    gg.getBatdau(),
                    gg.getKetthuc(),
                    "ACTION"
            });
        }

        view.updatePaginationButtons();
    }

    public void delete(int row) {

        String ma = view.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(
                view,
                "Xóa đợt giảm giá này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            if (service.delete(ma)) {
                JOptionPane.showMessageDialog(view, "Xóa thành công!");
                loadData();
            }
        }
    }

    public void edit(int row) {

        String ma = view.getValueAt(row, 0);
        String ten = view.getValueAt(row, 1);
        String bd = view.getValueAt(row, 2);
        String kt = view.getValueAt(row, 3);

        JTextField txtTen = new JTextField(ten);
        JTextField txtBD = new JTextField(bd);
        JTextField txtKT = new JTextField(kt);

        Object[] message = {
                "Tên đợt:", txtTen,
                "Bắt đầu:", txtBD,
                "Kết thúc:", txtKT
        };

        int option = JOptionPane.showConfirmDialog(
                view,
                message,
                "Sửa giảm giá",
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                if (service.update(
                        ma,
                        txtTen.getText(),
                        LocalDateTime.parse(txtBD.getText()),
                        LocalDateTime.parse(txtKT.getText())
                )) {
                    JOptionPane.showMessageDialog(view, "Cập nhật thành công!");
                    loadData();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view, "Sai định dạng ngày!");
            }
        }
    }

    public void add() {

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

        JTextField txtMa = new JTextField();
        JTextField txtTen = new JTextField();
        JTextField txtBD = new JTextField(
                LocalDateTime.now().format(formatter));
        JTextField txtKT = new JTextField(
                LocalDateTime.now().plusDays(7).format(formatter));

        Object[] message = {
                "Mã GG:", txtMa,
                "Tên đợt:", txtTen,
                "Bắt đầu:", txtBD,
                "Kết thúc:", txtKT
        };

        int option = JOptionPane.showConfirmDialog(
                view,
                message,
                "Thêm giảm giá",
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            try {
                if (service.add(
                        txtMa.getText(),
                        txtTen.getText(),
                        LocalDateTime.parse(txtBD.getText()),
                        LocalDateTime.parse(txtKT.getText())
                )) {
                    JOptionPane.showMessageDialog(view, "Thêm thành công!");
                    loadData();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view, "Sai định dạng ngày!");
            }
        }
    }
}