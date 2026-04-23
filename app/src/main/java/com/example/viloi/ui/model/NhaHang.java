package com.example.viloi.ui.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;
import java.util.List;

/**
 * Model ánh xạ collection "nha_hang" trong Firestore
 */
public class NhaHang {

    private String id; // document ID (e.g. "res001")

    @PropertyName("ten")
    private String ten;

    @PropertyName("ten_danh_muc")
    private String tenDanhMuc;

    @PropertyName("ten_khong_dau")
    private String tenKhongDau;

    @PropertyName("dia_chi")
    private String diaChi;

    @PropertyName("dia_chi_day_du")
    private String diaChiDayDu;

    @PropertyName("tinh")
    private String tinh;

    @PropertyName("phuong")
    private String phuong;

    @PropertyName("quan_huyen")
    private String quanHuyen;

    @PropertyName("dien_thoai")
    private String dienThoai;

    @PropertyName("gio_mo_cua")
    private String gioMoCua;

    @PropertyName("gia_mo_cua")
    private String giaMoCua;

    @PropertyName("hinh_anh")
    private List<String> hinhAnh;

    @PropertyName("the")
    private List<String> the;

    @PropertyName("ma_danh_muc")
    private String maDanhMuc;

    @PropertyName("hoat_dong")
    private boolean hoatDong;

    @PropertyName("noi_bat")
    private boolean noiBat;

    @PropertyName("so_la")
    private String soLa;

    @PropertyName("luot_tim_kiem")
    private int luotTimKiem;

    @PropertyName("luot_xem")
    private int luotXem;

    @PropertyName("luot_yeu_thich")
    private int luotYeuThich;

    @PropertyName("so_luong_danh_gia")
    private int soLuongDanhGia;

    @PropertyName("danh_gia_trung_binh")
    private double danhGiaTrungBinh;

    @PropertyName("tao_luc")
    private Timestamp taoLuc;

    @PropertyName("cap_nhat_luc")
    private Timestamp capNhatLuc;

    @PropertyName("uu_tien")
    private int uuTien;

    // Empty constructor for Firestore
    public NhaHang() {}

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }
    public String getTenDanhMuc() { return tenDanhMuc; }
    public void setTenDanhMuc(String v) { this.tenDanhMuc = v; }
    public String getTenKhongDau() { return tenKhongDau; }
    public void setTenKhongDau(String v) { this.tenKhongDau = v; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String v) { this.diaChi = v; }
    public String getDiaChiDayDu() { return diaChiDayDu; }
    public void setDiaChiDayDu(String v) { this.diaChiDayDu = v; }
    public String getTinh() { return tinh; }
    public void setTinh(String v) { this.tinh = v; }
    public String getPhuong() { return phuong; }
    public void setPhuong(String v) { this.phuong = v; }
    public String getQuanHuyen() { return quanHuyen; }
    public void setQuanHuyen(String v) { this.quanHuyen = v; }
    public String getDienThoai() { return dienThoai; }
    public void setDienThoai(String v) { this.dienThoai = v; }
    public String getGioMoCua() { return gioMoCua; }
    public void setGioMoCua(String v) { this.gioMoCua = v; }
    public String getGiaMoCua() { return giaMoCua; }
    public void setGiaMoCua(String v) { this.giaMoCua = v; }
    public List<String> getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(List<String> v) { this.hinhAnh = v; }
    public List<String> getThe() { return the; }
    public void setThe(List<String> v) { this.the = v; }
    public String getMaDanhMuc() { return maDanhMuc; }
    public void setMaDanhMuc(String v) { this.maDanhMuc = v; }
    public boolean isHoatDong() { return hoatDong; }
    public void setHoatDong(boolean v) { this.hoatDong = v; }
    public boolean isNoiBat() { return noiBat; }
    public void setNoiBat(boolean v) { this.noiBat = v; }
    public String getSoLa() { return soLa; }
    public void setSoLa(String v) { this.soLa = v; }
    public int getLuotTimKiem() { return luotTimKiem; }
    public void setLuotTimKiem(int v) { this.luotTimKiem = v; }
    public int getLuotXem() { return luotXem; }
    public void setLuotXem(int v) { this.luotXem = v; }
    public int getLuotYeuThich() { return luotYeuThich; }
    public void setLuotYeuThich(int v) { this.luotYeuThich = v; }
    public int getSoLuongDanhGia() { return soLuongDanhGia; }
    public void setSoLuongDanhGia(int v) { this.soLuongDanhGia = v; }
    public double getDanhGiaTrungBinh() { return danhGiaTrungBinh; }
    public void setDanhGiaTrungBinh(double v) { this.danhGiaTrungBinh = v; }
    public Timestamp getTaoLuc() { return taoLuc; }
    public void setTaoLuc(Timestamp v) { this.taoLuc = v; }
    public Timestamp getCapNhatLuc() { return capNhatLuc; }
    public void setCapNhatLuc(Timestamp v) { this.capNhatLuc = v; }
    public int getUuTien() { return uuTien; }
    public void setUuTien(int v) { this.uuTien = v; }

    // Display helpers
    public String getFirstImageUrl() {
        if (hinhAnh != null && !hinhAnh.isEmpty()) return hinhAnh.get(0);
        return null;
    }
    public String getRatingDisplay() { return String.format("%.1f", danhGiaTrungBinh); }
    public String getLuotTimKiemDisplay() { return "Đã tìm " + luotTimKiem + " lần"; }
    public String getLuotXemDisplay() { return "· " + luotXem + " lượt xem"; }
}
