package DTO;
import java.util.ArrayList;

public class DashboardDTO {
    private int tongSanPham;
    private double tongDoanhThu;
    private int tongNhanVien;
    private int tongKhachHang;
    private ArrayList<logDTO> danhSachLog;

    // Constructor mặc định (Bắt buộc phải có để dùng new DashboardDTO())
    public DashboardDTO() {
        this.danhSachLog = new ArrayList<>();
    }

    public DashboardDTO(int tongSanPham, double tongDoanhThu, int tongNhanVien, int tongKhachHang, ArrayList<logDTO> danhSachLog) {
        this.tongSanPham = tongSanPham;
        this.tongDoanhThu = tongDoanhThu;
        this.tongNhanVien = tongNhanVien;
        this.tongKhachHang = tongKhachHang;
        this.danhSachLog = danhSachLog;
    }

    // --- Getters và Setters ---
    public int getTongSanPham() { return tongSanPham; }
    public void setTongSanPham(int tongSanPham) { this.tongSanPham = tongSanPham; }

    public double getTongDoanhThu() { return tongDoanhThu; }
    public void setTongDoanhThu(double tongDoanhThu) { this.tongDoanhThu = tongDoanhThu; }

    public int getTongNhanVien() { return tongNhanVien; }
    public void setTongNhanVien(int tongNhanVien) { this.tongNhanVien = tongNhanVien; }

    public int getTongKhachHang() { return tongKhachHang; }
    public void setTongKhachHang(int tongKhachHang) { this.tongKhachHang = tongKhachHang; }

    public ArrayList<logDTO> getDanhSachLog() { return danhSachLog; }
    public void setDanhSachLog(ArrayList<logDTO> danhSachLog) { this.danhSachLog = danhSachLog; }
}