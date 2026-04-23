package com.example.viloi.ui.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;
import java.util.List;

/**
 * Model ánh xạ collection "nguoi_dung" trong Firestore
 */
public class NguoiDung {

    private String id; // document ID (e.g. "user001" hoặc Firebase Auth UID)

    @PropertyName("email")
    private String email;                       // "a@gmail.com"

    @PropertyName("ten_hien_thi")
    private String tenHienThi;                  // "Nguyễn Văn A"

    @PropertyName("vai_tro")
    private String vaiTro;                      // "user" | "admin"

    @PropertyName("hoat_dong")
    private boolean hoatDong;                   // true/false

    @PropertyName("tong_tim_kiem")
    private int tongTimKiem;                    // 10

    @PropertyName("tao_luc")
    private Timestamp taoLuc;                   // "2026-04-21T10:00:00Z"

    @PropertyName("lan_hoat_dong_cuoi")
    private Timestamp lanHoatDongCuoi;          // last active

    // Sub-collection "yeu_thich" và "lich_su_tim_kiem" được load riêng

    // Empty constructor for Firestore
    public NguoiDung() {}

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTenHienThi() { return tenHienThi; }
    public void setTenHienThi(String tenHienThi) { this.tenHienThi = tenHienThi; }
    public String getVaiTro() { return vaiTro; }
    public void setVaiTro(String vaiTro) { this.vaiTro = vaiTro; }
    public boolean isHoatDong() { return hoatDong; }
    public void setHoatDong(boolean hoatDong) { this.hoatDong = hoatDong; }
    public int getTongTimKiem() { return tongTimKiem; }
    public void setTongTimKiem(int tongTimKiem) { this.tongTimKiem = tongTimKiem; }
    public Timestamp getTaoLuc() { return taoLuc; }
    public void setTaoLuc(Timestamp taoLuc) { this.taoLuc = taoLuc; }
    public Timestamp getLanHoatDongCuoi() { return lanHoatDongCuoi; }
    public void setLanHoatDongCuoi(Timestamp lanHoatDongCuoi) { this.lanHoatDongCuoi = lanHoatDongCuoi; }

    /** Lấy chữ cái đầu của tên để hiển thị avatar */
    public String getInitial() {
        if (tenHienThi != null && !tenHienThi.isEmpty()) {
            return String.valueOf(tenHienThi.charAt(0)).toUpperCase();
        }
        if (email != null && !email.isEmpty()) {
            return String.valueOf(email.charAt(0)).toUpperCase();
        }
        return "U";
    }

    public boolean isAdmin() { return "admin".equals(vaiTro); }
}
