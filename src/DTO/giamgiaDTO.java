package DTO;
public class giamgiaDTO {
    private String MAGG; 
    private String maSP; 
    private String phantramgg;
    
    public giamgiaDTO(){}

    public giamgiaDTO(String MAGG, String maSP, String phantramgg) {
        this.MAGG = MAGG;
        this.maSP = maSP;
        this.phantramgg = phantramgg;
    }

    public String getMAGG() {
        return MAGG;
    }

    public void setMAGG(String MAGG) {
        this.MAGG = MAGG;
    }

    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
    }

    public String getPhantramgg() {
        return phantramgg;
    }

    public void setPhantramgg(String phantramgg) {
        this.phantramgg = phantramgg;
    }
    
    
}
