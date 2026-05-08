package com.example.viloi.ui.NhaHang;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viloi.R;
import com.example.viloi.ui.adapter.NhaHangAdapter;
import com.example.viloi.ui.model.NhaHang;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class NhaHangFragment extends Fragment {

    TextView txtTenDanhMuc, txtThemNhaHang;
    ImageView ivBack;
    RecyclerView recyclerView;

    FirebaseFirestore db;

    List<NhaHang> list = new ArrayList<>();
    NhaHangAdapter adapter;

    String maDanhMuc, tenDanhMuc;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_nha_hang, container, false);

        txtTenDanhMuc = view.findViewById(R.id.tetTenDanhMuc);
        txtThemNhaHang = view.findViewById(R.id.txtThemNhaHang);
        initClick();
        ivBack = view.findViewById(R.id.ivBack);
        recyclerView = view.findViewById(R.id.rv_DS_nha_hang);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(getContext())
        );

        adapter = new NhaHangAdapter(list, nhaHang -> {
            Bundle args = new Bundle();
            args.putString(ChiTietNhaHangFragment.ARG_MA_NHA_HANG, nhaHang.getId());
            NavHostFragment.findNavController(this)
                    .navigate(R.id.ChiTietNhaHangFragment, args);
        });
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            maDanhMuc = getArguments().getString("maDanhMuc");
            tenDanhMuc = getArguments().getString("tenDanhMuc");
        }

        txtTenDanhMuc.setText(tenDanhMuc);

        ivBack.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp());

        adapter.setOnSuaListener((nhaHang, position) -> {
            // Ví dụ: mở màn hình chỉnh sửa
            Intent intent = new Intent(getContext(), SuaNhaHangFragment.class);
            intent.putExtra("id", nhaHang.getId());
            startActivity(intent);
        });

        adapter.setOnXoaListener((nhaHang, position) -> {
            // Hiện dialog xác nhận trước khi xóa
            new AlertDialog.Builder(getContext())
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc muốn xóa \"" + nhaHang.getTen() + "\" không?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        // Gọi xóa trên server/db rồi cập nhật list
                        adapter.removeItem(position);
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        loadData();

        return view;
    }

    private void initClick() {

        txtThemNhaHang.setOnClickListener(v -> {

            NavHostFragment.findNavController(this)
                    .navigate(R.id.themNhaHangFragment);

        });

    }

    private void loadData() {
        db.collection("nha_hang")
                .whereEqualTo("ma_danh_muc", maDanhMuc)
                .whereEqualTo("hoat_dong", true)
                .get()
                .addOnSuccessListener(query -> {
                    list.clear();
                    for (var doc : query.getDocuments()) {
                        NhaHang nh = doc.toObject(NhaHang.class);
                        if (nh != null) {
                            nh.setId(doc.getId());
                            list.add(nh);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Lỗi tải dữ liệu: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
    }
}