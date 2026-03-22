package DTO;

public class CTggDTO {
    private String MAGG; 
    private String MaSP; 
    private int phantramgg;
    private double giasaugiam;
    
    public CTggDTO(){}

    public CTggDTO(String mAGG, String maSP, int phantramgg, double giasaugiam) {
        MAGG = mAGG;
        MaSP = maSP;
        this.phantramgg = phantramgg;
        this.giasaugiam = giasaugiam;
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

    public double getGiasaugiam() {
        return giasaugiam;
    }

    public void setGiasaugiam(double giasaugiam) {
        this.giasaugiam = giasaugiam;
    }
}
