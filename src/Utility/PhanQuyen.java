package Utility;

import javax.swing.JComponent;

public class PhanQuyen {
    //kiểm tra người dùng có quyền hay không
    public static boolean has(String permissionCode) {
        return RolePermission.hasPerms(permissionCode);
    }

    // tự động ẩn hiện nút/menu dựa trên quyền
    public static void checkAndApply(JComponent component, String permissionCode) {
        if (has(permissionCode)) {
            component.setVisible(true);
        } else {
            component.setVisible(false);
        }
    }

    //kiểm tra quyền trước khi dùng
    public static void throwIfNotAllowed(String permissionCode) throws SecurityException {
        if (!has(permissionCode)) {
            throw new SecurityException("Bạn không có quyền thực hiện hành động này!");
        }
    }
}