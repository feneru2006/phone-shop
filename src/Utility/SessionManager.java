package Utility;

import DTO.accountDTO;

public class SessionManager {
    // Lưu thông tin người dùng đang đăng nhập toàn hệ thống
    public static accountDTO currentUser = null;

    // Hàm tiện ích kiểm tra nhanh quyền
    public static boolean isAdmin() {
        return currentUser != null && "AD".equals(currentUser.getQuyen());
    }

    public static String getRole() {
        return (currentUser != null) ? currentUser.getQuyen() : "";
    }
}