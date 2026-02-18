package DTO;
public class chucnangDTO {
    private String MACN; 
    private String tenCN; 

    public chucnangDTO(String MACN, String tenCN) {
        this.MACN = MACN;
        this.tenCN = tenCN;
    }
    
    public chucnangDTO(){}

    public String getMACN() {
        return MACN;
    }

    public void setMACN(String MACN) {
        this.MACN = MACN;
    }

    public String getTenCN() {
        return tenCN;
    }

    public void setTenCN(String tenCN) {
        this.tenCN = tenCN;
    }
    
    
}
