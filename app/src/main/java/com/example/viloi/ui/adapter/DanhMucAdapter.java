package com.example.viloi.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
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
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {

        DanhMuc dm = items.get(position);

        // ===== TEXT =====
        h.tvTen.setText(dm.getTen());

        // ===== COLOR =====
        try {
            String mau = dm.getMauSac();

            if (mau != null && !mau.isEmpty()) {
                int color = Color.parseColor(mau);

                int bgColor = Color.argb(40,
                        Color.red(color),
                        Color.green(color),
                        Color.blue(color));

                h.bgIcon.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(bgColor)
                );

            } else {
                h.bgIcon.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(Color.parseColor("#EEEEEE"))
                );
            }

        } catch (Exception e) {
            h.bgIcon.setBackgroundColor(Color.parseColor("#EEEEEE"));
            h.ivIcon.clearColorFilter();
        }

        // ===== LOAD ICON =====
        Context ctx = h.itemView.getContext();

        String iconName = dm.getIcon() != null ? dm.getIcon() : "ic_category_default";
        Log.d("ICON_DEBUG", iconName);

        int resId = ctx.getResources().getIdentifier(
                iconName,
                "drawable",
                ctx.getPackageName()
        );

        if (resId != 0) {
            h.ivIcon.setImageResource(resId);
        } else {
            h.ivIcon.setImageResource(R.drawable.ic_category_default);
        }

        // ===== CLICK =====
        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(dm);
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    // ===== VIEW HOLDER =====
    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivIcon;
        View bgIcon;
        TextView tvTen;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivIcon = itemView.findViewById(R.id.iv_category_icon);
            bgIcon = itemView.findViewById(R.id.bg_icon);
            tvTen  = itemView.findViewById(R.id.tv_category_name);
        }
    }
}