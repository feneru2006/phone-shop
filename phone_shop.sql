-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Mar 09, 2026 at 05:22 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `phone_shop`
--

-- --------------------------------------------------------

--
-- Table structure for table `account`
--

CREATE TABLE `account` (
  `id` varchar(20) NOT NULL,
  `ten` varchar(50) NOT NULL,
  `pass` varchar(255) NOT NULL,
  `quyen` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `account`
--

INSERT INTO `account` (`id`, `ten`, `pass`, `quyen`) VALUES
('NV01', 'admin', '309a205ef98249a04f79fb054cb81d92c348e6b4af2985de5ab939f3dd4f5802', 'AD'),
('NV02', 'staff01', 'password', 'ST'),
('NV03', 'staff02', 'password', 'ST'),
('NV04', 'staff03', 'password', 'ST'),
('NV05', 'staff04', 'password', 'ST');

-- --------------------------------------------------------

--
-- Table structure for table `anhsp`
--

CREATE TABLE `anhsp` (
  `MAanh` varchar(20) NOT NULL,
  `MASP` varchar(20) DEFAULT NULL,
  `url` text NOT NULL,
  `isPrimary` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `chucnang`
--

CREATE TABLE `chucnang` (
  `MACN` varchar(20) NOT NULL,
  `tenCN` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `chucnang`
--

INSERT INTO `chucnang` (`MACN`, `tenCN`) VALUES
('CN01', 'Quản lý kho'),
('CN02', 'Bán hàng'),
('CN03', 'Báo cáo'),
('CN04', 'Nhân sự'),
('CN05', 'Cấu hình');

-- --------------------------------------------------------

--
-- Table structure for table `ctgg`
--

CREATE TABLE `ctgg` (
  `MAGG` varchar(20) NOT NULL,
  `MASP` varchar(20) NOT NULL,
  `phantramgg` int(11) DEFAULT 0 CHECK (`phantramgg` >= 0 and `phantramgg` <= 100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `ctgg`
--

INSERT INTO `ctgg` (`MAGG`, `MASP`, `phantramgg`) VALUES
('GG01', 'SP01', 10),
('GG02', 'SP04', 15),
('GG03', 'SP06', 20),
('GG04', 'SP02', 25),
('GG05', 'SP03', 30),
('GG06', 'SP07', 10),
('GG07', 'SP09', 5),
('GG08', 'SP10', 15),
('GG09', 'SP05', 20),
('GG10', 'SP08', 5);

-- --------------------------------------------------------

--
-- Table structure for table `cthd`
--

CREATE TABLE `cthd` (
  `MACTHD` varchar(20) NOT NULL,
  `MAHD` varchar(20) DEFAULT NULL,
  `MACTSP` varchar(50) DEFAULT NULL,
  `Dongia` double DEFAULT NULL CHECK (`Dongia` >= 0),
  `Thanhtien` double DEFAULT NULL CHECK (`Thanhtien` >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `cthd`
--

INSERT INTO `cthd` (`MACTHD`, `MAHD`, `MACTSP`, `Dongia`, `Thanhtien`) VALUES
('CTHD01', 'HD01', 'IMEI01', 20000000, 20000000),
('CTHD02', 'HD02', 'IMEI04', 18000000, 18000000),
('CTHD03', 'HD03', 'IMEI05', 15000000, 15000000),
('CTHD04', 'HD04', 'IMEI03', 25000000, 25000000),
('CTHD05', 'HD05', 'IMEI06', 30000000, 30000000),
('CTHD06', 'HD06', 'IMEI07', 8000000, 8000000),
('CTHD07', 'HD07', 'IMEI08', 10000000, 10000000),
('CTHD08', 'HD08', 'IMEI09', 500000, 500000),
('CTHD09', 'HD09', 'IMEI02', 200000, 200000),
('CTHD10', 'HD10', 'IMEI10', 2500000, 2500000);

--
-- Triggers `cthd`
--
DELIMITER $$
CREATE TRIGGER `Them_ChiTiet_HD` AFTER INSERT ON `cthd` FOR EACH ROW BEGIN
    UPDATE `sanpham`
    SET SLton = SLton - 1
    WHERE MASP = (SELECT MASP FROM ctsp WHERE MACTSP = NEW.MACTSP);
        UPDATE `ctsp`
        SET tinhtrang = 'Đã bán'
        WHERE MACTSP = NEW.MACTSP;
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `Xoa_ChiTiet_HD` AFTER DELETE ON `cthd` FOR EACH ROW BEGIN
    UPDATE `sanpham`
    SET SLton = SLton + 1
    WHERE MASP = (SELECT MASP FROM ctsp WHERE MACTSP = OLD.MACTSP);
        UPDATE `ctsp`
        SET tinhtrang = 'Sẵn có'
        WHERE MACTSP = OLD.MACTSP;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `ctphieunhap`
--

CREATE TABLE `ctphieunhap` (
  `MACTPN` varchar(20) NOT NULL,
  `MASP` varchar(20) DEFAULT NULL,
  `MAPNH` varchar(20) DEFAULT NULL,
  `SL` int(11) DEFAULT NULL CHECK (`SL` > 0),
  `dongia` double DEFAULT NULL CHECK (`dongia` > 0),
  `thanhtien` double GENERATED ALWAYS AS (`SL` * `dongia`) STORED
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `ctphieunhap`
--

INSERT INTO `ctphieunhap` (`MACTPN`, `MASP`, `MAPNH`, `SL`, `dongia`) VALUES
('CTPN01', 'SP01', 'PN01', 5, 19000000),
('CTPN02', 'SP04', 'PN01', 2, 24000000),
('CTPN03', 'SP02', 'PN02', 4, 17000000),
('CTPN04', 'SP03', 'PN02', 3, 14000000),
('CTPN05', 'SP05', 'PN03', 2, 29000000),
('CTPN06', 'SP06', 'PN04', 5, 7500000),
('CTPN07', 'SP07', 'PN05', 4, 9500000),
('CTPN08', 'SP08', 'PN06', 10, 450000),
('CTPN09', 'SP09', 'PN07', 20, 180000),
('CTPN10', 'SP10', 'PN08', 6, 2300000);

--
-- Triggers `ctphieunhap`
--
DELIMITER $$
CREATE TRIGGER `Them_Nhap_Hang` AFTER INSERT ON `ctphieunhap` FOR EACH ROW BEGIN
        UPDATE `sanpham` 
        SET SLton = SLton + NEW.SL 
        WHERE MASP = NEW.MASP;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `ctsp`
--

CREATE TABLE `ctsp` (
  `MACTSP` varchar(50) NOT NULL,
  `MASP` varchar(20) DEFAULT NULL,
  `MANCC` varchar(20) DEFAULT NULL,
  `tinhtrang` varchar(50) DEFAULT 'Sẵn có',
  `MACTPN` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `ctsp`
--

INSERT INTO `ctsp` (`MACTSP`, `MASP`, `MANCC`, `tinhtrang`, `MACTPN`) VALUES
('IMEI01', 'SP01', 'NCC01', 'Đã bán', 'CTPN01'),
('IMEI02', 'SP01', 'NCC01', 'Đã bán', 'CTPN01'),
('IMEI03', 'SP04', 'NCC01', 'Đã bán', 'CTPN02'),
('IMEI04', 'SP02', 'NCC02', 'Đã bán', 'CTPN03'),
('IMEI05', 'SP03', 'NCC02', 'Đã bán', 'CTPN04'),
('IMEI06', 'SP05', 'NCC03', 'Đã bán', 'CTPN05'),
('IMEI07', 'SP06', 'NCC04', 'Đã bán', 'CTPN06'),
('IMEI08', 'SP07', 'NCC05', 'Đã bán', 'CTPN07'),
('IMEI09', 'SP08', 'NCC06', 'Đã bán', 'CTPN08'),
('IMEI10', 'SP10', 'NCC08', 'Đã bán', 'CTPN10');

-- --------------------------------------------------------

--
-- Table structure for table `giamgia`
--

CREATE TABLE `giamgia` (
  `MAGG` varchar(20) NOT NULL,
  `dotGG` varchar(255) DEFAULT NULL,
  `batdau` datetime NOT NULL,
  `ketthuc` datetime NOT NULL CHECK (`ketthuc` > `batdau`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `giamgia`
--

INSERT INTO `giamgia` (`MAGG`, `dotGG`, `batdau`, `ketthuc`) VALUES
('GG01', 'Tôi là người béo', '2026-06-01 00:00:00', '2026-07-01 02:59:59'),
('GG02', 'Back to school', '2026-08-01 00:00:00', '2026-08-31 23:59:59'),
('GG03', 'Black Friday', '2026-11-20 00:00:00', '2026-11-27 23:59:59'),
('GG04', 'Giáng sinh', '2026-12-15 00:00:00', '2026-12-25 23:59:59'),
('GG05', 'Tết Nguyên Đán', '2027-01-01 00:00:00', '2027-01-20 23:59:59'),
('GG06', 'Valentine', '2026-02-10 00:00:00', '2026-02-15 23:59:59'),
('GG07', 'Quốc tế phụ nữ', '2026-03-01 00:00:00', '2026-03-08 23:59:59'),
('GG08', 'Giải phóng miền Nam', '2026-04-20 00:00:00', '2026-04-30 23:59:59'),
('GG09', 'Quốc tế lao động', '2026-05-01 00:00:00', '2026-05-05 23:59:59'),
('GG10', 'Quốc khánh', '2026-09-01 00:00:00', '2026-09-05 23:59:59');

-- --------------------------------------------------------

--
-- Table structure for table `hangsx`
--

CREATE TABLE `hangsx` (
  `MANSX` varchar(20) NOT NULL,
  `tenTH` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `hangsx`
--

INSERT INTO `hangsx` (`MANSX`, `tenTH`) VALUES
('HSX01', 'Apple'),
('HSX02', 'Samsung'),
('HSX03', 'Xiaomi'),
('HSX04', 'OPPO'),
('HSX05', 'Sony'),
('HSX06', 'Vivo'),
('HSX07', 'Huawei'),
('HSX08', 'Realme'),
('HSX09', 'LG'),
('HSX10', 'Nokia');

-- --------------------------------------------------------

--
-- Table structure for table `hoadon`
--

CREATE TABLE `hoadon` (
  `MAHD` varchar(20) NOT NULL,
  `MANV` varchar(20) DEFAULT NULL,
  `MAKH` varchar(20) DEFAULT NULL,
  `ngaylap` datetime DEFAULT current_timestamp(),
  `tongtien` double DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `hoadon`
--

INSERT INTO `hoadon` (`MAHD`, `MANV`, `MAKH`, `ngaylap`, `tongtien`) VALUES
('HD01', 'NV02', 'KH01', '2026-02-01 10:00:00', 20000000),
('HD02', 'NV03', 'KH02', '2026-02-02 11:00:00', 18000000),
('HD03', 'NV04', 'KH03', '2026-02-03 12:00:00', 15000000),
('HD04', 'NV05', 'KH04', '2026-02-04 13:00:00', 25000000),
('HD05', 'NV02', 'KH05', '2026-02-05 14:00:00', 30000000),
('HD06', 'NV03', 'KH06', '2026-02-06 15:00:00', 8000000),
('HD07', 'NV04', 'KH07', '2026-02-07 16:00:00', 10000000),
('HD08', 'NV05', 'KH08', '2026-02-08 10:00:00', 500000),
('HD09', 'NV02', 'KH09', '2026-02-09 11:00:00', 200000),
('HD10', 'NV03', 'KH10', '2026-02-10 12:00:00', 2500000);

-- --------------------------------------------------------

--
-- Table structure for table `khachhang`
--

CREATE TABLE `khachhang` (
  `MAKH` varchar(20) NOT NULL,
  `hoten` varchar(100) NOT NULL,
  `gioitinh` enum('Nam','Nữ','Khác') DEFAULT NULL,
  `SDT` varchar(15) DEFAULT NULL,
  `diachi` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `khachhang`
--

INSERT INTO `khachhang` (`MAKH`, `hoten`, `gioitinh`, `SDT`, `diachi`) VALUES
('KH01', 'Khách A', 'Nam', '0912000001', 'HCM'),
('KH02', 'Khách B', 'Nữ', '0912000002', 'HN'),
('KH03', 'Khách C', 'Nam', '0912000003', 'Đà Nẵng'),
('KH04', 'Khách D', 'Nữ', '0912000004', 'HCM'),
('KH05', 'Khách E', 'Nam', '0912000005', 'HN'),
('KH06', 'Khách F', 'Nữ', '0912000006', 'Cần Thơ'),
('KH07', 'Khách G', 'Nam', '0912000007', 'HCM'),
('KH08', 'Khách H', 'Nữ', '0912000008', 'HN'),
('KH09', 'Khách I', 'Nam', '0912000009', 'Đà Nẵng'),
('KH10', 'Khách J', 'Nữ', '0912000010', 'HCM');

-- --------------------------------------------------------

--
-- Table structure for table `loai`
--

CREATE TABLE `loai` (
  `MAloai` varchar(20) NOT NULL,
  `danhmuc` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `loai`
--

INSERT INTO `loai` (`MAloai`, `danhmuc`) VALUES
('L01', 'Smartphone'),
('L02', 'Tablet'),
('L03', 'Phụ kiện');

-- --------------------------------------------------------

--
-- Table structure for table `log`
--

CREATE TABLE `log` (
  `Malog` varchar(20) NOT NULL,
  `accountid` varchar(20) DEFAULT NULL,
  `hanhvi` varchar(100) DEFAULT NULL,
  `thucthe` varchar(100) DEFAULT NULL,
  `chitiethv` text DEFAULT NULL,
  `thoidiem` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `log`
--

INSERT INTO `log` (`Malog`, `accountid`, `hanhvi`, `thucthe`, `chitiethv`, `thoidiem`) VALUES
('LOG01', 'NV01', 'LOGIN', 'Account', 'Đăng nhập', '2026-01-01 08:00:00'),
('LOG02', 'NV01', 'INSERT', 'PhieuNhap', 'Nhập hàng PN01', '2026-01-01 09:00:00'),
('LOG03', 'NV04', 'INSERT', 'PhieuNhap', 'Nhập hàng PN02', '2026-01-15 09:30:00'),
('LOG04', 'NV02', 'LOGIN', 'Account', 'Đăng nhập staff01', '2026-02-01 08:00:00'),
('LOG05', 'NV02', 'INSERT', 'Hoadon', 'Bán hàng HD01', '2026-02-01 10:05:00'),
('LOG06', 'NV03', 'LOGIN', 'Account', 'Đăng nhập staff02', '2026-02-02 08:00:00'),
('LOG07', 'NV03', 'INSERT', 'Hoadon', 'Bán hàng HD02', '2026-02-02 11:05:00'),
('LOG08', 'NV04', 'INSERT', 'Hoadon', 'Bán hàng HD03', '2026-02-03 12:05:00'),
('LOG09', 'NV05', 'INSERT', 'Hoadon', 'Bán hàng HD04', '2026-02-04 13:05:00'),
('LOG10', 'NV02', 'INSERT', 'Hoadon', 'Bán hàng HD05', '2026-02-05 14:05:00');

-- --------------------------------------------------------

--
-- Table structure for table `ncc`
--

CREATE TABLE `ncc` (
  `MANCC` varchar(20) NOT NULL,
  `ten` varchar(255) NOT NULL,
  `diachi` text DEFAULT NULL,
  `SDT` varchar(15) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `ncc`
--

INSERT INTO `ncc` (`MANCC`, `ten`, `diachi`, `SDT`) VALUES
('NCC01', 'Digiworld', 'TP.HCM', '0281234567'),
('NCC02', 'FPT', 'Hà Nội', '0249876543'),
('NCC03', 'Petrosetco', 'TP.HCM', '0289876543'),
('NCC04', 'Viettel', 'Hà Nội', '0241234567'),
('NCC05', 'Thế Giới Số', 'Đà Nẵng', '0236123456'),
('NCC06', 'Samsung VN', 'Bắc Ninh', '0222123456'),
('NCC07', 'Apple VN', 'TP.HCM', '0281112222'),
('NCC08', 'Xiaomi VN', 'Hà Nội', '0243334444'),
('NCC09', 'Sony VN', 'TP.HCM', '0285556666'),
('NCC10', 'OPPO VN', 'Bình Dương', '0274777888');

-- --------------------------------------------------------

--
-- Table structure for table `nhanvien`
--

CREATE TABLE `nhanvien` (
  `MANV` varchar(20) NOT NULL,
  `hoten` varchar(100) NOT NULL,
  `gioitinh` enum('Nam','Nữ','Khác') DEFAULT NULL,
  `SDT` varchar(15) DEFAULT NULL,
  `diachi` text DEFAULT NULL,
  `thamnien` double DEFAULT 0,
  `luong` double DEFAULT 0,
  `trangthai` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `nhanvien`
--

INSERT INTO `nhanvien` (`MANV`, `hoten`, `gioitinh`, `SDT`, `diachi`, `thamnien`, `luong`, `trangthai`) VALUES
('NV01', 'Nguyễn Văn A', 'Nam', '0901000001', 'HCM', 3, 10000000, 1),
('NV02', 'Trần Thị B', 'Nữ', '0901000002', 'HN', 2, 8000000, 1),
('NV03', 'Lê Văn C', 'Nam', '0901000003', 'Đà Nẵng', 1, 7000000, 1),
('NV04', 'Phạm Thị D', 'Nữ', '0901000004', 'HCM', 4, 12000000, 1),
('NV05', 'Vũ Văn E', 'Nam', '0901000005', 'HN', 2, 8000000, 1),
('NV06', 'Hoàng Văn F', 'Nam', '0901000006', 'Cần Thơ', 5, 15000000, 1),
('NV07', 'Đỗ Thị G', 'Nữ', '0901000007', 'HCM', 1, 6000000, 1),
('NV08', 'Bùi Văn H', 'Nam', '0901000008', 'HN', 3, 9000000, 1),
('NV09', 'Ngô Thị I', 'Nữ', '0901000009', 'Đà Nẵng', 2, 7500000, 1),
('NV10', 'Dương Văn J', 'Nam', '0901000010', 'HCM', 4, 11000000, 1);

-- --------------------------------------------------------

--
-- Table structure for table `nhomquyen`
--

CREATE TABLE `nhomquyen` (
  `MAQUYEN` varchar(20) NOT NULL,
  `tenQUYEN` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `nhomquyen`
--

INSERT INTO `nhomquyen` (`MAQUYEN`, `tenQUYEN`) VALUES
('AD', 'Admin'),
('ST', 'Staff');

-- --------------------------------------------------------

--
-- Table structure for table `phanquyen`
--

CREATE TABLE `phanquyen` (
  `MAQUYEN` varchar(20) NOT NULL,
  `MACN` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `phanquyen`
--

INSERT INTO `phanquyen` (`MAQUYEN`, `MACN`) VALUES
('AD', 'CN01'),
('AD', 'CN02'),
('AD', 'CN03'),
('AD', 'CN04'),
('AD', 'CN05'),
('ST', 'CN01'),
('ST', 'CN02');

-- --------------------------------------------------------

--
-- Table structure for table `phieubaohanh`
--

CREATE TABLE `phieubaohanh` (
  `MABH` varchar(20) NOT NULL,
  `MACTHD` varchar(20) DEFAULT NULL,
  `MAKH` varchar(20) DEFAULT NULL,
  `ngayBD` date DEFAULT NULL,
  `thoihan` int(11) DEFAULT NULL,
  `trangthai` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `phieubaohanh`
--

INSERT INTO `phieubaohanh` (`MABH`, `MACTHD`, `MAKH`, `ngayBD`, `thoihan`, `trangthai`) VALUES
('BH01', 'CTHD01', 'KH01', '2026-02-01', 12, 'Đang bảo hành'),
('BH02', 'CTHD02', 'KH02', '2026-02-02', 12, 'Đang bảo hành'),
('BH03', 'CTHD03', 'KH03', '2026-02-03', 12, 'Đang bảo hành'),
('BH04', 'CTHD04', 'KH04', '2026-02-04', 24, 'Đang bảo hành'),
('BH05', 'CTHD05', 'KH05', '2026-02-05', 24, 'Đang bảo hành'),
('BH06', 'CTHD06', 'KH06', '2026-02-06', 12, 'Hết hạn'),
('BH07', 'CTHD07', 'KH07', '2026-02-07', 12, 'Hết hạn'),
('BH08', 'CTHD08', 'KH08', '2026-02-08', 6, 'Hết hạn'),
('BH09', 'CTHD09', 'KH09', '2026-02-09', 6, 'Hết hạn'),
('BH10', 'CTHD10', 'KH10', '2026-02-10', 12, 'Đang bảo hành');

-- --------------------------------------------------------

--
-- Table structure for table `phieunhap`
--

CREATE TABLE `phieunhap` (
  `MAPNH` varchar(20) NOT NULL,
  `MANV` varchar(20) DEFAULT NULL,
  `Ngaynhap` datetime DEFAULT current_timestamp(),
  `tongtien` double DEFAULT 0,
  `MANCC` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `phieunhap`
--

INSERT INTO `phieunhap` (`MAPNH`, `MANV`, `Ngaynhap`, `tongtien`, `MANCC`) VALUES
('PN01', 'NV01', '2026-01-01 08:00:00', 100000000, 'NCC01'),
('PN02', 'NV04', '2026-01-15 09:00:00', 80000000, 'NCC02'),
('PN03', 'NV06', '2026-02-01 10:00:00', 50000000, 'NCC03'),
('PN04', 'NV01', '2026-02-15 08:30:00', 90000000, 'NCC04'),
('PN05', 'NV04', '2026-03-01 09:30:00', 60000000, 'NCC05'),
('PN06', 'NV06', '2026-03-15 10:30:00', 70000000, 'NCC06'),
('PN07', 'NV01', '2026-04-01 08:00:00', 110000000, 'NCC07'),
('PN08', 'NV04', '2026-04-15 09:00:00', 120000000, 'NCC08'),
('PN09', 'NV06', '2026-05-01 10:00:00', 130000000, 'NCC09'),
('PN10', 'NV01', '2026-05-15 08:30:00', 140000000, 'NCC10');

-- --------------------------------------------------------

--
-- Table structure for table `sanpham`
--

CREATE TABLE `sanpham` (
  `MASP` varchar(20) NOT NULL,
  `tenSP` varchar(255) NOT NULL,
  `SLton` int(11) DEFAULT 0 CHECK (`SLton` >= 0),
  `gia` double NOT NULL CHECK (`gia` > 0),
  `trangthai` varchar(50) DEFAULT NULL,
  `MAloai` varchar(20) DEFAULT NULL,
  `cauhinh` text DEFAULT NULL,
  `NSX` varchar(20) DEFAULT NULL,
  `isDeleted` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `sanpham`
--

INSERT INTO `sanpham` (`MASP`, `tenSP`, `SLton`, `gia`, `trangthai`, `MAloai`, `cauhinh`, `NSX`, `isDeleted`) VALUES
('SP01', 'iPhone 15', 3, 20000000, 'Mới', 'L01', '128GB', 'HSX01', 0),
('SP02', 'Samsung S23', 3, 18000000, 'Mới', 'L01', '256GB', 'HSX02', 0),
('SP03', 'Xiaomi 13', 2, 15000000, 'Mới', 'L01', '256GB', 'HSX03', 0),
('SP04', 'iPad Pro', 1, 25000000, 'Mới', 'L02', '11 inch', 'HSX01', 0),
('SP05', 'MacBook Air', 1, 30000000, 'Mới', 'L02', 'M2, 8GB', 'HSX01', 0),
('SP06', 'Sony WH-1000XM5', 4, 8000000, 'Mới', 'L03', 'Chống ồn', 'HSX05', 0),
('SP07', 'Apple Watch S9', 3, 10000000, 'Mới', 'L03', '45mm', 'HSX01', 0),
('SP08', 'Sạc nhanh 20W', 9, 500000, 'Mới', 'L03', 'Type-C', 'HSX01', 0),
('SP09', 'Ốp lưng Silicon', 20, 200000, 'Mới', 'L03', 'Trong suốt', 'HSX02', 0),
('SP10', 'Loa JBL Flip 6', 5, 2500000, 'Mới', 'L03', 'Bluetooth', 'HSX05', 0);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `account`
--
ALTER TABLE `account`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `ten` (`ten`),
  ADD KEY `quyen` (`quyen`);

--
-- Indexes for table `anhsp`
--
ALTER TABLE `anhsp`
  ADD PRIMARY KEY (`MAanh`),
  ADD KEY `MASP` (`MASP`);

--
-- Indexes for table `chucnang`
--
ALTER TABLE `chucnang`
  ADD PRIMARY KEY (`MACN`);

--
-- Indexes for table `ctgg`
--
ALTER TABLE `ctgg`
  ADD PRIMARY KEY (`MAGG`,`MASP`),
  ADD KEY `MASP` (`MASP`);

--
-- Indexes for table `cthd`
--
ALTER TABLE `cthd`
  ADD PRIMARY KEY (`MACTHD`),
  ADD UNIQUE KEY `MACTSP` (`MACTSP`),
  ADD KEY `MAHD` (`MAHD`);

--
-- Indexes for table `ctphieunhap`
--
ALTER TABLE `ctphieunhap`
  ADD PRIMARY KEY (`MACTPN`),
  ADD KEY `MASP` (`MASP`),
  ADD KEY `MAPNH` (`MAPNH`);

--
-- Indexes for table `ctsp`
--
ALTER TABLE `ctsp`
  ADD PRIMARY KEY (`MACTSP`),
  ADD KEY `MASP` (`MASP`),
  ADD KEY `MANCC` (`MANCC`),
  ADD KEY `MACTPN` (`MACTPN`);

--
-- Indexes for table `giamgia`
--
ALTER TABLE `giamgia`
  ADD PRIMARY KEY (`MAGG`);

--
-- Indexes for table `hangsx`
--
ALTER TABLE `hangsx`
  ADD PRIMARY KEY (`MANSX`);

--
-- Indexes for table `hoadon`
--
ALTER TABLE `hoadon`
  ADD PRIMARY KEY (`MAHD`),
  ADD KEY `MANV` (`MANV`),
  ADD KEY `MAKH` (`MAKH`);

--
-- Indexes for table `khachhang`
--
ALTER TABLE `khachhang`
  ADD PRIMARY KEY (`MAKH`),
  ADD UNIQUE KEY `SDT` (`SDT`);

--
-- Indexes for table `loai`
--
ALTER TABLE `loai`
  ADD PRIMARY KEY (`MAloai`);

--
-- Indexes for table `log`
--
ALTER TABLE `log`
  ADD PRIMARY KEY (`Malog`),
  ADD KEY `accountid` (`accountid`);

--
-- Indexes for table `ncc`
--
ALTER TABLE `ncc`
  ADD PRIMARY KEY (`MANCC`),
  ADD UNIQUE KEY `SDT` (`SDT`);

--
-- Indexes for table `nhanvien`
--
ALTER TABLE `nhanvien`
  ADD PRIMARY KEY (`MANV`),
  ADD UNIQUE KEY `SDT` (`SDT`);

--
-- Indexes for table `nhomquyen`
--
ALTER TABLE `nhomquyen`
  ADD PRIMARY KEY (`MAQUYEN`);

--
-- Indexes for table `phanquyen`
--
ALTER TABLE `phanquyen`
  ADD PRIMARY KEY (`MAQUYEN`,`MACN`),
  ADD KEY `MACN` (`MACN`);

--
-- Indexes for table `phieubaohanh`
--
ALTER TABLE `phieubaohanh`
  ADD PRIMARY KEY (`MABH`),
  ADD KEY `MACTHD` (`MACTHD`),
  ADD KEY `MAKH` (`MAKH`);

--
-- Indexes for table `phieunhap`
--
ALTER TABLE `phieunhap`
  ADD PRIMARY KEY (`MAPNH`),
  ADD KEY `MANV` (`MANV`),
  ADD KEY `MANCC` (`MANCC`);

--
-- Indexes for table `sanpham`
--
ALTER TABLE `sanpham`
  ADD PRIMARY KEY (`MASP`),
  ADD KEY `MAloai` (`MAloai`),
  ADD KEY `NSX` (`NSX`);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `account`
--
ALTER TABLE `account`
  ADD CONSTRAINT `account_ibfk_1` FOREIGN KEY (`id`) REFERENCES `nhanvien` (`MANV`) ON DELETE CASCADE,
  ADD CONSTRAINT `account_ibfk_2` FOREIGN KEY (`quyen`) REFERENCES `nhomquyen` (`MAQUYEN`) ON DELETE SET NULL;

--
-- Constraints for table `anhsp`
--
ALTER TABLE `anhsp`
  ADD CONSTRAINT `anhsp_ibfk_1` FOREIGN KEY (`MASP`) REFERENCES `sanpham` (`MASP`) ON DELETE CASCADE;

--
-- Constraints for table `ctgg`
--
ALTER TABLE `ctgg`
  ADD CONSTRAINT `ctgg_ibfk_1` FOREIGN KEY (`MAGG`) REFERENCES `giamgia` (`MAGG`) ON DELETE CASCADE,
  ADD CONSTRAINT `ctgg_ibfk_2` FOREIGN KEY (`MASP`) REFERENCES `sanpham` (`MASP`) ON DELETE CASCADE;

--
-- Constraints for table `cthd`
--
ALTER TABLE `cthd`
  ADD CONSTRAINT `cthd_ibfk_1` FOREIGN KEY (`MAHD`) REFERENCES `hoadon` (`MAHD`) ON DELETE CASCADE,
  ADD CONSTRAINT `cthd_ibfk_2` FOREIGN KEY (`MACTSP`) REFERENCES `ctsp` (`MACTSP`);

--
-- Constraints for table `ctphieunhap`
--
ALTER TABLE `ctphieunhap`
  ADD CONSTRAINT `ctphieunhap_ibfk_1` FOREIGN KEY (`MASP`) REFERENCES `sanpham` (`MASP`),
  ADD CONSTRAINT `ctphieunhap_ibfk_2` FOREIGN KEY (`MAPNH`) REFERENCES `phieunhap` (`MAPNH`) ON DELETE CASCADE;

--
-- Constraints for table `ctsp`
--
ALTER TABLE `ctsp`
  ADD CONSTRAINT `ctsp_ibfk_1` FOREIGN KEY (`MASP`) REFERENCES `sanpham` (`MASP`),
  ADD CONSTRAINT `ctsp_ibfk_2` FOREIGN KEY (`MANCC`) REFERENCES `ncc` (`MANCC`),
  ADD CONSTRAINT `ctsp_ibfk_3` FOREIGN KEY (`MACTPN`) REFERENCES `ctphieunhap` (`MACTPN`);

--
-- Constraints for table `hoadon`
--
ALTER TABLE `hoadon`
  ADD CONSTRAINT `hoadon_ibfk_1` FOREIGN KEY (`MANV`) REFERENCES `nhanvien` (`MANV`),
  ADD CONSTRAINT `hoadon_ibfk_2` FOREIGN KEY (`MAKH`) REFERENCES `khachhang` (`MAKH`);

--
-- Constraints for table `log`
--
ALTER TABLE `log`
  ADD CONSTRAINT `log_ibfk_1` FOREIGN KEY (`accountid`) REFERENCES `account` (`id`) ON DELETE SET NULL;

--
-- Constraints for table `phanquyen`
--
ALTER TABLE `phanquyen`
  ADD CONSTRAINT `phanquyen_ibfk_1` FOREIGN KEY (`MAQUYEN`) REFERENCES `nhomquyen` (`MAQUYEN`) ON DELETE CASCADE,
  ADD CONSTRAINT `phanquyen_ibfk_2` FOREIGN KEY (`MACN`) REFERENCES `chucnang` (`MACN`) ON DELETE CASCADE;

--
-- Constraints for table `phieubaohanh`
--
ALTER TABLE `phieubaohanh`
  ADD CONSTRAINT `phieubaohanh_ibfk_1` FOREIGN KEY (`MACTHD`) REFERENCES `cthd` (`MACTHD`),
  ADD CONSTRAINT `phieubaohanh_ibfk_2` FOREIGN KEY (`MAKH`) REFERENCES `khachhang` (`MAKH`);

--
-- Constraints for table `phieunhap`
--
ALTER TABLE `phieunhap`
  ADD CONSTRAINT `phieunhap_ibfk_1` FOREIGN KEY (`MANV`) REFERENCES `nhanvien` (`MANV`),
  ADD CONSTRAINT `phieunhap_ibfk_2` FOREIGN KEY (`MANCC`) REFERENCES `ncc` (`MANCC`);

--
-- Constraints for table `sanpham`
--
ALTER TABLE `sanpham`
  ADD CONSTRAINT `sanpham_ibfk_1` FOREIGN KEY (`MAloai`) REFERENCES `loai` (`MAloai`),
  ADD CONSTRAINT `sanpham_ibfk_2` FOREIGN KEY (`NSX`) REFERENCES `hangsx` (`MANSX`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
