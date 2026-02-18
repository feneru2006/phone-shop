package DTO;
public class ChitietSPDTO {
    private String maCTSP;
    private String maSP;
    private String maNCC;
    private String Tinhtrang; // Serial/IMEI
    private String maCTPN;

    public ChitietSPDTO() {}

    public ChitietSPDTO(String maCTSP, String maSP, String maNCC, String Tinhtrang, String maCTPN) {
        this.maCTSP = maCTSP;
        this.maSP = maSP;
        this.maNCC = maNCC;
        this.Tinhtrang = Tinhtrang;
        this.maCTPN = maCTPN;
    }

    public String getMaCTSP() { return maCTSP; }
    public void setMaCTSP(String maCTSP) { this.maCTSP = maCTSP; }

    public String getMaSP() { return maSP; }
    public void setMaSP(String maSP) { this.maSP = maSP; }

    public String getMaNCC() { return maNCC; }
    public void setMaNCC(String maNCC) { this.maNCC = maNCC; }

    public String getTinhtrang() { return Tinhtrang; }
    public void setTinhtrang(String Tinhtrang) { this.Tinhtrang = Tinhtrang; }

    public String getMaCTPN() { return maCTPN; }
    public void setMaCTPN(String maCTPN) { this.maCTPN = maCTPN; }

}
