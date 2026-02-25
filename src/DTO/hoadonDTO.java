package DTO;
import java.time.LocalDateTime;

public class hoadonDTO {
    private String maHD;
    private String maNV;
    private String maKH;
    private LocalDateTime ngayLap;
    private double tongTien;

    public hoadonDTO() {}

    public hoadonDTO(String maHD, String maNV, String maKH, LocalDateTime ngayLap, double tongTien) {
        this.maHD = maHD;
        this.maNV = maNV;
        this.maKH = maKH;
        this.ngayLap = ngayLap;
        this.tongTien = tongTien;
    }

    public String getMaHD() { return maHD; }
    public void setMaHD(String maHD) { this.maHD = maHD; }

    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }

    public String getMaKH() { return maKH; }
    public void setMaKH(String maKH) { this.maKH = maKH; }

    public LocalDateTime getNgayLap() { return ngayLap; }
    public void setNgayLap(LocalDateTime ngayLap) { this.ngayLap = ngayLap; }

    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }


}
