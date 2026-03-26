package UI.Panel.Sales;

import javax.swing.*;
import java.awt.*;

public class KhuyenMaiActionEditor extends DefaultCellEditor {
    Cursor hand = new Cursor(Cursor.HAND_CURSOR);
    JPanel panel = new JPanel(new GridBagLayout());
    JButton btnEdit = new JButton("Sửa");
    JButton btnView = new JButton("Xem");
    JButton btnDelete = new JButton("Xóa");

    int currentRow;
    KhuyenMaiPanel parent;

    public KhuyenMaiActionEditor(KhuyenMaiPanel parent, JCheckBox box) {
        super(box);
        this.parent = parent;

        Dimension size = new Dimension(75, 35);

        btnEdit.setPreferredSize(size);
        btnView.setPreferredSize(size);
        btnDelete.setPreferredSize(size);
        btnEdit.setCursor(hand);
        btnView.setCursor(hand);
        btnDelete.setCursor(hand);

        JPanel inner = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        inner.setOpaque(false);

        inner.add(btnEdit);
        inner.add(btnView);
        inner.add(btnDelete);

        panel.add(inner);

        btnEdit.addActionListener(e -> {
            fireEditingStopped();
            parent.editRow(currentRow);
        });

        btnView.addActionListener(e -> {
            fireEditingStopped();
            parent.viewDetail(currentRow);
        });

        btnDelete.addActionListener(e -> {
            fireEditingStopped();
            parent.deleteRow(currentRow);
        });
    }

    public Component getTableCellEditorComponent(
            JTable table, Object value,
            boolean isSelected, int row, int column) {

        currentRow = row;
        return panel;
    }

    public Object getCellEditorValue() {
        return "ACTION";
    }
}