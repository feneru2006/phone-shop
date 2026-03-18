package UI.Utils;


import javax.swing.*;
import java.awt.*;
import java.io.File;

public class UIUtils {

    public static JPanel createTitlePanel(String iconPath, String text, int fontSize, Color fgColor) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setOpaque(false);
        
        JLabel lbIcon = new JLabel();
        try {
            File file = new File(iconPath);
            if (file.exists()) {
                ImageIcon originalIcon = new ImageIcon(file.getAbsolutePath());
                // Scale ảnh to hơn font chữ một xíu để cân đối với text
                Image scaledImage = originalIcon.getImage().getScaledInstance(fontSize + 6, fontSize + 6, Image.SCALE_SMOOTH);
                lbIcon.setIcon(new ImageIcon(scaledImage));
            } else {
                // Fallback nếu không tìm thấy file ảnh
                lbIcon.setText("❖");
                lbIcon.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
                lbIcon.setForeground(fgColor);
            }
        } catch (Exception e) {
            System.err.println("Lỗi load icon: " + iconPath);
        }
        
        JLabel lbText = new JLabel(text);
        lbText.setFont(new Font("Segoe UI", Font.BOLD, fontSize));
        lbText.setForeground(fgColor);
        
        panel.add(lbIcon);
        panel.add(lbText);
        return panel;
    }
}