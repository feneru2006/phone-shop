package BUS;

import DAL.DAO.LogDAO;
import DTO.logDTO;
import Utility.RolePermission;

// Import thư viện Log4j
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.LocalDateTime;
import java.util.List;

public class LogBUS {
    private LogDAO logDAO = new LogDAO();
    
    // Khởi tạo đối tượng Logger
    private static final Logger logger = LogManager.getLogger(LogBUS.class);

    public List<logDTO> getAll() {
        return logDAO.getAll();
    }

    private String taoMaLogTuDong() {
        String lastMa = logDAO.getLastMaLog();
        if (lastMa == null || lastMa.isEmpty()) {
            return "LOG0001";
        }
        try {
            int so = Integer.parseInt(lastMa.substring(3)) + 1;
            return String.format("LOG%04d", so);
        } catch (Exception e) {
            return "LOG" + System.currentTimeMillis(); 
        }
    }

    public void ghiNhatKy(String hanhvi, String thucthe, String chitiethv) {
        String accountId = "SYSTEM"; 
        
        if (RolePermission.getCurrentUser() != null) {
            accountId = RolePermission.getCurrentUser().getId();
        }

        // 1. SỬ DỤNG LOG4J ĐỂ GHI RA FILE TXT
        logger.info("Tài khoản: {} | Hành vi: {} | Thực thể: {} | Chi Tiết: {}", accountId, hanhvi, thucthe, chitiethv);

        // 2. GHI VÀO DATABASE BẰNG DAO NHƯ CŨ
        logDTO log = new logDTO(
                taoMaLogTuDong(),
                accountId,
                hanhvi,
                thucthe,
                chitiethv,
                LocalDateTime.now()
        );
        logDAO.insertLog(log);
    }
}