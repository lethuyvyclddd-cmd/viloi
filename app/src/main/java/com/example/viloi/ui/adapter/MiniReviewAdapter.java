package com.example.viloi.ui.adapter;

import android.graphics.Color;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.viloi.R;
import java.text.SimpleDateFormat;
import java.util.*;

public class MiniReviewAdapter
        extends RecyclerView.Adapter<MiniReviewAdapter.ViewHolder> {

    public interface OnReviewAction {
        void onAction(Map<String, Object> review);
    }

    private List<Map<String, Object>> data = new ArrayList<>();
    private final String         currentUserId;
    private final OnReviewAction onEdit;
    private final OnReviewAction onDelete;

    // Màu avatar theo chữ cái đầu
    private static final String[] BG_COLORS = {
            "#FFF0EB","#E3F2FD","#E8F5E9","#FCE4EC","#FFF8E1","#EDE7F6"
    };
    private static final String[] TEXT_COLORS = {
            "#CC4A18","#0C447C","#27500A","#72243E","#633806","#4527A0"
    };

    public MiniReviewAdapter(String userId,
                             OnReviewAction onEdit,
                             OnReviewAction onDelete) {
        this.currentUserId = userId;
        this.onEdit   = onEdit;
        this.onDelete = onDelete;
    }

    public void setData(List<Map<String, Object>> newData) {
        this.data = newData != null ? newData : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Map<String, Object> review = data.get(position);

        // ── Avatar chữ cái đầu ──
        String ten = getString(review, "ten_nguoi_dung", "?");
        String initial = ten.isEmpty() ? "?" : String.valueOf(ten.charAt(0)).toUpperCase();
        h.tvAvatar.setText(initial);
        int colorIdx = Math.abs(initial.charAt(0)) % BG_COLORS.length;
        h.tvAvatar.setBackgroundColor(Color.parseColor(BG_COLORS[colorIdx]));
        h.tvAvatar.setTextColor(Color.parseColor(TEXT_COLORS[colorIdx]));

        // ── Tên + ngày ──
        h.tvUserName.setText(ten);
        Object taoLuc = review.get("tao_luc");
        if (taoLuc instanceof com.google.firebase.Timestamp) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            h.tvDate.setText(sdf.format(((com.google.firebase.Timestamp) taoLuc).toDate()));
        }

        // ── Sao ──
        int soSao = 0;
        Object ss = review.get("so_sao");
        if (ss != null) soSao = (int) Math.round(((Number) ss).doubleValue());
        buildStars(h.layoutStars, soSao);

        // ── Bình luận ──
        h.tvComment.setText(getString(review, "binh_luan", ""));

        // ── Badge đã chỉnh sửa ──
        Boolean daChinhSua = (Boolean) review.get("da_chinh_sua");
        h.tvEdited.setVisibility(
                Boolean.TRUE.equals(daChinhSua) ? View.VISIBLE : View.GONE);

        // ── Nút sửa/xóa: chỉ hiện với review của mình ──
        String ownerUid = getString(review, "ma_nguoi_dung", "");
        if (!currentUserId.isEmpty() && currentUserId.equals(ownerUid)) {
            h.layoutActions.setVisibility(View.VISIBLE);
            h.btnEdit.setOnClickListener(v -> onEdit.onAction(review));
            h.btnDelete.setOnClickListener(v -> onDelete.onAction(review));
        } else {
            h.layoutActions.setVisibility(View.GONE);
        }
    }

    @Override public int getItemCount() { return data.size(); }

    // ── Tạo dòng sao ──────────────────────────────────────────
    private void buildStars(LinearLayout container, int filled) {
        container.removeAllViews();
        for (int i = 1; i <= 5; i++) {
            TextView star = new TextView(container.getContext());
            star.setText("★");
            star.setTextSize(14f);
            star.setTextColor(container.getContext().getResources().getColor(
                    i <= filled ? R.color.star_color : R.color.star_empty, null));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMarginEnd(2);
            star.setLayoutParams(lp);
            container.addView(star);
        }
    }

    private String getString(Map<String, Object> map, String key, String def) {
        Object val = map.get(key);
        return val != null ? val.toString() : def;
    }

    // ── ViewHolder ────────────────────────────────────────────
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView    tvAvatar, tvUserName, tvDate, tvComment, tvEdited;
        TextView    btnEdit, btnDelete;
        LinearLayout layoutStars, layoutActions;

        ViewHolder(@NonNull View v) {
            super(v);
            tvAvatar       = v.findViewById(R.id.tv_avatar);
            tvUserName     = v.findViewById(R.id.tv_user_name);
            tvDate         = v.findViewById(R.id.tv_date);
            tvComment      = v.findViewById(R.id.tv_comment);
            tvEdited       = v.findViewById(R.id.tv_edited);
            layoutStars    = v.findViewById(R.id.layout_stars);
            layoutActions  = v.findViewById(R.id.layout_actions);
            btnEdit        = v.findViewById(R.id.btn_edit);
            btnDelete      = v.findViewById(R.id.btn_delete);
        }
    }
}