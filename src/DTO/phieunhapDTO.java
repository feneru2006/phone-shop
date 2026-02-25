package DTO;
import java.time.LocalDateTime;
public class phieunhapDTO {
    private String maPNH;
    private String maNV;
    private LocalDateTime ngayNhap;
    private double tongTien;
    private String maNCC;

    public phieunhapDTO() {}

    public phieunhapDTO(String maPNH, String maNV, LocalDateTime ngayNhap, double tongTien, String maNCC) {
        this.maPNH = maPNH;
        this.maNV = maNV;
        this.ngayNhap = ngayNhap;
        this.tongTien = tongTien;
        this.maNCC = maNCC;
    }

    public String getMaPNH() { return maPNH; }
    public void setMaPNH(String maPNH) { this.maPNH = maPNH; }

    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }

    public LocalDateTime getNgayNhap() { return ngayNhap; }
    public void setNgayNhap(LocalDateTime ngayNhap) { this.ngayNhap = ngayNhap; }

    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }

    public String getMaNCC() { return maNCC; }
    public void setMaNCC(String maNCC) { this.maNCC = maNCC; }

}
