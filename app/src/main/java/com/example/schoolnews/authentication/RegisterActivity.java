package com.example.schoolnews.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.schoolnews.databinding.RegisterActivityBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private RegisterActivityBinding binding;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = RegisterActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();

        binding.btnReg.setOnClickListener(this);

        setContentView(view);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnReg) {
            if (binding.passwordReg.getText().toString().equals(binding.confirmPasswordReg.getText().toString())) {
                registration(binding.loginReg.getText().toString(), binding.passwordReg.getText().toString());
            } else {
                Toast.makeText(RegisterActivity.this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void registration(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(RegisterActivity.this, "Зарегистрирован", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, UpgradeActivity.class);
                    startActivity(intent);
                } else {
                    String error = task.getException().getMessage();
                    Toast.makeText(RegisterActivity.this, "Ошибка: " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}