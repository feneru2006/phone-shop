package DTO;
public class hangsxDTO {
    private String maNSX;
    private String tenTH;

    public hangsxDTO(String maNSX, String tenTH) {
        this.maNSX = maNSX;
        this.tenTH = tenTH;
    }
    public hangsxDTO() {}

    public String getMaNSX() { return maNSX; }
    public void setMaNSX(String maNSX) { this.maNSX = maNSX; }
    public String getTenTH() { return tenTH; }
    public void setTenTH(String tenTH) { this.tenTH = tenTH; }

}