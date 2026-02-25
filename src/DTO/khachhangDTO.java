package DTO;
public class khachhangDTO {
    public enum GioiTinh {
        NAM, 
        NU, 
        KHAC
    }

    private String maKH;
    private String hoTen;
    private GioiTinh gioiTinh; 
    private String sdt;
    private String diaChi;

    public khachhangDTO() {}

    public khachhangDTO(String maKH, String hoTen, GioiTinh gioiTinh, String sdt, String diaChi) {
        this.maKH = maKH;
        this.hoTen = hoTen;
        this.gioiTinh = gioiTinh;
        this.sdt = sdt;
        this.diaChi = diaChi;
    }

    public String getMaKH() { return maKH; }
    public void setMaKH(String maKH) { this.maKH = maKH; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public GioiTinh getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(GioiTinh gioiTinh) { this.gioiTinh = gioiTinh; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
}
