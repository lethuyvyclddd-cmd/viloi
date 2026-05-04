package com.example.viloi.ui.DanhMuc;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.viloi.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.*;

import java.util.HashMap;
import java.util.Map;

public class SuaDanhMucFragment extends Fragment {

    public static final String ARG_MA_DANH_MUC = "maDanhMuc";

    private EditText edtTen, edtMoTa, edtIcon;
    private Button   btnSua;
    private FirebaseFirestore db;
    private String maDanhMuc;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sua_danh_muc, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        if (getArguments() != null)
            maDanhMuc = getArguments().getString(ARG_MA_DANH_MUC);

        edtTen  = view.findViewById(R.id.edtSuaTenDanhMuc);
        edtMoTa = view.findViewById(R.id.edtSuaMoTa);
        edtIcon = view.findViewById(R.id.edtIconSua);
        btnSua  = view.findViewById(R.id.btnSuaDanhMuc);

        view.findViewById(R.id.btnBack_themdm).setOnClickListener(v ->
                Navigation.findNavController(v).navigateUp());

        if (maDanhMuc != null) loadDanhMuc();

        btnSua.setOnClickListener(v -> validate());
    }

    private void loadDanhMuc() {
        db.collection("danh_muc").document(maDanhMuc)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!isAdded() || !doc.exists()) return;
                    String ten  = doc.getString("ten");
                    String moTa = doc.getString("mo_ta");
                    String icon = doc.getString("icon");
                    if (ten  != null) edtTen.setText(ten);
                    if (moTa != null) edtMoTa.setText(moTa);
                    if (icon != null) edtIcon.setText(icon);
                });
    }

    private void validate() {
        String ten = edtTen.getText().toString().trim();
        if (ten.isEmpty()) {
            edtTen.setError("Bắt buộc");
            return;
        }
        btnSua.setEnabled(false);
        btnSua.setText("Đang lưu...");
        luu(ten);
    }

    private void luu(String ten) {
        String moTa = edtMoTa.getText().toString().trim();
        String icon = edtIcon.getText().toString().trim();

        Map<String, Object> data = new HashMap<>();
        data.put("ten",          ten);
        data.put("mo_ta",        moTa);
        data.put("cap_nhat_luc", Timestamp.now());
        if (!icon.isEmpty()) data.put("icon", icon);

        db.collection("danh_muc").document(maDanhMuc)
                .update(data)
                .addOnSuccessListener(unused -> {
                    if (!isAdded()) return;
                    Toast.makeText(getContext(), "Đã cập nhật danh mục ✓",
                            Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigateUp();
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    btnSua.setEnabled(true);
                    btnSua.setText("Sửa danh mục");
                    Toast.makeText(getContext(), "Lỗi: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }
}