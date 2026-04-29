package com.example.viloi.ui.yeuthich;


import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.*;
import com.example.viloi.R;
import com.example.viloi.ui.adapter.YeuThichAdapter;
import com.example.viloi.ui.model.NhaHang;  // ← import model thật
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import java.util.*;

public class YeuThichFragment extends Fragment {

    private RecyclerView    rvYeuThich;
    private LinearLayout    layoutEmpty;
    private YeuThichAdapter adapter;
    private final List<NhaHang> danhSach = new ArrayList<>();

    private FirebaseFirestore db;
    private String userId;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_yeu_thich, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db     = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        rvYeuThich  = view.findViewById(R.id.rvYeuThich);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);

        view.findViewById(R.id.btnBack).setOnClickListener(v ->
                Navigation.findNavController(v).navigateUp());

        adapter = new YeuThichAdapter(danhSach, this::confirmBoYeuThich);
        rvYeuThich.setLayoutManager(new LinearLayoutManager(getContext()));
        rvYeuThich.setAdapter(adapter);

        loadYeuThich();
    }

    private void loadYeuThich() {
        db.collection("nguoi_dung").document(userId)
                .collection("yeu_thich")
                .get()
                .addOnSuccessListener(snapshots -> {
                    if (!isAdded()) return;
                    danhSach.clear();

                    if (snapshots.isEmpty()) {
                        showEmpty(true);
                        return;
                    }

                    int total    = snapshots.size();
                    int[] loaded = {0};

                    for (QueryDocumentSnapshot doc : snapshots) {
                        String maNhaHang = doc.getId();

                        db.collection("nha_hang").document(maNhaHang)
                                .get()
                                .addOnSuccessListener(nhDoc -> {
                                    if (!isAdded()) return;

                                    if (nhDoc.exists()) {
                                        // Dùng toObject() với model có sẵn — gọn, đúng field
                                        NhaHang nh = nhDoc.toObject(NhaHang.class);
                                        if (nh != null) {
                                            nh.setId(nhDoc.getId());  // gán document ID
                                            danhSach.add(nh);
                                        }
                                    }

                                    loaded[0]++;
                                    if (loaded[0] == total) {
                                        adapter.notifyDataSetChanged();
                                        showEmpty(danhSach.isEmpty());
                                    }
                                });
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Lỗi tải danh sách", Toast.LENGTH_SHORT).show());
    }

    private void confirmBoYeuThich(NhaHang nhaHang, int position) {
        new AlertDialog.Builder(requireContext())
                .setMessage("Bỏ \"" + nhaHang.getTen() + "\" khỏi yêu thích?")
                .setPositiveButton("Bỏ", (d, w) -> doBoYeuThich(nhaHang, position))
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void doBoYeuThich(NhaHang nhaHang, int position) {
        db.collection("nguoi_dung").document(userId)
                .collection("yeu_thich").document(nhaHang.getId())
                .delete()
                .addOnSuccessListener(unused -> {
                    if (!isAdded()) return;
                    danhSach.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, danhSach.size());
                    showEmpty(danhSach.isEmpty());
                    Toast.makeText(getContext(), "Đã bỏ yêu thích", Toast.LENGTH_SHORT).show();
                });
    }

    private void showEmpty(boolean empty) {
        layoutEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        rvYeuThich.setVisibility(empty ? View.GONE    : View.VISIBLE);
    }
}