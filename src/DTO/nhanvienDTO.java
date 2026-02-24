package DTO;
import java.util.*; 
import java.util.Objects;
public class nhanvienDTO {
    private String maNV;
    private String hoTen;
    private String gioiTinh;
    private String sdt;
    private String diaChi;
    private double thamNien;
    private double luong;
    private boolean trangthai; 

    public nhanvienDTO() {
    }
    public nhanvienDTO(String maNV, String hoTen, String gioiTinh, String sdt, String diaChi, double thamNien, double luong, boolean trangthai) {
        this.maNV = maNV;
        this.hoTen = hoTen;
        this.gioiTinh = gioiTinh;
        this.sdt = sdt;
        this.diaChi = diaChi;
        this.thamNien = thamNien;
        this.luong = luong;
        this.trangthai = trangthai; 
    }

    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public double getThamNien() { return thamNien; }
    public void setThamNien(double thamNien) { this.thamNien = thamNien; }

    public double getLuong() { return luong; }
    public void setLuong(double luong) { this.luong = luong; }

    public boolean istrangthai() { return trangthai; }
    public void settrangthai(boolean trangthai) { this.trangthai = trangthai; }

}
