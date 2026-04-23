package com.example.viloi.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.viloi.R;
import com.example.viloi.ui.model.DanhMuc;

import java.util.List;

public class DanhMucAdapter extends RecyclerView.Adapter<DanhMucAdapter.ViewHolder> {

    public interface OnClickListener { void onClick(DanhMuc danhMuc); }

    private final List<DanhMuc> items;
    private final OnClickListener listener;

    public DanhMucAdapter(List<DanhMuc> items, OnClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        DanhMuc dm = items.get(position);
        h.tvTen.setText(dm.getTen());

        // Đổi màu nền icon theo mau_sac (hex string từ Firebase, vd "#9C27B0")
        if (dm.getMauSac() != null && !dm.getMauSac().isEmpty()) {
            try {
                int color = Color.parseColor(dm.getMauSac());
                // Làm màu nhạt hơn (thêm alpha ~20%)
                int bgColor = Color.argb(30,
                        Color.red(color), Color.green(color), Color.blue(color));
                h.ivIconBg.setBackgroundColor(bgColor);
                h.ivIcon.setColorFilter(color);
            } catch (IllegalArgumentException ignored) { }
        }

        // Load icon drawable theo tên resource (vd "ic_luxury")
        Context ctx = h.itemView.getContext();
        int resId = ctx.getResources().getIdentifier(
                dm.getIconResourceName(), "drawable", ctx.getPackageName());
        if (resId != 0) {
            h.ivIcon.setImageResource(resId);
        } else {
            h.ivIcon.setImageResource(R.drawable.ic_category_default);
        }

        h.itemView.setOnClickListener(v -> { if (listener != null) listener.onClick(dm); });
    }

    @Override public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        View ivIconBg;
        TextView tvTen;

        ViewHolder(@NonNull View v) {
            super(v);
            ivIcon   = v.findViewById(R.id.iv_category_icon);
            ivIconBg = v.findViewById(R.id.iv_category_icon); // same view for tint bg
            tvTen    = v.findViewById(R.id.tv_category_name);
        }
    }
}