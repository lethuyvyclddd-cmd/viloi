package com.example.viloi.ui.adapter;

import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.viloi.R;
import com.example.viloi.ui.model.NhaHang;
import java.util.List;

public class TimKiemAdapter extends RecyclerView.Adapter<TimKiemAdapter.VH> {

    public interface OnClick { void onClick(NhaHang nh); }

    private final List<NhaHang> list;
    private final OnClick callback;

    public TimKiemAdapter(List<NhaHang> list, OnClick callback) {
        this.list = list;
        this.callback = callback;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_nha_hang, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        NhaHang nh = list.get(position);

        h.tvTen.setText(nh.getTen() != null ? nh.getTen() : "");
        h.tvDiaChi.setText(nh.getDiaChi() != null ? nh.getDiaChi() : "");
        h.tvSao.setText("⭐ " + nh.getRatingDisplay());
        h.tvGia.setText(nh.getKhoangGia() != null ? nh.getKhoangGia() : "");
        h.tvYeuThich.setText("❤️ " + nh.getLuotYeuThich());

        List<String> images = nh.getHinhAnh();

        String img = null;
        if (images != null && !images.isEmpty()) {
            img = images.get(0); // lấy ảnh đầu
        }

        if (img == null || img.isEmpty()) {
            h.ivAnh.setImageResource(R.drawable.ic_category_default);
        } else {
            Glide.with(h.itemView.getContext())
                    .load(img)
                    .placeholder(R.drawable.ic_category_default)
                    .error(R.drawable.ic_category_default)
                    .into(h.ivAnh);
        }

        h.itemView.setOnClickListener(v -> {
            int pos = h.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION && callback != null) {
                callback.onClick(list.get(pos));
            }
        });
    }
    @Override
    public int getItemCount() { return list.size(); }

    static class VH extends RecyclerView.ViewHolder {

        TextView tvTen, tvDiaChi, tvSao, tvGia, tvYeuThich;
        ImageView ivAnh;

        VH(@NonNull View v) {
            super(v);
            tvTen      = v.findViewById(R.id.tvTen);
            tvDiaChi   = v.findViewById(R.id.tvDiaChi);
            tvSao      = v.findViewById(R.id.tvSao);
            tvGia      = v.findViewById(R.id.tvGia);
            tvYeuThich = v.findViewById(R.id.tvYeuThich);
            ivAnh      = v.findViewById(R.id.ivAnh);
        }
    }
}