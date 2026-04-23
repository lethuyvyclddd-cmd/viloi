package com.example.viloi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

import java.time.Instant;
import java.util.HashMap;

public class FirebaseHelper {

    public static void saveSearch(String restaurantId, String restaurantName) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();

        DatabaseReference root = FirebaseDatabase.getInstance()
                .getReference("nguoi_dung")
                .child(uid);

        DatabaseReference history = root
                .child("lich_su_tim_kiem")
                .child(restaurantId);

        String time = Instant.now().toString();

        history.get().addOnSuccessListener(snapshot -> {

            if (snapshot.exists()) {
                Long count = snapshot.child("so_lan_tim").getValue(Long.class);
                if (count == null) count = 0L;

                history.child("so_lan_tim").setValue(count + 1);
                history.child("lan_tim_cuoi").setValue(time);

            } else {

                HashMap<String, Object> map = new HashMap<>();
                map.put("ma_nha_hang", restaurantId);
                map.put("ten_nha_hang", restaurantName);
                map.put("so_lan_tim", 1);
                map.put("lan_tim_cuoi", time);

                history.setValue(map);
            }

            // tổng tìm
            root.child("tong_tim_kiem").get().addOnSuccessListener(snap -> {
                Long total = snap.getValue(Long.class);
                if (total == null) total = 0L;
                root.child("tong_tim_kiem").setValue(total + 1);
            });
        });
    }
}