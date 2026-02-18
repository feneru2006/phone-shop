package DTO;
import java.time.LocalDate;

public class DanhgiaDTO {
    private String maDG;
    private String maKH;
    private String maSP;
    private String noiDungDG;
    private int sao;
    private LocalDate ngayTao;

    public DanhgiaDTO(String maDG, String maKH, String maSP, String noiDungDG, int sao, LocalDate ngayTao) {
        this.maDG = maDG;
        this.maKH = maKH;
        this.maSP = maSP;
        this.noiDungDG = noiDungDG;
        this.sao = sao;
        this.ngayTao = ngayTao;
    }
    public DanhgiaDTO() {}

    public String getMaDG() {
        return maDG;
    }

    public void setMaDG(String maDG) {
        this.maDG = maDG;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
    }

    public String getNoiDungDG() {
        return noiDungDG;
    }

    public void setNoiDungDG(String noiDungDG) {
        this.noiDungDG = noiDungDG;
    }

    public int getSao() {
        return sao;
    }

    public void setSao(int sao) {
        this.sao = sao;
    }

    public LocalDate getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDate ngayTao) {
        this.ngayTao = ngayTao;
    }
    

    

}
