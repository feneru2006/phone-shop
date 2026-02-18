package DTO;
import java.time.LocalDate;

public class CTggDTO {
    private String MAGG; 
    private LocalDate dotGG; 
    private LocalDate batdau; 
    private LocalDate ketthuc;
    
    public CTggDTO(){}

    public CTggDTO(String MAGG, LocalDate dotGG, LocalDate batdau, LocalDate ketthuc) {
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

    public LocalDate getDotGG() {
        return dotGG;
    }

    public void setDotGG(LocalDate dotGG) {
        this.dotGG = dotGG;
    }

    public LocalDate getBatdau() {
        return batdau;
    }

    public void setBatdau(LocalDate batdau) {
        this.batdau = batdau;
    }

    public LocalDate getKetthuc() {
        return ketthuc;
    }

    public void setKetthuc(LocalDate ketthuc) {
        this.ketthuc = ketthuc;
    }
      
}
