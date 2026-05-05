package com.example.viloi.ui.DanhMuc;

import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viloi.R;
import com.example.viloi.ui.adapter.DanhMucAdapter;
import com.example.viloi.ui.model.DanhMuc;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_danh_muc, container, false);

        rvDanhMuc = view.findViewById(R.id.rv_categories_dm);

        db = FirebaseFirestore.getInstance();

        rvDanhMuc.setLayoutManager(new GridLayoutManager(getContext(), 4));

        adapter = new DanhMucAdapter(list, new DanhMucAdapter.OnClickListener() {

            @Override
            public void onClick(DanhMuc dm) {

                Bundle bundle = new Bundle();
                bundle.putString("maDanhMuc", dm.getId());
                bundle.putString("tenDanhMuc", dm.getTen());

                NavHostFragment.findNavController(DanhMucFragment.this)
                        .navigate(R.id.NhaHangFragment, bundle);
            }

            @Override
            public void onLongClick(DanhMuc dm) {
                showBottomSheet(dm);
            }
        });

        rvDanhMuc.setAdapter(adapter);

        loadDanhMuc();

        return view;
    }

    // ===== LOAD DATA =====
    private void loadDanhMuc() {

        db.collection("danh_muc")
                .whereEqualTo("hoat_dong", true)
                .orderBy("uu_tien", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(snapshots -> {

                    list.clear();

                    for (QueryDocumentSnapshot doc : snapshots) {
                        DanhMuc dm = doc.toObject(DanhMuc.class);
                        dm.setId(doc.getId());
                        list.add(dm);
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Log.e("FIREBASE_DM", e.getMessage())
                );
    }

    // ===== BOTTOM SHEET =====
    private void showBottomSheet(DanhMuc category) {

        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());

        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.bottom_sheet_action, null);

        TextView textTitle = view.findViewById(R.id.textTitle);
        TextView textEdit = view.findViewById(R.id.textEdit);
        TextView textDelete = view.findViewById(R.id.textDelete);

        textTitle.setText(category.getTen());

        textEdit.setOnClickListener(v -> {
            dialog.dismiss();
            showEdit(category);
        });

        textDelete.setOnClickListener(v -> {
            dialog.dismiss();
            showDelete(category);
        });

        dialog.setContentView(view);
        dialog.show();
    }

    // ===== EDIT =====
    private void showEdit(DanhMuc dm) {

        Bundle bundle = new Bundle();
        bundle.putString("maDanhMuc", dm.getId());
        bundle.putString("tenDanhMuc", dm.getTen());

        NavHostFragment.findNavController(this)
                .navigate(R.id.action_danhMucFragment_to_SuaDanhMucFragment, bundle);
    }

    // ===== DELETE =====
    private void showDelete(DanhMuc dm) {

        new AlertDialog.Builder(requireContext())
                .setTitle("Xoá")
                .setMessage("Xoá \"" + dm.getTen() + "\"?")
                .setPositiveButton("Xoá", (d, w) -> {

                    db.collection("danh_muc")
                            .document(dm.getId())
                            .delete()
                            .addOnSuccessListener(unused -> {
                                list.removeIf(item -> item.getId().equals(dm.getId()));
                                adapter.notifyDataSetChanged();
                            });
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }
}