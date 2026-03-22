package UI;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatLightLaf;
import BUS.AuthService;
import DTO.accountDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private final AuthService authService = new AuthService();

    // --- BẢNG MÀU CHUẨN ---
    private static final Color BG_DARK = Color.decode("#0F172A");    // Nền tối đậm (Bên trái)
    private static final Color ACCENT_BLUE = Color.decode("#2563EB"); // Xanh dương nút bấm
    private static final Color TEXT_LABEL = Color.decode("#1E293B");  // Màu chữ chính
    private static final Color TEXT_SUB = Color.decode("#64748B");    // Màu chữ phụ

    public LoginFrame() {
        setupLookAndFeel();
        initUI();
    }

    private void setupLookAndFeel() {
        try {
            FlatLightLaf.setup();
            UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 14));
            UIManager.put("Button.arc", 15); // Bo góc nút
            UIManager.put("Component.arc", 15); // Bo góc ô text
            UIManager.put("TextComponent.arc", 15);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initUI() {
        setTitle("Đăng nhập - Phone Shop Nhóm 4");
        setSize(850, 500); // Kích thước rộng hơn để chia đôi màn hình
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel chính chia làm 2 cột
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // ==========================================
        // CỘT TRÁI: BRANDING (THƯƠNG HIỆU)
        // ==========================================
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(BG_DARK);
        
        GridBagConstraints gbcLeft = new GridBagConstraints();
        gbcLeft.gridx = 0;
        gbcLeft.gridy = GridBagConstraints.RELATIVE;
        gbcLeft.insets = new Insets(10, 0, 10, 0);
        gbcLeft.anchor = GridBagConstraints.CENTER;

        // Có thể thay thế icon biểu tượng bằng hình ảnh thật (ImageIcon)
        JLabel lblLogo = new JLabel("📱");
        lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        lblLogo.setForeground(Color.WHITE);
        
        JLabel lblShopName = new JLabel("PHONE SHOP NHÓM 4");
        lblShopName.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblShopName.setForeground(Color.WHITE);
        
        JLabel lblSlogan = new JLabel("Hệ thống quản lý cửa hàng điện thoại");
        lblSlogan.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSlogan.setForeground(new Color(255, 255, 255, 180));

        leftPanel.add(lblLogo, gbcLeft);
        leftPanel.add(lblShopName, gbcLeft);
        leftPanel.add(lblSlogan, gbcLeft);


        // ==========================================
        // CỘT PHẢI: FORM ĐĂNG NHẬP
        // ==========================================
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(40, 50, 40, 50));

        // Tiêu đề form
        JLabel lblTitle = new JLabel("Chào mừng trở lại!");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(TEXT_LABEL);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubTitle = new JLabel("Vui lòng đăng nhập vào tài khoản của bạn.");
        lblSubTitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubTitle.setForeground(TEXT_SUB);
        lblSubTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblSubTitle.setBorder(new EmptyBorder(5, 0, 30, 0));

        // Tên đăng nhập
        JLabel lblUser = new JLabel("Tên đăng nhập");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUser.setForeground(TEXT_LABEL);
        
        txtUsername = new JTextField();
        txtUsername.setPreferredSize(new Dimension(300, 40));
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        // FlatLaf Properties: Thêm chữ mờ và nút xóa nhanh (Clear button)
        txtUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập tài khoản của bạn...");
        txtUsername.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);

        // Mật khẩu
        JLabel lblPass = new JLabel("Mật khẩu");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPass.setForeground(TEXT_LABEL);
        lblPass.setBorder(new EmptyBorder(15, 0, 0, 0)); // Tạo khoảng cách với ô trên
        
        txtPassword = new JPasswordField();
        txtPassword.setPreferredSize(new Dimension(300, 40));
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        // FlatLaf Properties: Thêm nút con mắt hiện/ẩn mật khẩu
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Nhập mật khẩu...");
        txtPassword.putClientProperty(FlatClientProperties.STYLE, "showRevealButton: true");

        // Nút đăng nhập
        btnLogin = new JButton("ĐĂNG NHẬP");
        btnLogin.setBackground(ACCENT_BLUE);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setPreferredSize(new Dimension(300, 45));
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(this::handleLogin);
        // Bỏ viền focus khi click
        btnLogin.setFocusPainted(false); 

        // Ráp các thành phần vào form
        formPanel.add(lblTitle);
        formPanel.add(lblSubTitle);
        
        formPanel.add(lblUser);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(txtUsername);
        
        formPanel.add(lblPass);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(txtPassword);
        
        formPanel.add(Box.createVerticalStrut(30));
        formPanel.add(btnLogin);

        rightPanel.add(formPanel);

        // ==========================================
        // THÊM VÀO MAIN PANEL
        // ==========================================
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        setContentPane(mainPanel);
    }

    private void handleLogin(ActionEvent e) {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        accountDTO account = authService.login(user, pass);
        if (account != null) {
            //gán dữ liệu này TRƯỚC khi khởi tạo MainFrameTest
            Utility.SessionManager.currentUser = account;
            // Sau khi đã có thông tin người dùng trong bộ nhớ, mới mở giao diện chính
            new MainFrameTest(account.getTen(), account.getQuyen()).setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!", "Lỗi đăng nhập", JOptionPane.ERROR_MESSAGE);
        }
    }
}