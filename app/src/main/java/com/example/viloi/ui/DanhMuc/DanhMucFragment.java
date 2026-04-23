package com.example.viloi.ui.DanhMuc;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viloi.R;
import com.example.viloi.ui.adapter.DanhMucAdapter;
import com.example.viloi.ui.model.DanhMuc;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class DanhMucFragment extends Fragment {

    private RecyclerView rvDanhMuc;
    private DanhMucAdapter adapter;
    private List<DanhMuc> list = new ArrayList<>();

    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_danh_muc, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        rvDanhMuc = view.findViewById(R.id.rv_categories_dm);

        // 👉 hiển thị dạng grid 2 cột
        rvDanhMuc.setLayoutManager(new GridLayoutManager(getContext(), 2));

        adapter = new DanhMucAdapter(list, danhMuc -> {
            // click item
            Log.d("DM_CLICK", danhMuc.getTen());
        });

        rvDanhMuc.setAdapter(adapter);

        loadDanhMuc();
    }

    private void loadDanhMuc() {

        db.collection("danh_muc")
                .orderBy("uu_tien")
                .get()
                .addOnSuccessListener(snapshots -> {

                    list.clear();

                    Log.d("DM", "SIZE = " + snapshots.size());

                    for (QueryDocumentSnapshot doc : snapshots) {

                        DanhMuc dm = doc.toObject(DanhMuc.class);
                        dm.setId(doc.getId());

                        list.add(dm);
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("DM", "ERROR = " + e.getMessage());
                });
    }
}