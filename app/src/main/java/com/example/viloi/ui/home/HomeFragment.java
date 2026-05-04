package com.example.viloi.ui.home;

import android.os.Bundle;
import android.view.*;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viloi.FirebaseHelper;
import com.example.viloi.R;
import com.example.viloi.ui.adapter.DanhMucAdapter;
import com.example.viloi.ui.adapter.GoiYAdapter;
import com.example.viloi.ui.adapter.HotAdapter;
import com.example.viloi.ui.model.DanhMuc;
import com.example.viloi.ui.model.NguoiDung;
import com.example.viloi.ui.model.NhaHang;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private TextView tvLocation, tvUserInitial;
    private RecyclerView rvDanhMuc, rvGoiY, rvHot;

    private DanhMucAdapter danhMucAdapter;
    private GoiYAdapter goiYAdapter;
    private HotAdapter hotAdapter;

    private final List<DanhMuc> danhMucList = new ArrayList<>();
    private final List<NhaHang> goiYList = new ArrayList<>();
    private final List<NhaHang> hotList = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private int soGoiYToiDa = 5;
    private int nguongTimKiem = 3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        bindViews(view);
        setupRecycler();
        setupUserInfo();
        loadConfig();
    }

    private void bindViews(View view) {
        tvLocation = view.findViewById(R.id.tv_location);
        tvUserInitial = view.findViewById(R.id.tv_user_initial);

        rvDanhMuc = view.findViewById(R.id.rv_categories);
        rvGoiY = view.findViewById(R.id.rv_suggested);
        rvHot = view.findViewById(R.id.rv_hot_restaurants);

        view.findViewById(R.id.tv_view_all_categories)
                .setOnClickListener(v ->
                        NavHostFragment.findNavController(this)
                                .navigate(R.id.categoryFragment));

        view.findViewById(R.id.tv_view_all_suggested)
                .setOnClickListener(v ->
                        NavHostFragment.findNavController(this)
                                .navigate(R.id.searchFragment));

        view.findViewById(R.id.tv_view_all_hot)
                .setOnClickListener(v ->
                        NavHostFragment.findNavController(this)
                                .navigate(R.id.searchFragment));
    }

    private void setupRecycler() {

        rvDanhMuc.setLayoutManager(
                new LinearLayoutManager(getContext(),
                        LinearLayoutManager.HORIZONTAL, false));

        danhMucAdapter = new DanhMucAdapter(
                danhMucList,
                this::openDanhMuc
        );

        rvDanhMuc.setAdapter(danhMucAdapter);

        rvGoiY.setLayoutManager(
                new LinearLayoutManager(getContext(),
                        LinearLayoutManager.HORIZONTAL, false));

        goiYAdapter = new GoiYAdapter(
                goiYList,
                this::openNhaHang
        );

        rvGoiY.setAdapter(goiYAdapter);

        rvHot.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHot.setNestedScrollingEnabled(false);

        hotAdapter = new HotAdapter(
                hotList,
                this::openNhaHang
        );

        rvHot.setAdapter(hotAdapter);
    }

    private void setupUserInfo() {

        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        db.collection("nguoi_dung")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(doc -> {

                    if (!isAdded()) return;

                    if (doc.exists()) {
                        try {
                            NguoiDung nd = doc.toObject(NguoiDung.class);

                            if (nd != null) {
                                tvUserInitial.setText(nd.getInitial());
                            }

                        } catch (Exception e) {
                            tvUserInitial.setText("U");
                        }
                    }
                });
    }

    private void loadConfig() {

        db.collection("cau_hinh_app")
                .document("goi_y")
                .get()
                .addOnSuccessListener(doc -> {

                    if (doc.exists()) {

                        Long so = doc.getLong("so_goi_y_toi_da");
                        Long nguong = doc.getLong("nguong_tim_kiem");

                        if (so != null) soGoiYToiDa = so.intValue();
                        if (nguong != null) nguongTimKiem = nguong.intValue();
                    }

                    loadData();
                })
                .addOnFailureListener(e -> loadData());
    }

    private void loadData() {
        loadDanhMuc();
        loadHot();
        loadSuggestedRealtime();
    }

    private void loadDanhMuc() {

        db.collection("danh_muc")
                .whereEqualTo("hoat_dong", true)
                .limit(5)
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (!isAdded()) return;

                    danhMucList.clear();

                    for (QueryDocumentSnapshot doc : snapshot) {
                        try {
                            DanhMuc dm = doc.toObject(DanhMuc.class);
                            dm.setId(doc.getId());
                            danhMucList.add(dm);

                        } catch (Exception ignored) {
                        }
                    }

                    danhMucAdapter.notifyDataSetChanged();
                });
    }

    private void loadHot() {

        db.collection("nha_hang")
                .whereEqualTo("hoat_dong", true)
                .limit(10)
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (!isAdded()) return;

                    hotList.clear();

                    for (QueryDocumentSnapshot doc : snapshot) {
                        try {
                            NhaHang nh = doc.toObject(NhaHang.class);
                            nh.setId(doc.getId());
                            hotList.add(nh);

                        } catch (Exception ignored) {
                        }
                    }

                    hotAdapter.notifyDataSetChanged();
                });
    }

    private void loadSuggestedRealtime() {

        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("nguoi_dung")
                .child(user.getUid())
                .child("lich_su_tim_kiem");

        ref.get().addOnSuccessListener(snapshot -> {

            goiYList.clear();

            for (DataSnapshot data : snapshot.getChildren()) {

                Long count = data.child("so_lan_tim").getValue(Long.class);

                if (count != null && count >= nguongTimKiem) {

                    String id = data.child("ma_nha_hang")
                            .getValue(String.class);

                    String ten = data.child("ten_nha_hang")
                            .getValue(String.class);

                    NhaHang nh = new NhaHang();
                    nh.setId(id);
                    nh.setTen(ten);

                    goiYList.add(nh);
                }
            }

            goiYAdapter.notifyDataSetChanged();
        });
    }

    private void openNhaHang(NhaHang nhaHang) {

        FirebaseHelper.saveSearch(
                nhaHang.getId(),
                nhaHang.getTen()
        );

        Bundle bundle = new Bundle();
        bundle.putString("maNhaHang", nhaHang.getId());

        NavHostFragment.findNavController(this)
                .navigate(
                        R.id.ChiTietNhaHangFragment,
                        bundle
                );
    }

    private void openDanhMuc(DanhMuc danhMuc) {

        Bundle bundle = new Bundle();
        bundle.putString("maDanhMuc", danhMuc.getId());
        bundle.putString("tenDanhMuc", danhMuc.getTen());

        NavHostFragment.findNavController(this)
                .navigate(
                        R.id.NhaHangFragment,
                        bundle
                );
    }
}