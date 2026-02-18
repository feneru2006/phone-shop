package DTO;
import java.time.LocalDate;
public class logDTO {
    public String Malog;
    public String accountid; 
    public String hanhvi; 
    public String thucthe; 
    public String chitiethv; 
    public LocalDate thoidiem; 

    public logDTO(String Malog, String accountid, String hanhvi, String thucthe, String chitiethv, LocalDate thoidiem) {
        this.Malog = Malog;
        this.accountid = accountid;
        this.hanhvi = hanhvi;
        this.thucthe = thucthe;
        this.chitiethv = chitiethv;
        this.thoidiem = thoidiem;
    }
    
    public logDTO(){}

    public String getMalog() {
        return Malog;
    }

    public void setMalog(String Malog) {
        this.Malog = Malog;
    }

    public String getAccountid() {
        return accountid;
    }

    public void setAccountid(String accountid) {
        this.accountid = accountid;
    }

    public String getHanhvi() {
        return hanhvi;
    }

    public void setHanhvi(String hanhvi) {
        this.hanhvi = hanhvi;
    }

    public String getThucthe() {
        return thucthe;
    }

    public void setThucthe(String thucthe) {
        this.thucthe = thucthe;
    }

    public String getChitiethv() {
        return chitiethv;
    }

    public void setChitiethv(String chitiethv) {
        this.chitiethv = chitiethv;
    }

    public LocalDate getThoidiem() {
        return thoidiem;
    }

    public void setThoidiem(LocalDate thoidiem) {
        this.thoidiem = thoidiem;
    }
    
}
