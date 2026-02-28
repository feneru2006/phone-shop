package test1;

import com.formdev.flatlaf.FlatLightLaf;
import DTO.accountDTO;
import Utility.RolePermission;


import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Main {
    // Để TRUE để vào thẳng giao diện chính, FALSE để hiện màn hình đăng nhập
    private static final boolean DEBUG_MODE = true;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // 1. Thiết lập giao diện FlatLaf hiện đại
                FlatLightLaf.setup();

                // 2. Cấu hình bo góc và font chữ đồng bộ (arc = 12)
                UIManager.put("defaultFont", new Font("Segoe UI", Font.PLAIN, 13));
                UIManager.put("Component.arc", 12);
                UIManager.put("Button.arc", 12);
                UIManager.put("TextComponent.arc", 12);
                UIManager.put("Component.focusWidth", 0);
                UIManager.put("Button.boldText", false);
                UIManager.put("TextField.margin", new Insets(8, 10, 8, 10));
                UIManager.put("PasswordField.margin", new Insets(8, 10, 8, 10));

            } catch (Exception ex) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignored) {}
            }

            if (DEBUG_MODE) {
                // TẠO ADMIN GIẢ LẬP TRỰC TIẾP
                accountDTO administrator = new accountDTO();
                
                // Lưu ý: Các hàm set này phải khớp với biến trong file accountDTO của bạn
                administrator.setTen("Admin_Debug"); 
                administrator.setPass("admin123");
                administrator.setQuyen("AD");
                administrator.setId("0"); // ID giả định

                // Cấp toàn bộ quyền để hiển thị đầy đủ Menu trên MainFrameTest
                Set<String> fullPerms = new HashSet<>(Arrays.asList(
                    "Dashboard", "Sản phẩm", "Nhân viên", "Khách hàng", "Bảo hành", "Hóa đơn"
                ));

                // Nạp vào hệ thống RolePermission để các trang khác có thể dùng
                RolePermission.set(administrator, fullPerms);

                // Mở giao diện chính
                MainFrameTest main = new MainFrameTest();
                main.setVisible(true);
                
                System.out.println("Đang chạy ở chế độ DEBUG: Đã bỏ qua đăng nhập.");
            } else {
                // Chạy luồng đăng nhập bình thường
                new LoginFrame().setVisible(true);
            }
        });
    }
}