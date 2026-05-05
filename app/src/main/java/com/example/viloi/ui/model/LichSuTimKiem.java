package com.example.viloi.ui.model;

import com.google.firebase.Timestamp;

import com.google.firebase.firestore.PropertyName;

public class LichSuTimKiem {

    private String ten_nha_hang;

    @PropertyName("ten_nha_hang")
    public String getTenNhaHang() {
        return ten_nha_hang;
    }

    @PropertyName("ten_nha_hang")
    public void setTenNhaHang(String ten_nha_hang) {
        this.ten_nha_hang = ten_nha_hang;
    }


    private String id; // 🔥 thêm cái này
    private String ma_nha_hang;
    private Timestamp thoi_gian;

    public LichSuTimKiem() {}


    // ===== GETTER =====
    public String getMaNhaHang() {
        return ma_nha_hang;
    }

    public Timestamp getThoiGian() {
        return thoi_gian;
    }

    public String getId() {
        return id;
    }

    // ===== SETTER =====
    public void setMaNhaHang(String ma_nha_hang) {
        this.ma_nha_hang = ma_nha_hang;
    }


    public void setThoiGian(Timestamp thoi_gian) {
        this.thoi_gian = thoi_gian;
    }

    public void setId(String id) {
        this.id = id;
    }
}