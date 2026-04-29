package com.example.viloi.ui.NhaHang;

import android.os.Bundle;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;

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

        adapter = new NhaHangAdapter(list);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        if (getArguments() != null) {
            maDanhMuc = getArguments().getString("maDanhMuc");
            tenDanhMuc = getArguments().getString("tenDanhMuc");
        }

        txtTenDanhMuc.setText(tenDanhMuc);

        ivBack.setOnClickListener(v ->
                NavHostFragment.findNavController(this).navigateUp());

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
                        nh.setId(doc.getId());
                        list.add(nh);
                    }

                    adapter.notifyDataSetChanged();
                });
    }
}