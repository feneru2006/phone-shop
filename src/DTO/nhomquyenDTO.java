package DTO;
public class nhomquyenDTO {
    private String Maquyen; 
    private String tenquyen; 
    
    public nhomquyenDTO(){}
    public nhomquyenDTO(String Maquyen, String tenquyen){
        this.Maquyen = Maquyen;
        this.tenquyen = tenquyen;
    }
    
    public String getMaquyen() {
        return Maquyen;
    }

    public void setMaquyen(String Maquyen) {
        this.Maquyen = Maquyen;
    }

    public String getTenquyen() {
        return tenquyen;
    }

    public void setTenquyen(String tenquyen) {
        this.tenquyen = tenquyen;
    }
    
}
