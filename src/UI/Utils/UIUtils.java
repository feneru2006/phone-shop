package UI.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;

public class UIUtils {
    public static JPanel createTitlePanel(String iconPath, String text, int fontSize, Color fgColor) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); // Tăng khoảng cách giữa icon và chữ
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
    
    public static class RoundedPanel extends JPanel {
        private int radius;
        private Color bgColor;
        private Image image; // Thêm thuộc tính lưu ảnh để vẽ avatar

        public RoundedPanel(int radius, Color bgColor) {
            super(new BorderLayout());
            this.radius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
        }

        // Phương thức để gán ảnh vào panel
        public void setImage(Image image) {
            this.image = image;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Vẽ nền màu bo tròn
            g2.setColor(bgColor);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
            
            // Nếu có ảnh, cắt clip bo tròn và vẽ ảnh đè lên
            if (image != null) {
                g2.setClip(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));
                g2.drawImage(image, 0, 0, getWidth(), getHeight(), this);
            }
            
            g2.dispose();
            super.paintComponent(g);
        }
    }
}