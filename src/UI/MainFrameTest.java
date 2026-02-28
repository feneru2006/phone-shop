package test1;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainFrameTest extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final Map<String, NavItem> navItems = new LinkedHashMap<>();
    private JLabel pageTitleLabel;

    // Theme Colors chuẩn hiện đại
    private static final Color TITLE_BAR_BG = Color.decode("#0F172A"); // Xanh đen đậm (Header)
    private static final Color BG_APP = Color.decode("#F8FAFF");        // Nền ứng dụng nhạt
    private static final Color SIDEBAR_BG = Color.decode("#FFFFFF");     // Sidebar trắng
    private static final Color SIDEBAR_ACTIVE = Color.decode("#2563EB"); // Xanh dương (Active)
    private static final Color TEXT_MAIN = Color.decode("#1E293B");      // Chữ chính
    private static final Color TEXT_MUTED = Color.decode("#64748B");    // Chữ mờ (Sub-text)

    public MainFrameTest() {
        setupLookAndFeel();

        setTitle("PHONE SHOP NHÓM 4 - HỆ THỐNG QUẢN LÝ");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1300, 800);
        setLocationRelativeTo(null);

        // Bố cục tổng thể
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_APP);

        // 1. Build các thành phần giao diện
        root.add(buildTitleBar(), BorderLayout.NORTH);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMainContent(), BorderLayout.CENTER);

        setContentPane(root);
        
        // Khởi tạo các trang nội dung
        initCards();
        showCard("Dashboard");
    }

    private void setupLookAndFeel() {
        try {
            FlatLightLaf.setup();
            UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("ScrollBar.width", 8);
            UIManager.put("ScrollBar.thumbArc", 10);
        } catch (Exception ignored) {}
    }

    private JPanel buildTitleBar() {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(TITLE_BAR_BG);
        titleBar.setPreferredSize(new Dimension(0, 55));
        titleBar.setBorder(new EmptyBorder(0, 20, 0, 20));

        // Trái: Thương hiệu
        // 1. Tạo một Panel trung gian (dùng FlowLayout để các thành phần nằm ngang)
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 15));
        leftPanel.setOpaque(false); // Để Panel này trong suốt, thấy được màu nền TitleBar

        // 2. Tạo Label cho Icon 
        JLabel shopicon = new JLabel("🛒");
        shopicon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20)); // Font hỗ trợ Emoji
        shopicon.setForeground(Color.WHITE);

        // 3. Tạo Label cho Tên Shop
        JLabel brand = new JLabel("PHONE SHOP NHÓM 4");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Font cho chữ
        brand.setForeground(Color.WHITE);

        // 4. Thêm Icon và Chữ vào Panel trung gian
        leftPanel.add(shopicon);
        leftPanel.add(brand);

        // 5. Thêm Panel trung gian vào vùng WEST của TitleBar
        titleBar.add(leftPanel, BorderLayout.WEST);
        
        // Phải: Thông tin người dùng
        JLabel userLabel = new JLabel("<html><div style='text-align: right;'><b>ADMINISTRATOR</b><br>"
                + "<font size='2' color='#94A3B8'>Quản trị hệ thống</font></div></html>");
        userLabel.setForeground(Color.WHITE);
        titleBar.add(userLabel, BorderLayout.EAST);

        return titleBar;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.decode("#E2E8F0")));

        // Tiêu đề nhỏ phía trên danh sách menu
        JLabel navLabel = new JLabel("DANH MỤC QUẢN LÝ");
        navLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        navLabel.setForeground(TEXT_MUTED);
        navLabel.setBorder(new EmptyBorder(25, 20, 15, 10));
        sidebar.add(navLabel, BorderLayout.NORTH);

        // Panel chứa các nút menu
        JPanel menuContainer = new JPanel();
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setBackground(SIDEBAR_BG);
        menuContainer.setBorder(new EmptyBorder(0, 10, 10, 10));

        // Danh sách menu từ yêu cầu của bạn
        String[] menus = {
            "Dashboard", "Sản phẩm (sanpham)", "Hình ảnh SP (anhsp)", "Chi tiết SP (ctsp)",
            "Loại SP (loai)", "Bán hàng (hoadon)", "Khách hàng (khachhang)", "Nhân viên (nhanvien)",
            "Nhập hàng (phieunhap)", "Nhà cung cấp (NCC)", "Bảo hành (Phieubaohanh)",
            "Khuyến mãi (giamgia)", "Đánh giá (Danhgia)", "Tài khoản (account)",
            "Phân quyền (nhomquyen)", "Nhật ký (log)"
        };

        for (String m : menus) {
            NavItem item = new NavItem(m);
            navItems.put(m, item);
            item.addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) { showCard(m); }
            });
            menuContainer.add(item);
            menuContainer.add(Box.createVerticalStrut(4));
        }

        JScrollPane scroll = new JScrollPane(menuContainer);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        sidebar.add(scroll, BorderLayout.CENTER);

        return sidebar;
    }

    private JPanel buildMainContent() {
        JPanel main = new JPanel(new BorderLayout());
        main.setOpaque(false);

        // Sub-header trắng chứa tiêu đề trang và nút công cụ
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#E2E8F0")));

        pageTitleLabel = new JLabel("Trang chủ");
        pageTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        pageTitleLabel.setForeground(TEXT_MAIN);
        pageTitleLabel.setBorder(new EmptyBorder(0, 25, 0, 0));
        header.add(pageTitleLabel, BorderLayout.WEST);

        // Nút Tìm kiếm giả lập bên phải
        JButton btnSearch = new JButton("🔍 Tìm kiếm nhanh");
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearch.setFocusPainted(false);
        btnSearch.setBackground(SIDEBAR_ACTIVE);
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSearch.setMargin(new Insets(8, 15, 8, 15));
        
        JPanel btnWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT, 25, 12));
        btnWrapper.setOpaque(false);
        btnWrapper.add(btnSearch);
        header.add(btnWrapper, BorderLayout.EAST);

        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        main.add(header, BorderLayout.NORTH);
        main.add(contentPanel, BorderLayout.CENTER);
        return main;
    }

    private void initCards() {
        for (String key : navItems.keySet()) {
            JPanel card = new JPanel(new GridBagLayout());
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createLineBorder(Color.decode("#E2E8F0"), 1));
            card.add(new JLabel("Hệ thống đang phát triển nội dung cho: " + key));
            contentPanel.add(card, key);
        }
    }

    private void showCard(String name) {
        navItems.forEach((k, v) -> v.setActive(k.equals(name)));
        if (pageTitleLabel != null) pageTitleLabel.setText(name);
        cardLayout.show(contentPanel, name);
    }

    // Lớp nội bộ tùy chỉnh Menu Item
    private class NavItem extends JPanel {
        private final JLabel label;

        NavItem(String text) {
            setLayout(new BorderLayout());
            setMaximumSize(new Dimension(240, 42));
            setPreferredSize(new Dimension(240, 42));
            setBackground(SIDEBAR_BG);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(0, 15, 0, 0));
            
            label = new JLabel(text);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            label.setForeground(TEXT_MAIN);
            add(label, BorderLayout.CENTER);
        }

        void setActive(boolean active) {
            setBackground(active ? SIDEBAR_ACTIVE : SIDEBAR_BG);
            label.setForeground(active ? Color.WHITE : TEXT_MAIN);
            label.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 14));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrameTest().setVisible(true));
    }
}