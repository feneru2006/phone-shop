package DTO;

import java.time.LocalDateTime;

/**
 * Data Transfer Object cho bảng Nhật ký hệ thống (Log)
 */
public class logDTO {
    private String malog;
    private String accountid; 
    private String hanhvi; 
    private String thucthe; 
    private String chitiethv; 
    private LocalDateTime thoidiem; // Đổi từ LocalDate sang LocalDateTime

    public logDTO(String malog, String accountid, String hanhvi, String thucthe, String chitiethv, LocalDateTime thoidiem) {
        this.malog = malog;
        this.accountid = accountid;
        this.hanhvi = hanhvi;
        this.thucthe = thucthe;
        this.chitiethv = chitiethv;
        this.thoidiem = thoidiem;
    }
    
    public logDTO() {}

    /* ============================================================
       GETTERS & SETTERS
       ============================================================ */
    public String getMalog() { return malog; }
    public void setMalog(String malog) { this.malog = malog; }

    public String getAccountid() { return accountid; }
    public void setAccountid(String accountid) { this.accountid = accountid; }

    public String getHanhvi() { return hanhvi; }
    public void setHanhvi(String hanhvi) { this.hanhvi = hanhvi; }

    public String getThucthe() { return thucthe; }
    public void setThucthe(String thucthe) { this.thucthe = thucthe; }

    public String getChitiethv() { return chitiethv; }
    public void setChitiethv(String chitiethv) { this.chitiethv = chitiethv; }

    public LocalDateTime getThoidiem() { return thoidiem; }
    public void setThoidiem(LocalDateTime thoidiem) { this.thoidiem = thoidiem; }

    /**
     * Hỗ trợ in nhanh thông tin log để kiểm tra
     */
    @Override
    public String toString() {
        return "LogDTO{" + "hanhvi='" + hanhvi + '\'' + ", thoidiem=" + thoidiem + '}';
    }
}