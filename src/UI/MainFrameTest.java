package UI;

import DAL.DAO.DBConnection;
import com.formdev.flatlaf.FlatLightLaf;
import Utility.SessionManager;
import Utility.PhanQuyen;
import UI.Panel.DashboardPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.sql.Connection;

public class MainFrameTest extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final Map<String, NavItem> navItems = new LinkedHashMap<>();
    private JLabel pageTitleLabel;
    private JPanel menuContainer;

    // --- MÀU SẮC THEME ---
    private static final Color HEADER_BG = Color.decode("#0F172A");
    private static final Color BG_APP = Color.decode("#F8FAFF");
    private static final Color SIDEBAR_BG = Color.decode("#FFFFFF");
    private static final Color SIDEBAR_ACTIVE = Color.decode("#2563EB");
    private static final Color TEXT_MAIN = Color.decode("#1E293B");
    private static final Color TEXT_MUTED = Color.decode("#64748B");

    public MainFrameTest() {
        setupLookAndFeel();

        setTitle("PHONE SHOP NHÓM 4 - HỆ THỐNG QUẢN LÝ");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1300, 850);
        setMinimumSize(new Dimension(1360, 820));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_APP);

        root.add(buildTitleBar(), BorderLayout.NORTH);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMainContent(), BorderLayout.CENTER);

        setContentPane(root);

        // 1. Khởi tạo các Card
        initCards();

        // 2. Thêm các Giao diện chính vào CardLayout
        contentPanel.add(new DashboardPanel(), "Dashboard");
        contentPanel.add(new TaiKhoanUI(), "Tài khoản");
        contentPanel.add(new PhanQuyenUI(), "Phân quyền");

        // [QUAN TRỌNG]: Chỉnh sửa chạy Database tại đây
        applyPermissionsFromDB();

        showCard("Dashboard");
    }

    // ==========================================================
    // LOGIC PHÂN QUYỀN ĐỘNG: SỬ DỤNG TRỰC TIẾP FILE PHANQUYEN.JAVA
    // ==========================================================
    private void applyPermissionsFromDB() {
        if (SessionManager.currentUser == null) return;

        String roleCode = SessionManager.currentUser.getQuyen();

        // 1. Nếu là Admin (AD) -> Hiển thị tất cả
        if (roleCode.equals("AD")) return;

        // 2. Lấy danh sách menu từ DB
        ArrayList<String> permittedMenus = new ArrayList<>();
        permittedMenus.add("dashboard"); // Luôn lưu dạng chữ thường để so sánh

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT c.tenCN FROM phanquyen p " +
                    "JOIN chucnang c ON p.MACN = c.MACN " +
                    "WHERE p.MAQUYEN = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, roleCode);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                // Ép hết về chữ thường để tránh lỗi hoa/thường
                permittedMenus.add(rs.getString("tenCN").toLowerCase().trim());
            }
        } catch (Exception e) { e.printStackTrace(); }

        // 3. CHẶN CỨNG: Nếu không phải Quản lý (M) -> Xóa 3 mục nhạy cảm
        if (!roleCode.equals("M")) {
            permittedMenus.remove("tài khoản");
            permittedMenus.remove("phân quyền");
            permittedMenus.remove("nhật ký");
        }

        // 4. Gọi hàm lọc với danh sách đã chuẩn hóa
        filterMenu(permittedMenus);
    }

    private void filterMenu(ArrayList<String> allowedMenus) {
        for (Map.Entry<String, NavItem> entry : navItems.entrySet()) {
            String key = entry.getKey().toLowerCase().trim();
            NavItem item = entry.getValue();

            // Nếu không có trong danh sách cho phép -> Ẩn
            if (!allowedMenus.contains(key)) {
                item.setVisible(false);
            } else {
                item.setVisible(true); // Đảm bảo các mục khác được hiện
            }
        }

        // QUAN TRỌNG: Làm mới lại toàn bộ Sidebar
        if (menuContainer != null) {
            menuContainer.revalidate();
            menuContainer.repaint();
        }
    }

    // ==========================================================
    // CÁC HÀM XÂY DỰNG GIAO DIỆN (Giữ nguyên giao diện đẹp của bạn)
    // ==========================================================
    private void setupLookAndFeel() {
        try {
            FlatLightLaf.setup();
            UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 13));
        } catch (Exception ignored) {}
    }

    private JPanel buildTitleBar() {
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(HEADER_BG);
        titleBar.setPreferredSize(new Dimension(0, 65));
        titleBar.setBorder(new EmptyBorder(0, 20, 0, 20));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        left.setOpaque(false);
        JLabel lbIcon = new JLabel("\uD83D\uDED2");
        lbIcon.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 18));
        lbIcon.setForeground(Color.WHITE);
        JLabel lbBrand = new JLabel("PHONE SHOP NHÓM 4");
        lbBrand.setFont(new Font("Segoe UI", Font.BOLD, 17));
        lbBrand.setForeground(Color.WHITE);
        left.add(lbIcon);
        left.add(lbBrand);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 12));
        right.setOpaque(false);

        RoundedPanel userPill = new RoundedPanel(30, Color.decode("#1E293B"));
        userPill.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 3));
        userPill.setBorder(new EmptyBorder(0, 5, 0, 15));

        String userName = (SessionManager.currentUser != null) ? SessionManager.currentUser.getTen() : "Admin";
        String userRole = (SessionManager.currentUser != null) ? SessionManager.currentUser.getQuyen() : "AD";

        JLabel userInfo = new JLabel("<html><b style='color:white;'>" + userName + "</b><br><font size='2' color='#94A3B8'>" + userRole + "</font></html>");
        userPill.add(userInfo);
        right.add(userPill);

        titleBar.add(left, BorderLayout.WEST);
        titleBar.add(right, BorderLayout.EAST);
        return titleBar;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.decode("#E2E8F0")));

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);

        // --- BÁN HÀNG ---
        JLabel saleLabel = new JLabel("HỆ THỐNG BÁN HÀNG");
        saleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        saleLabel.setForeground(TEXT_MUTED);
        saleLabel.setBorder(new EmptyBorder(25, 20, 10, 10));
        container.add(saleLabel);

        NavItem banHangItem = new NavItem("🛒 Bán hàng");
        navItems.put("Bán hàng", banHangItem);
        banHangItem.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { showCard("Bán hàng"); }
        });
        JPanel pWrapper = new JPanel(new BorderLayout());
        pWrapper.setOpaque(false);
        pWrapper.setBorder(new EmptyBorder(0, 10, 10, 10));
        pWrapper.add(banHangItem);
        container.add(pWrapper);

        container.add(new JSeparator());

        // --- QUẢN LÝ ---
        JLabel navLabel = new JLabel("DANH MỤC QUẢN LÝ");
        navLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        navLabel.setForeground(TEXT_MUTED);
        navLabel.setBorder(new EmptyBorder(15, 20, 10, 10));
        container.add(navLabel);

        menuContainer = new JPanel();
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setOpaque(false);
        menuContainer.setBorder(new EmptyBorder(0, 10, 10, 10));

        String[][] mngItems = {
                {"Dashboard", "📊 Dashboard"}, {"Sản phẩm", "📦 Sản phẩm"}, {"Hình ảnh SP", "🖼️ Hình ảnh SP"},
                {"Chi tiết SP", "📝 Chi tiết SP"}, {"Loại SP", "🏷️ Loại SP"}, {"Khách hàng", "👥 Khách hàng"},
                {"Nhân viên", "🆔 Nhân viên"}, {"Nhập hàng", "📥 Nhập hàng"}, {"Nhà cung cấp", "🏭 Nhà cung cấp"},
                {"Khuyến mãi", "🎁 Khuyến mãi"}, {"Tài khoản", "🔐 Tài khoản"}, {"Phân quyền", "🔑 Phân quyền"},
                {"Nhật ký", "📜 Nhật ký"}
        };

        for (String[] item : mngItems) {
            NavItem navItem = new NavItem(item[1]);
            navItems.put(item[0], navItem);
            navItem.addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) { showCard(item[0]); }
            });
            menuContainer.add(navItem);
            menuContainer.add(Box.createVerticalStrut(4));
        }

        sidebar.add(container, BorderLayout.NORTH);
        JScrollPane scroll = new JScrollPane(menuContainer);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(SIDEBAR_BG);
        sidebar.add(scroll, BorderLayout.CENTER);

        return sidebar;
    }

    private JPanel buildMainContent() {
        JPanel main = new JPanel(new BorderLayout());
        main.setOpaque(false);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#E2E8F0")));

        pageTitleLabel = new JLabel("Dashboard");
        pageTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        pageTitleLabel.setForeground(TEXT_MAIN);
        pageTitleLabel.setBorder(new EmptyBorder(0, 25, 0, 0));
        header.add(pageTitleLabel, BorderLayout.WEST);

        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        main.add(header, BorderLayout.NORTH);
        main.add(contentPanel, BorderLayout.CENTER);
        return main;
    }

    private void initCards() {
        for (String key : navItems.keySet()) {
            if (key.equals("Dashboard") || key.equals("Tài khoản") || key.equals("Phân quyền")) continue;
            JPanel card = new JPanel(new GridBagLayout());
            card.setBackground(Color.WHITE);
            card.add(new JLabel("Chức năng: " + key));
            contentPanel.add(card, key);
        }
    }

    private void showCard(String name) {
        // --- THÊM ĐOẠN KIỂM TRA NULL NÀY VÀO ĐẦU HÀM ---
        if (SessionManager.currentUser == null) {
            // Nếu chưa có user, vẫn cho hiện Dashboard nhưng bỏ qua kiểm tra quyền
            navItems.forEach((k, v) -> v.setActive(k.equals(name)));
            if (pageTitleLabel != null) pageTitleLabel.setText(name);
            cardLayout.show(contentPanel, name);
            return;
        }
        // ----------------------------------------------

        // Logic kiểm tra quyền
        String role = SessionManager.currentUser.getQuyen();
        if (!role.equals("AD") && !role.equals("M")) {
            if (name.equals("Tài khoản") || name.equals("Phân quyền") || name.equals("Nhật ký")) {
                JOptionPane.showMessageDialog(this, "Bạn không có quyền truy cập!");
                return;
            }
        }

        navItems.forEach((k, v) -> v.setActive(k.equals(name)));
        if (pageTitleLabel != null) pageTitleLabel.setText(name);
        cardLayout.show(contentPanel, name);
    }

    class RoundedPanel extends JPanel {
        private int radius;
        private Color bgColor;
        public RoundedPanel(int radius, Color bgColor) {
            super(new BorderLayout());
            this.radius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private class NavItem extends JPanel {
        private final JLabel labelIcon;
        private final JLabel labelText;
        private boolean active = false;

        NavItem(String textWithEmoji) {
            setLayout(new BorderLayout());
            setMaximumSize(new Dimension(240, 35));
            setPreferredSize(new Dimension(240, 35));
            setOpaque(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            labelIcon = new JLabel(textWithEmoji.substring(0, 2));
            labelIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            labelIcon.setBorder(new EmptyBorder(0, 15, 0, 0));

            labelText = new JLabel(textWithEmoji.substring(2).trim());
            labelText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            labelText.setBorder(new EmptyBorder(0, 10, 0, 0));

            add(labelIcon, BorderLayout.WEST);
            add(labelText, BorderLayout.CENTER);
        }

        void setActive(boolean active) {
            this.active = active;
            Color textColor = active ? Color.WHITE : TEXT_MAIN;
            labelIcon.setForeground(textColor);
            labelText.setForeground(textColor);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            if (active) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SIDEBAR_ACTIVE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
            }
            super.paintComponent(g);
        }
    }
}
