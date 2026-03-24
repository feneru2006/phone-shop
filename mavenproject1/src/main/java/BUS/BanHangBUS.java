package BUS;

import DAL.DAO.BanHangDAO;
import java.util.List;

public class BanHangBUS {
    private BanHangDAO bhDAO = new BanHangDAO();

    public Object[] getGiaKhuyenMai(String maSP, double giaGoc) { return bhDAO.getGiaKhuyenMai(maSP, giaGoc); }
    public int getSoLuongMayRanh(String maSP) { return bhDAO.getSoLuongMayRanh(maSP); }
    public String getTenKhachBySDT(String sdt) { return bhDAO.getTenKhachBySDT(sdt); }
    public String themKhachHangNhanh(String ten, String sdt, String gioiTinh, String diaChi) throws Exception { return bhDAO.themKhachHangNhanh(ten, sdt, gioiTinh, diaChi); }
    public String getIMEIKhaDung(String maSP, List<String> imeiTrongGio) { return bhDAO.getIMEIKhaDung(maSP, imeiTrongGio); }
    public double getGiaGocByIMEI(String imei) { return bhDAO.getGiaGocByIMEI(imei); }
    public List<Object[]> getLichSuHoaDon() { return bhDAO.getLichSuHoaDon(); }
    
    // TRANSACTION CHỐT ĐƠN
    public List<String> chotDonHang(String sdt, String tenNV, double tongTien, List<Object[]> gioHang) throws Exception {
        return bhDAO.chotDonHang(sdt, tenNV, tongTien, gioHang);
    }
}