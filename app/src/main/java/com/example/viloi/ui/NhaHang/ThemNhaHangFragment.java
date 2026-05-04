package com.example.viloi.ui.NhaHang;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.activity.result.*;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.*;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.viloi.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;

import java.io.*;
import java.util.*;

public class ThemNhaHangFragment extends Fragment {

    private TextInputEditText edtTen, edtKhoangGia, edtMoTa;
    private TextInputEditText edtPhuong, edtQuanHuyen, edtDiaChi, edtDiaChiDayDu;
    private TextInputEditText edtPhone, edtGioMoCua, edtGioDongCua;
    private Spinner spinnerDanhMuc;
    private ImageView ivHinhNhaHang;
    private Button btnSelectImage, btnSave;

    private FirebaseFirestore db;

    private Uri selectedImageUri = null;
    private final List<String> danhSachMaDM = new ArrayList<>();
    private final List<String> danhSachTenDM = new ArrayList<>();

    // chọn ảnh
    private final ActivityResultLauncher<Intent> imageLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK
                                && result.getData() != null) {
                            selectedImageUri = result.getData().getData();
                            ivHinhNhaHang.setImageURI(selectedImageUri);
                        }
                    });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_them_nha_hang, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        bindViews(view);
        loadDanhMuc();

        view.findViewById(R.id.ivBack).setOnClickListener(v ->
                Navigation.findNavController(v).navigateUp());

        btnSelectImage.setOnClickListener(v -> chonAnh());
        btnSave.setOnClickListener(v -> validate());
    }

    private void bindViews(View v) {
        edtTen = v.findViewById(R.id.edtTenNhaHang);
        edtKhoangGia = v.findViewById(R.id.edtKhoangGia);
        edtMoTa = v.findViewById(R.id.edtMoTaNhaHang);
        edtPhuong = v.findViewById(R.id.edtPhuong);
        edtQuanHuyen = v.findViewById(R.id.edtQuanHuyen);
        edtDiaChi = v.findViewById(R.id.edtDiaChi);
        edtDiaChiDayDu = v.findViewById(R.id.edtDiaChiDayDux);
        edtPhone = v.findViewById(R.id.edtPhone);
        edtGioMoCua = v.findViewById(R.id.edtGioMoCua);
        edtGioDongCua = v.findViewById(R.id.edtGioDongCua);
        spinnerDanhMuc = v.findViewById(R.id.spinnerDanhMuc);
        ivHinhNhaHang = v.findViewById(R.id.ivHinhNhaHang);
        btnSelectImage = v.findViewById(R.id.btnSelectImage);
        btnSave = v.findViewById(R.id.btnSaveNhaHang);
    }

    private void loadDanhMuc() {
        db.collection("danh_muc")
                .whereEqualTo("hoat_dong", true)
                .orderBy("uu_tien")
                .get()
                .addOnSuccessListener(snap -> {
                    if (!isAdded()) return;

                    danhSachMaDM.clear();
                    danhSachTenDM.clear();

                    for (QueryDocumentSnapshot doc : snap) {
                        danhSachMaDM.add(doc.getId());
                        danhSachTenDM.add(doc.getString("ten"));
                    }

                    ArrayAdapter<String> adp = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            danhSachTenDM);

                    adp.setDropDownViewResource(
                            android.R.layout.simple_spinner_dropdown_item);

                    spinnerDanhMuc.setAdapter(adp);
                });
    }

    private void chonAnh() {
        if (android.os.Build.VERSION.SDK_INT >= 33) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 1);
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imageLauncher.launch(intent);
    }

    // 🔥 copy ảnh vào app → trả về đường dẫn thật
    private String saveImageToApp(Uri uri) {
        try {
            InputStream input = requireActivity()
                    .getContentResolver()
                    .openInputStream(uri);

            File file = new File(requireActivity().getFilesDir(),
                    "img_" + System.currentTimeMillis() + ".jpg");

            OutputStream output = new FileOutputStream(file);

            byte[] buf = new byte[1024];
            int len;
            while ((len = input.read(buf)) > 0) {
                output.write(buf, 0, len);
            }

            output.close();
            input.close();

            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void validate() {
        String ten = getText(edtTen);
        String dc = getText(edtDiaChi);
        String sdt = getText(edtPhone);

        if (ten.isEmpty()) {
            edtTen.setError("Bắt buộc");
            return;
        }
        if (dc.isEmpty()) {
            edtDiaChi.setError("Bắt buộc");
            return;
        }
        if (sdt.isEmpty()) {
            edtPhone.setError("Bắt buộc");
            return;
        }

        btnSave.setEnabled(false);
        btnSave.setText("Đang lưu...");

        List<String> anhList = new ArrayList<>();

        if (selectedImageUri != null) {
            String path = saveImageToApp(selectedImageUri); // 🔥 FIX CHÍNH
            if (path != null) {
                anhList.add(path);
            }
        }

        luuFirestore(anhList);
    }

    private void luuFirestore(List<String> anhList) {
        int idx = spinnerDanhMuc.getSelectedItemPosition();

        String maDM = idx >= 0 && idx < danhSachMaDM.size()
                ? danhSachMaDM.get(idx) : "";

        String tenDM = idx >= 0 && idx < danhSachTenDM.size()
                ? danhSachTenDM.get(idx) : "";

        String ten = getText(edtTen);
        String phuong = getText(edtPhuong);
        String quanH = getText(edtQuanHuyen);
        String dc = getText(edtDiaChi);
        String dcDayDu = getText(edtDiaChiDayDu);
        String sdt = getText(edtPhone);
        String moTa = getText(edtMoTa);
        String gia = getText(edtKhoangGia);
        String gioMo = getText(edtGioMoCua);
        String gioDong = getText(edtGioDongCua);

        String adminUid = FirebaseAuth.getInstance().getUid();

        String tenKD = ten.toLowerCase()
                .replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a")
                .replaceAll("[èéẹẻẽêềếệểễ]", "e")
                .replaceAll("[ìíịỉĩ]", "i")
                .replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o")
                .replaceAll("[ùúụủũưừứựửữ]", "u")
                .replaceAll("[ỳýỵỷỹ]", "y")
                .replaceAll("[đ]", "d");

        Map<String, Object> data = new HashMap<>();

        data.put("ten", ten);
        data.put("ten_khong_dau", tenKD);
        data.put("ten_danh_muc", tenDM);
        data.put("ma_danh_muc", maDM);
        data.put("mo_ta", moTa);
        data.put("khoang_gia", gia);
        data.put("phuong", phuong);
        data.put("quan_huyen", quanH);
        data.put("tinh", "Cà Mau");
        data.put("dia_chi", dc);
        data.put("dia_chi_day_du", dcDayDu.isEmpty() ? dc + ", " + quanH : dcDayDu);
        data.put("dien_thoai", sdt);
        data.put("gio_mo_cua", gioMo.isEmpty() ? "07:00" : gioMo);
        data.put("gio_dong_cua", gioDong.isEmpty() ? "21:00" : gioDong);

        data.put("hinh_anh", anhList); // 🔥 lưu path thật

        data.put("hoat_dong", true);
        data.put("da_xoa", false);
        data.put("noi_bat", false);

        data.put("luot_xem", 0);
        data.put("luot_tim_kiem", 0);
        data.put("luot_yeu_thich", 0);

        data.put("so_luong_danh_gia", 0);
        data.put("danh_gia_trung_binh", 0.0);

        data.put("uu_tien", 0);

        data.put("tao_boi", adminUid);
        data.put("cap_nhat_boi", adminUid);

        data.put("tao_luc", Timestamp.now());
        data.put("cap_nhat_luc", Timestamp.now());

        db.collection("nha_hang")
                .add(data)
                .addOnSuccessListener(ref -> {
                    if (!isAdded()) return;

                    Toast.makeText(getContext(),
                            "Đã thêm nhà hàng ✓", Toast.LENGTH_SHORT).show();

                    Navigation.findNavController(requireView()).navigateUp();
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;

                    btnSave.setEnabled(true);
                    btnSave.setText("Lưu thông tin");

                    Toast.makeText(getContext(),
                            "Lỗi: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private String getText(TextInputEditText et) {
        return et.getText() != null
                ? et.getText().toString().trim()
                : "";
    }
}