package UI;

import com.formdev.flatlaf.FlatLightLaf;

import UI.Panel.DashboardPanel;
import UI.Panel.Sales.KhuyenMaiPanel;
import UI.Panel.KhoPanel;
import UI.Panel.LogPanel;
import UI.Panel.BaoHanh.BaoHanhPanel;
import UI.Panel.NCC.NCCPanel;
import UI.Panel.PN.PNPanel;
import UI.Utils.UIUtils; // Import UIUtils
import Utility.SessionManager;
import Utility.PhanQuyen;
import DAL.DAO.DBConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.apache.poi.ss.formula.functions.Log;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
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
    private JLabel pageTitleLabel;
    private JPanel menuContainer;
    // Biến lưu trữ thông tin tài khoản đang đăng nhập
    private String currentUsername;
    private String currentRole;

    // --- MÀU SẮC THEME ---
    private static final Color HEADER_BG = Color.decode("#3a5170");
    private static final Color BG_APP = Color.decode("#30475e");
    private static final Color SIDEBAR_BG = Color.decode("#2C3E50");
    private static final Color SIDEBAR_ACTIVE = Color.decode("#2563EB");
    private static final Color TEXT_MAIN = Color.decode("#f0faff");
    private static final Color TEXT_MUTED = Color.decode("#cbffc7");

    // Constructor cập nhật để nhận tài khoản
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
        initCards();
        
        DashboardPanel dashboard = new DashboardPanel();
        contentPanel.add(dashboard, "Dashboard");
        KhuyenMaiPanel giamgia = new KhuyenMaiPanel();
        contentPanel.add(giamgia,"Khuyến mãi");
        KhoPanel khoPanel = new KhoPanel();
        contentPanel.add(khoPanel, "Kho");
        TaiKhoanUI taiKhoanUI = new TaiKhoanUI();
        contentPanel.add(taiKhoanUI, "Tài khoản");
        PhanQuyenUI phanQuyenUI = new PhanQuyenUI();
        contentPanel.add(phanQuyenUI, "Phân quyền");
        SanPhamPanel sanPhamPanel = new SanPhamPanel();
        contentPanel.add(sanPhamPanel, "Sản phẩm");
        PNPanel pnPanel = new PNPanel();
        contentPanel.add(pnPanel, "Nhập hàng");
        NCCPanel nccPanel = new NCCPanel();
        contentPanel.add(nccPanel, "Nhà cung cấp");
        GiaoDienBanHang banHangPanel = new GiaoDienBanHang();
        contentPanel.add(banHangPanel, "Bán hàng");
        BaoHanhPanel baoHanhPanel = new BaoHanhPanel();
        contentPanel.add(baoHanhPanel, "Bảo hành");
        contentPanel.add(new LogPanel("src/main/java/resources/file.png"),"Nhật ký");
        contentPanel.add(new AnhSPPanel(),"Hình ảnh SP");
        contentPanel.add(new LoaiPanel(),"Loại SP");
        contentPanel.add(new CTSPPanel(),"Chi tiết SP");
        // LogPanel logPanel = new LogPanel();
        // contentPanel.add(logPanel, "Log");
        applyPermissionsFromDB();
        showCard("Dashboard");
    }

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

        // Trái: Logo
        // Bọc vào panel có FlowLayout padding để căn giữa trục Y đều với thanh tiêu đề
        JPanel leftWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 15)); 
        leftWrapper.setOpaque(false);
        
        JPanel titlePanel = UIUtils.createTitlePanel("src/main/java/resources/cart.png", "PHONE SHOP NHÓM 4", 16, Color.WHITE);
        leftWrapper.add(titlePanel);
        
        // Phải: User Pill bo tròn
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        right.setOpaque(false);

        UIUtils.RoundedPanel userPill = new UIUtils.RoundedPanel(30, Color.decode("#1E293B"));
        userPill.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 3));
        userPill.setBorder(new EmptyBorder(0, 5, 0, 15));
        UIUtils.RoundedPanel avatar = new UIUtils.RoundedPanel(30, SIDEBAR_ACTIVE);
        avatar.setPreferredSize(new Dimension(30, 30));

        try {
            File avatarFile = new File("src/main/java/resources/human.png"); // Thay bằng ảnh avatar thực tế
            if (avatarFile.exists()) {
                Image img = new ImageIcon(avatarFile.getAbsolutePath()).getImage();
                avatar.setImage(img);
            } else {
                // Fallback: Nếu không tìm thấy ảnh thì hiển thị chữ cái đầu tiên của Username
                JLabel avTxt = new JLabel(currentUsername.substring(0, 1).toUpperCase(), SwingConstants.CENTER);
                avTxt.setForeground(Color.WHITE);
                avTxt.setFont(new Font("Segoe UI", Font.BOLD, 14));
                avatar.add(avTxt, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Truyền thông tin tài khoản đang đăng nhập
        JLabel userInfo = new JLabel("<html><b style='color:white;'>" + currentUsername + "</b><br><font size='2' color='#94A3B8'>" + currentRole + "</font></html>");
        userPill.add(avatar);
        userPill.add(userInfo);
        right.add(userPill);
                JButton btnLogout = new JButton("Đăng xuất");
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setToolTipText("Đăng xuất hệ thống");
        btnLogout.putClientProperty(com.formdev.flatlaf.FlatClientProperties.STYLE, "" +
                "arc: 10;" +                // Bo góc
                "background: #ef4444;" +    // Màu đỏ nhạt (Tailwind Red 500)
                "foreground: #ffffff;" +    // Chữ trắng
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

        JPanel menuContainer = new JPanel();
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
        // Khóa thanh cuộn
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // Chặn luôn con lăn chuột
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

    private void initCards() {
        for (String key : navItems.keySet()) {
            if (key.equals("Dashboard") || key.equals("Khuyến mãi") || key.equals("Kho") ||
             key.equals("Sản phẩm") || key.equals("Nhập hàng") || key.equals("Nhà cung cấp") ||
              key.equals("Bán hàng")
             || key.equals("Bảo hành") || key.equals("Nhật ký") ||  key.equals("Hình ảnh SP")
            || key.equals("Loại SP")) continue; 

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
        // 1. Hiển thị thông báo xác nhận bằng tiếng Việt
        int confirm = JOptionPane.showConfirmDialog(
                this, 
                "Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?", 
                "Xác nhận đăng xuất", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            // 2. Xóa dữ liệu trong SessionManager và RolePermission (Các file bạn đã cung cấp)
            Utility.SessionManager.currentUser = null;
            Utility.RolePermission.clear(); 
            Utility.RolePermission.set(null, null);

            // 3. Đóng cửa sổ hiện tại (MainFrameTest)
            this.dispose();

            // 4. Mở lại màn hình Đăng nhập (LoginFrame)
            java.awt.EventQueue.invokeLater(() -> {
                new LoginFrame().setVisible(true);
            });
        }
    }
}
