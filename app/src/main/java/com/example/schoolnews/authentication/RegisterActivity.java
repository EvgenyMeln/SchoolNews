package com.example.schoolnews.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
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

        binding.loginReg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.loginReg.getText().toString().trim().isEmpty()) {
                    binding.textField3.setError("Поле не может быть пустым");
                } else if (!binding.loginReg.getText().toString().trim().isEmpty()) {
                    binding.textField3.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (binding.loginReg.getText().toString().trim().isEmpty()) {
                    binding.textField3.setError("Поле не может быть пустым");
                } else if (!binding.loginReg.getText().toString().trim().isEmpty()) {
                    binding.textField3.setError(null);
                }
            }
        });
        binding.passwordReg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.passwordReg.getText().toString().trim().isEmpty()) {
                    binding.textField4.setError("Поле не может быть пустым");
                } else if (!binding.passwordReg.getText().toString().trim().isEmpty()) {
                    binding.textField4.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (binding.passwordReg.getText().toString().trim().isEmpty()) {
                    binding.textField4.setError("Поле не может быть пустым");
                } else if (!binding.passwordReg.getText().toString().trim().isEmpty()) {
                    binding.textField4.setError(null);
                }
            }
        });
        binding.confirmPasswordReg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (binding.confirmPasswordReg.getText().toString().trim().isEmpty()) {
                    binding.textField5.setError("Поле не может быть пустым");
                } else if (!binding.confirmPasswordReg.getText().toString().trim().isEmpty()) {
                    binding.textField5.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (binding.confirmPasswordReg.getText().toString().trim().isEmpty()) {
                    binding.textField5.setError("Поле не может быть пустым");
                } else if (!binding.confirmPasswordReg.getText().toString().trim().isEmpty()) {
                    binding.textField5.setError(null);
                }
            }
        });

        binding.tvReturn.setPaintFlags(binding.tvReturn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        binding.tvReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                RegisterActivity.this.finish();
            }
        });

        setContentView(view);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.btnReg) {
            if (binding.loginReg.getText().toString().trim().isEmpty() && binding.passwordReg.getText().toString().trim().isEmpty() && binding.confirmPasswordReg.getText().toString().trim().isEmpty()) {
                binding.textField3.setError("Поле не может быть пустым");
                binding.textField4.setError("Поле не может быть пустым");
                binding.textField5.setError("Поле не может быть пустым");
            } else if (binding.loginReg.getText().toString().trim().isEmpty() && binding.passwordReg.getText().toString().trim().isEmpty()) {
                binding.textField3.setError("Поле не может быть пустым");
                binding.textField4.setError("Поле не может быть пустым");
                binding.textField5.setError(null);
            } else if (binding.passwordReg.getText().toString().trim().isEmpty() && binding.confirmPasswordReg.getText().toString().trim().isEmpty()) {
                binding.textField3.setError(null);
                binding.textField4.setError("Поле не может быть пустым");
                binding.textField5.setError("Поле не может быть пустым");
            } else if (binding.loginReg.getText().toString().trim().isEmpty() && binding.confirmPasswordReg.getText().toString().trim().isEmpty()) {
                binding.textField3.setError("Поле не может быть пустым");
                binding.textField4.setError(null);
                binding.textField5.setError("Поле не может быть пустым");
            } else if (binding.loginReg.getText().toString().trim().isEmpty()) {
                binding.textField3.setError("Поле не может быть пустым");
                binding.textField4.setError(null);
                binding.textField5.setError(null);
            } else if (binding.passwordReg.getText().toString().trim().isEmpty()) {
                binding.textField3.setError(null);
                binding.textField4.setError("Поле не может быть пустым");
                binding.textField5.setError(null);
            } else if (binding.confirmPasswordReg.getText().toString().trim().isEmpty()) {
                binding.textField3.setError(null);
                binding.textField4.setError(null);
                binding.textField5.setError("Поле не может быть пустым");
            } else if (!binding.passwordReg.getText().toString().equals(binding.confirmPasswordReg.getText().toString())) {
                binding.textField3.setError(null);
                binding.textField4.setError(null);
                binding.textField5.setError(null);
                Toast.makeText(RegisterActivity.this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            } else {
                binding.btnReg.setEnabled(false);
                binding.progressBarRegisterActivity.setVisibility(View.VISIBLE);
                registration(binding.loginReg.getText().toString(), binding.passwordReg.getText().toString());
            }
        }
    }

    public void registration(String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    binding.btnReg.setEnabled(true);
                    binding.progressBarRegisterActivity.setVisibility(View.INVISIBLE);
                    Toast.makeText(RegisterActivity.this, "Зарегистрирован", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, UpgradeActivity.class);
                    startActivity(intent);
                    RegisterActivity.this.finish();
                } else {
                    binding.btnReg.setEnabled(true);
                    binding.progressBarRegisterActivity.setVisibility(View.INVISIBLE);
                    String error = task.getException().getMessage();
                    Toast.makeText(RegisterActivity.this, "Ошибка: " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}