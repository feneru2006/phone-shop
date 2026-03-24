package BUS;

import DAL.DAO.KhachHangDAO;
import DTO.khachhangDTO;
import java.util.ArrayList;

public class KhachHangBUS {
    private KhachHangDAO khDAO = new KhachHangDAO();

    public ArrayList<khachhangDTO> timKiem(String keyword) {
        return khDAO.timKiem(keyword);
    }
    
    public ArrayList<khachhangDTO> getList() {
        return khDAO.timKiem("");
    }

    public boolean themKH(khachhangDTO kh) {
        // Có thể check thêm logic trùng SĐT ở đây nếu thích
        return khDAO.insert(kh);
    }

    public boolean suaKH(khachhangDTO kh) {
        return khDAO.update(kh);
    }

    public boolean xoaKH(String maKH) {
        return khDAO.delete(maKH);
    }
    // Gọi DAO lấy lịch sử mua hàng
    public ArrayList<Object[]> getLichSuMuaHang(String maKH) {
        return khDAO.getLichSuMuaHang(maKH);
    }
}