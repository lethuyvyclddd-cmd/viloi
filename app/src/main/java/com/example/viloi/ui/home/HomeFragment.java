package com.example.viloi.ui.home;

import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.viloi.R;
import com.example.viloi.adapter.CategoryAdapter;
import com.example.viloi.adapter.HotRestaurantAdapter;
import com.example.viloi.adapter.SuggestedAdapter;
import com.example.viloi.model.Category;
import com.example.viloi.model.Restaurant;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    // UI Components
    private TextView tvLocation;
    private TextView tvUserInitial;
    private EditText etSearch;
    private RecyclerView rvCategories;
    private RecyclerView rvSuggested;
    private RecyclerView rvHotRestaurants;

    // Adapters
    private CategoryAdapter categoryAdapter;
    private SuggestedAdapter suggestedAdapter;
    private HotRestaurantAdapter hotRestaurantAdapter;

    // Data Lists
    private List<Category> categoryList = new ArrayList<>();
    private List<Restaurant> suggestedList = new ArrayList<>();
    private List<Restaurant> hotList = new ArrayList<>();

    // Firebase
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Bind views
        tvLocation = view.findViewById(R.id.tv_location);
        tvUserInitial = view.findViewById(R.id.tv_user_initial);
        etSearch = view.findViewById(R.id.et_search);
        rvCategories = view.findViewById(R.id.rv_categories);
        rvSuggested = view.findViewById(R.id.rv_suggested);
        rvHotRestaurants = view.findViewById(R.id.rv_hot_restaurants);

        setupUser();
        setupRecyclerViews();
        setupSearch();
        loadDataFromFirebase();
    }

    private void setupUser() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String displayName = user.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                tvUserInitial.setText(String.valueOf(displayName.charAt(0)).toUpperCase());
            } else {
                String email = user.getEmail();
                if (email != null && !email.isEmpty()) {
                    tvUserInitial.setText(String.valueOf(email.charAt(0)).toUpperCase());
                }
            }
        }

        // Get user location from Firestore
        if (user != null) {
            db.collection("users").document(user.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String location = documentSnapshot.getString("location");
                            if (location != null) {
                                tvLocation.setText(location);
                            }
                        }
                    });
        }
    }

    private void setupRecyclerViews() {
        // Categories - Horizontal
        rvCategories.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(categoryList, category -> {
            // Navigate to category detail
            Toast.makeText(getContext(), "Danh mục: " + category.getName(), Toast.LENGTH_SHORT).show();
        });
        rvCategories.setAdapter(categoryAdapter);

        // Suggested - Horizontal
        rvSuggested.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        suggestedAdapter = new SuggestedAdapter(suggestedList, restaurant -> {
            navigateToRestaurantDetail(restaurant);
        });
        rvSuggested.setAdapter(suggestedAdapter);

        // Hot Restaurants - Vertical
        rvHotRestaurants.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHotRestaurants.setNestedScrollingEnabled(false);
        hotRestaurantAdapter = new HotRestaurantAdapter(hotList, restaurant -> {
            navigateToRestaurantDetail(restaurant);
        });
        rvHotRestaurants.setAdapter(hotRestaurantAdapter);
    }

    private void setupSearch() {
        etSearch.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                // Navigate to Search Fragment
                // Navigation.findNavController(v).navigate(R.id.action_home_to_search);
            }
        });
    }

    private void loadDataFromFirebase() {
        loadCategories();
        loadSuggestedRestaurants();
        loadHotRestaurants();
    }

    private void loadCategories() {
        db.collection("categories")
                .orderBy("order")
                .limit(5)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    categoryList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Category category = doc.toObject(Category.class);
                        category.setId(doc.getId());
                        categoryList.add(category);
                    }
                    categoryAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi tải danh mục", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadSuggestedRestaurants() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) return;

        // Load search history or top-rated for user
        db.collection("restaurants")
                .orderBy("searchCount", Query.Direction.DESCENDING)
                .limit(5)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    suggestedList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Restaurant restaurant = doc.toObject(Restaurant.class);
                        restaurant.setId(doc.getId());
                        suggestedList.add(restaurant);
                    }
                    suggestedAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Load fallback without ordering
                    db.collection("restaurants")
                            .limit(5)
                            .get()
                            .addOnSuccessListener(snapshots -> {
                                suggestedList.clear();
                                for (QueryDocumentSnapshot doc : snapshots) {
                                    Restaurant restaurant = doc.toObject(Restaurant.class);
                                    restaurant.setId(doc.getId());
                                    suggestedList.add(restaurant);
                                }
                                suggestedAdapter.notifyDataSetChanged();
                            });
                });
    }

    private void loadHotRestaurants() {
        db.collection("restaurants")
                .whereEqualTo("isHot", true)
                .orderBy("viewCount", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    hotList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Restaurant restaurant = doc.toObject(Restaurant.class);
                        restaurant.setId(doc.getId());
                        hotList.add(restaurant);
                    }
                    hotRestaurantAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Fallback: load top rated
                    db.collection("restaurants")
                            .orderBy("rating", Query.Direction.DESCENDING)
                            .limit(10)
                            .get()
                            .addOnSuccessListener(snapshots -> {
                                hotList.clear();
                                for (QueryDocumentSnapshot doc : snapshots) {
                                    Restaurant restaurant = doc.toObject(Restaurant.class);
                                    restaurant.setId(doc.getId());
                                    hotList.add(restaurant);
                                }
                                hotRestaurantAdapter.notifyDataSetChanged();
                            });
                });
    }

    private void navigateToRestaurantDetail(Restaurant restaurant) {
        // Example: use NavController
        // Bundle bundle = new Bundle();
        // bundle.putString("restaurantId", restaurant.getId());
        // Navigation.findNavController(requireView())
        //           .navigate(R.id.action_home_to_restaurantDetail, bundle);
        Toast.makeText(getContext(), "Mở: " + restaurant.getName(), Toast.LENGTH_SHORT).show();
    }
}
