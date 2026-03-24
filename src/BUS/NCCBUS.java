package BUS;

import java.util.ArrayList;
import java.util.List;
import DAL.DAO.NCCDAO;
import DTO.NCCDTO;

    public class NCCBUS {
        private NCCDAO nccDAO = new NCCDAO();
        private List<NCCDTO> listNCC = new ArrayList<>();

        public NCCBUS() {
            docDanhSach(); 
        }

        public void docDanhSach() {
            this.listNCC = nccDAO.getAll();
        }

        public List<NCCDTO> getListNCC() {
            return this.listNCC;
        }

        public boolean themNCC(NCCDTO ncc, List<String> permissions) {
            if (permissions == null || !permissions.contains("CREATE_NCC")) {
                System.out.println("Lỗi: Bạn không có quyền thêm Nhà Cung Cấp!");
                return false;
            }
            listNCC.add(ncc);
            boolean isSuccess = nccDAO.insert(ncc);

            if (!isSuccess) {
                listNCC.remove(ncc);
                System.out.println("Lỗi Database: Không thể thêm Nhà cung cấp!");
            }

            return isSuccess;
        }

        public boolean suaNCC(NCCDTO nccMoi, List<String> permissions) {
            if (permissions == null || !permissions.contains("UPDATE_NCC")) {
                System.out.println("Lỗi: Bạn không có quyền sửa Nhà Cung Cấp!");
                return false;
            }

            NCCDTO nccCu = null;
            int index = -1;
            for (int i = 0; i < listNCC.size(); i++) {
                if (listNCC.get(i).getMaNCC().equals(nccMoi.getMaNCC())) {
                    nccCu = listNCC.get(i);
                    index = i;
                    listNCC.set(i, nccMoi); 
                    break;
                }
            }

            if (index == -1) {
                System.out.println("Không tìm thấy Nhà cung cấp trong danh sách tạm!");
                return false;
            }

            boolean isSuccess = nccDAO.update(nccMoi);
            if (!isSuccess) {
                listNCC.set(index, nccCu); 
                System.out.println("Lỗi Database: Không thể cập nhật!");
            }

            return isSuccess;
        }

    public boolean xoaNCC(String maNCC, List<String> permissions) {
        if (permissions == null || !permissions.contains("DELETE_NCC")) {
            System.out.println("Lỗi: Bạn không có quyền xóa Nhà Cung Cấp!");
            return false;
        }

        NCCDTO nccBiXoa = null;
        int index = -1;
        for (int i = 0; i < listNCC.size(); i++) {
            if (listNCC.get(i).getMaNCC().equals(maNCC)) {
                nccBiXoa = listNCC.get(i);
                index = i;
                listNCC.get(i).setIsDeleted(1);
                break;
            }
        }
        if (nccBiXoa == null) return false;

        boolean isSuccess = nccDAO.delete(maNCC);
        if (!isSuccess) {
            listNCC.get(index).setIsDeleted(0); 
        }
        return isSuccess;
    }

    public boolean moKhoaNCC(String maNCC) {
        if (nccDAO.unlock(maNCC)) {
            for (NCCDTO ncc : listNCC) {
                if (ncc.getMaNCC().equals(maNCC)) {
                    ncc.setIsDeleted(0); 
                    break;
                }
            }
            return true;
        }
        return false;
    }   

        public List<NCCDTO> timKiem(String tuKhoa, String tieuChi) {
            List<NCCDTO> result = new ArrayList<>();
            String txt = tuKhoa.toLowerCase().trim();

            for (NCCDTO ncc : listNCC) {
                switch (tieuChi) {
                    case "Tất cả":
                        if (ncc.getMaNCC().toLowerCase().contains(txt) || 
                            ncc.getTen().toLowerCase().contains(txt) || 
                            ncc.getDiaChi().toLowerCase().contains(txt) || 
                            ncc.getSdt().toLowerCase().contains(txt)) {
                            result.add(ncc);
                        }
                        break;
                    case "Mã nhà cung cấp":
                        if (ncc.getMaNCC().toLowerCase().contains(txt)) result.add(ncc);
                        break;
                    case "Tên nhà cung cấp":
                        if (ncc.getTen().toLowerCase().contains(txt)) result.add(ncc);
                        break;
                    case "Số điện thoại":
                        if (ncc.getSdt().toLowerCase().contains(txt)) result.add(ncc);
                        break;
                }
            }
            return result;
        }

        public String[] layMangTenNCC() {
            String[] result = new String[listNCC.size()];
            for (int i = 0; i < listNCC.size(); i++) {
                result[i] = listNCC.get(i).getTen();
            }
            return result;
        }

        public NCCDTO timNccTheoTen(String tenNCC) {
            for (NCCDTO ncc : listNCC) {
                if (ncc.getTen().equalsIgnoreCase(tenNCC.trim())) {
                    return ncc;
                }
            }
            return null;
        }

        public String layTenNccTheoMa(String maNCC) {
            for (NCCDTO ncc : listNCC) {
                if (ncc.getMaNCC().equals(maNCC)) {
                    return ncc.getTen();
                }
            }
            return "Không xác định";
        }
    }