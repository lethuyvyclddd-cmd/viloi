package com.example.viloi.ui.DanhMuc;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.viloi.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ThemDanhMucFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ThemDanhMucFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ThemDanhMucFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ThemDanhMucFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ThemDanhMucFragment newInstance(String param1, String param2) {
        ThemDanhMucFragment fragment = new ThemDanhMucFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_them_danh_muc, container, false);

        EditText ten = view.findViewById(R.id.edtTenDanhMuc);
        EditText mota = view.findViewById(R.id.edtMoTa);
        EditText icon = view.findViewById(R.id.edtIcon);
        EditText mau = view.findViewById(R.id.edtMau);
        Button btn = view.findViewById(R.id.btnThemDanhMuc);

        btn.setOnClickListener(v -> {
            String t = ten.getText().toString();
            String m = mota.getText().toString();
            String i = icon.getText().toString();
            String c = mau.getText().toString();

            // test trước
            Toast.makeText(getContext(), "Đã nhập: " + t, Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}