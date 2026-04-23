package com.example.viloi.ui.model;

import com.google.firebase.firestore.PropertyName;

import java.util.List;
import java.util.Locale;

public class Restaurant {
    private String id;
    private String name;
    private String address;
    private String imageUrl;
    private double rating;
    private int searchCount;
    private int viewCount;
    private boolean isHot;
    private List<String> categories;
    private String categoryDisplay;

    // Bắt buộc cho Firestore
    public Restaurant() {}

    public Restaurant(String id, String name, String address, String imageUrl,
                      double rating, int searchCount, int viewCount,
                      boolean isHot, List<String> categories) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.searchCount = searchCount;
        this.viewCount = viewCount;
        this.isHot = isHot;
        this.categories = categories;
    }

    // ===== Getter / Setter =====
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name != null ? name : ""; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address != null ? address : ""; }
    public void setAddress(String address) { this.address = address; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getSearchCount() { return searchCount; }
    public void setSearchCount(int searchCount) { this.searchCount = searchCount; }

    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }

    @PropertyName("isHot")
    public boolean isHot() { return isHot; }

    @PropertyName("isHot")
    public void setHot(boolean hot) { isHot = hot; }

    public List<String> getCategories() { return categories; }
    public void setCategories(List<String> categories) { this.categories = categories; }

    // ===== Helper UI =====
    public String getCategoryDisplay() {
        if (categories != null && !categories.isEmpty()) {
            return String.join(" · ", categories);
        }
        return categoryDisplay != null ? categoryDisplay : "";
    }

    public void setCategoryDisplay(String categoryDisplay) {
        this.categoryDisplay = categoryDisplay;
    }

    public String getSearchCountDisplay() {
        return searchCount + " lần tìm";
    }

    public String getViewCountDisplay() {
        return "· " + viewCount + " lượt xem";
    }

    public String getRatingDisplay() {
        return String.format(Locale.getDefault(), "%.1f", rating);
    }
}