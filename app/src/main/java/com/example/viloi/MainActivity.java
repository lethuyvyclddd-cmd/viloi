package com.example.viloi;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomMenu;
    private NavController navController;

    // Danh sách ID của các màn hình top-level (tab)
    private static final int[] TOP_LEVEL_IDS = {
            R.id.homeFragment,
            R.id.categoryFragment,
            R.id.searchFragment,
            R.id.favoriteFragment,
            R.id.profileFragment
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        bottomMenu = findViewById(R.id.bottom_menu);

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host);

        navController = navHostFragment.getNavController();

        // KHÔNG dùng setupWithNavController nữa — tự handle để kiểm soát back stack
        setupBottomNav();

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            int id = destination.getId();

            // Ẩn bottom nav ở các màn hình con
            if (id == R.id.themDanhMucFragment
                    || id == R.id.SuaDanhMucFragment
                    || id == R.id.themNhaHangFragment
                    || id == R.id.SuaNhaHangFragment
                    || id == R.id.NhaHangFragment
                    || id == R.id.ChiTietNhaHangFragment  // ← thêm dòng này
                    || id == R.id.lichSuTimKiemFragment
                    || id == R.id.caiDatFragment) {

                bottomMenu.setVisibility(View.GONE);

            } else {
                bottomMenu.setVisibility(View.VISIBLE);

                // Đồng bộ item được chọn trên bottom nav
                syncBottomNavSelection(id);
            }
        });
    }

    private void setupBottomNav() {
        bottomMenu.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            // Nếu đang ở tab đó rồi thì không làm gì
            if (navController.getCurrentDestination() != null
                    && navController.getCurrentDestination().getId() == itemId) {
                return true;
            }

            // NavOptions: luôn popUpTo homeFragment để không chồng stack
            NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(R.id.homeFragment, false)
                    .setLaunchSingleTop(true)
                    .build();

            navController.navigate(itemId, null, navOptions);
            return true;
        });
    }

    // Đồng bộ highlight đúng tab khi back bằng nút hệ thống
    private void syncBottomNavSelection(int destinationId) {
        for (int id : TOP_LEVEL_IDS) {
            if (id == destinationId) {
                // Dùng setSelectedItemId chỉ khi chưa được chọn để tránh vòng lặp
                if (bottomMenu.getSelectedItemId() != destinationId) {
                    bottomMenu.setSelectedItemId(destinationId);
                }
                return;
            }
        }
    }
}