package com.example.viloi.ui.DanhMuc;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viloi.R;
import com.example.viloi.ui.adapter.DanhMucAdapter;
import com.example.viloi.ui.model.DanhMuc;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DanhMucFragment extends Fragment {

    private RecyclerView rvDanhMuc;
    private DanhMucAdapter adapter;
    private List<DanhMuc> list = new ArrayList<>();

    private FirebaseFirestore db;

    private EditText etSearch;
    private TextView tvUserInitial;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_danh_muc, container, false);

        // ===== ÁNH XẠ VIEW =====
        rvDanhMuc = view.findViewById(R.id.rv_categories_dm);
        etSearch = view.findViewById(R.id.et_search_dm);
        tvUserInitial = view.findViewById(R.id.tv_user_initial);

        // ===== CLICK THÊM MỚI =====
        view.findViewById(R.id.txtThemMoi).setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_danhMucFragment_to_themDanhMucFragment);
        });

        // ===== FIREBASE =====
        db = FirebaseFirestore.getInstance();

        // ===== SETUP RECYCLER =====
        rvDanhMuc.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new DanhMucAdapter(list, danhMuc -> {
            Log.d("CLICK_DM", "Clicked: " + danhMuc.getTen());
        });

        rvDanhMuc.setAdapter(adapter);

        // ===== LOAD DATA =====
        loadDanhMuc();

        return view;
    }

    // ================= LOAD DANH MỤC =================
    private void loadDanhMuc() {

        Log.d("FIREBASE_DM", "Start loading...");

        db.collection("danh_muc")
                .whereEqualTo("hoat_dong", true)
                .orderBy("uu_tien", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(snapshots -> {

                    list.clear();

                    Log.d("FIREBASE_DM", "SIZE = " + snapshots.size());

                    for (QueryDocumentSnapshot doc : snapshots) {

                        DanhMuc dm = doc.toObject(DanhMuc.class);
                        dm.setId(doc.getId());

                        // debug icon
                        Log.d("DM_DATA",
                                "ten=" + dm.getTen() +
                                        " | icon=" + dm.getIcon() +
                                        " | mau=" + dm.getMauSac());

                        list.add(dm);
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("FIREBASE_DM", "ERROR: " + e.getMessage());
                });
    }
}