package DTO;
import java.time.LocalDate;
public class phieunhapDTO {
    private String maPNH;
    private String maNV;
    private LocalDate ngayNhap;
    private double tongTien;
    private String maNCC;

    public phieunhapDTO() {}

    public phieunhapDTO(String maPNH, String maNV, LocalDate ngayNhap, double tongTien, String maNCC) {
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

    public LocalDate getNgayNhap() { return ngayNhap; }
    public void setNgayNhap(LocalDate ngayNhap) { this.ngayNhap = ngayNhap; }

    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }

    public String getMaNCC() { return maNCC; }
    public void setMaNCC(String maNCC) { this.maNCC = maNCC; }

}
