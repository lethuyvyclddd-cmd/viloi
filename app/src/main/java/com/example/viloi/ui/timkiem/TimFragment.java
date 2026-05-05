package com.example.viloi.ui.timkiem;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viloi.R;
import com.example.viloi.ui.NhaHang.ChiTietNhaHangFragment;
import com.example.viloi.ui.adapter.TimKiemAdapter;
import com.example.viloi.ui.model.NhaHang;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class TimFragment extends Fragment {

    private EditText edtSearch;
    private ChipGroup chipGroupDanhmuc;
    private RecyclerView rvSearch;
    private TextView tvSoKetQua;
    private TextView tvEmpty;

    private TimKiemAdapter adapter;

    private final List<NhaHang> tatCa = new ArrayList<>();
    private final List<NhaHang> hienThi = new ArrayList<>();

    private FirebaseFirestore db;
    private String userId;

    private String filterDanhMuc = null; // null = tất cả

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tim, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        bindView(view);
        setupRecycler();
        setupSearch();
        loadData();
    }

    private void bindView(View view) {
        edtSearch = view.findViewById(R.id.edtSearch);
        chipGroupDanhmuc = view.findViewById(R.id.chipGroupDanhmuc);
        rvSearch = view.findViewById(R.id.rvSearchLichsutim);
        tvSoKetQua = view.findViewById(R.id.tvSoKetQua);
        tvEmpty = view.findViewById(R.id.tvEmpty);
    }

    private void setupRecycler() {

        adapter = new TimKiemAdapter(hienThi, nhaHang -> {

            luuLichSuTimKiem(nhaHang);

            Bundle args = new Bundle();
            args.putString(
                    ChiTietNhaHangFragment.ARG_MA_NHA_HANG,
                    nhaHang.getId()
            );

            Navigation.findNavController(requireView())
                    .navigate(
                            R.id.action_searchFragment_to_ChiTietNhaHangFragment,
                            args
                    );
        });

        rvSearch.setLayoutManager(new LinearLayoutManager(getContext()));
        rvSearch.setAdapter(adapter);
    }
    private void setupSearch() {

        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s,
                                          int start,
                                          int count,
                                          int after) {
            }

            @Override
            public void onTextChanged(CharSequence s,
                                      int start,
                                      int before,
                                      int count) {
                locDuLieu();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        chipGroupDanhmuc.setOnCheckedChangeListener(
                (group, checkedId) -> locDuLieu()
        );
    }

    private void loadData() {

        db.collection("nha_hang")
                .whereEqualTo("hoat_dong", true)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    tatCa.clear();

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {

                        NhaHang nh = doc.toObject(NhaHang.class);

                        if (nh != null) {
                            nh.setId(doc.getId());
                            tatCa.add(nh);
                        }
                    }

                    taoChipDanhMuc();
                    locDuLieu();
                });
    }

    private void taoChipDanhMuc() {

        chipGroupDanhmuc.removeAllViews();

        Chip chipTatCa = new Chip(requireContext());
        chipTatCa.setText("Tất cả");
        chipTatCa.setCheckable(true);
        chipTatCa.setChecked(true);
        chipTatCa.setId(View.generateViewId()); // FIX
        chipGroupDanhmuc.addView(chipTatCa);

        List<String> dsDanhMuc = new ArrayList<>();

        for (NhaHang nh : tatCa) {
            String ten = nh.getTenDanhMuc();
            if (ten != null && !dsDanhMuc.contains(ten)) {
                dsDanhMuc.add(ten);
            }
        }

        for (String tenDm : dsDanhMuc) {
            Chip chip = new Chip(requireContext());
            chip.setText(tenDm);
            chip.setCheckable(true);
            chip.setId(View.generateViewId()); // FIX
            chipGroupDanhmuc.addView(chip);
        }
    }

    private void locDuLieu() {

        hienThi.clear();

        String keyword = edtSearch.getText()
                .toString()
                .trim()
                .toLowerCase(Locale.ROOT);

        int checkedId = chipGroupDanhmuc.getCheckedChipId();

        if (checkedId != View.NO_ID) {
            Chip chip = chipGroupDanhmuc.findViewById(checkedId);

            if (chip != null) {
                String text = chip.getText().toString();

                if (text.equalsIgnoreCase("Tất cả")) {
                    filterDanhMuc = null;
                } else {
                    filterDanhMuc = text;
                }
            }
        }

        for (NhaHang nh : tatCa) {

            boolean hopTen = true;
            boolean hopDanhMuc = true;

            String ten = nh.getTen() == null
                    ? ""
                    : nh.getTen().toLowerCase(Locale.ROOT);

            String diaChi = nh.getDiaChi() == null
                    ? ""
                    : nh.getDiaChi().toLowerCase(Locale.ROOT);

            if (!keyword.isEmpty()) {
                hopTen = ten.contains(keyword)
                        || diaChi.contains(keyword);
            }

            if (filterDanhMuc != null) {

                String dm = nh.getTenDanhMuc();

                hopDanhMuc = dm != null
                        && dm.equalsIgnoreCase(filterDanhMuc);
            }

            if (hopTen && hopDanhMuc) {
                hienThi.add(nh);
            }
        }

        adapter.notifyDataSetChanged();
        tvSoKetQua.setText(hienThi.size() + " kết quả");
        if (hienThi.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            rvSearch.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            rvSearch.setVisibility(View.VISIBLE);
        }
    }

    private void luuLichSuTimKiem(NhaHang nhaHang) {

        if (userId == null || nhaHang == null) return;

        HashMap<String, Object> data = new HashMap<>();
        data.put("ma_nha_hang", nhaHang.getId());
        data.put("ten_nha_hang", nhaHang.getTen());
        data.put("thoi_gian", new com.google.firebase.Timestamp(new java.util.Date()));

        // FIX: dùng add thay vì document(id)
        db.collection("nguoi_dung")
                .document(userId)
                .collection("lich_su_tim_kiem")
                .add(data);

        db.collection("nha_hang")
                .document(nhaHang.getId())
                .update("luot_tim_kiem", FieldValue.increment(1));
    }
}