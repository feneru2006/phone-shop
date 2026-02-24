package DTO;
import java.time.LocalDateTime;
public class giamgiaDTO {
    private String MAGG; 
    private String dotGG; 
    private LocalDateTime batdau;  
    private LocalDateTime ketthuc;

    
    public giamgiaDTO(){}
    public giamgiaDTO(String MAGG, String dotGG, LocalDateTime batdau, LocalDateTime ketthuc) {
        this.MAGG = MAGG;
        this.dotGG = dotGG;
        this.batdau = batdau;
        this.ketthuc = ketthuc;
    }

    public String getMAGG() {
        return MAGG;
    }

    public void setMAGG(String MAGG) {
        this.MAGG = MAGG;
    }

    public String getdotGG() {
        return dotGG;
    }

    public void setdotGG(String dotGG) {
        this.dotGG = dotGG;
    }

    public LocalDateTime getBatdau() {
        return batdau;
    }

    public void setBatdau(LocalDateTime batdau) {
        this.batdau = batdau;
    }

    public LocalDateTime getKetthuc() {
        return ketthuc;
    }

    public void setKetthuc(LocalDateTime ketthuc) {
        this.ketthuc = ketthuc;
    }

}
