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
import com.example.viloi.ui.model.NhaHang;
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

        // Nút back (layout của bạn có btnBack)
        view.findViewById(R.id.btnBack).setOnClickListener(v ->
                Navigation.findNavController(v).navigateUp());

        adapter = new YeuThichAdapter(
                danhSach,
                this::confirmBoYeuThich,
                nhaHang -> {
                    Bundle args = new Bundle();
                    args.putString("maNhaHang", nhaHang.getId());
                    Navigation.findNavController(view)
                            .navigate(
                                    R.id.action_favoriteFragment_to_ChiTietNhaHangFragment,
                                    args);
                }
        );

        rvYeuThich.setLayoutManager(new LinearLayoutManager(getContext()));
        rvYeuThich.setAdapter(adapter);

        loadYeuThich();
    }

    // ── Load yêu thích từ nested map ─────────────────────────
    // Cấu trúc Firestore:
    // nguoi_dung/{userId}/yeu_thich/{maNhaHang}: { ma_nha_hang, ten_nha_hang, them_luc }
    @SuppressWarnings("unchecked")
    private void loadYeuThich() {

        db.collection("nguoi_dung")
                .document(userId)
                .collection("yeu_thich")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    if (!isAdded()) return;

                    danhSach.clear();

                    if (queryDocumentSnapshots.isEmpty()) {
                        showEmpty(true);
                        return;
                    }

                    int total = queryDocumentSnapshots.size();
                    int[] loaded = {0};

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {

                        String maNH = doc.getString("ma_nha_hang");

                        if (maNH == null || maNH.isEmpty()) {
                            loaded[0]++;
                            continue;
                        }

                        db.collection("nha_hang")
                                .document(maNH)
                                .get()
                                .addOnSuccessListener(nhDoc -> {

                                    if (!isAdded()) return;

                                    if (nhDoc.exists()) {

                                        NhaHang nhaHang = nhDoc.toObject(NhaHang.class);

                                        if (nhaHang != null) {
                                            nhaHang.setId(nhDoc.getId());
                                            danhSach.add(nhaHang);
                                        }
                                    }

                                    loaded[0]++;

                                    if (loaded[0] == total) {
                                        adapter.notifyDataSetChanged();
                                        showEmpty(danhSach.isEmpty());
                                    }
                                })
                                .addOnFailureListener(e -> {

                                    loaded[0]++;

                                    if (loaded[0] == total) {
                                        adapter.notifyDataSetChanged();
                                        showEmpty(danhSach.isEmpty());
                                    }
                                });
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(),
                                "Lỗi tải yêu thích: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }

    private void confirmBoYeuThich(NhaHang nhaHang, int position) {
        new AlertDialog.Builder(requireContext())
                .setMessage("Bỏ \"" + nhaHang.getTen() + "\" khỏi yêu thích?")
                .setPositiveButton("Bỏ", (d, w) -> doBoYeuThich(nhaHang, position))
                .setNegativeButton("Huỷ", null)
                .show();
    }

    private void doBoYeuThich(NhaHang nhaHang, int position) {
        // Xoá key trong nested map
        db.collection("nguoi_dung")
                .document(userId)
                .collection("yeu_thich")
                .document(nhaHang.getId())
                .delete()
                .addOnSuccessListener(unused -> {
                    if (!isAdded()) return;
                    // Giảm counter ở nha_hang
                    db.collection("nha_hang").document(nhaHang.getId())
                            .update("luot_yeu_thich", FieldValue.increment(-1));
                    danhSach.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, danhSach.size());
                    showEmpty(danhSach.isEmpty());
                    Toast.makeText(getContext(), "Đã bỏ yêu thích",
                            Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Lỗi: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }

    private void showEmpty(boolean empty) {
        layoutEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        rvYeuThich.setVisibility(empty ? View.GONE    : View.VISIBLE);
    }
}