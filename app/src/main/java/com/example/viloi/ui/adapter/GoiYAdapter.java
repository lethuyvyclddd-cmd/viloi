package com.example.viloi.ui.adapter;

import android.net.Uri;
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
        String dc = nh.getDiaChiDayDu() != null
                ? nh.getDiaChiDayDu() : nh.getDiaChi();;
        h.tvDiaChi.setText(dc);

        h.tvRating.setText(nh.getRatingDisplay());

        h.tvLuotTimKiem.setText(nh.getLuotTimKiemDisplay());
        h.tvBadge.setText(nh.getLuotTimKiemDisplay());

        String path = nh.getFirstImageUrl();
        if (path != null && !path.isEmpty()) {
            Glide.with(h.itemView.getContext())
                    .load(path.startsWith("/") ? new java.io.File(path) : Uri.parse(path))
                    .placeholder(R.drawable.ic_category_default)
                    .error(R.drawable.ic_category_default)
                    .centerCrop()
                    .into(h.ivHinhAnh);
        } else {
            h.ivHinhAnh.setImageResource(R.drawable.ic_category_default);
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
