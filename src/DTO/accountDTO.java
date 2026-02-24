package DTO;
public class accountDTO {
    private String id;
    private String ten;
    private String pass;
    private String quyen;

    public accountDTO() {}

    public accountDTO(String id, String ten, String pass, String quyen) {
        this.id = id;
        this.ten = ten;
        this.pass = pass;
        this.quyen = quyen;
    }
    
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }

    public String getPass() { return pass; }
    public void setPass(String pass) { this.pass = pass; }

    public String getQuyen() { return quyen; }
    public void setQuyen(String quyen) { this.quyen = quyen; }

}
