package com.example.viloi.ui.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.viloi.R;
import com.example.viloi.ui.model.NhaHang;

import java.util.List;

public class NhaHangAdapter extends RecyclerView.Adapter<NhaHangAdapter.ViewHolder> {

    public interface OnClickListener {
        void onClick(NhaHang nhaHang);
    }

    // Thêm 2 interface mới
    public interface OnSuaListener {
        void onSua(NhaHang nhaHang, int position);
    }

    public interface OnXoaListener {
        void onXoa(NhaHang nhaHang, int position);
    }

    private final List<NhaHang> items;
    private final OnClickListener listener;

    private OnSuaListener onSuaListener;

    private OnXoaListener onXoaListener;

    public NhaHangAdapter(List<NhaHang> items) {
        this.items = items;
        this.listener = null;
    }

    public NhaHangAdapter(List<NhaHang> items, OnClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    // Setter cho 2 listener mới
    public void setOnSuaListener(OnSuaListener onSuaListener) {
        this.onSuaListener = onSuaListener;
    }

    public void setOnXoaListener(OnXoaListener onXoaListener) {
        this.onXoaListener = onXoaListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_nha_hang_sua_xoa, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        NhaHang nh = items.get(position);

        // Tên
        holder.tvTen.setText(nh.getTen());

        // Địa chỉ
        String diaChi = nh.getDiaChiDayDu() != null
                ? nh.getDiaChiDayDu()
                : nh.getDiaChi();
        holder.tvDiaChi.setText(diaChi);

        // Rating
        holder.tvSao.setText("⭐ " + nh.getRatingDisplay());

        // Giá
        holder.tvGia.setText(formatGia(nh.getKhoangGia()));

        // Yêu thích
        holder.tvYeuThich.setText("❤️ " + nh.getLuotYeuThich());

        // Ảnh
        String path = nh.getFirstImageUrl();
        if (path != null && !path.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(path.startsWith("/") ? new java.io.File(path) : Uri.parse(path))
                    .placeholder(R.drawable.ic_category_default)
                    .error(R.drawable.ic_category_default)
                    .centerCrop()
                    .into(holder.ivAnh);
        } else {
            holder.ivAnh.setImageResource(R.drawable.ic_category_default);
        }

        // Click
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(nh);
        });

        holder.btnSua.setOnClickListener(v -> {
            if(onSuaListener != null)
                onSuaListener.onSua(nh, position);

        });

        holder.btnXoa.setOnClickListener(v -> {
            if(onXoaListener != null)
                onXoaListener.onXoa(nh, position);

        });
    }

    public void removeItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, items.size());
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

        Button btnSua, btnXoa;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardNhaHang);
            ivAnh = itemView.findViewById(R.id.ivAnh);
            tvTen = itemView.findViewById(R.id.tvTen);
            tvDiaChi = itemView.findViewById(R.id.tvDiaChi);
            tvSao = itemView.findViewById(R.id.tvSao);
            tvGia = itemView.findViewById(R.id.tvGia);
            tvYeuThich = itemView.findViewById(R.id.tvYeuThich);
            btnSua = itemView.findViewById(R.id.btnSua);
            btnXoa = itemView.findViewById(R.id.btnXoa);
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