package com.example.viloi.ui.adapter;

import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viloi.R;
import com.example.viloi.ui.model.LichSuTimKiem;

import java.util.List;

public class LichSuAdapter extends RecyclerView.Adapter<LichSuAdapter.VH> {

    public interface OnXoaClick {
        void onXoa(LichSuTimKiem item, int position);
    }

    private final List<LichSuTimKiem> list;
    private final OnXoaClick callback;

    public LichSuAdapter(List<LichSuTimKiem> list, OnXoaClick callback) {
        this.list = list;
        this.callback = callback;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_lich_su, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        LichSuTimKiem item = list.get(position);

        // Tên quán
        holder.tvTuKhoa.setText(
                item.getTenNhaHang() != null ? item.getTenNhaHang() : ""
        );

        // Thời gian
        if (item.getThoiGian() != null) {
            long time = item.getThoiGian().toDate().getTime();
            holder.tvThoiGian.setText(formatTime(time));
        } else {
            holder.tvThoiGian.setText("");
        }

        // Nút xoá
        holder.btnXoa.setOnClickListener(v -> {
            if (callback != null) {
                callback.onXoa(item, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTuKhoa, tvThoiGian;
        ImageView btnXoa;

        VH(@NonNull View v) {
            super(v);
            tvTuKhoa   = v.findViewById(R.id.tvTuKhoa);
            tvThoiGian = v.findViewById(R.id.tvThoiGian);
            btnXoa     = v.findViewById(R.id.btnXoa);
        }
    }

    // ✅ Format thời gian kiểu "5 phút trước"
    private String formatTime(long time) {
        return android.text.format.DateUtils.getRelativeTimeSpanString(
                time,
                System.currentTimeMillis(),
                android.text.format.DateUtils.MINUTE_IN_MILLIS
        ).toString();
    }
}