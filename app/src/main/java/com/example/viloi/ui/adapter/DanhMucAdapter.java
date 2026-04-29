package com.example.viloi.ui.adapter;

import android.content.Context;
import android.util.Log;
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

    public interface OnClickListener {
        void onClick(DanhMuc danhMuc);
    }

    private final List<DanhMuc> items;
    private final OnClickListener listener;

    public DanhMucAdapter(List<DanhMuc> items, OnClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DanhMuc dm = items.get(position);

        // ===== TÊN DANH MỤC =====
        holder.tvTen.setText(dm.getTen());


        // ===== LOAD ICON =====
        Context ctx = holder.itemView.getContext();

        String iconName = dm.getIcon();

        if (iconName == null || iconName.trim().isEmpty()) {
            iconName = "ic_category_default";
        }

        Log.d("ICON_DEBUG", iconName);

        int resId = ctx.getResources().getIdentifier(
                iconName,
                "drawable",
                ctx.getPackageName()
        );

        if (resId != 0) {
            holder.ivIcon.setImageResource(resId);
        } else {
            holder.ivIcon.setImageResource(R.drawable.ic_category_default);
        }

        // ===== CLICK ITEM =====
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(dm);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    // ==================================================
    // VIEW HOLDER
    // ==================================================
    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivIcon;
        TextView tvTen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivIcon = itemView.findViewById(R.id.iv_category_icon);
            tvTen  = itemView.findViewById(R.id.tv_category_name);
        }
    }
}