#phone-shop  Tiến độ
## Tổng quan
Hệ thống quản lý cửa hàng điện thoại, sử dụng Java Swing với kiến trúc 3 tầng:
  1.Tầng DAL/DAO (Data Access Layer)
  2.Tầng BUS/Service (Business Logic Layer)
  3.Tầng DTO (Data Transfer Object)

## Chức năng

### 1. Quản lý Sản phẩm (Product)

* **Thêm:** Nhập dòng máy mới, cấu hình (RAM, CPU, Pin), hãng sản xuất, loại máy và giá bán.
* **Xóa:** Ngừng kinh doanh sản phẩm (thường cập nhật trạng thái `INACTIVE` để giữ lịch sử hóa đơn).
* **Sửa:** Điều chỉnh giá bán, cập nhật mô tả cấu hình hoặc thay đổi hãng/loại.
* **Tìm kiếm:** Tìm theo tên máy, mã sản phẩm hoặc quét Barcode.
* **Nghiệp vụ đặc thù:** Cảnh báo sản phẩm sắp hết hàng dựa trên ngưỡng tồn kho tối thiểu (`minStock`).

### 2. Quản lý Thiết bị & Lô hàng (Inventory Lot/Serial)
Quản lý thực tế số máy đang nằm trong kho theo MAMAY(Serial).

* **Thêm:** Tự động tạo mã thiết bị khi hoàn tất phiếu nhập hàng.
* **Xóa:** Loại bỏ mã máy khỏi kho nếu có yêu cầu trả hàng hoặc máy lỗi khi chưa bán.
* **Sửa:** Cập nhật trạng thái từng máy (Sẵn sàng bán, Đã hỏng, Đang chuyển kho).
* **Tìm kiếm:** Tra cứu nguồn gốc của một máy cụ thể dựa trên mã lô hoặc mã máy.
* 
### 3. Quản lý Bán hàng & Hóa đơn (Sales/Invoice)

* **Thêm:** Lập hóa đơn mới, chọn máy cụ thể từ kho, áp dụng giảm giá và tính tổng tiền.
* **Xóa:** Hủy hóa đơn (yêu cầu quyền quản lý, máy sẽ tự động quay lại kho).
* **Sửa:** Thay đổi thông tin khách hàng hoặc ghi chú trên hóa đơn đã lập.
* **Tìm kiếm:** Tìm hóa đơn theo mãHD, mãKH, theo ngày.

### 4. Quản lý Nhập hàng & Phiếu nhập (Goods Receipt)

* **Thêm:** Tạo phiếu nhập hàng, chọn nhà cung cấp, nhập số lượng và đơn giá nhập.
* **Xóa:** Hủy phiếu nhập khi có sai sót (chỉ thực hiện được khi hàng chưa bán lẻ).
* **Sửa:** Điều chỉnh thông tin phiếu khi hàng thực tế về lệch so với phiếu tạm.
* **Tìm kiếm:** Tra cứu lịch sử nhập hàng theo thời gian hoặc theo Nhà cung cấp.
* **Nghiệp vụ đặc thù:** **Import từ Excel** danh sách hàng trăm mã máy thay vì nhập tay.

### 5. Quản lý Khách hàng (Customer)

* **Thêm:** Đăng ký khách hàng mới (thường thực hiện ngay khi bán hàng).
* **Xóa:** Loại bỏ thông tin khách hàng ảo hoặc trùng lặp.
* **Sửa:** Cập nhật thông tin liên lạc (SDT, địa chỉ) và theo dõi điểm tích lũy.
* **Tìm kiếm:** Tìm nhanh khách hàng qua số điện thoại để xem lịch sử mua máy.

### 6. Quản lý Khuyến mãi (Promotion)

* **Thêm:** Thiết kế đợt giảm giá, chọn sản phẩm áp dụng và mức % giảm.
* **Xóa:** Kết thúc sớm hoặc hủy bỏ chương trình khuyến mãi.
* **Sửa:** Gia hạn thời gian hoặc thay đổi danh sách sản phẩm được ưu đãi.
* **Tìm kiếm:** Xem danh sách các mã giảm giá đang còn hiệu lực.

### 7. Quản lý Bảo hành (Warranty)

* **Thêm:** Tự động sinh phiếu bảo hành ngay khi bán máy.
* **Sửa:** Cập nhật tình trạng máy khi khách mang đến sửa (Đang sửa, Đã trả khách).
* **Tìm kiếm:** Tra cứu máy có còn hạn bảo hành hay không dựa trên MAMAY.

### 8. Quản lý Tài khoản & Phân quyền (Account/RBAC)

* **Thêm:** Cấp tài khoản cho nhân viên mới, gán vào nhóm quyền (Bán hàng, Thủ kho).
* **Xóa:** Vô hiệu hóa tài khoản khi nhân viên nghỉ việc.
* **Sửa:** Đổi mật khẩu, đổi nhóm quyền hoặc cập nhật thông tin nhân viên.
* **Nghiệp vụ đặc thù:** Phân quyền chi tiết (chức năng nào nhân viên được thấy, chức năng nào không).

### 9. Quản lý Báo cáo & Nhật ký (Report/Audit Log)

* **Quản lý Thống kê:** Tổng hợp doanh thu, lợi nhuận, top máy bán chạy theo biểu đồ.
* **Quản lý Kiểm kho:** Tạo phiếu điều chỉnh số lượng tồn kho thực tế.
* **Quản lý Log:** Theo dõi thao tác "Thêm/Xóa/Sửa"
* **Nghiệp vụ đặc thù:** **Export Excel**

---
### Tổng kết kiến trúc triển khai:

1. **Giao diện (Swing/FlatLaf):** Nhận tương tác.
2. **Dữ liệu (DAO/SQL):** Lưu/Chỉnh sửa ArrayList -> Database.
