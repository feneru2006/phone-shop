package DTO;

public class SanPhamDTO {
    private String maSP;
    private String tenSP;
    private int slTon;
    private double gia;
    private String trangThai;
    private String maLoai;
    private String cauHinh;
    private String nsx;
    private boolean isDeleted; 
    private double phanTramLoiNhuan;
    
    public SanPhamDTO() {}

    public SanPhamDTO(String maSP, String tenSP, int slTon, double gia, String trangThai, String maLoai, String cauHinh, String nsx, boolean isDeleted, double phanTramLoiNhuan) {
        this.maSP = maSP;
        this.tenSP = tenSP;
        this.slTon = slTon;
        this.gia = gia;
        this.trangThai = trangThai;
        this.maLoai = maLoai;
        this.cauHinh = cauHinh;
        this.nsx = nsx;
        this.isDeleted = isDeleted;
        this.phanTramLoiNhuan = phanTramLoiNhuan;
    }

    public String getMaSP() { return maSP; }
    public void setMaSP(String maSP) { this.maSP = maSP; }

    public String getTenSP() { return tenSP; }
    public void setTenSP(String tenSP) { this.tenSP = tenSP; }

    public int getSlTon() { return slTon; }
    public void setSlTon(int slTon) { this.slTon = slTon; }

    public double getGia() { return gia; }
    public void setGia(double gia) { this.gia = gia; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getMaLoai() { return maLoai; }
    public void setMaLoai(String maLoai) { this.maLoai = maLoai; }

    public String getCauHinh() { return cauHinh; }
    public void setCauHinh(String cauHinh) { this.cauHinh = cauHinh; }

    public String getNsx() { return nsx; }
    public void setNsx(String nsx) { this.nsx = nsx; }

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean isDeleted) { this.isDeleted = isDeleted; }

    public double getPhanTramLoiNhuan() { return phanTramLoiNhuan; }
    public void setPhanTramLoiNhuan(double phanTramLoiNhuan) { this.phanTramLoiNhuan = phanTramLoiNhuan; }

    public double getGiaBan() {
        return this.gia + (this.gia * this.phanTramLoiNhuan / 100);
    }
}