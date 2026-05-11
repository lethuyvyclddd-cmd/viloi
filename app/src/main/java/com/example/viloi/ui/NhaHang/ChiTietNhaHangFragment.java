package com.example.viloi.ui.NhaHang;

import static android.text.TextUtils.isEmpty;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.viloi.R;
import com.example.viloi.ui.adapter.MiniReviewAdapter;
import com.example.viloi.ui.model.NhaHang;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class ChiTietNhaHangFragment extends Fragment {

    private static final String TAG = "ChiTietNhaHang";
    public static final String ARG_MA_NHA_HANG = "maNhaHang";

    // Views chi tiết
    private ImageView  ivAnh, btnBack, btnYeuThich;
    private TextView   tvTen, tvDanhMuc, tvSao, tvSoLuongDanhGia;
    private TextView   tvTrangThai, tvDiaChi, tvDienThoai;
    private TextView   tvGioMoCua, tvKhoangGia, tvMoTa;
    private TextView   tvLuotXem, tvLuotTim, tvLuotYeuThich;
    private ChipGroup  chipGroupThe;

    // Views đánh giá
    private TextView        tvAvgScore, tvTotalReviews, tvSortReview;
    private LinearLayout    layoutStarSelect, layoutBars;
    private EditText        etComment;
    private Button          btnSubmitReview;
    private RecyclerView    rvReviews;

    // State
    private FirebaseFirestore db;
    private FirebaseAuth      auth;
    private String userId, maNhaHang;
    private boolean daYeuThich   = false;
    private int     selectedStar = 0;
    private String  editingReviewId = null;
    private boolean sortNewest   = true;

    private MiniReviewAdapter reviewAdapter;

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

        if (getArguments() != null)
            maNhaHang = getArguments().getString(ARG_MA_NHA_HANG);

        if (maNhaHang == null) {
            Navigation.findNavController(view).navigateUp();
            return;
        }

        bindViews(view);
        setupStarSelector();
        setupReviewRecyclerView();
        loadNhaHang();
        loadDanhGia(true);

        // kiểm tra có yêu thích nhà hàng đó không
        kiemTraYeuThich();

        btnBack.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());

        // thêm / bỏ yêu thích
        btnYeuThich.setOnClickListener(v -> toggleYeuThich());
        btnSubmitReview.setOnClickListener(v -> guiDanhGia());
        tvSortReview.setOnClickListener(v -> {
            sortNewest = !sortNewest;
            tvSortReview.setText(sortNewest ? "Mới nhất ▾" : "Cũ nhất ▾");
            loadDanhGia(sortNewest);
        });

        tvDienThoai.setOnClickListener(v -> {
            String sdt = tvDienThoai.getText().toString().trim();
            if (!sdt.isEmpty()) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + sdt)));
            }
        });
    }

    // ─── BIND VIEWS ───────────────────────────────────────────
    private void bindViews(View v) {
        ivAnh            = v.findViewById(R.id.ivAnhNhaHang);
        btnBack          = v.findViewById(R.id.btnBack);
        btnYeuThich      = v.findViewById(R.id.btnYeuThich);
        tvTen            = v.findViewById(R.id.tvTenNhaHang);
        tvDanhMuc        = v.findViewById(R.id.tvDanhMuc);
        tvSao            = v.findViewById(R.id.tvSao);
        tvSoLuongDanhGia = v.findViewById(R.id.tvSoLuongDanhGia);
        tvTrangThai      = v.findViewById(R.id.tvTrangThai);
        tvDiaChi         = v.findViewById(R.id.tvDiaChi);
        tvDienThoai      = v.findViewById(R.id.tvDienThoai);
        tvGioMoCua       = v.findViewById(R.id.tvGioMoCua);
        tvKhoangGia      = v.findViewById(R.id.tvKhoangGia);
        tvMoTa           = v.findViewById(R.id.tvMoTa);
        tvLuotXem        = v.findViewById(R.id.tvLuotXem);
        tvLuotTim        = v.findViewById(R.id.tvLuotTim);
        tvLuotYeuThich   = v.findViewById(R.id.tvLuotYeuThich);
        chipGroupThe     = v.findViewById(R.id.chipGroupThe);

        tvAvgScore       = v.findViewById(R.id.tvAvgScore);
        tvTotalReviews   = v.findViewById(R.id.tvTotalReviews);
        tvSortReview     = v.findViewById(R.id.tvSortReview);
        layoutStarSelect = v.findViewById(R.id.layoutStarSelect);
        layoutBars       = v.findViewById(R.id.layoutBars);
        etComment        = v.findViewById(R.id.etComment);
        btnSubmitReview  = v.findViewById(R.id.btnSubmitReview);
        rvReviews        = v.findViewById(R.id.rvReviews);
    }

    // ─── LOAD NHÀ HÀNG ────────────────────────────────────────
    private void loadNhaHang() {
        db.collection("nha_hang").document(maNhaHang)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!isAdded() || !doc.exists()) return;
                    NhaHang nh = doc.toObject(NhaHang.class);
                    if (nh == null) return;
                    nh.setId(doc.getId());

                    // hiển thị thông tin nhà hàng
                    hienThiThongTin(nh);
                    doc.getReference().update("luot_xem", FieldValue.increment(1));

                    // ghi lịch sử tìm kiếm
                    if (userId != null) ghiLichSuTimKiem(nh);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Không tải được dữ liệu", Toast.LENGTH_SHORT).show());
    }

    private void hienThiThongTin(NhaHang nh) {
        tvTen.setText(nh.getTen() != null ? nh.getTen() : "");
        tvDanhMuc.setText(nh.getTenDanhMuc() != null ? nh.getTenDanhMuc() : "");

        double sao = nh.getDanhGiaTrungBinh();
        tvSao.setText(sao > 0 ? String.format(Locale.getDefault(), "%.1f", sao) : "—");
        tvSoLuongDanhGia.setText("(" + nh.getSoLuongDanhGia() + " đánh giá)");

        String dc = !isEmpty(nh.getDiaChiDayDu()) ? nh.getDiaChiDayDu() : nh.getDiaChi();
        tvDiaChi.setText(dc != null ? dc : "");

        if (!isEmpty(nh.getDienThoai())) tvDienThoai.setText(nh.getDienThoai());

        String gioMo = nh.getGioMoCua() != null ? nh.getGioMoCua() : "07:00";
        String gioDong = nh.getGioDongCua() != null ? nh.getGioDongCua() : "21:00";
        tvGioMoCua.setText(gioMo + " – " + gioDong);

        boolean moCua = laMoCua(gioMo, gioDong);
        tvTrangThai.setText(moCua ? "● Đang mở" : "● Đã đóng");
        tvTrangThai.setTextColor(getResources().getColor(
                moCua ? android.R.color.holo_green_dark : android.R.color.holo_red_dark, null));

        tvKhoangGia.setText(formatGia(nh.getKhoangGia()));
        tvMoTa.setText(!isEmpty(nh.getMoTa()) ? nh.getMoTa() : "Chưa có mô tả.");

        chipGroupThe.removeAllViews();
        if (nh.getThe() != null) {
            for (String tag : nh.getThe()) {
                Chip chip = new Chip(requireContext());
                chip.setText(tag);
                chip.setClickable(false);
                chipGroupThe.addView(chip);
            }
        }

        tvLuotXem.setText(String.valueOf(nh.getLuotXem()));
        tvLuotTim.setText(String.valueOf(nh.getLuotTimKiem()));
        tvLuotYeuThich.setText(String.valueOf(nh.getLuotYeuThich()));

        List<String> hinhs = nh.getHinhAnh();
        if (hinhs != null && !hinhs.isEmpty()) {
            Glide.with(this)
                    .load(hinhs.get(0))
                    .placeholder(R.drawable.bg_image_placeholder)
                    .centerCrop()
                    .into(ivAnh);
        }
    }

    // ─── GHI LỊCH SỬ TÌM KIẾM ────────────────────────────────
    private void ghiLichSuTimKiem(NhaHang nh) {
        DocumentReference histRef = db.collection("nguoi_dung")
                .document(userId)
                .collection("lich_su_tim_kiem")
                .document(maNhaHang);

        db.runTransaction(transaction -> {
            DocumentSnapshot snap = transaction.get(histRef);
            long soLan = snap.exists() ? snap.getLong("so_lan_tim") : 0L;
            long soLanMoi = soLan + 1;

            Map<String, Object> data = new HashMap<>();
            data.put("ma_nha_hang", maNhaHang);
            data.put("ten_nha_hang", nh.getTen());
            data.put("so_lan_tim", soLanMoi);
            data.put("lan_tim_cuoi", FieldValue.serverTimestamp());
            transaction.set(histRef, data);

            transaction.update(
                    db.collection("nha_hang").document(maNhaHang),
                    "luot_tim_kiem", FieldValue.increment(1)
            );

            if (soLanMoi >= 3 && !daYeuThich) {
                DocumentReference favRef = db.collection("nguoi_dung")
                        .document(userId)
                        .collection("yeu_thich")
                        .document(maNhaHang);
                DocumentSnapshot favSnap = transaction.get(favRef);
                if (!favSnap.exists()) {
                    Map<String, Object> fav = new HashMap<>();
                    fav.put("ma_nha_hang", maNhaHang);
                    fav.put("ten_nha_hang", nh.getTen());
                    fav.put("them_luc", FieldValue.serverTimestamp());
                    transaction.set(favRef, fav);
                }
            }
            return null;
        });
    }

    // ─── YÊU THÍCH ────────────────────────────────────────────
    private void kiemTraYeuThich() {
        if (userId == null) return;
        db.collection("nguoi_dung").document(userId)
                .collection("yeu_thich").document(maNhaHang)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!isAdded()) return;
                    daYeuThich = doc.exists();
                    capNhatIconYeuThich();
                });
    }

    private void toggleYeuThich() {
        if (userId == null) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }
        DocumentReference favRef = db.collection("nguoi_dung")
                .document(userId).collection("yeu_thich").document(maNhaHang);
        DocumentReference nhRef = db.collection("nha_hang").document(maNhaHang);

        if (daYeuThich) {
            favRef.delete().addOnSuccessListener(unused -> {
                if (!isAdded()) return;
                daYeuThich = false;
                capNhatIconYeuThich();
                nhRef.update("luot_yeu_thich", FieldValue.increment(-1));
                int cur = Integer.parseInt(tvLuotYeuThich.getText().toString());
                tvLuotYeuThich.setText(String.valueOf(Math.max(0, cur - 1)));
                Toast.makeText(getContext(), "Đã bỏ yêu thích", Toast.LENGTH_SHORT).show();
            });
        } else {
            Map<String, Object> entry = new HashMap<>();
            entry.put("ma_nha_hang", maNhaHang);
            entry.put("ten_nha_hang", tvTen.getText().toString());
            entry.put("them_luc", FieldValue.serverTimestamp());
            favRef.set(entry).addOnSuccessListener(unused -> {
                if (!isAdded()) return;
                daYeuThich = true;
                capNhatIconYeuThich(); // đổi màu icon yêu thích
                nhRef.update("luot_yeu_thich", FieldValue.increment(1));
                int cur = Integer.parseInt(tvLuotYeuThich.getText().toString());
                tvLuotYeuThich.setText(String.valueOf(cur + 1));
                Toast.makeText(getContext(), "Đã thêm yêu thích ❤️", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void capNhatIconYeuThich() {
        if (btnYeuThich == null) return;
        btnYeuThich.setColorFilter(getResources().getColor(
                daYeuThich ? android.R.color.holo_red_light : android.R.color.white, null));
    }

    // ─── CHỌN SAO ─────────────────────────────────────────────
    private void setupStarSelector() {
        if (layoutStarSelect == null) return;
        layoutStarSelect.removeAllViews();
        for (int i = 1; i <= 5; i++) {
            final int starValue = i;
            TextView star = new TextView(requireContext());
            star.setText("★");
            star.setTextSize(28f);
            star.setTextColor(getResources().getColor(R.color.star_empty, null));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMarginEnd(4);
            star.setLayoutParams(lp);
            star.setOnClickListener(v -> chonSao(starValue));
            layoutStarSelect.addView(star);
        }
    }

    private void chonSao(int sao) {
        selectedStar = sao;
        for (int i = 0; i < layoutStarSelect.getChildCount(); i++) {
            TextView tv = (TextView) layoutStarSelect.getChildAt(i);
            tv.setTextColor(getResources().getColor(
                    i < sao ? R.color.star_color : R.color.star_empty, null));
        }
    }

    // ─── RECYCLERVIEW ─────────────────────────────────────────
    private void setupReviewRecyclerView() {
        if (rvReviews == null) return;

        reviewAdapter = new MiniReviewAdapter(
                userId != null ? userId : "",
                review -> moCheDoSua(review),
                review -> xacNhanXoa(review)
        );

        // FIX: LinearLayoutManager thông thường, không dùng setAutoMeasureEnabled (deprecated)
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvReviews.setLayoutManager(layoutManager);
        rvReviews.setNestedScrollingEnabled(false);
        rvReviews.setHasFixedSize(false);
        rvReviews.setAdapter(reviewAdapter);
    }

    // ─── LOAD ĐÁNH GIÁ ────────────────────────────────────────
    // FIX CHÍNH: Bỏ whereEqualTo("hien_thi", true) để tránh lỗi thiếu Composite Index.
    // Thay vào đó lọc hien_thi trên client. Nếu muốn giữ server-side filter,
    // bạn phải tạo Composite Index trong Firestore Console:
    //   Collection: danh_gia | Fields: ma_nha_hang ASC, hien_thi ASC, tao_luc DESC/ASC
    private void loadDanhGia(boolean newest) {
        Query.Direction dir = newest ? Query.Direction.DESCENDING : Query.Direction.ASCENDING;

        db.collection("danh_gia")
                .whereEqualTo("ma_nha_hang", maNhaHang)
                // ĐÃ BỎ: .whereEqualTo("hien_thi", true)  ← gây lỗi thiếu composite index
                .orderBy("tao_luc", dir)
                .get()
                .addOnSuccessListener(result -> {
                    if (!isAdded()) return;

                    List<Map<String, Object>> danhGias = new ArrayList<>();
                    List<Double> soSaoList = new ArrayList<>();

                    for (DocumentSnapshot doc : result.getDocuments()) {
                        Map<String, Object> item = doc.getData();
                        if (item == null) continue;

                        // FIX: lọc hien_thi trên client thay vì server
                        Object hienThi = item.get("hien_thi");
                        if (Boolean.FALSE.equals(hienThi)) continue; // bỏ qua bị ẩn

                        item.put("id", doc.getId());
                        danhGias.add(item);

                        Object ss = item.get("so_sao");
                        if (ss != null) soSaoList.add(((Number) ss).doubleValue());
                    }

                    Log.d(TAG, "Loaded " + danhGias.size() + " reviews");

                    // FIX: chỉ gọi setData, không gọi notifyDataSetChanged() thêm
                    reviewAdapter.setData(danhGias);
                    capNhatTomTatDanhGia(soSaoList);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "loadDanhGia error: " + e.getMessage(), e);
                    Toast.makeText(getContext(),
                            "Lỗi tải đánh giá: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    // ─── TÓM TẮT ĐIỂM + THANH BAR ────────────────────────────
    private void capNhatTomTatDanhGia(List<Double> soSaoList) {
        if (tvAvgScore == null || layoutBars == null) return;

        int total = soSaoList.size();
        if (total == 0) {
            tvAvgScore.setText("—");
            tvTotalReviews.setText("Chưa có đánh giá");
            tvSao.setText("—");
            tvSoLuongDanhGia.setText("(0 đánh giá)");
            layoutBars.removeAllViews();
            return;
        }

        double avg = 0;
        int[] counts = new int[6];
        for (double s : soSaoList) {
            avg += s;
            int idx = (int) Math.round(s);
            if (idx >= 1 && idx <= 5) counts[idx]++;
        }
        avg /= total;

        String avgText = String.format(Locale.getDefault(), "%.1f", avg);
        tvAvgScore.setText(avgText);
        tvTotalReviews.setText(total + " đánh giá");
        tvSao.setText(avgText);
        tvSoLuongDanhGia.setText("(" + total + " đánh giá)");

        layoutBars.removeAllViews();
        for (int star = 5; star >= 1; star--) {
            float percent = (float) counts[star] / total;
            layoutBars.addView(taoHangBar(star, counts[star], percent));
        }
    }

    private View taoHangBar(int star, int count, float percent) {
        LinearLayout row = new LinearLayout(requireContext());
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(android.view.Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams rowLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        rowLp.bottomMargin = 6;
        row.setLayoutParams(rowLp);

        TextView lbl = new TextView(requireContext());
        lbl.setText(star + "★");
        lbl.setTextSize(12f);
        lbl.setTextColor(getResources().getColor(R.color.text_secondary, null));
        lbl.setLayoutParams(new LinearLayout.LayoutParams(64, LinearLayout.LayoutParams.WRAP_CONTENT));

        FrameLayout track = new FrameLayout(requireContext());
        LinearLayout.LayoutParams trackLp = new LinearLayout.LayoutParams(0,
                (int)(getResources().getDisplayMetrics().density * 6), 1f);
        trackLp.setMarginStart(8); trackLp.setMarginEnd(8);
        track.setLayoutParams(trackLp);
        track.setBackgroundResource(R.drawable.bg_bar_track);

        View fill = new View(requireContext());
        fill.setBackgroundResource(R.drawable.bg_bar_fill);
        final float p = percent;
        track.post(() -> {
            int w = (int)(track.getWidth() * p);
            FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(
                    Math.max(w, 0), FrameLayout.LayoutParams.MATCH_PARENT);
            fill.setLayoutParams(flp);
        });
        track.addView(fill);

        TextView cnt = new TextView(requireContext());
        cnt.setText(String.valueOf(count));
        cnt.setTextSize(11f);
        cnt.setTextColor(getResources().getColor(R.color.text_secondary, null));
        cnt.setLayoutParams(new LinearLayout.LayoutParams(40, LinearLayout.LayoutParams.WRAP_CONTENT));

        row.addView(lbl); row.addView(track); row.addView(cnt);
        return row;
    }

    // ─── GỬI ĐÁNH GIÁ ────────────────────────────────────────
    private void guiDanhGia() {
        if (userId == null) {
            Toast.makeText(getContext(), "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedStar == 0) {
            Toast.makeText(getContext(), "Vui lòng chọn số sao", Toast.LENGTH_SHORT).show();
            return;
        }
        String comment = etComment.getText().toString().trim();
        if (comment.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập bình luận", Toast.LENGTH_SHORT).show();
            return;
        }

        String userName = auth.getCurrentUser() != null
                ? auth.getCurrentUser().getDisplayName() : "Ẩn danh";
        if (isEmpty(userName)) userName = "Người dùng";

        if (editingReviewId != null) {
            capNhatDanhGia(editingReviewId, selectedStar, comment);
        } else {
            themDanhGiaMoi(userId, userName, selectedStar, comment);
        }
    }

    private void themDanhGiaMoi(String uid, String tenUser, int sao, String binhLuan) {
        // FIX: bỏ whereEqualTo("hien_thi",...) ở đây cũng để tránh lỗi index
        db.collection("danh_gia")
                .whereEqualTo("ma_nha_hang", maNhaHang)
                .whereEqualTo("ma_nguoi_dung", uid)
                .get()
                .addOnSuccessListener(existing -> {
                    if (!isAdded()) return;
                    if (!existing.isEmpty()) {
                        Toast.makeText(getContext(),
                                "Bạn đã đánh giá quán này rồi!\nNhấn Sửa để thay đổi.",
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    Map<String, Object> data = new HashMap<>();
                    data.put("ma_nha_hang", maNhaHang);
                    data.put("ma_nguoi_dung", uid);
                    data.put("ten_nguoi_dung", tenUser);
                    data.put("so_sao", (double) sao);
                    data.put("binh_luan", binhLuan);
                    data.put("tao_luc", FieldValue.serverTimestamp());
                    data.put("cap_nhat_luc", FieldValue.serverTimestamp());
                    data.put("da_chinh_sua", false);
                    data.put("hien_thi", true);

                    db.collection("danh_gia").add(data)
                            .addOnSuccessListener(ref -> {
                                if (!isAdded()) return;
                                tinhLaiDiemTB();
                                resetForm();
                                loadDanhGia(sortNewest);
                                Toast.makeText(getContext(), "Đã gửi đánh giá!", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show());
                });
    }

    private void capNhatDanhGia(String reviewId, int sao, String binhLuan) {
        db.collection("danh_gia").document(reviewId)
                .update("so_sao", (double) sao,
                        "binh_luan", binhLuan,
                        "da_chinh_sua", true,
                        "cap_nhat_luc", FieldValue.serverTimestamp())
                .addOnSuccessListener(unused -> {
                    if (!isAdded()) return;
                    tinhLaiDiemTB();
                    resetForm();
                    loadDanhGia(sortNewest);
                    Toast.makeText(getContext(), "Đã cập nhật!", Toast.LENGTH_SHORT).show();
                });
    }

    private void xacNhanXoa(Map<String, Object> review) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Xóa đánh giá")
                .setMessage("Bạn chắc chắn muốn xóa?")
                .setPositiveButton("Xóa", (d, w) -> {
                    String id = (String) review.get("id");
                    if (id == null) return;
                    db.collection("danh_gia").document(id).delete()
                            .addOnSuccessListener(unused -> {
                                if (!isAdded()) return;
                                tinhLaiDiemTB();
                                loadDanhGia(sortNewest);
                                Toast.makeText(getContext(), "Đã xóa", Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void moCheDoSua(Map<String, Object> review) {
        editingReviewId = (String) review.get("id");
        Object ss = review.get("so_sao");
        if (ss != null) chonSao(((Number) ss).intValue());
        Object bl = review.get("binh_luan");
        if (bl != null) etComment.setText(bl.toString());
        btnSubmitReview.setText("Cập nhật đánh giá");
        if (etComment != null) etComment.requestFocus();
    }

    // ─── TÍNH LẠI ĐIỂM TB ────────────────────────────────────
    private void tinhLaiDiemTB() {
        db.collection("danh_gia")
                .whereEqualTo("ma_nha_hang", maNhaHang)
                // FIX: bỏ whereEqualTo("hien_thi", true) ở đây cũng
                .get()
                .addOnSuccessListener(result -> {
                    List<Double> saoList = new ArrayList<>();
                    for (DocumentSnapshot doc : result.getDocuments()) {
                        // Lọc client-side
                        Object hienThi = doc.get("hien_thi");
                        if (Boolean.FALSE.equals(hienThi)) continue;

                        Object ss = doc.get("so_sao");
                        if (ss != null) saoList.add(((Number) ss).doubleValue());
                    }
                    double avg = saoList.isEmpty() ? 0.0
                            : saoList.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

                    db.collection("nha_hang").document(maNhaHang)
                            .update("danh_gia_trung_binh", avg,
                                    "so_luong_danh_gia", saoList.size());
                });
    }

    // ─── RESET FORM ───────────────────────────────────────────
    private void resetForm() {
        etComment.setText("");
        selectedStar = 0;
        chonSao(0);
        editingReviewId = null;
        btnSubmitReview.setText("Gửi đánh giá");
    }

    // ─── HELPERS ──────────────────────────────────────────────
    private boolean laMoCua(String gioMo, String gioDong) {
        try {
            int mo = Integer.parseInt(gioMo.split(":")[0]);
            int dong = Integer.parseInt(gioDong.split(":")[0]);
            int now = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            return now >= mo && now < dong;
        } catch (Exception e) { return true; }
    }

    private String formatGia(String raw) {
        if (raw == null || raw.isEmpty()) return "Chưa cập nhật";
        try {
            String[] parts = raw.split("-");
            if (parts.length == 2) {
                long min = Long.parseLong(parts[0].trim());
                long max = Long.parseLong(parts[1].trim());
                return String.format("%,dđ – %,dđ", min, max).replace(",", ".");
            }
        } catch (Exception ignored) {}
        return raw;
    }
}