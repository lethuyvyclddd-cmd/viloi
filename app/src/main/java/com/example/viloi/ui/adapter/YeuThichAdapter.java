package com.example.viloi.ui.adapter;

import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.viloi.R;
import com.example.viloi.ui.model.NhaHang;

import java.util.List;

public class YeuThichAdapter extends RecyclerView.Adapter<YeuThichAdapter.VH> {

    public interface OnBoYeuThich { void onBo(NhaHang nh, int position); }
    public interface OnItemClick  { void onClick(NhaHang nh); }

    private final List<NhaHang>  list;
    private final OnBoYeuThich   onBo;
    private final OnItemClick    onClick;

    public YeuThichAdapter(List<NhaHang> list,
                           OnBoYeuThich onBo,
                           OnItemClick onClick) {
        this.list    = list;
        this.onBo    = onBo;
        this.onClick = onClick;
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

        h.tvTenQuan.setText(q.getTen()    != null ? q.getTen()    : "");
        h.tvDiaChi.setText(q.getDiaChi()  != null ? q.getDiaChi() : "");
        h.tvDiem.setText(q.getRatingDisplay());

        // Load ảnh bằng Glide
        String anhUrl = q.getFirstImageUrl();
        if (anhUrl != null && !anhUrl.isEmpty()) {
            Glide.with(h.ivAnh.getContext())
                    .load(anhUrl)
                    .centerCrop()
                    .placeholder(android.R.color.darker_gray)
                    .into(h.ivAnh);
        } else {
            h.ivAnh.setImageResource(android.R.color.darker_gray);
        }

        // Bấm icon tim → bỏ yêu thích
        h.btnBo.setOnClickListener(v -> {
            int pos = h.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) onBo.onBo(q, pos);
        });

        // Bấm vào card → mở chi tiết
        h.itemView.setOnClickListener(v -> {
            int pos = h.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) onClick.onClick(list.get(pos));
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