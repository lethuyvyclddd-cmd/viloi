package com.example.viloi.ui.DanhMuc;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.viloi.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class ThemDanhMucFragment extends Fragment {

    private ImageView btnBack;
    private EditText  edtTenDanhMuc, edtMoTa, edtIcon;
    private Button    btnThemDanhMuc;

    private FirebaseFirestore db;
    private boolean isLoading = false;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_them_danh_muc, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        bindView(view);
        initClick();
    }

    private void bindView(View view) {
        btnBack        = view.findViewById(R.id.btnBack_themdm);
        edtTenDanhMuc  = view.findViewById(R.id.edtTenDanhMuc);
        edtMoTa        = view.findViewById(R.id.edtMoTa);
        edtIcon        = view.findViewById(R.id.edtIcon);
        btnThemDanhMuc = view.findViewById(R.id.btnThemDanhMuc);
    }

    private void initClick() {
        btnBack.setOnClickListener(v ->
                requireActivity().getOnBackPressedDispatcher().onBackPressed());

        btnThemDanhMuc.setOnClickListener(v -> validateAndSave());
    }

    private void validateAndSave() {
        if (isLoading) return;

        String ten  = edtTenDanhMuc.getText().toString().trim();
        String moTa = edtMoTa.getText().toString().trim();
        String icon = edtIcon.getText().toString().trim();

        // Tên bắt buộc
        if (TextUtils.isEmpty(ten)) {
            edtTenDanhMuc.setError("Nhập tên danh mục");
            edtTenDanhMuc.requestFocus();
            return;
        }
        if (ten.length() < 2) {
            edtTenDanhMuc.setError("Ít nhất 2 ký tự");
            edtTenDanhMuc.requestFocus();
            return;
        }

        // Icon mặc định nếu bỏ trống
        if (TextUtils.isEmpty(icon)) {
            icon = "ic_category_default";
        }

        kiemTraTenTrung(ten, moTa, icon);
    }

    private void kiemTraTenTrung(String ten, String moTa, String icon) {
        setLoading(true);

        db.collection("danh_muc")
                .whereEqualTo("ten", ten)
                .limit(1)
                .get()
                .addOnSuccessListener(snapshots -> {
                    if (!isAdded()) return;

                    if (!snapshots.isEmpty()) {
                        setLoading(false);
                        edtTenDanhMuc.setError("Tên đã tồn tại");
                        edtTenDanhMuc.requestFocus();
                    } else {
                        layUuTienVaLuu(ten, moTa, icon);
                    }
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    setLoading(false);
                    showToast("Lỗi kiểm tra trùng tên");
                });
    }

    private void layUuTienVaLuu(String ten, String moTa, String icon) {
        db.collection("danh_muc")
                .orderBy("uu_tien", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(snapshots -> {
                    if (!isAdded()) return;

                    int uuTienMoi = 1;
                    if (!snapshots.isEmpty()) {
                        Long max = snapshots.getDocuments().get(0).getLong("uu_tien");
                        if (max != null) uuTienMoi = max.intValue() + 1;
                    }

                    luuDanhMuc(ten, moTa, icon, uuTienMoi);
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    luuDanhMuc(ten, moTa, icon, 99); // fallback
                });
    }


    private void luuDanhMuc(String ten, String moTa, String icon, int uuTien) {
        Timestamp now = Timestamp.now();

        Map<String, Object> data = new HashMap<>();
        data.put("ten",          ten);
        data.put("mo_ta",        moTa);
        data.put("icon",         icon);
        data.put("mau_sac",      "");
        data.put("hoat_dong",    true);
        data.put("mac_dinh",     false);
        data.put("uu_tien",      uuTien);
        data.put("tao_luc",      now);
        data.put("cap_nhat_luc", now);

        db.collection("danh_muc")
                .add(data)
                .addOnSuccessListener(docRef -> {
                    if (!isAdded()) return;
                    setLoading(false);
                    showToast("✅ Thêm \"" + ten + "\" thành công");
                    clearForm();
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    setLoading(false);
                    showToast("❌ Lưu thất bại: " + e.getMessage());
                });
    }

    private void setLoading(boolean loading) {
        isLoading = loading;
        btnThemDanhMuc.setEnabled(!loading);
        btnThemDanhMuc.setText(loading ? "Đang lưu..." : "Thêm danh mục");
        btnThemDanhMuc.setAlpha(loading ? 0.6f : 1f);
    }

    private void clearForm() {
        edtTenDanhMuc.setText("");
        edtMoTa.setText("");
        edtIcon.setText("");
    }

    private void showToast(String msg) {
        if (isAdded() && getContext() != null)
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}