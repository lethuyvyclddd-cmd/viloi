package com.example.viloi.ui.timkiem;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viloi.R;
import com.example.viloi.ui.adapter.LichSuAdapter;
import com.example.viloi.ui.model.LichSuTimKiem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class LichSuTimKiemFragment extends Fragment {

    private ImageView btnBack;
    private TextView btnXoaTatCa;
    private RecyclerView rvLichSu;
    private LinearLayout layoutEmpty;

    private LichSuAdapter adapter;
    private final List<LichSuTimKiem> danhSach = new ArrayList<>();

    private FirebaseFirestore db;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lich_su_tim_kiem, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        btnBack      = view.findViewById(R.id.btnBack);
        btnXoaTatCa  = view.findViewById(R.id.btnXoaTatCa);
        rvLichSu     = view.findViewById(R.id.rvLichSu);
        layoutEmpty  = view.findViewById(R.id.layoutEmpty);

        adapter = new LichSuAdapter(danhSach, (item, position) -> {
            xoaMotItem(item, position);
        });

        rvLichSu.setLayoutManager(new LinearLayoutManager(getContext()));
        rvLichSu.setAdapter(adapter);

        btnBack.setOnClickListener(v ->
                Navigation.findNavController(view).popBackStack()
        );

        btnXoaTatCa.setOnClickListener(v -> xoaTatCa());

        loadLichSu();
    }

    private void loadLichSu() {
        if (userId == null) return;

        db.collection("nguoi_dung")
                .document(userId)
                .collection("lich_su_tim_kiem")
                .orderBy("lan_tim_cuoi", Query.Direction.DESCENDING)
                .addSnapshotListener((querySnapshot, error) -> {

                    if (error != null || querySnapshot == null) return;

                    danhSach.clear();

                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {

                        // 🔥 FIX CỨNG - KHÔNG DÙNG toObject()
                        LichSuTimKiem item = new LichSuTimKiem();

                        item.setId(doc.getId());
                        item.setMaNhaHang(doc.getString("ma_nha_hang"));
                        item.setTenNhaHang(doc.getString("ten_nha_hang"));
                        item.setThoiGian(doc.getTimestamp("lan_tim_cuoi"));

                        // 🔍 DEBUG
                        Log.d("TIME_DEBUG", "RAW: " + doc.get("lan_tim_cuoi"));
                        Log.d("TIME_DEBUG", "SET: " + item.getThoiGian());

                        danhSach.add(item);
                    }

                    adapter.notifyDataSetChanged();
                    capNhatTrangThai();
                });
    }

    private void xoaMotItem(LichSuTimKiem item, int position) {
        if (userId == null) return;

        db.collection("nguoi_dung")
                .document(userId)
                .collection("lich_su_tim_kiem")
                .document(item.getId())
                .delete()
                .addOnSuccessListener(unused -> {

                    // Kiểm tra position hợp lệ
                    if (position >= 0 && position < danhSach.size()) {

                        danhSach.remove(position);

                        // Cách ổn định hơn
                        adapter.notifyDataSetChanged();

                        capNhatTrangThai();
                    }
                });
    }

    private void xoaTatCa() {
        if (userId == null) return;

        db.collection("nguoi_dung")
                .document(userId)
                .collection("lich_su_tim_kiem")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        doc.getReference().delete();
                    }
                    danhSach.clear();
                    adapter.notifyDataSetChanged();
                    capNhatTrangThai();
                });
    }

    private void capNhatTrangThai() {
        if (danhSach.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvLichSu.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvLichSu.setVisibility(View.VISIBLE);
        }
    }
}