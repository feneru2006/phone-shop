package BUS;

import DAL.DAO.LogDAO;
import DTO.logDTO;
import Utility.RolePermission;
import java.time.LocalDateTime;

public class LogBUS {

    private final LogDAO logDAO = new LogDAO();
    public void ghiLog(String hanhVi, String thucThe, String chiTietHV) {
        //du lieu dau vao
        String maLog = "L" + System.currentTimeMillis();

        String accountId = "GUEST";
        if (RolePermission.getCurrentUser() != null) {
            accountId = String.valueOf(RolePermission.getCurrentUser().getId());
        }

        LocalDateTime bayGio = LocalDateTime.now();

        //tao doi tuong dto
        logDTO log = new logDTO(maLog, accountId, hanhVi, thucThe, chiTietHV, bayGio);

        //dung DAO de luu
        try {
            logDAO.insertLog(log);
        } catch (Exception e) {
            //xuat loi ra console neu db co van de
            System.err.println("Loi ghi log: " + e.getMessage());
        }
    }
}