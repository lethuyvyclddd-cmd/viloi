package com.example.viloi.ui.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

/**
 * Model ánh xạ collection "danh_muc" trong Firestore
 * VD document: cat001
 */
public class DanhMuc {

    private String id;
    private String ten;
    private String icon;
    private String mauSac;
    private String moTa;
    private boolean hoatDong;
    private boolean macDinh;
    private int uuTien;
    private Timestamp taoLuc;
    private Timestamp capNhatLuc;

    public DanhMuc() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    @PropertyName("ten")
    public String getTen() { return ten; }

    @PropertyName("ten")
    public void setTen(String ten) { this.ten = ten; }

    @PropertyName("icon")
    public String getIcon() { return icon; }

    @PropertyName("icon")
    public void setIcon(String icon) { this.icon = icon; }

    @PropertyName("mau_sac")
    public String getMauSac() { return mauSac; }

    @PropertyName("mau_sac")
    public void setMauSac(String mauSac) { this.mauSac = mauSac; }

    @PropertyName("mo_ta")
    public String getMoTa() { return moTa; }

    @PropertyName("mo_ta")
    public void setMoTa(String moTa) { this.moTa = moTa; }

    @PropertyName("hoat_dong")
    public boolean getHoatDong() { return hoatDong; }

    @PropertyName("hoat_dong")
    public void setHoatDong(boolean hoatDong) { this.hoatDong = hoatDong; }

    @PropertyName("mac_dinh")
    public boolean getMacDinh() { return macDinh; }

    @PropertyName("mac_dinh")
    public void setMacDinh(boolean macDinh) { this.macDinh = macDinh; }

    @PropertyName("uu_tien")
    public int getUuTien() { return uuTien; }

    @PropertyName("uu_tien")
    public void setUuTien(int uuTien) { this.uuTien = uuTien; }

    @PropertyName("tao_luc")
    public Timestamp getTaoLuc() { return taoLuc; }

    @PropertyName("tao_luc")
    public void setTaoLuc(Timestamp taoLuc) { this.taoLuc = taoLuc; }

    @PropertyName("cap_nhat_luc")
    public Timestamp getCapNhatLuc() { return capNhatLuc; }

    @PropertyName("cap_nhat_luc")
    public void setCapNhatLuc(Timestamp capNhatLuc) { this.capNhatLuc = capNhatLuc; }

    public String getIconResourceName() {
        return (icon != null && !icon.isEmpty())
                ? icon
                : "ic_category_default";
    }
}