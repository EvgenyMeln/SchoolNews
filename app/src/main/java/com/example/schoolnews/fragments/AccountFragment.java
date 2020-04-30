package com.example.schoolnews.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.schoolnews.authentication.LoginActivity;
import com.example.schoolnews.authentication.UpgradeActivity;
import com.example.schoolnews.databinding.FragmentAccountBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.Calendar;

public class AccountFragment extends Fragment {

    public AccountFragment() {
        // Required empty public constructor
    }

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private FragmentAccountBinding binding;

    private String TAG = "MyLog";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        getInfoFromBase();

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        binding.userDateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AccountFragment.this.getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month += 1;
                        String date = day + "/" + month + "/" + year;
                        binding.userDateEdit.setText(date);
                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String User_id = mAuth.getCurrentUser().getUid();

                final DocumentReference DocRef = firebaseFirestore.collection("Users").document(User_id);

                firebaseFirestore.runTransaction(new Transaction.Function<Void>() {
                    @Override
                    public Void apply(Transaction transaction) throws FirebaseFirestoreException {

                        transaction.update(DocRef, "name", binding.userNameEdit.getText().toString());
                        transaction.update(DocRef, "date", binding.userDateEdit.getText().toString());
                        transaction.update(DocRef, "school", binding.userSchoolEdit.getText().toString());
                        transaction.update(DocRef, "class_number", binding.userClassNumberEdit.getText().toString());
                        transaction.update(DocRef, "class_letter", binding.userClassLetterEdit.getText().toString());

                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(AccountFragment.this.getActivity(), "Изменено", Toast.LENGTH_SHORT).show();
                            }
                        });

                        return null;
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Transaction success!");
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Transaction failure.", e);
                            }
                        });
            }
        });

        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(AccountFragment.this.getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void getInfoFromBase() {
        String User_id = mAuth.getCurrentUser().getUid();

        DocumentReference docRef = firebaseFirestore.collection("Users").document(User_id);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        binding.userNameEdit.setText((String) document.getData().get("name"));
                        binding.userDateEdit.setText((String) document.getData().get("date"));
                        binding.userSchoolEdit.setText((String) document.getData().get("school"));
                        binding.userClassNumberEdit.setText((String) document.getData().get("class_number"));
                        binding.userClassLetterEdit.setText((String) document.getData().get("class_letter"));

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}