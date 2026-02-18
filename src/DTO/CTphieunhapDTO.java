package DTO;
public class CTphieunhapDTO {
    private String maCTPN;
    private String maPNH;
    private String maSP;
    private int sl;
    private double donGia;
    private double thanhTien;

    public CTphieunhapDTO() {}

    public CTphieunhapDTO(String maCTPN, String maPNH, String maSP, int sl, double donGia, double thanhTien) {
        this.maCTPN = maCTPN;
        this.maPNH = maPNH;
        this.maSP = maSP;
        this.sl = sl;
        this.donGia = donGia;
        this.thanhTien = thanhTien;
    }

    public String getMaCTPN() { return maCTPN; }
    public void setMaCTPN(String maCTPN) { this.maCTPN = maCTPN; }

    public String getMaPNH() { return maPNH; }
    public void setMaPNH(String maPNH) { this.maPNH = maPNH; }

    public String getMaSP() { return maSP; }
    public void setMaSP(String maSP) { this.maSP = maSP; }

    public int getSl() { return sl; }
    public void setSl(int sl) { this.sl = sl; }

    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) { this.donGia = donGia; }

    public double getThanhTien() { return thanhTien; }
    public void setThanhTien(double thanhTien) { this.thanhTien = thanhTien; }

}