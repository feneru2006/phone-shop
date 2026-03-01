package UI;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.util.LinkedHashMap;
import java.util.Map;
import UI.DashboardPanel;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class MainFrameTest extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final Map<String, NavItem> navItems = new LinkedHashMap<>();
    private JLabel pageTitleLabel;

    // --- MÀU SẮC THEME ---
    private static final Color HEADER_BG = Color.decode("#0F172A"); // Xanh đen đậm
    private static final Color BG_APP = Color.decode("#F8FAFF");
    private static final Color SIDEBAR_BG = Color.decode("#FFFFFF");
    private static final Color SIDEBAR_ACTIVE = Color.decode("#2563EB");
    private static final Color TEXT_MAIN = Color.decode("#1E293B");
    private static final Color TEXT_MUTED = Color.decode("#64748B");

    public MainFrameTest() {
        setupLookAndFeel();

        // Ẩn khung viền mặc định để tự vẽ
        setUndecorated(true);
        setTitle("PHONE SHOP NHÓM 4 - HỆ THỐNG QUẢN LÝ");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1300, 800);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_APP);

        root.add(buildTitleBar(), BorderLayout.NORTH);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMainContent(), BorderLayout.CENTER);

        setContentPane(root);
        
        initCards();
        DashboardPanel dashboard = new DashboardPanel();
        contentPanel.add(dashboard, "Dashboard");
        
        showCard("Dashboard");
    }

    private void setupLookAndFeel() {
        try {
            FlatLightLaf.setup();
            UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("ScrollBar.width", 8);
        } catch (Exception ignored) {}
    }

    // --- BUILD TITLE BAR VỚI BO GÓC ---
    private JPanel buildTitleBar() {
        // Sử dụng RoundedPanel cho titleBar, bo góc trên (topOnly = true)
        RoundedPanel titleBar = new RoundedPanel(20, HEADER_BG, true);
        titleBar.setPreferredSize(new Dimension(0, 60));
        titleBar.setBorder(new EmptyBorder(0, 20, 0, 10));

        // --- BÊN TRÁI: LOGO & BRAND ---
        JPanel leftSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        leftSection.setOpaque(false);

        // Icon giỏ hàng
        JLabel lbCartIcon = new JLabel("🛒"); 
        lbCartIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        lbCartIcon.setForeground(Color.WHITE);
        lbCartIcon.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255,255,255,50), 1, true),
            new EmptyBorder(5, 5, 5, 5)
        ));

        JLabel lbBrand = new JLabel("PHONE SHOP NHÓM 4");
        lbBrand.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lbBrand.setForeground(Color.WHITE);

        leftSection.add(lbCartIcon);
        leftSection.add(lbBrand);

        // --- BÊN PHẢI: USER INFO & CLOSE BUTTON ---
        JPanel rightSection = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 8));
        rightSection.setOpaque(false);

        // --- PILLED USER VỚI BO GÓC CAO (30px) ---
        RoundedPanel userPill = new RoundedPanel(30, Color.decode("#1E293B"));
        userPill.setPreferredSize(new Dimension(200, 40));
        userPill.setBorder(new EmptyBorder(0, 10, 0, 10));
        
        // --- AVATAR VỚI BO GÓC 100% (TRÒN) ---
        RoundedPanel avatar = new RoundedPanel(50, Color.decode("#2563EB"));
        avatar.setPreferredSize(new Dimension(30, 30));
        JLabel avatarLabel = new JLabel("A", SwingConstants.CENTER);
        avatarLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        avatarLabel.setForeground(Color.WHITE);
        avatar.add(avatarLabel, BorderLayout.CENTER);
        
        // Text info (Name & Email)
        JLabel userInfo = new JLabel("<html><div style='color:white; font-weight:bold; font-size:10px;'>ADMINISTRATOR</div>"
                + "<div style='color:#3B82F6; font-size:9px;'>admin@phoneshop.vn</div></html>");
        
        // Icon người nhỏ bên cạnh
        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
        userIcon.setForeground(new Color(255,255,255,150));

        userPill.add(avatar, BorderLayout.WEST);
        userPill.add(userInfo, BorderLayout.CENTER);
        userPill.add(userIcon, BorderLayout.EAST);

        // Nút 'X' Close
        JButton btnClose = new JButton("✕");
        btnClose.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        btnClose.setForeground(new Color(255,255,255,150));
        btnClose.setBorderPainted(false);
        btnClose.setContentAreaFilled(false);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> System.exit(0));

        rightSection.add(userPill);
        rightSection.add(btnClose);

        titleBar.add(leftSection, BorderLayout.WEST);
        titleBar.add(rightSection, BorderLayout.EAST);

        return titleBar;
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.decode("#E2E8F0")));

        JLabel navLabel = new JLabel("DANH MỤC QUẢN LÝ");
        navLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        navLabel.setForeground(TEXT_MUTED);
        navLabel.setBorder(new EmptyBorder(25, 20, 15, 10));
        sidebar.add(navLabel, BorderLayout.NORTH);

        JPanel menuContainer = new JPanel();
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setBackground(SIDEBAR_BG);
        menuContainer.setBorder(new EmptyBorder(0, 10, 10, 10));

        String[] menus = {
            "Dashboard", "Sản phẩm", "Hình ảnh SP", "Chi tiết SP",
            "Loại SP", "Bán hàng", "Khách hàng", "Nhân viên",
            "Nhập hàng", "Nhà cung cấp", "Bảo hành",
            "Khuyến mãi", "Đánh giá", "Tài khoản",
            "Phân quyền", "Nhật ký"
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
            JPanel card = new JPanel(new GridBagLayout());
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createLineBorder(Color.decode("#E2E8F0"), 1));
            card.add(new JLabel("Nội dung cho: " + key));
            contentPanel.add(card, key);
        }
    }

    private void showCard(String name) {
        navItems.forEach((k, v) -> v.setActive(k.equals(name)));
        if (pageTitleLabel != null) pageTitleLabel.setText(name);
        cardLayout.show(contentPanel, name);
    }

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

    // --- LỚP ROUNDEDPANEL DÙNG CHUNG ---
    class RoundedPanel extends JPanel {
        private int cornerRadius;
        private Color backgroundColor;
        private boolean topOnly = false;

        public RoundedPanel(int cornerRadius, Color backgroundColor) {
            super(new BorderLayout());
            this.cornerRadius = cornerRadius;
            this.backgroundColor = backgroundColor;
            setOpaque(false);
        }

        public RoundedPanel(int cornerRadius, Color backgroundColor, boolean topOnly) {
            this(cornerRadius, backgroundColor);
            this.topOnly = topOnly;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);

            if (topOnly) {
                Area area = new Area(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
                area.add(new Area(new Rectangle(0, getHeight() / 2, getWidth(), getHeight() / 2)));
                g2.fill(area);
            } else {
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
            }
            g2.dispose();
        }
    }
}