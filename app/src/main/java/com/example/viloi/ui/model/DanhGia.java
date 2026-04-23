package com.example.viloi.ui.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

/**
 * Model ánh xạ collection "danh_gia" trong Firestore
 */
public class DanhGia {

    private String id; // document ID (e.g. "rev001")

    @PropertyName("ma_nha_hang")
    private String maNhaHang;           // "res001"

    @PropertyName("ma_nguoi_dung")
    private String maNguoiDung;         // "user001"

    @PropertyName("ten_nguoi_dung")
    private String tenNguoiDung;        // "Nguyễn Văn A"

    @PropertyName("so_sao")
    private double soSao;               // 4.5

    @PropertyName("binh_luan")
    private String binhLuan;            // "Quán ngon, phục vụ tốt"

    @PropertyName("hien_thi")
    private boolean hienThi;            // true — hiển thị công khai

    @PropertyName("da_chinh_sua")
    private boolean daChinhSua;         // false

    @PropertyName("tao_luc")
    private Timestamp taoLuc;

    @PropertyName("cap_nhat_luc")
    private Timestamp capNhatLuc;

    // Empty constructor for Firestore
    public DanhGia() {}

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getMaNhaHang() { return maNhaHang; }
    public void setMaNhaHang(String maNhaHang) { this.maNhaHang = maNhaHang; }
    public String getMaNguoiDung() { return maNguoiDung; }
    public void setMaNguoiDung(String maNguoiDung) { this.maNguoiDung = maNguoiDung; }
    public String getTenNguoiDung() { return tenNguoiDung; }
    public void setTenNguoiDung(String tenNguoiDung) { this.tenNguoiDung = tenNguoiDung; }
    public double getSoSao() { return soSao; }
    public void setSoSao(double soSao) { this.soSao = soSao; }
    public String getBinhLuan() { return binhLuan; }
    public void setBinhLuan(String binhLuan) { this.binhLuan = binhLuan; }
    public boolean isHienThi() { return hienThi; }
    public void setHienThi(boolean hienThi) { this.hienThi = hienThi; }
    public boolean isDaChinhSua() { return daChinhSua; }
    public void setDaChinhSua(boolean daChinhSua) { this.daChinhSua = daChinhSua; }
    public Timestamp getTaoLuc() { return taoLuc; }
    public void setTaoLuc(Timestamp taoLuc) { this.taoLuc = taoLuc; }
    public Timestamp getCapNhatLuc() { return capNhatLuc; }
    public void setCapNhatLuc(Timestamp capNhatLuc) { this.capNhatLuc = capNhatLuc; }

    public String getSoSaoDisplay() { return String.format("%.1f", soSao); }
}

