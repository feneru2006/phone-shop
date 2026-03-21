package Utility;

import DAL.DAO.DBConnection;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PhanQuyen {

    // 1. Kiểm tra quyền logic (Trả về true/false)
    public static boolean canDo(String screen, String action) {
        if (SessionManager.currentUser == null) return false;

        String role = SessionManager.currentUser.getQuyen();

        // 1. Admin (AD) là tối cao, thao tác gì ở trang nào cũng được
        if (role.equals("AD")) return true;

        // =================================================================
        // 2. LOGIC KIỂM TRA ĐỘNG TỪ DATABASE CHO QUYỀN TRUY CẬP TRANG (VIEW)
        // =================================================================
        boolean isScreenAllowed = false;
        try (Connection conn = DBConnection.getConnection()) {
            // Hỏi CSDL xem quyền (role) này có được cấp cho Chức năng (screen) này không
            String sql = "SELECT p.MACN FROM phanquyen p " +
                    "JOIN chucnang c ON p.MACN = c.MACN " +
                    "WHERE p.MAQUYEN = ? AND c.tenCN = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, role);
            ps.setString(2, screen);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                isScreenAllowed = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // [QUAN TRỌNG]: Nếu DB không cho phép truy cập Menu này -> CẤM TOÀN BỘ mọi thao tác
        if (!isScreenAllowed) {
            return false;
        }

        // Nếu chỉ đang yêu cầu XEM (VIEW) và CSDL đã cho phép ở trên -> OK
        if (action.equals("VIEW")) {
            return true;
        }

        // =================================================================
        // 3. LOGIC KIỂM TRA TĨNH CHO THAO TÁC (ADD, EDIT, DELETE)
        // =================================================================

        // Quản lý (M): Được làm tất cả, NHƯNG chặn Xóa các mục quan trọng
        if (role.equals("M")) {
            if (action.equals("DELETE") && (screen.equals("Sản phẩm") || screen.equals("Nhân viên"))) {
                return false;
            }
            return true;
        }

        // Thủ kho (WM)
        if (role.equals("WM")) {
            if (screen.equals("Sản phẩm") || screen.equals("Nhập hàng") || screen.equals("Loại SP")) {
                return !action.equals("DELETE"); // Được thêm/sửa, cấm xóa
            }
            // Nếu đã qua bước VIEW ở trên mà nhảy xuống tận đây ở các màn hình khác thì cấm thao tác
            return false;
        }

        // Nhân viên bán hàng (SA)
        if (role.equals("SA")) {
            if (screen.equals("Bán hàng") || screen.equals("Khách hàng")) {
                return !action.equals("DELETE"); // Được thêm/sửa, cấm xóa
            }
            return false;
        }

        return false;
    }

    // 2. Tự động ẩn/hiện thành phần giao diện (Nút bấm, Menu...)
    public static void checkAndApply(JComponent component, String screen, String action) {
        boolean hasPermission = canDo(screen, action);
        component.setVisible(hasPermission);
        // component.setEnabled(hasPermission);
    }

    // 3. Kiểm tra quyền trước khi thực hiện hành động (Dùng cho các nút quan trọng)
    public static void throwIfNotAllowed(String screen, String action) {
        if (!canDo(screen, action)) {
            JOptionPane.showMessageDialog(null,
                    "Bạn không có quyền thực hiện hành động [" + action + "] tại mục [" + screen + "]!",
                    "Từ chối truy cập",
                    JOptionPane.ERROR_MESSAGE);
            // Dừng luồng chạy nếu đang ném ra lỗi (bạn có thể thay bằng Exception tùy hệ thống)
            throw new RuntimeException("Access Denied");
        }
    }
}
