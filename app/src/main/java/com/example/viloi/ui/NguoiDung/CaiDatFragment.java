package com.example.viloi.ui.NguoiDung;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.viloi.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CaiDatFragment extends Fragment {

    private ImageView   btnBack;
    private EditText    etTenHienThi;
    private TextView    tvEmailReadonly;
    private Button      btnLuu;
    private LinearLayout layoutDoiMatKhau;

    private FirebaseFirestore db;
    private FirebaseAuth      auth;
    private String            userId;

    public CaiDatFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cai_dat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db   = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Navigation.findNavController(view).popBackStack();
            return;
        }
        userId = user.getUid();

        bindViews(view);
        loadUserInfo();
        setupClickListeners(view);
    }

    // ─── BIND VIEWS ───────────────────────────────────────────
    private void bindViews(View view) {
        btnBack          = view.findViewById(R.id.btnBack);
        etTenHienThi     = view.findViewById(R.id.etTenHienThi);
        tvEmailReadonly  = view.findViewById(R.id.tvEmailReadonly);
        btnLuu           = view.findViewById(R.id.btnLuu);
        layoutDoiMatKhau = view.findViewById(R.id.layoutDoiMatKhau);
    }

    // ─── LOAD THÔNG TIN TỪ FIRESTORE ─────────────────────────
    private void loadUserInfo() {
        db.collection("nguoi_dung")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!isAdded() || doc == null) return;

                    String ten   = doc.getString("ten_hien_thi");
                    String email = doc.getString("email");

                    if (ten != null)   etTenHienThi.setText(ten);
                    if (email != null) tvEmailReadonly.setText(email);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(),
                                "Không tải được thông tin", Toast.LENGTH_SHORT).show());
    }

    // ─── CLICK LISTENERS ──────────────────────────────────────
    private void setupClickListeners(View view) {

        // Nút back
        btnBack.setOnClickListener(v ->
                Navigation.findNavController(view).popBackStack());

        // Nút lưu
        btnLuu.setOnClickListener(v -> luuThayDoi());

        // Đổi mật khẩu
        layoutDoiMatKhau.setOnClickListener(v -> guiEmailDoiMatKhau());
    }

    // ─── LƯU THAY ĐỔI ────────────────────────────────────────
    private void luuThayDoi() {
        String tenMoi = etTenHienThi.getText().toString().trim();

        if (tenMoi.isEmpty()) {
            etTenHienThi.setError("Vui lòng nhập tên hiển thị");
            etTenHienThi.requestFocus();
            return;
        }

        btnLuu.setEnabled(false);
        btnLuu.setText("Đang lưu...");

        // 1. Cập nhật Firestore
        Map<String, Object> data = new HashMap<>();
        data.put("ten_hien_thi", tenMoi);
        data.put("cap_nhat_luc", com.google.firebase.Timestamp.now());

        db.collection("nguoi_dung")
                .document(userId)
                .update(data)
                .addOnSuccessListener(unused -> {

                    // 2. Cập nhật displayName trên FirebaseAuth luôn
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        UserProfileChangeRequest profileUpdate =
                                new UserProfileChangeRequest.Builder()
                                        .setDisplayName(tenMoi)
                                        .build();
                        user.updateProfile(profileUpdate);
                    }

                    if (!isAdded()) return;
                    btnLuu.setEnabled(true);
                    btnLuu.setText("Lưu thay đổi");
                    Toast.makeText(getContext(),
                            "Đã lưu thông tin ✓", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    btnLuu.setEnabled(true);
                    btnLuu.setText("Lưu thay đổi");
                    Toast.makeText(getContext(),
                            "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    // ─── ĐỔI MẬT KHẨU QUA EMAIL ──────────────────────────────
    private void guiEmailDoiMatKhau() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null || user.getEmail() == null) return;

        auth.sendPasswordResetEmail(user.getEmail())
                .addOnSuccessListener(unused ->
                        Toast.makeText(getContext(),
                                "Đã gửi email đổi mật khẩu đến " + user.getEmail(),
                                Toast.LENGTH_LONG).show())
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(),
                                "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }
}