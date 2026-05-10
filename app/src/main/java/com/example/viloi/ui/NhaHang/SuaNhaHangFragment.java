package com.example.viloi.ui.NhaHang;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.activity.result.*;
//import androidx.activity.result.register.*;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.viloi.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.*;

import java.util.*;

public class SuaNhaHangFragment extends Fragment {

    public static final String ARG_MA_NHA_HANG = "maNhaHang";

    private TextInputEditText edtTen, edtKhoangGia, edtMoTa;
    private TextInputEditText edtPhuong, edtQuanHuyen, edtDiaChi, edtDiaChiDayDu;
    private TextInputEditText edtPhone, edtGioMoCua, edtGioDongCua;
    private Spinner           spinnerCategory;
    private ImageView         ivAnhHienTai;
    private Button            btnSuaAnh, btnSua, btnCancel;

    private FirebaseFirestore db;
    private FirebaseStorage   storage;
    private String maNhaHang;
    private Uri    selectedImageUri = null;
    private List<String> anhHienTai = new ArrayList<>();

    private final List<String> dsMaDM  = new ArrayList<>();
    private final List<String> dsTenDM = new ArrayList<>();

    private final ActivityResultLauncher<Intent> imageLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == Activity.RESULT_OK
                                && result.getData() != null) {
                            selectedImageUri = result.getData().getData();
                            // ✅ Hiển thị ảnh mới chọn ngay lập tức
                            Glide.with(this)
                                    .load(selectedImageUri)
                                    .placeholder(R.drawable.ic_category_default)
                                    .error(R.drawable.ic_category_default)
                                    .centerCrop()
                                    .into(ivAnhHienTai);
                        }
                    });

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sua_nha_hang, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db      = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        if (getArguments() != null)
            maNhaHang = getArguments().getString(ARG_MA_NHA_HANG);

        bindViews(view);

        view.findViewById(R.id.ivThoat).setOnClickListener(v ->
                Navigation.findNavController(v).navigateUp());
        btnCancel.setOnClickListener(v ->
                Navigation.findNavController(view).navigateUp());
        btnSuaAnh.setOnClickListener(v -> chonAnh());
        btnSua.setOnClickListener(v -> validate());

        loadDanhMuc();
    }

    private void bindViews(View v) {
        edtTen         = v.findViewById(R.id.edtSuaTenNhaHang);
        edtKhoangGia   = v.findViewById(R.id.edtSuaKhoangGia);
        edtMoTa        = v.findViewById(R.id.edtDescription);
        edtPhuong      = v.findViewById(R.id.edtSuaPhuong);
        edtQuanHuyen   = v.findViewById(R.id.edtSuaQuanHuyen);
        edtDiaChi      = v.findViewById(R.id.edtSuaDiaChi);
        edtDiaChiDayDu = v.findViewById(R.id.edtSuaDiaChiDayDu);
        edtPhone       = v.findViewById(R.id.edtSuaSoDT);
        edtGioMoCua    = v.findViewById(R.id.edtSuaGioMoCua);
        edtGioDongCua  = v.findViewById(R.id.edtSuaGioDongCua);
        spinnerCategory = v.findViewById(R.id.spinnerCategory);
        ivAnhHienTai   = v.findViewById(R.id.ivAnhNhaHangHienTai);
        btnSuaAnh      = v.findViewById(R.id.btnSuaAnh);
        btnSua         = v.findViewById(R.id.btnSua);
        btnCancel      = v.findViewById(R.id.btnCancel);
    }

    private void loadDanhMuc() {
        db.collection("danh_muc")
                .whereEqualTo("hoat_dong", true)
                .orderBy("uu_tien")
                .get()
                .addOnSuccessListener(snap -> {
                    if (!isAdded()) return;
                    dsMaDM.clear(); dsTenDM.clear();
                    for (QueryDocumentSnapshot doc : snap) {
                        dsMaDM.add(doc.getId());
                        dsTenDM.add(doc.getString("ten"));
                    }
                    ArrayAdapter<String> adp = new ArrayAdapter<>(
                            requireContext(),
                            android.R.layout.simple_spinner_item, dsTenDM);
                    adp.setDropDownViewResource(
                            android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(adp);
                    // Sau khi load DM thì load NH để set spinner đúng
                    if (maNhaHang != null) loadNhaHang();
                });
    }

    private void loadNhaHang() {
        if (dsMaDM.isEmpty()) return; // chờ loadDanhMuc xong
        db.collection("nha_hang").document(maNhaHang)
                .get()
                .addOnSuccessListener(doc -> {
                    if (!isAdded() || !doc.exists()) return;
                    setText(edtTen,         doc.getString("ten"));
                    setText(edtKhoangGia,   doc.getString("khoang_gia"));
                    setText(edtMoTa,        doc.getString("mo_ta"));
                    setText(edtPhuong,      doc.getString("phuong"));
                    setText(edtQuanHuyen,   doc.getString("quan_huyen"));
                    setText(edtDiaChi,      doc.getString("dia_chi"));
                    setText(edtDiaChiDayDu, doc.getString("dia_chi_day_du"));
                    setText(edtPhone,       doc.getString("dien_thoai"));
                    setText(edtGioMoCua,    doc.getString("gio_mo_cua"));
                    setText(edtGioDongCua,  doc.getString("gio_dong_cua"));

                    // Set spinner danh mục
                    String maDM = doc.getString("ma_danh_muc");
                    int idx = dsMaDM.indexOf(maDM);
                    if (idx >= 0) spinnerCategory.setSelection(idx);

                    List<String> anh = (List<String>) doc.get("hinh_anh");
                    if (anh != null && !anh.isEmpty()) {
                        anhHienTai = anh;
                        Glide.with(this)
                                .load(anh.get(0))
                                .placeholder(R.drawable.ic_category_default)
                                .error(R.drawable.ic_category_default)
                                .centerCrop()
                                .into(ivAnhHienTai);
                    }
                });
    }

    private void chonAnh() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imageLauncher.launch(intent);
    }

    private void validate() {
        String ten = getText(edtTen);
        String dc  = getText(edtDiaChi);
        if (ten.isEmpty()) { edtTen.setError("Bắt buộc"); return; }
        if (dc.isEmpty())  { edtDiaChi.setError("Bắt buộc"); return; }

        btnSua.setEnabled(false);
        btnSua.setText("Đang lưu...");

        if (selectedImageUri != null) {
            uploadAnhRoiLuu();
        } else {
            luuFirestore(anhHienTai);
        }
    }

    private void uploadAnhRoiLuu() {
//        if (selectedImageUri == null) {
//            luuFirestore(anhHienTai);
//            return;
//        }
//
//        // ✅ Đọc bytes từ URI local trước, rồi mới upload
//        try {
//            java.io.InputStream inputStream = requireContext()
//                    .getContentResolver()
//                    .openInputStream(selectedImageUri);
//
//            if (inputStream == null) {
//                Toast.makeText(getContext(), "Không đọc được ảnh", Toast.LENGTH_SHORT).show();
//                resetButton();
//                return;
//            }
//
//            byte[] bytes = readBytes(inputStream);
//            inputStream.close();
//
//            String fileName = "nha_hang/" + maNhaHang + "_" + System.currentTimeMillis() + ".jpg";
//            StorageReference ref = storage.getReference().child(fileName);
//
//            ref.putBytes(bytes)
//                    .addOnSuccessListener(t -> ref.getDownloadUrl()
//                            .addOnSuccessListener(uri -> luuFirestore(
//                                    Collections.singletonList(uri.toString())))
//                            .addOnFailureListener(e -> {
//                                resetButton();
//                                Toast.makeText(getContext(),
//                                        "Lấy URL ảnh thất bại: " + e.getMessage(),
//                                        Toast.LENGTH_SHORT).show();
//                            }))
//                    .addOnFailureListener(e -> {
//                        resetButton();
//                        Toast.makeText(getContext(),
//                                "Upload ảnh thất bại: " + e.getMessage(),
//                                Toast.LENGTH_SHORT).show();
//                    });
//
//        } catch (Exception e) {
//            resetButton();
//            Toast.makeText(getContext(),
//                    "Lỗi đọc ảnh: " + e.getMessage(),
//                    Toast.LENGTH_SHORT).show();
//        }
    }
    private byte[] readBytes(java.io.InputStream inputStream) throws java.io.IOException {
        java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
        byte[] temp = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(temp)) != -1) {
            buffer.write(temp, 0, bytesRead);
        }
        return buffer.toByteArray();
    }


    private void luuFirestore(List<String> anhList) {
        int    idx    = spinnerCategory.getSelectedItemPosition();
        String maDM   = idx >= 0 && idx < dsMaDM.size()  ? dsMaDM.get(idx)  : "";
        String tenDM  = idx >= 0 && idx < dsTenDM.size() ? dsTenDM.get(idx) : "";
        String dc     = getText(edtDiaChi);
        String quanH  = getText(edtQuanHuyen);
        String dcDayDu = getText(edtDiaChiDayDu);

        Map<String, Object> data = new HashMap<>();
        data.put("ten",             getText(edtTen));
        data.put("ma_danh_muc",     maDM);
        data.put("ten_danh_muc",    tenDM);
        data.put("mo_ta",           getText(edtMoTa));
        data.put("khoang_gia",      getText(edtKhoangGia));
        data.put("phuong",          getText(edtPhuong));
        data.put("quan_huyen",      quanH);
        data.put("dia_chi",         dc);
        data.put("dia_chi_day_du",  dcDayDu.isEmpty() ? dc + ", " + quanH : dcDayDu);
        data.put("dien_thoai",      getText(edtPhone));
        data.put("gio_mo_cua",      getText(edtGioMoCua));
        data.put("gio_dong_cua",    getText(edtGioDongCua));
        data.put("hinh_anh",        anhList);
        data.put("cap_nhat_boi",    FirebaseAuth.getInstance().getUid());
        data.put("cap_nhat_luc",    Timestamp.now());

        db.collection("nha_hang").document(maNhaHang)
                .update(data)
                .addOnSuccessListener(unused -> {
                    if (!isAdded()) return;
                    Toast.makeText(getContext(), "Đã cập nhật ✓",
                            Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(requireView()).navigateUp();
                })
                .addOnFailureListener(e -> {
                    if (!isAdded()) return;
                    btnSua.setEnabled(true);
                    btnSua.setText("Sửa");
                    Toast.makeText(getContext(), "Lỗi: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void resetButton() {
        btnSua.setEnabled(true);
        btnSua.setText("Sửa");
    }

    private void setText(TextInputEditText et, String val) {
        if (et != null && val != null) et.setText(val);
    }

    private String getText(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }
}