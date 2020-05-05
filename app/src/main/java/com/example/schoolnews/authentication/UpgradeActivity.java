package com.example.schoolnews.authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import com.example.schoolnews.MainActivity;
import com.example.schoolnews.databinding.UpgradeActivityBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
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

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        binding.userDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(UpgradeActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month += 1;
                        String date = day + "/" + month + "/" + year;
                        binding.userDate.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });
        setContentView(view);
    }

    public void onClickReg(View view) {

        if (binding.userName.getText().toString().trim().isEmpty()) {
            binding.textField6.setError("Поле не может быть пустым");
        } else {
            binding.btnUpgrade.setEnabled(false);
            binding.progressBarUpgradeActivity.setVisibility(View.VISIBLE);
            binding.textField6.setError(null);

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
                    binding.progressBarUpgradeActivity.setVisibility(View.INVISIBLE);
                    binding.btnUpgrade.setEnabled(true);
                    Log.d(TAG, "DocumentSnapshot successfully written!");
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            binding.progressBarUpgradeActivity.setVisibility(View.INVISIBLE);
                            binding.btnUpgrade.setEnabled(true);
                            Log.w(TAG, "Error writing document", e);
                        }
                    });

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }
}