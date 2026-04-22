package com.example.viloi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText edtUsername, edtEmail, edtPassword, edtVerifyPassword;
    private MaterialButton btnRegister;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;

    private static final String FIREBASE_URL =
            "https://vilo-24bb7-default-rtdb.firebaseio.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // ===== ÁNH XẠ VIEW =====
        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtVerifyPassword = findViewById(R.id.edtVerifyPassword);
        btnRegister = findViewById(R.id.btnSignUp);

        // ===== FIREBASE =====
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance(FIREBASE_URL)
                .getReference("nguoi_dung");

        // ===== CLICK REGISTER =====
        btnRegister.setOnClickListener(v -> registerUser());

        // ===== CHUYỂN LOGIN =====
        findViewById(R.id.txtLogin).setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void registerUser() {

        String username = edtUsername.getText() != null
                ? edtUsername.getText().toString().trim() : "";

        String email = edtEmail.getText() != null
                ? edtEmail.getText().toString().trim() : "";

        String password = edtPassword.getText() != null
                ? edtPassword.getText().toString().trim() : "";

        String verifyPassword = edtVerifyPassword.getText() != null
                ? edtVerifyPassword.getText().toString().trim() : "";

        // ===== VALIDATE =====
        if (TextUtils.isEmpty(username)) {
            edtUsername.setError("Vui lòng nhập họ tên");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Vui lòng nhập email");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Email không hợp lệ");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Vui lòng nhập mật khẩu");
            return;
        }

        if (password.length() < 6) {
            edtPassword.setError("Mật khẩu >= 6 ký tự");
            return;
        }

        if (!password.equals(verifyPassword)) {
            edtVerifyPassword.setError("Mật khẩu không khớp");
            return;
        }

        // ===== LOADING =====
        btnRegister.setEnabled(false);
        btnRegister.setText("Đang đăng ký...");

        // ===== FIREBASE AUTH =====
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {

                    FirebaseUser user = authResult.getUser();
                    if (user == null) {
                        showError("Không lấy được user");
                        return;
                    }

                    String userId = user.getUid();

                    // ===== DATA USER =====
                    HashMap<String, Object> userMap = new HashMap<>();

                    String currentTime = String.valueOf(System.currentTimeMillis()); // ✅ FIX API

                    userMap.put("email", email);
                    userMap.put("ten_hien_thi", username);
                    userMap.put("vai_tro", "user");

                    userMap.put("hoat_dong", true);
                    userMap.put("tong_tim_kiem", 0);

                    userMap.put("tao_luc", currentTime);
                    userMap.put("lan_hoat_dong_cuoi", currentTime);

                    userMap.put("yeu_thich", new HashMap<>());
                    userMap.put("lich_su_tim_kiem", new HashMap<>());

                    // ===== LƯU DATABASE =====
                    databaseReference.child(userId).setValue(userMap)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                showError("Lỗi lưu DB: " + e.getMessage());
                            });

                })
                .addOnFailureListener(e -> {

                    btnRegister.setEnabled(true);
                    btnRegister.setText("ĐĂNG KÝ NGAY");

                    if (e instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(this, "Email đã tồn tại! Chuyển sang đăng nhập...", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showError(String message) {
        Toast.makeText(this, "Lỗi: " + message, Toast.LENGTH_LONG).show();
        btnRegister.setEnabled(true);
        btnRegister.setText("ĐĂNG KÝ NGAY");
    }
}