package com.example.schoolnews.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.schoolnews.MainActivity;
import com.example.schoolnews.databinding.LoginActivityBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private LoginActivityBinding binding;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();

        binding.btnSignIn.setOnClickListener(this);
        binding.btnRegister.setOnClickListener(this);
        binding.btnGuest.setOnClickListener(this);

        setContentView(view);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnSignIn) {
            if (binding.login.getText().toString().trim().isEmpty() && binding.password.getText().toString().trim().isEmpty()) {
                binding.textField.setError("Поле не может быть пустым");
                binding.textField2.setError("Поле не может быть пустым");
            } else if (binding.login.getText().toString().trim().isEmpty()) {
                binding.textField.setError("Поле не может быть пустым");
                binding.textField2.setError(null);
            } else if (binding.password.getText().toString().trim().isEmpty()) {
                binding.textField2.setError("Поле не может быть пустым");
                binding.textField.setError(null);
            } else {
                binding.btnSignIn.setEnabled(false);
                binding.btnGuest.setEnabled(false);
                binding.btnRegister.setEnabled(false);
                binding.progressBarLoginActivity.setVisibility(View.VISIBLE);
                binding.textField.setError(null);
                binding.textField2.setError(null);
                sign_in(binding.login.getText().toString(), binding.password.getText().toString());
            }
        } else if (v == binding.btnRegister) {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        } else if (v == binding.btnGuest) {
            if (mAuth.getCurrentUser() != null){
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
    }

    public void sign_in(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    binding.btnSignIn.setEnabled(true);
                    binding.btnGuest.setEnabled(true);
                    binding.btnRegister.setEnabled(true);
                    binding.progressBarLoginActivity.setVisibility(View.INVISIBLE);
                    Toast.makeText(LoginActivity.this, "Вход выполнен", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                } else {
                    binding.btnSignIn.setEnabled(true);
                    binding.btnGuest.setEnabled(true);
                    binding.btnRegister.setEnabled(true);
                    String error = task.getException().getMessage();
                    binding.progressBarLoginActivity.setVisibility(View.INVISIBLE);
                    Toast.makeText(LoginActivity.this, "Ошибка: " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}