package com.example.viloi.ui.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;
import java.util.List;

public class NhaHang {

    private String id;

    private String ten;
    private String tenDanhMuc;
    private String tenKhongDau;
    private String diaChi;
    private String diaChiDayDu;
    private String tinh;
    private String phuong;
    private String quanHuyen;
    private String dienThoai;
    private String gioMoCua;
    private String gioDongCua;
    private List<String> hinhAnh;
    private List<String> the;
    private String maDanhMuc;
    private boolean hoatDong;
    private boolean noiBat;
    private String soLa;
    private int luotTimKiem;
    private int luotXem;
    private int luotYeuThich;
    private int soLuongDanhGia;
    private double danhGiaTrungBinh;
    private Timestamp taoLuc;
    private Timestamp capNhatLuc;
    private int uuTien;
    private String khoangGia;
    private String moTa;

    public NhaHang() {}

    // ===== ID =====
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    // ===== STRING =====
    @PropertyName("ten")
    public String getTen() { return ten; }
    @PropertyName("ten")
    public void setTen(String ten) { this.ten = ten; }

    @PropertyName("ten_danh_muc")
    public String getTenDanhMuc() { return tenDanhMuc; }
    @PropertyName("ten_danh_muc")
    public void setTenDanhMuc(String v) { this.tenDanhMuc = v; }

    @PropertyName("ten_khong_dau")
    public String getTenKhongDau() { return tenKhongDau; }
    @PropertyName("ten_khong_dau")
    public void setTenKhongDau(String v) { this.tenKhongDau = v; }

    @PropertyName("dia_chi")
    public String getDiaChi() { return diaChi; }
    @PropertyName("dia_chi")
    public void setDiaChi(String v) { this.diaChi = v; }

    @PropertyName("dia_chi_day_du")
    public String getDiaChiDayDu() { return diaChiDayDu; }
    @PropertyName("dia_chi_day_du")
    public void setDiaChiDayDu(String v) { this.diaChiDayDu = v; }

    @PropertyName("tinh")
    public String getTinh() { return tinh; }
    @PropertyName("tinh")
    public void setTinh(String v) { this.tinh = v; }

    @PropertyName("phuong")
    public String getPhuong() { return phuong; }
    @PropertyName("phuong")
    public void setPhuong(String v) { this.phuong = v; }

    @PropertyName("quan_huyen")
    public String getQuanHuyen() { return quanHuyen; }
    @PropertyName("quan_huyen")
    public void setQuanHuyen(String v) { this.quanHuyen = v; }

    @PropertyName("dien_thoai")
    public String getDienThoai() { return dienThoai; }
    @PropertyName("dien_thoai")
    public void setDienThoai(String v) { this.dienThoai = v; }

    @PropertyName("gio_mo_cua")
    public String getGioMoCua() { return gioMoCua; }
    @PropertyName("gio_mo_cua")
    public void setGioMoCua(String v) { this.gioMoCua = v; }

    @PropertyName("gio_dong_cua")
    public String getGioDongCua() { return gioDongCua; }
    @PropertyName("gio_dong_cua")
    public void setGioDongCua(String v) { this.gioDongCua = v; }

    @PropertyName("khoang_gia")
    public String getKhoangGia() { return khoangGia; }
    @PropertyName("khoang_gia")
    public void setKhoangGia(String v) { this.khoangGia = v; }

    @PropertyName("mo_ta")
    public String getMoTa() { return moTa; }
    @PropertyName("mo_ta")
    public void setMoTa(String v) { this.moTa = v; }

    @PropertyName("ma_danh_muc")
    public String getMaDanhMuc() { return maDanhMuc; }
    @PropertyName("ma_danh_muc")
    public void setMaDanhMuc(String v) { this.maDanhMuc = v; }

    @PropertyName("so_la")
    public String getSoLa() { return soLa; }
    @PropertyName("so_la")
    public void setSoLa(String v) { this.soLa = v; }

    // ===== LIST =====
    @PropertyName("hinh_anh")
    public List<String> getHinhAnh() { return hinhAnh; }
    @PropertyName("hinh_anh")
    public void setHinhAnh(List<String> v) { this.hinhAnh = v; }

    @PropertyName("the")
    public List<String> getThe() { return the; }
    @PropertyName("the")
    public void setThe(List<String> v) { this.the = v; }

    // ===== BOOLEAN =====
    @PropertyName("hoat_dong")
    public boolean isHoatDong() { return hoatDong; }
    @PropertyName("hoat_dong")
    public void setHoatDong(boolean v) { this.hoatDong = v; }

    @PropertyName("noi_bat")
    public boolean isNoiBat() { return noiBat; }
    @PropertyName("noi_bat")
    public void setNoiBat(boolean v) { this.noiBat = v; }

    // ===== INT =====
    @PropertyName("luot_tim_kiem")
    public int getLuotTimKiem() { return luotTimKiem; }
    @PropertyName("luot_tim_kiem")
    public void setLuotTimKiem(int v) { this.luotTimKiem = v; }

    @PropertyName("luot_xem")
    public int getLuotXem() { return luotXem; }
    @PropertyName("luot_xem")
    public void setLuotXem(int v) { this.luotXem = v; }

    @PropertyName("luot_yeu_thich")
    public int getLuotYeuThich() { return luotYeuThich; }
    @PropertyName("luot_yeu_thich")
    public void setLuotYeuThich(int v) { this.luotYeuThich = v; }

    @PropertyName("so_luong_danh_gia")
    public int getSoLuongDanhGia() { return soLuongDanhGia; }
    @PropertyName("so_luong_danh_gia")
    public void setSoLuongDanhGia(int v) { this.soLuongDanhGia = v; }

    @PropertyName("uu_tien")
    public int getUuTien() { return uuTien; }
    @PropertyName("uu_tien")
    public void setUuTien(int v) { this.uuTien = v; }

    // ===== DOUBLE =====
    @PropertyName("danh_gia_trung_binh")
    public double getDanhGiaTrungBinh() { return danhGiaTrungBinh; }
    @PropertyName("danh_gia_trung_binh")
    public void setDanhGiaTrungBinh(double v) { this.danhGiaTrungBinh = v; }

    // ===== TIMESTAMP =====
    @PropertyName("tao_luc")
    public Timestamp getTaoLuc() { return taoLuc; }
    @PropertyName("tao_luc")
    public void setTaoLuc(Timestamp v) { this.taoLuc = v; }

    @PropertyName("cap_nhat_luc")
    public Timestamp getCapNhatLuc() { return capNhatLuc; }
    @PropertyName("cap_nhat_luc")
    public void setCapNhatLuc(Timestamp v) { this.capNhatLuc = v; }

    // ===== HELPER (GIỮ NGUYÊN) =====
    public String getFirstImageUrl() {
        if (hinhAnh != null && !hinhAnh.isEmpty()) return hinhAnh.get(0);
        return null;
    }

    public String getRatingDisplay() {
        return String.format("%.1f", danhGiaTrungBinh);
    }

    public String getLuotTimKiemDisplay() {
        return "Đã tìm " + luotTimKiem + " lần";
    }

    public String getLuotXemDisplay() {
        return "· " + luotXem + " lượt xem";
    }
}