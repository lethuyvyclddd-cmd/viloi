package com.example.viloi.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private TextView tvLocation, tvUserInitial;

    private RecyclerView rvDanhMuc;
    private RecyclerView rvGoiY;
    private RecyclerView rvHot;

    private DanhMucAdapter danhMucAdapter;
    private GoiYAdapter goiYAdapter;
    private HotAdapter hotAdapter;

    private final List<DanhMuc> danhMucList = new ArrayList<>();
    private final List<NhaHang> goiYList = new ArrayList<>();
    private final List<NhaHang> hotList = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth auth;

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

        // ánh xạ
        bindViews(view);

        //cấu hình Recycler
        setupRecycler();

        // load user hiệ tại
        setupUserInfo();

        //load dữ liệu từ firebase
        loadDanhMuc();
        loadGoiY();
        loadHot();
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

        // ===== DANH MUC =====
        rvDanhMuc.setLayoutManager(
                new LinearLayoutManager(
                        getContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                )
        );

        danhMucAdapter = new DanhMucAdapter(
                danhMucList,
                new DanhMucAdapter.OnClickListener() {
                    @Override
                    public void onClick(DanhMuc dm) {
                        openDanhMuc(dm);
                    }

                    @Override
                    public void onLongClick(DanhMuc dm) {
                        showBottomSheet(dm);
                    }
                }
        );

        rvDanhMuc.setAdapter(danhMucAdapter);

        // ===== GOI Y =====
        rvGoiY.setLayoutManager(
                new LinearLayoutManager(
                        getContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                )
        );

        goiYAdapter = new GoiYAdapter(
                goiYList,
                this::openNhaHang
        );

        rvGoiY.setAdapter(goiYAdapter);

        // ===== HOT =====
        rvHot.setLayoutManager(
                new LinearLayoutManager(getContext())
        );

        rvHot.setNestedScrollingEnabled(false);

        hotAdapter = new HotAdapter(
                hotList,
                this::openNhaHang
        );

        rvHot.setAdapter(hotAdapter);
    }

    private void setupUserInfo() {

        FirebaseUser user = auth.getCurrentUser();

        // kiểm tra login chưa
        if (user == null) return;

        db.collection("nguoi_dung")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(doc -> {

                    if (!isAdded()) return;

                    if (doc.exists()) {

                        NguoiDung nd = doc.toObject(NguoiDung.class);

                        if (nd != null) {
                            tvUserInitial.setText(nd.getInitial());
                        }
                    }
                });
    }

    // =========================
    // LOAD DANH MUC
    // =========================
    private void loadDanhMuc() {

        db.collection("danh_muc")
                .whereEqualTo("hoat_dong", true)
                .limit(5)
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (!isAdded()) return;

                    danhMucList.clear();

                    for (QueryDocumentSnapshot doc : snapshot) {

                        DanhMuc dm = doc.toObject(DanhMuc.class);

                        dm.setId(doc.getId());

                        danhMucList.add(dm);
                    }

                    danhMucAdapter.notifyDataSetChanged();
                });
    }

    // =========================
    // GOI Y - tim kiem nhieu
    // =========================
    private void loadGoiY() {

        FirebaseUser user = auth.getCurrentUser();

        if (user == null) return;

        db.collection("nguoi_dung")
                .document(user.getUid())
                .collection("lich_su_tim_kiem")
                .orderBy("so_lan_tim", Query.Direction.DESCENDING)
                .limit(2)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    goiYList.clear();

                    for (QueryDocumentSnapshot historyDoc
                            : querySnapshot) {

                        String maNhaHang =
                                historyDoc.getString("ma_nha_hang");

                        Long soLanTim =
                                historyDoc.getLong("so_lan_tim");

                        if (maNhaHang == null) continue;

                        db.collection("nha_hang")
                                .document(maNhaHang)
                                .get()
                                .addOnSuccessListener(doc -> {

                                    if (!doc.exists()) return;

                                    NhaHang nh =
                                            doc.toObject(NhaHang.class);

                                    if (nh != null) {

                                        nh.setId(doc.getId());

                                        // hiện số lần tìm
                                        if (soLanTim != null) {
                                            nh.setLuotTimKiem(
                                                    soLanTim.intValue()
                                            );
                                        }

                                        goiYList.add(nh);

                                        goiYAdapter.notifyDataSetChanged();
                                    }
                                });
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                getContext(),
                                e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }
    // =========================
    // HOT - rating >= 4.5
    // =========================
    private void loadHot() {

        db.collection("nha_hang")
                .whereEqualTo("hoat_dong", true)
                .whereGreaterThanOrEqualTo("danh_gia_trung_binh", 4.0)
                .orderBy("danh_gia_trung_binh", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(snapshot -> {

                    if (!isAdded()) return;

                    hotList.clear();

                    for (QueryDocumentSnapshot doc : snapshot) {

                        NhaHang nh = doc.toObject(NhaHang.class);

                        if (nh != null) {

                            nh.setId(doc.getId());

                            hotList.add(nh);
                        }
                    }

                    hotAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                getContext(),
                                "Lỗi hot: " + e.getMessage(),
                                Toast.LENGTH_LONG
                        ).show()
                );
    }

    private void openNhaHang(NhaHang nhaHang) {

        FirebaseHelper.saveSearch(
                nhaHang.getId(),
                nhaHang.getTen()
        );

        Bundle bundle = new Bundle();

        bundle.putString(
                "maNhaHang",
                nhaHang.getId()
        );

        NavHostFragment.findNavController(this)
                .navigate(
                        R.id.ChiTietNhaHangFragment,
                        bundle
                );
    }

    private void openDanhMuc(DanhMuc danhMuc) {

        Bundle bundle = new Bundle();

        bundle.putString(
                "maDanhMuc",
                danhMuc.getId()
        );

        bundle.putString(
                "tenDanhMuc",
                danhMuc.getTen()
        );

        NavHostFragment.findNavController(this)
                .navigate(
                        R.id.NhaHangFragment,
                        bundle
                );
    }

    private void showBottomSheet(DanhMuc dm) {

    }
}