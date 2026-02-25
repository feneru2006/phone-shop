package BUS;

import DAL.DAO.AccountDAO;
import DAL.DAO.PhanQuyenDAO;
import DTO.accountDTO;
import Utility.RolePermission;
import java.util.HashSet;
import java.util.Set;
import java.util.ArrayList;

public class AuthService {

    private final AccountDAO accountDAO = new AccountDAO();
    private final PhanQuyenDAO phanQuyenDAO = new PhanQuyenDAO();

    public accountDTO login(String username, String password) {
        // Kiểm đầu vào
        if (username == null || username.isBlank()) return null;
        if (password == null) return null;

        // Tìm tk DAO
        accountDTO user = accountDAO.findByUsername(username.trim());
        if (user == null) return null;

        // Kt pass dùng user.getPass() từ accountDTO
        if (!user.getPass().equals(password)) {
            return null;
        }

        // Lấy danh sách quyền dùng user.getQuyen() từ accountDTO
        ArrayList<String> listPerms = phanQuyenDAO.getChucNangByMaQuyen(user.getQuyen());
        Set<String> permsSet = new HashSet<>(listPerms);

        // Nạp vào hệ thống để dùng cho Kiểm tra quyền và Ghi Log
        RolePermission.set(user, permsSet);

        return user;
    }

    public void logout() {
        RolePermission.clear();
    }
}