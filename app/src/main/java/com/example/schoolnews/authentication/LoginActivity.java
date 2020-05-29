package com.example.schoolnews.authentication;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

    public static String guest_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LoginActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();

        binding.btnSignIn.setOnClickListener(this);
        binding.btnGuest.setOnClickListener(this);

        binding.login.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.login.getText().toString().trim().isEmpty()) {
                    binding.textField.setError("Поле не может быть пустым");
                } else if (!binding.login.getText().toString().trim().isEmpty()) {
                    binding.textField.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (binding.login.getText().toString().trim().isEmpty()) {
                    binding.textField.setError("Поле не может быть пустым");
                } else if (!binding.login.getText().toString().trim().isEmpty()) {
                    binding.textField.setError(null);
                }
            }
        });
        binding.password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.password.getText().toString().trim().isEmpty()) {
                    binding.textField2.setError("Поле не может быть пустым");
                } else if (!binding.password.getText().toString().trim().isEmpty()) {
                    binding.textField2.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (binding.password.getText().toString().trim().isEmpty()) {
                    binding.textField2.setError("Поле не может быть пустым");
                } else if (!binding.password.getText().toString().trim().isEmpty()) {
                    binding.textField2.setError(null);
                }
            }
        });

        binding.tvReg.setPaintFlags(binding.tvReg.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        binding.tvReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnSignIn.setEnabled(false);
                binding.btnGuest.setEnabled(false);
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
        });

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
                binding.progressBarLoginActivity.setVisibility(View.VISIBLE);
                binding.textField.setError(null);
                binding.textField2.setError(null);
                sign_in(binding.login.getText().toString(), binding.password.getText().toString());
            }
        } else if (v == binding.btnGuest) {
            if (mAuth.getCurrentUser() != null) {
                guest_id = RandomString.getAlphaNumericString(28);
                binding.btnSignIn.setEnabled(false);
                binding.btnGuest.setEnabled(false);
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            } else {
                guest_id = RandomString.getAlphaNumericString(28);
                binding.btnSignIn.setEnabled(false);
                binding.btnGuest.setEnabled(false);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
        }
    }

    public void sign_in(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    binding.progressBarLoginActivity.setVisibility(View.INVISIBLE);
                    Toast.makeText(LoginActivity.this, "Вход выполнен", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                } else {
                    binding.btnSignIn.setEnabled(true);
                    binding.btnGuest.setEnabled(true);
                    String error = task.getException().getMessage();
                    binding.progressBarLoginActivity.setVisibility(View.INVISIBLE);
                    Toast.makeText(LoginActivity.this, "Ошибка: " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static class RandomString {
        static String getAlphaNumericString(int n) {
            String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
            StringBuilder sb = new StringBuilder(n);
            for (int i = 0; i < n; i++) {
                int index = (int) (AlphaNumericString.length() * Math.random());
                sb.append(AlphaNumericString.charAt(index));
            }
            return sb.toString();
        }
    }

    public static String getGuest_id() {
        return guest_id;
    }
}