package com.example.viloi;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        bottomMenu = findViewById(R.id.bottom_menu);

        // lấy NavHostFragment
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host);

        // lấy NavController
        NavController navController = navHostFragment.getNavController();

        // gắn BottomNavigation với Navigation
        NavigationUI.setupWithNavController(bottomMenu, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {

            int id = destination.getId();

            if (id == R.id.themDanhMucFragment
                    || id == R.id.SuaDanhMucFragment
                    || id == R.id.themNhaHangFragment
                    || id == R.id.SuaNhaHangFragment
                    || id == R.id.NhaHangFragment) {

                bottomMenu.setVisibility(View.GONE); // ẨN

            } else {
                bottomMenu.setVisibility(View.VISIBLE); // HIỆN
            }

        });

    }
}