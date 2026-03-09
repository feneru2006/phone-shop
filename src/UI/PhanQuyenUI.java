package UI;

import DAL.DAO.PhanQuyenDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PhanQuyenUI extends JPanel {
    private final PhanQuyenDAO phanQuyenDAO = new PhanQuyenDAO();
    private JPanel cardsContainer;
    private JList<String> roleList;
    private DefaultListModel<String> roleModel;

    // Map để quản lý Checkbox theo mã chức năng (CN01, CN02...)
    private Map<String, JCheckBox> checkboxMap = new HashMap<>();

    // Dữ liệu mẫu khớp với Database của bạn
    private final String[][] functions = {
            {"CN01", "Tổng quan", "Xem thống kê doanh thu và hoạt động"},
            {"CN02", "Sản phẩm", "Quản lý danh mục điện thoại, kho hàng"},
            {"CN06", "Bán hàng", "Lập hóa đơn và thanh toán cho khách"},
            {"CN08", "Nhân viên", "Quản lý hồ sơ và ca làm việc"},
            {"CN15", "Phân quyền", "Thiết lập quyền hạn hệ thống"},
            {"CN09", "Nhập hàng", "Quản lý phiếu nhập và nhà cung cấp"}
    };

    public PhanQuyenUI() {
        initComponents();
        loadRoles();
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 0));
        setBackground(Color.decode("#F8FAFF"));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- BÊN TRÁI: DANH SÁCH NHÓM QUYỀN ---
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(250, 0));

        JLabel lblRole = new JLabel("Nhóm người dùng");
        lblRole.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblRole.setBorder(new EmptyBorder(0, 0, 10, 0));
        leftPanel.add(lblRole, BorderLayout.NORTH);

        roleModel = new DefaultListModel<>();
        roleList = new JList<>(roleModel);
        roleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roleList.setFixedCellHeight(50);
        roleList.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Render List nhìn cho xịn
        roleList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setBorder(new EmptyBorder(0, 15, 0, 0));
                if (isSelected) {
                    label.setBackground(Color.decode("#2563EB"));
                    label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(Color.WHITE);
                    label.setForeground(Color.decode("#1E293B"));
                }
                return label;
            }
        });

        roleList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadPermissionsForSelectedRole();
        });

        JScrollPane scrollList = new JScrollPane(roleList);
        scrollList.setBorder(BorderFactory.createLineBorder(Color.decode("#E2E8F0")));
        leftPanel.add(scrollList, BorderLayout.CENTER);

        // --- BÊN PHẢI: CHI TIẾT QUYỀN ---
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);

        // Header bên phải
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel lblDetail = new JLabel("Chi tiết quyền hạn");
        lblDetail.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.add(lblDetail, BorderLayout.WEST);

        JButton btnSave = new JButton("Lưu cấu hình");
        btnSave.setBackground(Color.decode("#2563EB"));
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.addActionListener(e -> savePermissions());
        header.add(btnSave, BorderLayout.EAST);

        rightPanel.add(header, BorderLayout.NORTH);

        // Danh sách Card chức năng
        cardsContainer = new JPanel(new GridLayout(0, 2, 15, 15)); // Chia 2 cột
        cardsContainer.setOpaque(false);

        for (String[] func : functions) {
            cardsContainer.add(createFunctionCard(func[0], func[1], func[2]));
        }

        JScrollPane scrollCards = new JScrollPane(cardsContainer);
        scrollCards.setBorder(null);
        scrollCards.setOpaque(false);
        scrollCards.getViewport().setOpaque(false);
        rightPanel.add(scrollCards, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }

    private JPanel createFunctionCard(String code, String name, String desc) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#E2E8F0"), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Info bên trái card
        JPanel info = new JPanel(new GridLayout(2, 1));
        info.setOpaque(false);
        JLabel lblName = new JLabel(name);
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JLabel lblDesc = new JLabel("<html><body style='width: 150px'>" + desc + "</body></html>");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDesc.setForeground(Color.GRAY);
        info.add(lblName);
        info.add(lblDesc);

        // Checkbox bên phải card
        JCheckBox cb = new JCheckBox();
        cb.setOpaque(false);
        checkboxMap.put(code, cb);

        card.add(info, BorderLayout.CENTER);
        card.add(cb, BorderLayout.EAST);

        return card;
    }

    private void loadRoles() {
        // Bạn có thể dùng RoleDAO để lấy từ DB, đây mình giả lập theo code của bạn
        roleModel.addElement("Q01 - Quản trị viên");
        roleModel.addElement("Q02 - Quản lý");
        roleModel.addElement("Q03 - Nhân viên");
    }

    private void loadPermissionsForSelectedRole() {
        String selected = roleList.getSelectedValue();
        if (selected == null) return;

        String maQuyen = selected.split(" - ")[0];
        ArrayList<String> activePerms = phanQuyenDAO.getChucNangByMaQuyen(maQuyen);

        // Reset tất cả checkbox
        checkboxMap.values().forEach(cb -> cb.setSelected(false));

        // Tick những quyền đang có
        for (String code : activePerms) {
            if (checkboxMap.containsKey(code)) {
                checkboxMap.get(code).setSelected(true);
            }
        }
    }

    private void savePermissions() {
        String selected = roleList.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một nhóm quyền!");
            return;
        }

        String maQuyen = selected.split(" - ")[0];
        ArrayList<String> selectedCodes = new ArrayList<>();

        checkboxMap.forEach((code, cb) -> {
            if (cb.isSelected()) selectedCodes.add(code);
        });

        if (phanQuyenDAO.updateDanhSachQuyen(maQuyen, selectedCodes)) {
            JOptionPane.showMessageDialog(this, "Cập nhật thành công cho nhóm " + maQuyen);
        } else {
            JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi lưu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}