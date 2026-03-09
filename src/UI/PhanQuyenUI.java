package UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class PhanQuyenUI extends JPanel {

    public PhanQuyenUI() {
        setLayout(new BorderLayout(0, 20));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initTopPanel();
        initTripleTableMatrix();
    }

    private void initTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Ma trận phân quyền");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));

        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnGroup.setOpaque(false);
        btnGroup.add(createStyledButton("Thêm nhóm quyền", "#10B981"));
        btnGroup.add(createStyledButton("Tạo nhóm quyền mới", "#3B82F6"));

        topPanel.add(lblTitle, BorderLayout.WEST);
        topPanel.add(btnGroup, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);
    }

    private void initTripleTableMatrix() {
        // 1. Định nghĩa dữ liệu
        String[] allCols = {"Tên nhóm quyền", "CN01", "CN02", "CN03", "CN04", "CN05", "CN06", "CN07", "Thao tác"};
        Object[][] data = {
                {"<html><b>Administrator</b><br><font color='gray'>Q01</font></html>", true, true, true, true, true, true, true, ""},
                {"<html><b>Sales Manager</b><br><font color='gray'>Q02</font></html>", false, true, true, false, false, true, false, ""},
                {"<html><b>Inventory Specialist</b><br><font color='gray'>Q03</font></html>", true, false, false, false, false, false, true, ""},
                {"<html><b>Accountant</b><br><font color='gray'>Q04</font></html>", false, false, true, false, false, true, false, ""},
                {"<html><b>Basic Staff</b><br><font color='gray'>Q05</font></html>", false, true, false, false, false, false, false, ""}
        };

        // 2. Tạo Model dùng chung
        DefaultTableModel model = new DefaultTableModel(data, allCols) {
            @Override
            public Class<?> getColumnClass(int col) {
                if (col >= 1 && col <= 7) return Boolean.class;
                return super.getColumnClass(col);
            }
        };

        // 3. Tạo 3 bảng: Trái (Cố định), Giữa (Cuộn), Phải (Cố định)
        JTable leftTable = createTable(model, new int[]{0});
        JTable centerTable = createTable(model, new int[]{1, 2, 3, 4, 5, 6, 7});
        JTable rightTable = createTable(model, new int[]{8});

        // Đặt độ rộng cho cột cố định
        leftTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        rightTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        rightTable.getColumnModel().getColumn(0).setCellRenderer(new ActionButtonsRenderer());

        // Đồng bộ hóa việc chọn dòng giữa 3 bảng
        ListSelectionModel selectionModel = leftTable.getSelectionModel();
        centerTable.setSelectionModel(selectionModel);
        rightTable.setSelectionModel(selectionModel);

        // 4. Sắp xếp vào Layout
        // Bảng giữa bọc trong ScrollPane để có overflow
        centerTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for(int i=0; i<centerTable.getColumnCount(); i++) {
            centerTable.getColumnModel().getColumn(i).setPreferredWidth(120);
            centerTable.getColumnModel().getColumn(i).setHeaderValue(getHtmlHeader("CN0"+(i+1), "Chức năng " + (i+1)));
        }

        JScrollPane centerScroll = new JScrollPane(centerTable);

        // Tạo Panel chứa 3 bảng
        JPanel container = new JPanel(new BorderLayout());
        container.add(leftTable.getTableHeader(), BorderLayout.NORTH); // Header giả cho bảng trái/phải sẽ xử lý sau

        // Cấu trúc chuẩn: Trái | Giữa (Scroll) | Phải
        JPanel mainContent = new JPanel(new BorderLayout());

        // Panel Trái: Gồm Header của nó và Body của nó
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(leftTable.getTableHeader(), BorderLayout.NORTH);
        leftPanel.add(leftTable, BorderLayout.CENTER);

        // Panel Phải: Gồm Header của nó và Body của nó
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(rightTable.getTableHeader(), BorderLayout.NORTH);
        rightPanel.add(rightTable, BorderLayout.CENTER);

        mainContent.add(leftPanel, BorderLayout.WEST);
        mainContent.add(centerScroll, BorderLayout.CENTER);
        mainContent.add(rightPanel, BorderLayout.EAST);

        add(mainContent, BorderLayout.CENTER);
    }

    private JTable createTable(DefaultTableModel model, int[] includeCols) {
        JTable table = new JTable(model);
        // Loại bỏ các cột không thuộc phạm vi của bảng này
        // (Xóa từ phải sang trái để tránh lỗi index)
        for (int i = model.getColumnCount() - 1; i >= 0; i--) {
            boolean keep = false;
            for (int col : includeCols) { if (i == col) keep = true; }
            if (!keep) table.removeColumn(table.getColumnModel().getColumn(i));
        }

        table.setRowHeight(60);
        table.setGridColor(Color.decode("#E2E8F0"));
        table.getTableHeader().setPreferredSize(new Dimension(0, 50));
        table.getTableHeader().setBackground(Color.decode("#F8FAFF"));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        return table;
    }

    private String getHtmlHeader(String code, String name) {
        return "<html><center>"+code+"<br><font size='2' weight='plain'>"+name+"</font></center></html>";
    }

    private JButton createStyledButton(String text, String hexColor) {
        JButton btn = new JButton(text);
        btn.setBackground(Color.decode(hexColor));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(8, 15, 8, 15));
        return btn;
    }

    class ActionButtonsRenderer extends JPanel implements TableCellRenderer {
        public ActionButtonsRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 8, 15));
            setOpaque(true);
            JLabel lblDetail = new JLabel("Chi tiết");
            JLabel lblEdit = new JLabel("✏");
            JLabel lblDel = new JLabel("🗑");
            lblDetail.setForeground(Color.BLUE);
            lblEdit.setForeground(Color.decode("#9BD7DB"));
            lblDel.setForeground(Color.RED);
            add(lblDetail); add(lblEdit); add(lblDel);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return this;
        }
    }
}
