package com.example.viloi.ui.adapter;


import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.viloi.R;
import com.example.viloi.ui.model.NhaHang;
import java.util.List;

public class TimKiemAdapter extends RecyclerView.Adapter<TimKiemAdapter.VH> {

    public interface OnClick { void onClick(NhaHang nh); }

    private final List<NhaHang> list;
    private final OnClick       callback;

    public TimKiemAdapter(List<NhaHang> list, OnClick callback) {
        this.list     = list;
        this.callback = callback;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_nha_hang_search, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        NhaHang nh = list.get(position);
        h.tvTen.setText(nh.getTen() != null ? nh.getTen() : "");
        h.tvDiaChi.setText(nh.getDiaChi() != null ? nh.getDiaChi() : "");
        h.tvDanhMuc.setText(nh.getTenDanhMuc() != null ? nh.getTenDanhMuc() : "");
        h.tvSao.setText(nh.getRatingDisplay());
        h.tvLuotXem.setText(nh.getLuotXemDisplay());

        h.itemView.setOnClickListener(v -> {
            int pos = h.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) callback.onClick(list.get(pos));
        });
    }

    @Override public int getItemCount() { return list.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTen, tvDiaChi, tvDanhMuc, tvSao, tvLuotXem;
        VH(@NonNull View v) {
            super(v);
            tvTen     = v.findViewById(R.id.tvTenNhaHang);
            tvDiaChi  = v.findViewById(R.id.tvDiaChi);
            tvDanhMuc = v.findViewById(R.id.tvDanhMuc);
            tvSao     = v.findViewById(R.id.tvSao);
            tvLuotXem = v.findViewById(R.id.tvLuotXem);
        }
    }
}