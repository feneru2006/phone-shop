package UI;

import com.formdev.flatlaf.FlatLightLaf;

import UI.Panel.DashboardPanel;
import UI.Panel.Sales.KhuyenMaiPanel;
import UI.Panel.KhoPanel;
import UI.Panel.LogPanel;
import UI.Panel.BaoHanh.BaoHanhPanel;
import UI.Panel.NCC.NCCPanel;
import UI.Panel.PN.PNPanel;
import UI.Utils.UIUtils; 
import Utility.SessionManager;
import Utility.PhanQuyen;
import DAL.DAO.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.sql.Connection;

public class MainFrameTest extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    
    private final Map<String, NavItem> navItems = new LinkedHashMap<>();
    private final Map<String, JPanel> cachedPanels = new HashMap<>();
    
    private JLabel pageTitleLabel;
    private JPanel menuContainer;
    
    private String currentUsername;
    private String currentRole;

    private static final Color HEADER_BG = Color.decode("#3a5170");
    private static final Color BG_APP = Color.decode("#30475e");
    private static final Color SIDEBAR_BG = Color.decode("#2C3E50");
    private static final Color SIDEBAR_ACTIVE = Color.decode("#2563EB");
    private static final Color TEXT_MAIN = Color.decode("#f0faff");
    private static final Color TEXT_MUTED = Color.decode("#cbffc7");

    public MainFrameTest(String username, String role) {
        this.currentUsername = (username != null && !username.isEmpty()) ? username : "Guest";
        this.currentRole = (role != null && !role.isEmpty()) ? role : "Người dùng";

        setupLookAndFeel();

        setTitle("PHONE SHOP NHÓM 4 - HỆ THỐNG QUẢN LÝ");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1350, 820);
        setMinimumSize(new Dimension(1350, 820));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_APP);

        root.add(buildTitleBar(), BorderLayout.NORTH);
        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMainContent(), BorderLayout.CENTER);

        setContentPane(root);
        
        applyPermissionsFromDB();
        
        showCard("Dashboard");
    }

    private void applyPermissionsFromDB() {
        if (SessionManager.currentUser == null) return;
        String roleCode = SessionManager.currentUser.getQuyen();
        if (roleCode.equals("AD")) return;

        ArrayList<String> permittedMenus = new ArrayList<>();
        permittedMenus.add("dashboard"); 

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT c.tenCN FROM phanquyen p " +
                    "JOIN chucnang c ON p.MACN = c.MACN " +
                    "WHERE p.MAQUYEN = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, roleCode);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                permittedMenus.add(rs.getString("tenCN").toLowerCase().trim());
            }
        } catch (Exception e) { e.printStackTrace(); }
        
        if (!roleCode.equals("M")) {
            permittedMenus.remove("tài khoản");
            permittedMenus.remove("phân quyền");
            permittedMenus.remove("nhật ký");
        }
        
        filterMenu(permittedMenus);
    }

    private void filterMenu(ArrayList<String> allowedMenus) {
        for (Map.Entry<String, NavItem> entry : navItems.entrySet()) {
            String key = entry.getKey().toLowerCase().trim();
            NavItem item = entry.getValue();

            if (!allowedMenus.contains(key)) {
                item.setVisible(false);
            } else {
                item.setVisible(true); 
            }
        }

        if (menuContainer != null) {
            menuContainer.revalidate();
            menuContainer.repaint();
        }
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
        titleBar.setPreferredSize(new Dimension(0, 55));
        titleBar.setBorder(new EmptyBorder(0, 20, 0, 30));

        JPanel leftWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 15)); 
        leftWrapper.setOpaque(false);
        
        JPanel titlePanel = UIUtils.createTitlePanel("src/main/java/resources/cart.png", "PHONE SHOP NHÓM 4", 16, Color.WHITE);
        leftWrapper.add(titlePanel);
        
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        right.setOpaque(false);

        UIUtils.RoundedPanel userPill = new UIUtils.RoundedPanel(30, Color.decode("#1E293B"));
        userPill.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 3));
        userPill.setBorder(new EmptyBorder(0, 5, 0, 15));
        UIUtils.RoundedPanel avatar = new UIUtils.RoundedPanel(30, SIDEBAR_ACTIVE);
        avatar.setPreferredSize(new Dimension(30, 30));

        try {
            File avatarFile = new File("src/main/java/resources/human.png"); 
            if (avatarFile.exists()) {
                Image img = new ImageIcon(avatarFile.getAbsolutePath()).getImage();
                avatar.setImage(img);
            } else {
                JLabel avTxt = new JLabel(currentUsername.substring(0, 1).toUpperCase(), SwingConstants.CENTER);
                avTxt.setForeground(Color.WHITE);
                avTxt.setFont(new Font("Segoe UI", Font.BOLD, 14));
                avatar.add(avTxt, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        JLabel userInfo = new JLabel("<html><b style='color:white;'>" + currentUsername + "</b><br><font size='2' color='#94A3B8'>" + currentRole + "</font></html>");
        userPill.add(avatar);
        userPill.add(userInfo);
        right.add(userPill);
        
        JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setToolTipText("Đăng xuất hệ thống");
        btnLogout.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "" +
                "arc: 10;" +                
                "background: #ef4444;" +    
                "foreground: #ffffff;" +    
                "borderWidth: 0;" +
                "focusWidth: 0;");
        btnLogout.addActionListener(e -> performLogout());
        right.add(btnLogout);
        
        titleBar.add(leftWrapper, BorderLayout.WEST);
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

        JLabel saleLabel = new JLabel("HỆ THỐNG BÁN HÀNG");
        saleLabel.setFont(new Font("Montserrat", Font.BOLD, 14));
        saleLabel.setForeground(TEXT_MUTED);
        saleLabel.setBorder(new EmptyBorder(15, 0, 10, 10));
        saleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        saleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        container.add(saleLabel);

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

        JLabel navLabel = new JLabel("DANH MỤC QUẢN LÝ");
        navLabel.setFont(new Font("Montserrat", Font.BOLD, 14));
        navLabel.setForeground(TEXT_MUTED);
        navLabel.setBorder(new EmptyBorder(15, 0, 10, 10));
        navLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        navLabel.setHorizontalAlignment(SwingConstants.CENTER);
        container.add(navLabel);

        menuContainer = new JPanel();
        menuContainer.setLayout(new BoxLayout(menuContainer, BoxLayout.Y_AXIS));
        menuContainer.setOpaque(false);
        menuContainer.setBorder(new EmptyBorder(0, 10, 10, 10));

        String[][] managementItems = {
            {"Dashboard", "📊 Dashboard"},
            {"Sản phẩm", "📦 Sản phẩm"},
            {"Hình ảnh SP", "🖼️ Hình ảnh SP"},
            {"Chi tiết SP", "📝 Chi tiết SP"},
            {"Loại SP", "🏷️ Loại SP"},
            {"Khách hàng", "👥 Khách hàng"},
            {"Nhân viên", "🆔 Nhân viên"},
            {"Kho", "🗄️ Kho"},
            {"Nhập hàng", "📥 Nhập hàng"},
            {"Nhà cung cấp", "🏭 Nhà cung cấp"},
            {"Bảo hành", "🛠️ Bảo hành"},
            {"Khuyến mãi", "🎁 Khuyến mãi"},
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
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setWheelScrollingEnabled(false);
        
        sidebar.add(scroll, BorderLayout.CENTER);

        return sidebar;
    }

    private JPanel buildMainContent() {
        JPanel main = new JPanel(new BorderLayout());
        main.setOpaque(false);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG_APP);
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.decode("#E2E8F0")));

        pageTitleLabel = new JLabel("Dashboard");
        pageTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        pageTitleLabel.setForeground(TEXT_MAIN);
        pageTitleLabel.setBorder(new EmptyBorder(0, 25, 0, 0));
        header.add(pageTitleLabel, BorderLayout.WEST);

        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        main.add(header, BorderLayout.NORTH);
        main.add(contentPanel, BorderLayout.CENTER);
        return main;
    }

    private JPanel createPanelByName(String name) {
        switch (name) {
            case "Dashboard": return new DashboardPanel();
            case "Khuyến mãi": return new KhuyenMaiPanel();
            case "Kho": return new KhoPanel();
            case "Tài khoản": return new TaiKhoanUI();
            case "Phân quyền": return new PhanQuyenUI();
            case "Sản phẩm": return new SanPhamPanel();
            case "Nhập hàng": return new PNPanel();
            case "Nhà cung cấp": return new NCCPanel();
            case "Bán hàng": return new GiaoDienBanHang();
            case "Bảo hành": return new BaoHanhPanel();
            case "Nhật ký": return new LogPanel("src/main/java/resources/file.png");
            case "Hình ảnh SP": return new AnhSPPanel();
            case "Loại SP": return new LoaiPanel();
            case "Chi tiết SP": return new CTSPPanel();
            case "Khách hàng": return new GiaoDienKhachHang();
            case "Nhân viên": return new GiaoDienNhanVien();
            default:
                JPanel devPanel = new JPanel(new GridBagLayout());
                devPanel.setBackground(Color.WHITE);
                devPanel.add(new JLabel("Đang phát triển chức năng: " + name));
                return devPanel;
        }
    }

    public void showCard(String name) {
        navItems.forEach((k, v) -> v.setActive(k.equals(name)));
        if (pageTitleLabel != null) pageTitleLabel.setText(name);

        boolean keepCache = name.equals("Kho") || name.equals("Dashboard");

        if (keepCache) {
            if (!cachedPanels.containsKey(name)) {
                JPanel panel = createPanelByName(name);
                cachedPanels.put(name, panel);
                contentPanel.add(panel, name);
            } else {
                // SỬA LỖI KHÔNG LÀM MỚI KHI CLICK TAB KHO:
                // Nếu panel Kho đã có trong cache, gọi hàm reloadData() để tải lại data mới nhất
                JPanel existingPanel = cachedPanels.get(name);
                if (name.equals("Kho") && existingPanel instanceof KhoPanel) {
                    ((KhoPanel) existingPanel).reloadData();
                }
            }
        } else {
            if (cachedPanels.containsKey(name)) {
                contentPanel.remove(cachedPanels.get(name));
                cachedPanels.remove(name); 
            }
            JPanel newPanel = createPanelByName(name);
            cachedPanels.put(name, newPanel);
            contentPanel.add(newPanel, name);
        }

        cardLayout.show(contentPanel, name);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public JPanel getContentPanel() {
        return contentPanel;
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

            String emoji = textWithEmoji.substring(0, 2);
            String text = textWithEmoji.substring(2).trim();

            labelIcon = new JLabel(emoji);
            labelIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
            labelIcon.setForeground(TEXT_MAIN);
            labelIcon.setBorder(new EmptyBorder(0, 15, 0, 0));

            labelText = new JLabel(text);
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
    
    private void performLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this, 
                "Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?", 
                "Xác nhận đăng xuất", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            Utility.SessionManager.currentUser = null;
            Utility.RolePermission.clear(); 
            Utility.RolePermission.set(null, null);

            this.dispose();

            java.awt.EventQueue.invokeLater(() -> {
                new LoginFrame().setVisible(true);
            });
        }
    }
}