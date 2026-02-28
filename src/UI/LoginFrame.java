package test1;


import com.formdev.flatlaf.FlatLightLaf;
import BUS.AuthService;
import DTO.accountDTO;
import Utility.Validator;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private final AuthService authService = new AuthService();

    public LoginFrame() {
        setupLookAndFeel();
        initUI();
    }

    private void setupLookAndFeel() {
        try {
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
        } catch (Exception ignored) {}
    }

    private void initUI() {
        setTitle("HỆ THỐNG QUẢN LÝ PHONE SHOP");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel lblTitle = new JLabel("ĐĂNG NHẬP");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        txtUsername = new JTextField();
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        txtPassword = new JPasswordField();
        txtPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        btnLogin = new JButton("ĐĂNG NHẬP");
        btnLogin.setBackground(Color.decode("#2563EB"));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnLogin.addActionListener(this::handleLogin);

        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(40));
        panel.add(new JLabel("Tên đăng nhập:"));
        panel.add(txtUsername);
        panel.add(Box.createVerticalStrut(20));
        panel.add(new JLabel("Mật khẩu:"));
        panel.add(txtPassword);
        panel.add(Box.createVerticalStrut(30));
        panel.add(btnLogin);

        setContentPane(panel);
    }

    private void handleLogin(ActionEvent e) {
        String user = txtUsername.getText().trim();
        String pass = new String(txtPassword.getPassword());

        if (Validator.isNullOrEmpty(user)) {
            JOptionPane.showMessageDialog(this, Validator.requiredMessage("Tên đăng nhập"));
            return;
        }

        accountDTO account = authService.login(user, pass);
        if (account != null) {
            new MainFrameTest().setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Tài khoản hoặc mật khẩu không chính xác!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}