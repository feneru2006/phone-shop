package DTO;
public class phanquyenDTO {
    private String Maquyen; 
    private String MACN;

    public phanquyenDTO(String Maquyen, String MACN) {
        this.Maquyen = Maquyen;
        this.MACN = MACN;
    }
    
    public phanquyenDTO(){}

    public String getMaquyen() {
        return Maquyen;
    }

    public void setMaquyen(String Maquyen) {
        this.Maquyen = Maquyen;
    }

    public String getMACN() {
        return MACN;
    }

    public void setMACN(String MACN) {
        this.MACN = MACN;
    }
    
}
