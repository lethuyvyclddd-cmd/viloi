package com.example.viloi.ui.adapter;

import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.viloi.R;
import com.example.viloi.ui.model.NhaHang;  
import java.util.List;

public class YeuThichAdapter extends RecyclerView.Adapter<YeuThichAdapter.VH> {

    public interface OnBoYeuThich {
        void onBo(NhaHang nhaHang, int position);
    }

    private final List<NhaHang>    list;
    private final OnBoYeuThich     callback;

    public YeuThichAdapter(List<NhaHang> list, OnBoYeuThich callback) {
        this.list     = list;
        this.callback = callback;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_quan_yeu_thich, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        NhaHang q = list.get(position);

        h.tvTenQuan.setText(q.getTen() != null ? q.getTen() : "");
        h.tvDiaChi.setText(q.getDiaChi() != null ? q.getDiaChi() : "");
        h.tvDiem.setText(q.getRatingDisplay());  // dùng helper có sẵn "4.5"

        // Load ảnh đầu tiên nếu có (dùng Glide nếu project có)
        // String anhUrl = q.getFirstImageUrl();
        // if (anhUrl != null) Glide.with(h.ivAnh).load(anhUrl).into(h.ivAnh);

        h.btnBo.setOnClickListener(v -> {
            int pos = h.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) callback.onBo(q, pos);
        });
    }

    @Override public int getItemCount() { return list.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivAnh, btnBo;
        TextView  tvTenQuan, tvDiaChi, tvDiem;
        VH(@NonNull View v) {
            super(v);
            ivAnh     = v.findViewById(R.id.ivAnhQuan);
            tvTenQuan = v.findViewById(R.id.tvTenQuan);
            tvDiaChi  = v.findViewById(R.id.tvDiaChi);
            tvDiem    = v.findViewById(R.id.tvDiemDanhGia);
            btnBo     = v.findViewById(R.id.btnBoYeuThich);
        }
    }
}
