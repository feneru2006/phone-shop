package DTO;
public class NCCDTO {
    private String maNCC;
    private String ten;
    private String diaChi;
    private String sdt;

    public NCCDTO() {}

    public NCCDTO(String maNCC, String ten, String diaChi, String sdt) {
        this.maNCC = maNCC;
        this.ten = ten;
        this.diaChi = diaChi;
        this.sdt = sdt;
    }

    public String getMaNCC() { return maNCC; }
    public void setMaNCC(String maNCC) { this.maNCC = maNCC; }

    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

}
