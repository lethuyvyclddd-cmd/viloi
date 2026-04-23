package com.example.viloi.ui.home;

import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

    private TextView tvLocation;
    private TextView tvUserInitial;
    private RecyclerView rvDanhMuc;
    private RecyclerView rvGoiY;
    private RecyclerView rvHot;

    private DanhMucAdapter danhMucAdapter;
    private GoiYAdapter goiYAdapter;
    private HotAdapter hotAdapter;

    private final List<DanhMuc> danhMucList = new ArrayList<>();
    private final List<NhaHang> goiYList    = new ArrayList<>();
    private final List<NhaHang> hotList     = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    // Cấu hình từ cau_hinh_app/goi_y
    private int soGoiYToiDa   = 5;
    private int nguongTimKiem = 3;
    private int nguongNoiBat  = 50;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db   = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        bindViews(view);
        setupRecyclerViews();
        setupUserInfo();
        loadCauHinhApp();
    }

    private void bindViews(View view) {
        tvLocation    = view.findViewById(R.id.tv_location);
        tvUserInitial = view.findViewById(R.id.tv_user_initial);
        rvDanhMuc     = view.findViewById(R.id.rv_categories);
        rvGoiY        = view.findViewById(R.id.rv_suggested);
        rvHot         = view.findViewById(R.id.rv_hot_restaurants);

        view.findViewById(R.id.tv_view_all_categories)
                .setOnClickListener(v -> navigateToDanhMuc());
        view.findViewById(R.id.tv_view_all_suggested)
                .setOnClickListener(v -> navigateToGoiY());
        view.findViewById(R.id.tv_view_all_hot)
                .setOnClickListener(v -> navigateToHot());
    }

    private void setupRecyclerViews() {
        rvDanhMuc.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        danhMucAdapter = new DanhMucAdapter(danhMucList, this::navigateToDanhMucDetail);
        rvDanhMuc.setAdapter(danhMucAdapter);

        rvGoiY.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        goiYAdapter = new GoiYAdapter(goiYList, this::navigateToNhaHangDetail);
        rvGoiY.setAdapter(goiYAdapter);

        rvHot.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHot.setNestedScrollingEnabled(false);
        hotAdapter = new HotAdapter(hotList, this::navigateToNhaHangDetail);
        rvHot.setAdapter(hotAdapter);
    }

    // ── USER INFO ──────────────────────────────────
    private void setupUserInfo() {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null) return;

        // Document ID trong "nguoi_dung" = Firebase Auth UID
        db.collection("nguoi_dung")
                .document(firebaseUser.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    if (!isAdded()) return;
                    if (doc.exists()) {
                        NguoiDung user = doc.toObject(NguoiDung.class);
                        if (user != null) {
                            user.setId(doc.getId());
                            tvUserInitial.setText(user.getInitial());
                        }
                    }
                });
    }

    // ── CẤU HÌNH APP ──────────────────────────────
    private void loadCauHinhApp() {
        db.collection("cau_hinh_app")
                .document("goi_y")
                .get()
                .addOnSuccessListener(doc -> {
                    if (!isAdded()) return;
                    if (doc.exists()) {
                        Long sg  = doc.getLong("so_goi_y_toi_da");
                        Long ntk = doc.getLong("nguong_tim_kiem");
                        Long nnb = doc.getLong("nguong_noi_bat");
                        if (sg  != null) soGoiYToiDa   = sg.intValue();
                        if (ntk != null) nguongTimKiem = ntk.intValue();
                        if (nnb != null) nguongNoiBat  = nnb.intValue();
                    }
                    loadAllData();
                })
                .addOnFailureListener(e -> loadAllData()); // dùng default nếu lỗi
    }

    private void loadAllData() {
        loadDanhMuc();
        loadHot();
        loadSuggestedRealtime();
    }

    // ── DANH MỤC ──────────────────────────────────
    // collection: danh_muc | filter: hoat_dong=true, mac_dinh=true | sort: uu_tien ASC
    private void loadDanhMuc() {
        db.collection("danh_muc")
                .whereEqualTo("hoat_dong", true)
                .whereEqualTo("mac_dinh", true)
                .orderBy("uu_tien", Query.Direction.ASCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(snapshots -> {
                    if (!isAdded()) return;
                    danhMucList.clear();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        DanhMuc dm = doc.toObject(DanhMuc.class);
                        dm.setId(doc.getId());
                        danhMucList.add(dm);
                    }
                    danhMucAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> showError("Lỗi tải danh mục"));
    }

    // ── GỢI Ý ─────────────────────────────────────
    // collection: nha_hang | filter: hoat_dong=true, luot_tim_kiem >= nguong_tim_kiem
    // sort: luot_tim_kiem DESC | limit: so_goi_y_toi_da (từ cau_hinh_app)
    private void loadGoiY() {
        db.collection("nha_hang")
                .whereEqualTo("hoat_dong", true)
                .whereGreaterThanOrEqualTo("luot_tim_kiem", nguongTimKiem)
                .orderBy("luot_tim_kiem", Query.Direction.DESCENDING)
                .limit(soGoiYToiDa)
                .get()
                .addOnSuccessListener(snapshots -> {
                    if (!isAdded()) return;
                    goiYList.clear();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        NhaHang nh = doc.toObject(NhaHang.class);
                        nh.setId(doc.getId());
                        goiYList.add(nh);
                    }
                    goiYAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Fallback không dùng whereGreaterThan (tránh lỗi index chưa tạo)
                    db.collection("nha_hang")
                            .whereEqualTo("hoat_dong", true)
                            .orderBy("luot_tim_kiem", Query.Direction.DESCENDING)
                            .limit(soGoiYToiDa)
                            .get()
                            .addOnSuccessListener(snapshots -> {
                                if (!isAdded()) return;
                                goiYList.clear();
                                for (QueryDocumentSnapshot doc : snapshots) {
                                    NhaHang nh = doc.toObject(NhaHang.class);
                                    nh.setId(doc.getId());
                                    goiYList.add(nh);
                                }
                                goiYAdapter.notifyDataSetChanged();
                            });
                });
    }

    // ── HOT ───────────────────────────────────────
    // collection: nha_hang | filter: hoat_dong=true, noi_bat=true
    // sort: luot_xem DESC
    private void loadHot() {
        db.collection("nha_hang")
                .whereEqualTo("hoat_dong", true)
                .whereEqualTo("noi_bat", true)
                .orderBy("luot_xem", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(snapshots -> {
                    if (!isAdded()) return;
                    hotList.clear();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        NhaHang nh = doc.toObject(NhaHang.class);
                        nh.setId(doc.getId());
                        hotList.add(nh);
                    }
                    hotAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Fallback: top luot_xem không cần noi_bat
                    db.collection("nha_hang")
                            .whereEqualTo("hoat_dong", true)
                            .orderBy("luot_xem", Query.Direction.DESCENDING)
                            .limit(10)
                            .get()
                            .addOnSuccessListener(snapshots -> {
                                if (!isAdded()) return;
                                hotList.clear();
                                for (QueryDocumentSnapshot doc : snapshots) {
                                    NhaHang nh = doc.toObject(NhaHang.class);
                                    nh.setId(doc.getId());
                                    hotList.add(nh);
                                }
                                hotAdapter.notifyDataSetChanged();
                            });
                });
    }

    // ── NAVIGATION ────────────────────────────────
    private void navigateToNhaHangDetail(NhaHang nhaHang) {

        FirebaseHelper.saveSearch(
                nhaHang.getId(),
                nhaHang.getTen()
        );

        Toast.makeText(getContext(), nhaHang.getTen(), Toast.LENGTH_SHORT).show();
    }

    private void navigateToDanhMucDetail(DanhMuc danhMuc) {
        Toast.makeText(getContext(), danhMuc.getTen(), Toast.LENGTH_SHORT).show();
    }

    private void loadSuggestedRealtime() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("nguoi_dung")
                .child(user.getUid())
                .child("lich_su_tim_kiem");

        ref.get().addOnSuccessListener(snapshot -> {

            goiYList.clear();

            for (DataSnapshot data : snapshot.getChildren()) {

                Long count = data.child("so_lan_tim").getValue(Long.class);

                if (count != null && count >= 3) {

                    String id = data.child("ma_nha_hang").getValue(String.class);
                    String name = data.child("ten_nha_hang").getValue(String.class);

                    NhaHang nh = new NhaHang();
                    nh.setId(id);
                    nh.setTen(name);

                    goiYList.add(nh);
                }
            }

            goiYAdapter.notifyDataSetChanged();
        });
    }

    private void navigateToDanhMuc() { /* TODO */ }
    private void navigateToGoiY()    { /* TODO */ }
    private void navigateToHot()     { /* TODO */ }

    private void showError(String msg) {
        if (isAdded() && getContext() != null)
            Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}