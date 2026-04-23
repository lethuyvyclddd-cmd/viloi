package com.example.viloi.ui.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

/**
 * Model ánh xạ collection "danh_muc" trong Firestore
 * VD document: cat001
 */
public class DanhMuc {

    private String id; // document ID (e.g. "cat001")

    @PropertyName("ten")
    private String ten;             // "Nhà hàng cao cấp"

    @PropertyName("icon")
    private String icon;            // "ic_luxury" — tên drawable resource

    @PropertyName("mau_sac")
    private String mauSac;          // "#9C27B0" — hex màu nền icon

    @PropertyName("mo_ta")
    private String moTa;            // "Không gian sang trọng, giá cao"

    @PropertyName("hoat_dong")
    private boolean hoatDong;       // true/false

    @PropertyName("mac_dinh")
    private boolean macDinh;        // hiện trên home mặc định

    @PropertyName("uu_tien")
    private int uuTien;             // thứ tự hiển thị

    @PropertyName("tao_luc")
    private Timestamp taoLuc;

    @PropertyName("cap_nhat_luc")
    private Timestamp capNhatLuc;

    // Empty constructor for Firestore
    public DanhMuc() {}

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    public String getMauSac() { return mauSac; }
    public void setMauSac(String mauSac) { this.mauSac = mauSac; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
    public boolean isHoatDong() { return hoatDong; }
    public void setHoatDong(boolean hoatDong) { this.hoatDong = hoatDong; }
    public boolean isMacDinh() { return macDinh; }
    public void setMacDinh(boolean macDinh) { this.macDinh = macDinh; }
    public int getUuTien() { return uuTien; }
    public void setUuTien(int uuTien) { this.uuTien = uuTien; }
    public Timestamp getTaoLuc() { return taoLuc; }
    public void setTaoLuc(Timestamp taoLuc) { this.taoLuc = taoLuc; }
    public Timestamp getCapNhatLuc() { return capNhatLuc; }
    public void setCapNhatLuc(Timestamp capNhatLuc) { this.capNhatLuc = capNhatLuc; }

    /**
     * Lấy resource ID của icon theo tên drawable.
     * Gọi: context.getResources().getIdentifier(danhMuc.getIcon(), "drawable", context.getPackageName())
     */
    public String getIconResourceName() { return icon != null ? icon : "ic_category_default"; }
}
