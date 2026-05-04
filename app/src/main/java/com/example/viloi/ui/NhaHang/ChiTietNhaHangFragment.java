package com.example.viloi.ui.NhaHang;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.viloi.R;
import com.example.viloi.ui.model.NhaHang;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.util.*;

public class ChiTietNhaHangFragment extends Fragment {

    // Key để truyền maNhaHang qua Bundle
    public static final String ARG_MA_NHA_HANG = "maNhaHang";

    private ImageView    ivAnh;
    private ImageView    btnBack, btnYeuThich;
    private TextView     tvTen, tvDanhMuc, tvSao, tvSoLuongDanhGia;
    private TextView     tvTrangThai, tvDiaChi, tvDienThoai;
    private TextView     tvGioMoCua, tvKhoangGia, tvMoTa;
    private TextView     tvLuotXem, tvLuotTim, tvLuotYeuThich;
    private ChipGroup    chipGroupThe;

    private FirebaseFirestore db;
    private FirebaseAuth      auth;
    private String userId;
    private String maNhaHang;
    private boolean daYeuThich = false;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chi_tiet_nha_hang, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db   = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) userId = auth.getCurrentUser().getUid();

        // Lấy maNhaHang từ arguments
        if (getArguments() != null) {
            maNhaHang = getArguments().getString(ARG_MA_NHA_HANG);
        }
        if (maNhaHang == null) {
            Navigation.findNavController(view).navigateUp();
            return;
        }

        bindViews(view);
        loadNhaHang();
        kiemTraYeuThich();

        btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
        btnYeuThich.setOnClickListener(v -> toggleYeuThich());
    }

    private void bindViews(View v) {
        ivAnh             = v.findViewById(R.id.ivAnhNhaHang);
        btnBack           = v.findViewById(R.id.btnBack);
        btnYeuThich       = v.findViewById(R.id.btnYeuThich);
        tvTen             = v.findViewById(R.id.tvTenNhaHang);
        tvDanhMuc         = v.findViewById(R.id.tvDanhMuc);
        tvSao             = v.findViewById(R.id.tvSao);
        tvSoLuongDanhGia  = v.findViewById(R.id.tvSoLuongDanhGia);
        tvTrangThai       = v.findViewById(R.id.tvTrangThai);
        tvDiaChi          = v.findViewById(R.id.tvDiaChi);
        tvDienThoai       = v.findViewById(R.id.tvDienThoai);
        tvGioMoCua        = v.findViewById(R.id.tvGioMoCua);
        tvKhoangGia       = v.findViewById(R.id.tvKhoangGia);
        tvMoTa            = v.findViewById(R.id.tvMoTa);
        tvLuotXem         = v.findViewById(R.id.tvLuotXem);
        tvLuotTim         = v.findViewById(R.id.tvLuotTim);
        tvLuotYeuThich    = v.findViewById(R.id.tvLuotYeuThich);
        chipGroupThe      = v.findViewById(R.id.chipGroupThe);
    }

    // ─── Load dữ liệu nhà hàng ───────────────────────────────
    private void loadNhaHang() {
        db.collection("nha_hang").document(maNhaHang)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!isAdded() || !doc.exists()) return;

                    NhaHang nh = doc.toObject(NhaHang.class);
                    if (nh == null) return;
                    nh.setId(doc.getId());

                    hienThiThongTin(nh);

                    // Tăng lượt xem
                    doc.getReference().update("luot_xem",
                            FieldValue.increment(1));
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Không tải được dữ liệu",
                                Toast.LENGTH_SHORT).show());
    }

    private void hienThiThongTin(NhaHang nh) {
        // Tên
        tvTen.setText(nh.getTen() != null ? nh.getTen() : "");

        // Danh mục
        tvDanhMuc.setText(nh.getTenDanhMuc() != null ? nh.getTenDanhMuc() : "");

        // Đánh giá
        tvSao.setText(nh.getRatingDisplay());
        tvSoLuongDanhGia.setText("(" + nh.getSoLuongDanhGia() + " đánh giá)");

        // Địa chỉ đầy đủ
        String dc = nh.getDiaChiDayDu() != null ? nh.getDiaChiDayDu() : nh.getDiaChi();
        tvDiaChi.setText(dc != null ? dc : "");

        // Điện thoại — bấm để gọi
        if (nh.getDienThoai() != null) {
            tvDienThoai.setText(nh.getDienThoai());
            tvDienThoai.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:" + nh.getDienThoai()));
                startActivity(intent);
            });
        }

        // Giờ mở cửa
        String gio = nh.getGioMoCua() + " – " + getGioDongCua(nh);
        tvGioMoCua.setText(gio);

        // Trạng thái mở/đóng cửa (đơn giản theo giờ hiện tại)
        tvTrangThai.setText(laMoCua(nh) ? "● Đang mở" : "● Đã đóng");
        tvTrangThai.setTextColor(getResources().getColor(
                laMoCua(nh) ? android.R.color.holo_green_dark
                        : android.R.color.holo_red_dark, null));

        // Khoảng giá — format "20000-50000" → "20.000đ – 50.000đ"
        tvKhoangGia.setText(formatGia(nh.getKhoangGia()));

        // Mô tả — dùng field "mo_ta" (không có trong model, lấy thủ công)
        // Nếu muốn thêm vào NhaHang model thì thêm field mo_ta
        tvMoTa.setText(""); // sẽ được set từ Firestore phía dưới

        // Tags
        chipGroupThe.removeAllViews();
        if (nh.getThe() != null) {
            for (String tag : nh.getThe()) {
                Chip chip = new Chip(requireContext());
                chip.setText(tag);
                chip.setClickable(false);
                chip.setChipBackgroundColorResource(android.R.color.darker_gray);;
                chipGroupThe.addView(chip);
            }
        }

        // Thống kê
        tvLuotXem.setText(String.valueOf(nh.getLuotXem()));
        tvLuotTim.setText(String.valueOf(nh.getLuotTimKiem()));
        tvLuotYeuThich.setText(String.valueOf(nh.getLuotYeuThich()));

        // Load ảnh (dùng Glide nếu có)
        // String anhUrl = nh.getFirstImageUrl();
        // if (anhUrl != null) Glide.with(this).load(anhUrl).into(ivAnh);

        // Load mo_ta riêng vì chưa có trong model
        loadMoTa();
    }

    /** Load thêm field mo_ta chưa có trong NhaHang model */
    private void loadMoTa() {
        db.collection("nha_hang").document(maNhaHang)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!isAdded()) return;
                    String moTa = doc.getString("mo_ta");
                    if (moTa != null) tvMoTa.setText(moTa);
                });
    }

    // ─── Kiểm tra đã yêu thích chưa ─────────────────────────
    // yeu_thich là nested MAP trong document nguoi_dung/{userId}
    private void kiemTraYeuThich() {
        if (userId == null) return;
        db.collection("nguoi_dung").document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!isAdded() || doc == null) return;

                    // Đọc nested map yeu_thich
                    Map<String, Object> mapYeuThich =
                            (Map<String, Object>) doc.get("yeu_thich");

                    daYeuThich = mapYeuThich != null && mapYeuThich.containsKey(maNhaHang);
                    capNhatIconYeuThich();
                });
    }

    // ─── Toggle yêu thích ────────────────────────────────────
    private void toggleYeuThich() {
        if (userId == null) return;

        DocumentReference userRef = db.collection("nguoi_dung").document(userId);
        DocumentReference nhRef   = db.collection("nha_hang").document(maNhaHang);

        if (daYeuThich) {
            // Bỏ yêu thích: xoá key trong map + giảm counter
            userRef.update("yeu_thich." + maNhaHang, FieldValue.delete())
                    .addOnSuccessListener(unused -> {
                        if (!isAdded()) return;
                        daYeuThich = false;
                        capNhatIconYeuThich();
                        nhRef.update("luot_yeu_thich", FieldValue.increment(-1));
                        // Cập nhật UI counter
                        int cur = Integer.parseInt(tvLuotYeuThich.getText().toString());
                        tvLuotYeuThich.setText(String.valueOf(Math.max(0, cur - 1)));
                        Toast.makeText(getContext(), "Đã bỏ yêu thích",
                                Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Thêm yêu thích: set key trong map + tăng counter
            Map<String, Object> entry = new HashMap<>();
            entry.put("ma_nha_hang", maNhaHang);
            entry.put("ten_nha_hang", tvTen.getText().toString());
            entry.put("them_luc", com.google.firebase.Timestamp.now());

            userRef.update("yeu_thich." + maNhaHang, entry)
                    .addOnSuccessListener(unused -> {
                        if (!isAdded()) return;
                        daYeuThich = true;
                        capNhatIconYeuThich();
                        nhRef.update("luot_yeu_thich", FieldValue.increment(1));
                        int cur = Integer.parseInt(tvLuotYeuThich.getText().toString());
                        tvLuotYeuThich.setText(String.valueOf(cur + 1));
                        Toast.makeText(getContext(), "Đã thêm yêu thích ❤️",
                                Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void capNhatIconYeuThich() {
        if (btnYeuThich == null) return;
        // Đổi màu icon tùy trạng thái
        btnYeuThich.setColorFilter(
                getResources().getColor(
                        daYeuThich ? android.R.color.holo_red_light
                                : android.R.color.white, null));
    }

    // ─── Helpers ─────────────────────────────────────────────
    private String getGioDongCua(NhaHang nh) {
        // Field gio_dong_cua chưa có trong model, đọc thủ công ở loadMoTa
        // Tạm return chuỗi rỗng, sẽ update sau khi load xong
        return "?";
    }

    /** Kiểm tra có đang trong giờ mở cửa không (đơn giản) */
    private boolean laMoCua(NhaHang nh) {
        try {
            String gioMo = nh.getGioMoCua(); // "07:00"
            if (gioMo == null) return true;
            String[] parts = gioMo.split(":");
            java.util.Calendar now = java.util.Calendar.getInstance();
            int nowH = now.get(java.util.Calendar.HOUR_OF_DAY);
            int openH = Integer.parseInt(parts[0]);
            // Giả định đóng lúc 21:00 nếu không có field
            return nowH >= openH && nowH < 21;
        } catch (Exception e) { return true; }
    }

    /** Format "20000-50000" → "20.000đ – 50.000đ" */
    private String formatGia(String raw) {
        if (raw == null || raw.isEmpty()) return "Chưa cập nhật";
        try {
            String[] parts = raw.split("-");
            if (parts.length == 2) {
                long min = Long.parseLong(parts[0].trim());
                long max = Long.parseLong(parts[1].trim());
                return String.format("%,dđ – %,dđ", min, max)
                        .replace(",", ".");
            }
        } catch (Exception ignored) {}
        return raw;
    }
}