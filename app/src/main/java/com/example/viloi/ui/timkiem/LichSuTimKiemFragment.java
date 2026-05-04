package com.example.viloi.ui.timkiem;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.viloi.R;

public class LichSuTimKiemFragment extends Fragment {

    private ImageView btnBack;

    public LichSuTimKiemFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lich_su_tim_kiem, container, false);

        btnBack = view.findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v ->
                Navigation.findNavController(view).popBackStack()
        );

        return view;
    }
}