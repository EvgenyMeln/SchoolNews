package com.example.schoolnews.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.schoolnews.MainActivity;
import com.example.schoolnews.databinding.UpgradeActivityBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UpgradeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private UpgradeActivityBinding binding;

    private String TAG = "myLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = UpgradeActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        setContentView(view);
    }

    public void onClickReg(View view) {
        String User_id = mAuth.getCurrentUser().getUid();

        Map<String, String> userMap = new HashMap<>();
        userMap.put("name", binding.userName.getText().toString());
        userMap.put("date", binding.userDate.getText().toString());
        userMap.put("school", binding.userSchool.getText().toString());
        userMap.put("class_number", binding.userClassNumber.getText().toString());
        userMap.put("class_letter", binding.userClassLetter.getText().toString());

        firebaseFirestore.collection("Users").document(User_id).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "DocumentSnapshot successfully written!");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}