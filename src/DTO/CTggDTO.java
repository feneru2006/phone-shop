package DTO;

public class CTggDTO {
    private String MAGG; 
    private String MaSP; 
    private int phantramgg;
    
    public CTggDTO(){}

    public CTggDTO(String mAGG, String maSP, int phantramgg) {
        MAGG = mAGG;
        MaSP = maSP;
        this.phantramgg = phantramgg;
    }

    public String getMAGG() {
        return MAGG;
    }

    public void setMAGG(String mAGG) {
        MAGG = mAGG;
    }

    public String getMaSP() {
        return MaSP;
    }

    public void setMaSP(String maSP) {
        MaSP = maSP;
    }

    public int getPhantramgg() {
        return phantramgg;
    }

    public void setPhantramgg(int phantramgg) {
        this.phantramgg = phantramgg;
    }     
}
