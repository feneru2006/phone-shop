package DTO;
public class anhspDTO {
    private String MaAnh; 
    private String maSP; 
    private String Url; 
    private boolean primary;
    
    public anhspDTO(String MaAnh, String maSP, String Url, boolean primary) {
        this.MaAnh = MaAnh;
        this.maSP = maSP;
        this.Url = Url;
        this.primary = primary;
    }
    
    public anhspDTO(){}

    public String getMaAnh() {
        return MaAnh;
    }

    public void setMaAnh(String MaAnh) {
        this.MaAnh = MaAnh;
    }

    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String Url) {
        this.Url = Url;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }
    
    
}
