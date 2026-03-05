package UI;

import com.formdev.flatlaf.FlatLightLaf;

import UI.Panel.DashboardPanel;
import UI.Panel.Sales.KhuyenMaiPanel;
import UI.Panel.BaoHanh.BaoHanhPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainFrameTest extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final Map<String, NavItem> navItems = new LinkedHashMap<>();
    private JLabel pageTitleLabel;

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
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_APP);

        root.add(buildTitleBar(), BorderLayout.NORTH);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMainContent(), BorderLayout.CENTER);

        setContentPane(root);
        
        // 1. Khởi tạo các Card trống cho các chức năng chưa làm
        initCards();
        
        // 2. Add Dashboard thật của bạn vào
        DashboardPanel dashboard = new DashboardPanel();
        contentPanel.add(dashboard, "Dashboard");

        //
        KhuyenMaiPanel giamgia = new KhuyenMaiPanel();
        contentPanel.add(giamgia,"Khuyến mãi");
        //
        BaoHanhPanel baohanh = new BaoHanhPanel();
        contentPanel.add(baohanh,"Bảo hành");
        // 3. Hiển thị Dashboard lên đầu tiên
        showCard("Dashboard");
    }

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

        // Trái: Logo
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

        // Phải: User Pill bo tròn
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 12));
        right.setOpaque(false);

        RoundedPanel userPill = new RoundedPanel(30, Color.decode("#1E293B"));
        userPill.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 3));
        userPill.setBorder(new EmptyBorder(0, 5, 0, 15));
        
        RoundedPanel avatar = new RoundedPanel(30, SIDEBAR_ACTIVE);
        avatar.setPreferredSize(new Dimension(30, 30));
        JLabel avTxt = new JLabel("A", SwingConstants.CENTER);
        avTxt.setForeground(Color.WHITE);
        avTxt.setFont(new Font("Segoe UI", Font.BOLD, 14));
        avatar.add(avTxt, BorderLayout.CENTER);

        JLabel userInfo = new JLabel("<html><b style='color:white;'>Admin</b><br><font size='2' color='#94A3B8'>Quản trị viên</font></html>");
        userPill.add(avatar);
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

        // --- PHẦN BÁN HÀNG ---
        JLabel saleLabel = new JLabel("HỆ THỐNG BÁN HÀNG");
        saleLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        saleLabel.setForeground(TEXT_MUTED);
        saleLabel.setBorder(new EmptyBorder(25, 20, 10, 10));
        container.add(saleLabel);

        // 🛒 Nút Bán hàng
        NavItem banHangItem = new NavItem("🛒 Bán hàng tại quầy");
        navItems.put("Bán hàng", banHangItem);
        banHangItem.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { showCard("Bán hàng"); }
        });
        JPanel pWrapper = new JPanel(new BorderLayout());
        pWrapper.setOpaque(false);
        pWrapper.setBorder(new EmptyBorder(0, 10, 10, 10));
        pWrapper.add(banHangItem);
        container.add(pWrapper);

        container.add(Box.createVerticalStrut(10));
        container.add(new JSeparator());

        // --- PHẦN QUẢN LÝ ---
        JLabel navLabel = new JLabel("DANH MỤC QUẢN LÝ");
        navLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        navLabel.setForeground(TEXT_MUTED);
        navLabel.setBorder(new EmptyBorder(15, 20, 10, 10));
        container.add(navLabel);

        JPanel menuContainer = new JPanel();
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setOpaque(false);
        menuContainer.setBorder(new EmptyBorder(0, 10, 10, 10));

        // --- DANH SÁCH MỤC ---
        String[][] managementItems = {
            {"Dashboard", "📊 Dashboard"},
            {"Sản phẩm", "📦 Sản phẩm"},
            {"Hình ảnh SP", "🖼️ Hình ảnh SP"},
            {"Chi tiết SP", "📝 Chi tiết SP"},
            {"Loại SP", "🏷️ Loại SP"},
            {"Khách hàng", "👥 Khách hàng"},
            {"Nhân viên", "🆔 Nhân viên"},
            {"Nhập hàng", "📥 Nhập hàng"},
            {"Nhà cung cấp", "🏭 Nhà cung cấp"},
            {"Bảo hành", "🛠️ Bảo hành"},
            {"Khuyến mãi", "🎁 Khuyến mãi"},
            {"Đánh giá", "⭐ Đánh giá"},
            {"Tài khoản", "🔐 Tài khoản"},
            {"Phân quyền", "🔑 Phân quyền"},
            {"Nhật ký", "📜 Nhật ký"}
        };

        for (String[] item : managementItems) {
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
        scroll.getVerticalScrollBar().setUnitIncrement(16);
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
            if (key.equals("Dashboard") || key.equals("Khuyến mãi") || key.equals("Bảo hành")) continue; 

            JPanel card = new JPanel(new GridBagLayout());
            card.setBackground(Color.WHITE);
            card.add(new JLabel("Đang phát triển chức năng: " + key));
            contentPanel.add(card, key);
        }
    }

    private void showCard(String name) {
        navItems.forEach((k, v) -> v.setActive(k.equals(name)));
        if (pageTitleLabel != null) pageTitleLabel.setText(name);
        cardLayout.show(contentPanel, name);
    }

    // --- LỚP HỖ TRỢ BO GÓC ---
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

    // --- LỚP NÚT SIDENAV CÓ EMOJI ---
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

            // Tách Emoji và Text
            String emoji = textWithEmoji.substring(0, 2);
            String text = textWithEmoji.substring(2).trim();

            labelIcon = new JLabel(emoji);
            // Font cho icon (hỗ trợ Emoji)
            labelIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            labelIcon.setForeground(TEXT_MAIN);
            labelIcon.setBorder(new EmptyBorder(0, 15, 0, 0));

            labelText = new JLabel(text);
            // Font cho chữ (hỗ trợ tiếng Việt)
            labelText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            labelText.setForeground(TEXT_MAIN);
            labelText.setBorder(new EmptyBorder(0, 10, 0, 0));

            add(labelIcon, BorderLayout.WEST);
            add(labelText, BorderLayout.CENTER);
        }

        void setActive(boolean active) {
            this.active = active;
            Color textColor = active ? Color.WHITE : TEXT_MAIN;
            labelIcon.setForeground(textColor);
            labelText.setForeground(textColor);
            
            // Cập nhật bold khi active
            labelIcon.setFont(new Font("Segoe UI Emoji", active ? Font.BOLD : Font.PLAIN, 14));
            labelText.setFont(new Font("Segoe UI", active ? Font.BOLD : Font.PLAIN, 14));
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