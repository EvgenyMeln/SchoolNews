package com.example.schoolnews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText email_reg;
    private EditText password_reg;
    private EditText confirm_password_reg;

    private String pass1;
    private String pass2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                } else {
                    // User is signed out
                }
            }
        };

        email_reg = findViewById(R.id.login_reg);
        password_reg = findViewById(R.id.password_reg);
        confirm_password_reg = findViewById(R.id.confirm_password_reg);

        findViewById(R.id.btn_reg).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        pass1 = password_reg.getText().toString();
        pass2 = confirm_password_reg.getText().toString();

        if(view.getId() == R.id.btn_reg)
        {
            if (pass1.equals(pass2)){
                registration(email_reg.getText().toString(),password_reg.getText().toString());
            }
            else {
                Toast.makeText(RegisterActivity.this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void registration (String email , String password){

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(RegisterActivity.this, "Зарегистрирован", Toast.LENGTH_SHORT).show();
                    check(true);
                }
                else{
                    String error = task.getException().getMessage();
                    Toast.makeText(RegisterActivity.this, "Ошибка: " + error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void check(boolean t){
        if (t = true){
            Intent intent = new Intent(this, UpgradeActivity.class);
            startActivity(intent);
        }
    }
}