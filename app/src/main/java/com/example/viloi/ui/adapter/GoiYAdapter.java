package com.example.viloi.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.viloi.R;
import com.example.viloi.ui.model.NhaHang;


import java.util.List;

public class GoiYAdapter extends RecyclerView.Adapter<GoiYAdapter.ViewHolder> {

    public interface OnClickListener { void onClick(NhaHang nhaHang); }

    private final List<NhaHang> items;
    private final OnClickListener listener;

    public GoiYAdapter(List<NhaHang> items, OnClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_suggested_restaurant, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        NhaHang nh = items.get(position);

        h.tvTen.setText(nh.getTen());
        h.tvDiaChi.setText(nh.getDiaChi());
        h.tvRating.setText(nh.getRatingDisplay());
        h.tvLuotTimKiem.setText(nh.getLuotTimKiemDisplay());
        // Badge: "Đã tìm 5 lần"
        h.tvBadge.setText(nh.getLuotTimKiemDisplay());

        String imgUrl = nh.getFirstImageUrl();
        if (imgUrl != null) {
            Glide.with(h.itemView.getContext())
                    .load(imgUrl)
                    .placeholder(R.drawable.ic_food_placeholder)
                    .centerCrop()
                    .into(h.ivHinhAnh);
        }

        h.itemView.setOnClickListener(v -> { if (listener != null) listener.onClick(nh); });
    }

    @Override public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivHinhAnh;
        TextView tvTen, tvDiaChi, tvRating, tvLuotTimKiem, tvBadge;

        ViewHolder(@NonNull View v) {
            super(v);
            ivHinhAnh      = v.findViewById(R.id.iv_restaurant_image);
            tvTen          = v.findViewById(R.id.tv_restaurant_name);
            tvDiaChi       = v.findViewById(R.id.tv_restaurant_address);
            tvRating       = v.findViewById(R.id.tv_rating);
            tvLuotTimKiem  = v.findViewById(R.id.tv_search_count);
            tvBadge        = v.findViewById(R.id.tv_search_count_badge);
        }
    }
}
