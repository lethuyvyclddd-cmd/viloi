package com.example.viloi;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.viloi.MainActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private MaterialButton btnLogin;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private static final String FIREBASE_URL =
            "https://viloi-7c05e-default-rtdb.firebaseio.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);

        btnLogin = findViewById(R.id.btnLogin);
        mAuth = FirebaseAuth.getInstance();

        databaseReference = FirebaseDatabase.getInstance(FIREBASE_URL).getReference("Users");

        btnLogin.setOnClickListener(v -> loginUser());

        findViewById(R.id.txtRegister).setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });

        findViewById(R.id.txtForgotPassword).setOnClickListener(v -> {
            startActivity(new Intent(this, ForgotPasswordActivity.class));
        });
    }

    private void loginUser() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        // Validate
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Vui lòng nhập email");
            edtEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Vui lòng nhập mật khẩu");
            edtPassword.requestFocus();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Đang đăng nhập...");

        // 🔥 Firebase Auth login
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                    // Chuyển sang MainActivity
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Sai email hoặc mật khẩu!", Toast.LENGTH_SHORT).show();

                    btnLogin.setEnabled(true);
                    btnLogin.setText("ĐĂNG NHẬP");
                });
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        btnLogin.setEnabled(true);
        btnLogin.setText("ĐĂNG NHẬP");
    }
}