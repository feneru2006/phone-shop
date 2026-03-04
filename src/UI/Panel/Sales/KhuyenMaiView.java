package UI.Panel.Sales;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class KhuyenMaiView extends JPanel {

    private JTable table;
    private DefaultTableModel model;

    private JButton btnPrev;
    private JButton btnNext;
    private JLabel lblPage;

    private int currentPage = 1;
    private int rowsPerPage = 5;
    private int totalPages = 1;

    private KhuyenMaiController controller;

    public KhuyenMaiView() {

        setLayout(new BorderLayout());
        setOpaque(false);

        controller = new KhuyenMaiController(this);

        initUI();
        controller.loadData();
    }

    private void initUI() {

        // ===== TITLE =====
        JLabel lblTitle = new JLabel("KHUYẾN MÃI");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JButton btnAdd = new JButton("TẠO ĐỢT KHUYẾN MÃI");
        btnAdd.putClientProperty("JButton.buttonType", "roundRect");
        btnAdd.setBackground(new Color(37, 99, 235));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnAdd.addActionListener(e -> controller.add());

        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        top.setOpaque(false);
        top.add(lblTitle, BorderLayout.WEST);
        top.add(btnAdd, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);

        // ===== TABLE =====
        String[] cols = {"MAGG","ĐỢT GG","BẮT ĐẦU","KẾT THÚC","THAO TÁC"};

        model = new DefaultTableModel(cols,0){
            public boolean isCellEditable(int r,int c){
                return c==4;
            }
        };

        table = new JTable(model);

        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(37, 99, 235));
        table.setSelectionForeground(Color.WHITE);

        table.getTableHeader().setFont(
                new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setReorderingAllowed(false);

        table.getColumn("THAO TÁC")
                .setCellRenderer(new ActionRenderer());
        table.getColumn("THAO TÁC")
                .setCellEditor(new ActionEditor(new JCheckBox()));

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(
                BorderFactory.createEmptyBorder(0,20,10,20));
        scroll.getViewport().setOpaque(false);

        // ===== PAGINATION =====
        btnPrev = new JButton("<< Trước");
        btnNext = new JButton("Sau >>");
        lblPage = new JLabel();

        btnPrev.addActionListener(e->{
            currentPage--;
            controller.loadData();
        });

        btnNext.addActionListener(e->{
            currentPage++;
            controller.loadData();
        });

        JPanel pagination = new JPanel(
                new FlowLayout(FlowLayout.CENTER,20,10));
        pagination.setOpaque(false);
        pagination.add(btnPrev);
        pagination.add(lblPage);
        pagination.add(btnNext);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(scroll,BorderLayout.CENTER);
        center.add(pagination,BorderLayout.SOUTH);

        add(center,BorderLayout.CENTER);
    }

    // ===== METHODS =====

    public void clearTable(){
        model.setRowCount(0);
    }

    public void addRow(Object[] row){
        model.addRow(row);
    }

    public String getValueAt(int row,int col){
        return model.getValueAt(row,col).toString();
    }

    public int getStartIndex(){
        return (currentPage-1)*rowsPerPage;
    }

    public int getEndIndex(int totalRows){

        totalPages = (int)Math.ceil((double)totalRows/rowsPerPage);
        if(totalPages==0) totalPages=1;
        if(currentPage>totalPages) currentPage=totalPages;
        if(currentPage<1) currentPage=1;

        return Math.min(getStartIndex()+rowsPerPage,totalRows);
    }

    public void preparePagination(int totalRows){
        totalPages = (int)Math.ceil((double)totalRows/rowsPerPage);
    }

    public void updatePaginationButtons(){
        lblPage.setText("Trang "+currentPage+" / "+totalPages);
        btnPrev.setEnabled(currentPage>1);
        btnNext.setEnabled(currentPage<totalPages);
    }

    // ===== ACTION RENDERER =====

    class ActionRenderer extends JPanel
            implements javax.swing.table.TableCellRenderer {

        JButton btnEdit = new JButton("Sửa");
        JButton btnDelete = new JButton("Xóa");

        public ActionRenderer(){
            setOpaque(false);
            setLayout(new FlowLayout(
                    FlowLayout.CENTER,15,5));

            btnEdit.putClientProperty(
                    "JButton.buttonType","roundRect");
            btnDelete.putClientProperty(
                    "JButton.buttonType","roundRect");

            add(btnEdit);
            add(btnDelete);
        }

        public Component getTableCellRendererComponent(
                JTable table,Object value,
                boolean isSelected,boolean hasFocus,
                int row,int column){
            return this;
        }
    }

    class ActionEditor extends DefaultCellEditor {

        JPanel panel;
        JButton btnEdit;
        JButton btnDelete;
        int currentRow;

        public ActionEditor(JCheckBox box){
            super(box);

            panel = new JPanel(
                    new FlowLayout(
                            FlowLayout.CENTER,15,5));
            panel.setOpaque(false);

            btnEdit = new JButton("Sửa");
            btnDelete = new JButton("Xóa");

            btnEdit.putClientProperty(
                    "JButton.buttonType","roundRect");
            btnDelete.putClientProperty(
                    "JButton.buttonType","roundRect");

            panel.add(btnEdit);
            panel.add(btnDelete);

            btnEdit.addActionListener(e->{
                fireEditingStopped();
                controller.edit(currentRow);
            });

            btnDelete.addActionListener(e->{
                fireEditingStopped();
                controller.delete(currentRow);
            });
        }

        public Component getTableCellEditorComponent(
                JTable table,Object value,
                boolean isSelected,int row,int column){
            currentRow=row;
            return panel;
        }

        public Object getCellEditorValue(){
            return "ACTION";
        }
    }
}