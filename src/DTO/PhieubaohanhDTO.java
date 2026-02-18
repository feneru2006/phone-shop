package DTO;
import java.time.LocalDate;

public class PhieubaohanhDTO {
    private String maBH;
    private String maCTHD;
    private String Trangthai;
    private String maKH;
    private LocalDate ngayBD;
    private int thoiHan;

    public PhieubaohanhDTO(String maBH, String maCTHD, String Trangthai, String maKH, LocalDate ngayBD, int thoiHan) {
        this.maBH = maBH;
        this.maCTHD = maCTHD;
        this.Trangthai = Trangthai;
        this.maKH = maKH;
        this.ngayBD = ngayBD;
        this.thoiHan = thoiHan;
    }
    public PhieubaohanhDTO() {}

    public String getMaBH() { return maBH; }
    public void setMaBH(String maBH) { this.maBH = maBH; }

    public String getMaCTHD() {
        return maCTHD;
    }

    public void setMaCTHD(String maCTHD) {
        this.maCTHD = maCTHD;
    }

    public String getTrangthai() {
        return Trangthai;
    }

    public void setTrangthai(String Trangthai) {
        this.Trangthai = Trangthai;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public LocalDate getNgayBD() {
        return ngayBD;
    }

    public void setNgayBD(LocalDate ngayBD) {
        this.ngayBD = ngayBD;
    }

    public int getThoiHan() {
        return thoiHan;
    }

    public void setThoiHan(int thoiHan) {
        this.thoiHan = thoiHan;
    }

}