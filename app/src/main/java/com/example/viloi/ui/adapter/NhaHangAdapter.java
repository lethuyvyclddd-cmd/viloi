package com.example.viloi.ui.adapter;

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
        this.items    = items;
        this.listener = null;
    }

    public NhaHangAdapter(List<NhaHang> items, OnClickListener listener) {
        this.items    = items;
        this.listener = listener;
    }

    public void setOnSuaListener(OnSuaListener l)  { this.onSuaListener  = l; }
    public void setOnXoaListener(OnXoaListener l)  { this.onXoaListener  = l; }

    // ─────────────────────────────────────────────────────────
    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_nha_hang_sua_xoa, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NhaHang nh = items.get(position);

        // Tên
        holder.tvTen.setText(nh.getTen() != null ? nh.getTen() : "");

        // Địa chỉ
        String diaChi = nh.getDiaChiDayDu() != null
                ? nh.getDiaChiDayDu() : nh.getDiaChi();
        holder.tvDiaChi.setText(diaChi != null ? diaChi : "");

        // Rating
        holder.tvSao.setText("⭐ " + nh.getRatingDisplay());

        // Giá
        holder.tvGia.setText(formatGia(nh.getKhoangGia()));

        // Yêu thích
        holder.tvYeuThich.setText("❤️ " + nh.getLuotYeuThich());

        // ✅ SỬA: Glide tự xử lý URL Firebase Storage, không cần kiểm tra startsWith
        String url = nh.getFirstImageUrl();
        if (url != null && !url.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(url)
                    .placeholder(R.drawable.ic_category_default)
                    .error(R.drawable.ic_category_default)
                    .centerCrop()
                    .into(holder.ivAnh);
        } else {
            holder.ivAnh.setImageResource(R.drawable.ic_category_default);
        }

        // Click vào card → xem chi tiết
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(nh);
        });

        // Nút Sửa
        holder.btnSua.setOnClickListener(v -> {
            if (onSuaListener != null) onSuaListener.onSua(nh, holder.getAdapterPosition());
        });

        // Nút Xóa
        holder.btnXoa.setOnClickListener(v -> {
            if (onXoaListener != null) onXoaListener.onXoa(nh, holder.getAdapterPosition());
        });
    }

    /** Xóa item khỏi danh sách sau khi xóa trên Firestore thành công */
    public void removeItem(int position) {
        if (position < 0 || position >= items.size()) return;
        items.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, items.size());
    }

    @Override
    public int getItemCount() { return items != null ? items.size() : 0; }

    // ─────────────────────────────────────────────────────────
    // VIEW HOLDER
    // ─────────────────────────────────────────────────────────
    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView  cardView;
        ImageView ivAnh;
        TextView  tvTen, tvDiaChi, tvSao, tvGia, tvYeuThich;
        Button    btnSua, btnXoa;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView   = itemView.findViewById(R.id.cardNhaHang);
            ivAnh      = itemView.findViewById(R.id.ivAnh);
            tvTen      = itemView.findViewById(R.id.tvTen);
            tvDiaChi   = itemView.findViewById(R.id.tvDiaChi);
            tvSao      = itemView.findViewById(R.id.tvSao);
            tvGia      = itemView.findViewById(R.id.tvGia);
            tvYeuThich = itemView.findViewById(R.id.tvYeuThich);
            btnSua     = itemView.findViewById(R.id.btnSua);
            btnXoa     = itemView.findViewById(R.id.btnXoa);
        }
    }

    // ─────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────
    private String formatGia(String raw) {
        if (raw == null || raw.isEmpty()) return "Chưa cập nhật";
        try {
            String[] arr = raw.split("-");
            if (arr.length == 2) {
                long min = Long.parseLong(arr[0].trim());
                long max = Long.parseLong(arr[1].trim());
                return String.format("%,dđ - %,dđ", min, max).replace(",", ".");
            }
        } catch (Exception ignored) {}
        return raw;
    }
}