package com.example.viloi.ui.NguoiDung;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.viloi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class NguoiDungFragment extends Fragment {

    // Views
    private TextView tvUserName, tvUserEmail, tvUserStatus;
    private TextView avatarUser;
    private TextView tvFavoriteCount, tvVisitCount, tvReviewCount;
    private LinearLayout layoutFavorites, layoutHistory, layoutMyReviews;
    private LinearLayout layoutSettings, layoutLogout;

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nguoi_dung, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db   = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null) {
            // Chưa đăng nhập → về màn hình login
            navigateToLogin();
            return;
        }
        userId = firebaseUser.getUid();

        bindViews(view);
        loadUserInfo();
        loadStats();
        setupClickListeners();
    }

    // ─────────────────────────────────────────────
    // BIND VIEWS
    // ─────────────────────────────────────────────
    private void bindViews(View view) {
        avatarUser       = view.findViewById(R.id.avatarUser);
        tvUserName       = view.findViewById(R.id.tvUserName);
        tvUserEmail      = view.findViewById(R.id.tvUserEmail);
        tvUserStatus     = view.findViewById(R.id.tvUserStatus);
        tvFavoriteCount  = view.findViewById(R.id.tvFavoriteCount);
        tvVisitCount     = view.findViewById(R.id.tvVisitCount);
        tvReviewCount    = view.findViewById(R.id.tvReviewCount);
        layoutFavorites  = view.findViewById(R.id.layoutFavorites);
        layoutHistory    = view.findViewById(R.id.layoutHistory);
        layoutMyReviews  = view.findViewById(R.id.layoutMyReviews);
        layoutSettings   = view.findViewById(R.id.layoutSettings);
        layoutLogout     = view.findViewById(R.id.layoutLogout);
    }

    // ─────────────────────────────────────────────
    // LOAD THÔNG TIN NGƯỜI DÙNG
    // collection: nguoi_dung / {userId}
    // fields: ten_hien_thi, email, vai_tro, tong_tim_kiem
    // ─────────────────────────────────────────────
    private void loadUserInfo() {
        db.collection("nguoi_dung")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!isAdded() || doc == null) return;

                    String tenHienThi = doc.getString("ten_hien_thi");
                    String email      = doc.getString("email");
                    String vaiTro     = doc.getString("vai_tro");
                    Long tongTK       = doc.getLong("tong_tim_kiem");

                    // Tên & avatar chữ cái đầu
                    if (tenHienThi != null && !tenHienThi.isEmpty()) {
                        tvUserName.setText(tenHienThi);
                        avatarUser.setText(String.valueOf(tenHienThi.charAt(0)).toUpperCase());
                    }

                    // Email
                    if (email != null) tvUserEmail.setText(email);

                    // Vai trò badge
                    if ("admin".equals(vaiTro)) {
                        tvUserStatus.setText("Quản trị viên");
                    } else {
                        tvUserStatus.setText("Người dùng");
                    }

                    // Số lần đã tìm (stats giữa)
                    if (tongTK != null) tvVisitCount.setText(String.valueOf(tongTK));
                })
                .addOnFailureListener(e -> showError("Không tải được thông tin người dùng"));
    }

    // ─────────────────────────────────────────────
    // LOAD THỐNG KÊ (3 con số)
    // ─────────────────────────────────────────────
    private void loadStats() {
        // 1. Đếm Yêu thích: sub-collection nguoi_dung/{userId}/yeu_thich
        db.collection("nguoi_dung")
                .document(userId)
                .collection("yeu_thich")
                .get()
                .addOnSuccessListener((QuerySnapshot snap) -> {
                    if (!isAdded()) return;
                    tvFavoriteCount.setText(String.valueOf(snap.size()));

                    // Cập nhật subtitle menu "X quán đã lưu"
                    updateFavoriteSubtitle(snap.size());
                });

        // 2. Đếm Lịch sử: sub-collection nguoi_dung/{userId}/lich_su_tim_kiem
        db.collection("nguoi_dung")
                .document(userId)
                .collection("lich_su_tim_kiem")
                .get()
                .addOnSuccessListener((QuerySnapshot snap) -> {
                    if (!isAdded()) return;
                    // tvVisitCount đã dùng tong_tim_kiem từ loadUserInfo,
                    // nhưng nếu muốn đếm chính xác từ sub-collection:
                    // tvVisitCount.setText(String.valueOf(snap.size()));
                    updateHistorySubtitle(snap.size());
                });

        // 3. Đếm Đánh giá: collection danh_gia where ma_nguoi_dung == userId
        db.collection("danh_gia")
                .whereEqualTo("ma_nguoi_dung", userId)
                .get()
                .addOnSuccessListener((QuerySnapshot snap) -> {
                    if (!isAdded()) return;
                    tvReviewCount.setText(String.valueOf(snap.size()));
                    updateReviewSubtitle(snap.size());
                });
    }

    // ─────────────────────────────────────────────
    // CẬP NHẬT SUBTITLE CÁC MỤC MENU
    // ─────────────────────────────────────────────
    private void updateFavoriteSubtitle(int count) {
        if (!isAdded()) return;
        View menu = getView();
        if (menu == null) return;
        TextView tv = menu.findViewWithTag("subtitle_favorites");
        if (tv == null) {
            // Tìm theo vị trí trong layout (TextView thứ 2 trong layoutFavorites)
            if (layoutFavorites != null && layoutFavorites.getChildCount() >= 2) {
                View inner = layoutFavorites.getChildAt(1);
                if (inner instanceof LinearLayout) {
                    View child = ((LinearLayout) inner).getChildAt(1);
                    if (child instanceof TextView) {
                        ((TextView) child).setText(count + " quán đã lưu");
                    }
                }
            }
        }
    }

    private void updateHistorySubtitle(int count) {
        if (!isAdded() || layoutHistory == null) return;
        if (layoutHistory.getChildCount() >= 2) {
            View inner = layoutHistory.getChildAt(1);
            if (inner instanceof LinearLayout) {
                View child = ((LinearLayout) inner).getChildAt(1);
                if (child instanceof TextView) {
                    ((TextView) child).setText(count + " lần tìm kiếm");
                }
            }
        }
    }

    private void updateReviewSubtitle(int count) {
        if (!isAdded() || layoutMyReviews == null) return;
        if (layoutMyReviews.getChildCount() >= 2) {
            View inner = layoutMyReviews.getChildAt(1);
            if (inner instanceof LinearLayout) {
                View child = ((LinearLayout) inner).getChildAt(1);
                if (child instanceof TextView) {
                    ((TextView) child).setText(count + " đánh giá");
                }
            }
        }
    }

    // ─────────────────────────────────────────────
    // CLICK LISTENERS
    // ─────────────────────────────────────────────
    private void setupClickListeners() {
        // Nút sửa hồ sơ (icon pencil trên header)
        if (getView() != null) {
            getView().findViewById(R.id.ivEditND)
                    .setOnClickListener(v -> navigateToEditProfile());
        }

        // Quán yêu thích
        layoutFavorites.setOnClickListener(v -> navigateToFavorites());

        // Lịch sử tìm kiếm
        layoutHistory.setOnClickListener(v -> navigateToHistory());

        // Đánh giá của tôi
        layoutMyReviews.setOnClickListener(v -> navigateToMyReviews());

        // Cài đặt
        layoutSettings.setOnClickListener(v -> navigateToSettings());

        // Đăng xuất
        layoutLogout.setOnClickListener(v -> showLogoutDialog());
    }

    // ─────────────────────────────────────────────
    // NAVIGATION
    // ─────────────────────────────────────────────
    private void navigateToEditProfile() {
        // Navigation.findNavController(requireView())
        //           .navigate(R.id.action_hoSo_to_editProfile);
        Toast.makeText(getContext(), "Chỉnh sửa hồ sơ", Toast.LENGTH_SHORT).show();
    }

    private void navigateToFavorites() {
        // Navigation.findNavController(requireView())
        //           .navigate(R.id.action_hoSo_to_yeuThich);
        Toast.makeText(getContext(), "Quán yêu thích", Toast.LENGTH_SHORT).show();
    }

    private void navigateToHistory() {
        // Navigation.findNavController(requireView())
        //           .navigate(R.id.action_hoSo_to_lichSu);
        Toast.makeText(getContext(), "Lịch sử tìm kiếm", Toast.LENGTH_SHORT).show();
    }

    private void navigateToMyReviews() {
        // Navigation.findNavController(requireView())
        //           .navigate(R.id.action_hoSo_to_danhGia);
        Toast.makeText(getContext(), "Đánh giá của tôi", Toast.LENGTH_SHORT).show();
    }

    private void navigateToSettings() {
        // Navigation.findNavController(requireView())
        //           .navigate(R.id.action_hoSo_to_settings);
        Toast.makeText(getContext(), "Cài đặt", Toast.LENGTH_SHORT).show();
    }

    private void navigateToLogin() {
        // startActivity(new Intent(getActivity(), LoginActivity.class));
        // getActivity().finish();
    }

    // ─────────────────────────────────────────────
    // ĐĂNG XUẤT
    // ─────────────────────────────────────────────
    private void showLogoutDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> doLogout())
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void doLogout() {
        // Cập nhật lan_hoat_dong_cuoi trước khi logout
        if (userId != null) {
            db.collection("nguoi_dung")
                    .document(userId)
                    .update("lan_hoat_dong_cuoi",
                            com.google.firebase.Timestamp.now())
                    .addOnCompleteListener(task -> {
                        auth.signOut();
                        navigateToLogin();
                    });
        } else {
            auth.signOut();
            navigateToLogin();
        }
    }

    private void showError(String msg) {
        if (isAdded() && getContext() != null)
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}