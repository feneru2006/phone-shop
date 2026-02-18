
package DTO;
public class loaiDTO {
    private String maLoai;
    private String danhMuc;

    public loaiDTO (String maLoai, String danhMuc) {
        this.maLoai = maLoai;
        this.danhMuc = danhMuc;
    }
    public loaiDTO() {}

    public String getMaLoai() { return maLoai; }
    public void setMaLoai(String maLoai) { this.maLoai = maLoai; }
    public String getDanhMuc() { return danhMuc; }
    public void setDanhMuc(String danhMuc) { this.danhMuc = danhMuc; }

}