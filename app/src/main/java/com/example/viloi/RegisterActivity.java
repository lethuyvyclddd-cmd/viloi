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
        databaseReference = FirebaseDatabase.getInstance()
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
                .addOnCompleteListener(task -> {

                    btnRegister.setEnabled(true);
                    btnRegister.setText("ĐĂNG KÝ NGAY");

                    if (task.isSuccessful()) {

                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user == null) {
                            Toast.makeText(this, "User null", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String userId = user.getUid();

                        HashMap<String, Object> userMap = new HashMap<>();
                        String currentTime = String.valueOf(System.currentTimeMillis());

                        userMap.put("email", email);
                        userMap.put("ten_hien_thi", username);
                        userMap.put("vai_tro", "user");
                        userMap.put("tao_luc", currentTime);

                        databaseReference.child(userId).setValue(userMap)
                                .addOnCompleteListener(task2 -> {

                                    if (task2.isSuccessful()) {
                                        Toast.makeText(this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();

                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                        finish();

                                    } else {
                                        Toast.makeText(this, "Lỗi DB: " + task2.getException(), Toast.LENGTH_LONG).show();
                                    }
                                });

                    } else {
                        Toast.makeText(this, "Lỗi: " + task.getException(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showError(String message) {
        Toast.makeText(this, "Lỗi: " + message, Toast.LENGTH_LONG).show();
        btnRegister.setEnabled(true);
        btnRegister.setText("ĐĂNG KÝ NGAY");
    }
}