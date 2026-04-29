package com.example.viloi.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viloi.R;
import com.example.viloi.ui.model.NhaHang;

import java.util.List;

public class NhaHangAdapter extends RecyclerView.Adapter<NhaHangAdapter.ViewHolder> {

    public interface OnClickListener {
        void onClick(NhaHang nhaHang);
    }

    private final List<NhaHang> items;
    private final OnClickListener listener;

    public NhaHangAdapter(List<NhaHang> items) {
        this.items = items;
        this.listener = null;
    }

    public NhaHangAdapter(List<NhaHang> items, OnClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_nha_hang, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        NhaHang nh = items.get(position);

        // tên nhà hàng
        holder.tvTen.setText(nh.getTen());

        // địa chỉ
        holder.tvDiaChi.setText(
                nh.getDiaChiDayDu() != null
                        ? nh.getDiaChiDayDu()
                        : nh.getDiaChi()
        );

        // sao đánh giá
        holder.tvSao.setText("⭐ " + nh.getRatingDisplay());

        // giá
        holder.tvGia.setText(formatGia(nh.getKhoanggia()));

        // lượt yêu thích
        holder.tvYeuThich.setText("❤️ " + nh.getLuotYeuThich());

        // icon mặc định
        holder.ivAnh.setImageResource(R.drawable.ic_category_default);

        // click item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(nh);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    // =========================
    // VIEW HOLDER
    // =========================
    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivAnh;
        TextView tvTen, tvDiaChi, tvSao, tvGia, tvYeuThich;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardNhaHang);
            ivAnh = itemView.findViewById(R.id.ivAnh);
            tvTen = itemView.findViewById(R.id.tvTen);
            tvDiaChi = itemView.findViewById(R.id.tvDiaChi);
            tvSao = itemView.findViewById(R.id.tvSao);
            tvGia = itemView.findViewById(R.id.tvGia);
            tvYeuThich = itemView.findViewById(R.id.tvYeuThich);
        }
    }

    // =========================
    // FORMAT GIÁ
    // =========================
    private String formatGia(String raw) {

        if (raw == null || raw.isEmpty()) return "Chưa cập nhật";

        try {
            String[] arr = raw.split("-");

            if (arr.length == 2) {
                long min = Long.parseLong(arr[0].trim());
                long max = Long.parseLong(arr[1].trim());

                return String.format("%,dđ - %,dđ", min, max)
                        .replace(",", ".");
            }

        } catch (Exception ignored) {
        }

        return raw;
    }
}