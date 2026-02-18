package DTO;
public class CTHDDTO {
    private String maCTHD; // Trong CSDL bạn đặt là MACTHD
    private String maHD;
    private String maCTSP;
    private double donGia;
    private double thanhTien;

    public CTHDDTO() {}

    public CTHDDTO(String maCTHD, String maHD, String maCTSP, double donGia, double thanhTien) {
        this.maCTHD = maCTHD;
        this.maHD = maHD;
        this.maCTSP = maCTSP;
        this.donGia = donGia;
        this.thanhTien = thanhTien;
    }

    public String getMaCTHD() { return maCTHD; }
    public void setMaCTHD(String maCTHD) { this.maCTHD = maCTHD; }

    public String getMaHD() { return maHD; }
    public void setMaHD(String maHD) { this.maHD = maHD; }

    public String getMaCTSP() { return maCTSP; }
    public void setMaCTSP(String maCTSP) { this.maCTSP = maCTSP; }

    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) { this.donGia = donGia; }

    public double getThanhTien() { return thanhTien; }
    public void setThanhTien(double thanhTien) { this.thanhTien = thanhTien; }

}