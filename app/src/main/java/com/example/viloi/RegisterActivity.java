package com.example.viloi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.*;
import com.google.firebase.database.*;

import java.time.Instant;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText edtUsername, edtEmail, edtPassword, edtVerifyPassword;
    private MaterialButton btnRegister;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtUsername = findViewById(R.id.edtUsername);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtVerifyPassword = findViewById(R.id.edtVerifyPassword);
        btnRegister = findViewById(R.id.btnSignUp);

        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("nguoi_dung");

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {

        String name = edtUsername.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String pass = edtPassword.getText().toString().trim();
        String verify = edtVerifyPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)) { edtUsername.setError("Nhập tên"); return; }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { edtEmail.setError("Email sai"); return; }
        if (pass.length() < 6) { edtPassword.setError(">=6 ký tự"); return; }
        if (!pass.equals(verify)) { edtVerifyPassword.setError("Không khớp"); return; }

        btnRegister.setEnabled(false);

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener(authResult -> {

                    String uid = mAuth.getCurrentUser().getUid();
                    String time = Instant.now().toString();

                    HashMap<String, Object> user = new HashMap<>();
                    user.put("email", email);
                    user.put("ten_hien_thi", name);
                    user.put("vai_tro", "user");
                    user.put("hoat_dong", true);
                    user.put("tao_luc", time);
                    user.put("lan_hoat_dong_cuoi", time);
                    user.put("tong_tim_kiem", 0);

                    user.put("lich_su_tim_kiem", new HashMap<>());
                    user.put("yeu_thich", new HashMap<>());

                    userRef.child(uid).setValue(user)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(this, "Đăng ký OK", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(this, MainActivity.class));
                                finish();
                            });

                })
                .addOnFailureListener(e -> {
                    btnRegister.setEnabled(true);
                    Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}