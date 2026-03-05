package UI.Panel.Sales;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class KhuyenMaiActionRenderer extends JPanel implements TableCellRenderer {

    JButton btnEdit = new JButton("Sửa");
    JButton btnView = new JButton("Xem");
    JButton btnDelete = new JButton("Xóa");

    public KhuyenMaiActionRenderer(){

        setLayout(new GridBagLayout());
        setOpaque(true);

        Dimension size = new Dimension(75, 35);

        btnEdit.setPreferredSize(size);
        btnView.setPreferredSize(size);
        btnDelete.setPreferredSize(size);

        JPanel inner = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        inner.setOpaque(false);

        inner.add(btnEdit);
        inner.add(btnView);
        inner.add(btnDelete);

        add(inner);
    }

    public Component getTableCellRendererComponent(
            JTable table,Object value,
            boolean isSelected,boolean hasFocus,
            int row,int column){

        if(isSelected)
            setBackground(table.getSelectionBackground());
        else
            setBackground(Color.WHITE);

        return this;
    }
}