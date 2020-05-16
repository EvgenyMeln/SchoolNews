package com.example.schoolnews.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
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

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

public class AccountFragment extends Fragment {

    public AccountFragment() {
        // Required empty public constructor
    }

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;

    private FragmentAccountBinding binding;

    private String TAG = "MyLog";

    private Uri profileUriEdit = null;

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
        storageReference = FirebaseStorage.getInstance().getReference();
        getInfoFromBase();

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        binding.profileImageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(getContext(), AccountFragment.this);
            }
        });

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
                binding.progressBarAccountFragment.setVisibility(View.VISIBLE);
                final String User_id = mAuth.getCurrentUser().getUid();
                if (profileUriEdit != null) {
                    final StorageReference filepath = storageReference.child("profile_images").child(AccountFragment.RandomString.getAlphaNumericString(28) + ".jpg");
                    filepath.putFile(profileUriEdit).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String url_profile_edit = uri.toString();

                                    final DocumentReference DocRef = firebaseFirestore.collection("Users").document(User_id);

                                    firebaseFirestore.runTransaction(new Transaction.Function<Void>() {
                                        @Override
                                        public Void apply(Transaction transaction) throws FirebaseFirestoreException {

                                            transaction.update(DocRef, "name", binding.userNameEdit.getText().toString());
                                            transaction.update(DocRef, "date", binding.userDateEdit.getText().toString());
                                            transaction.update(DocRef, "school", binding.userSchoolEdit.getText().toString());
                                            transaction.update(DocRef, "class_number", binding.userClassNumberEdit.getText().toString());
                                            transaction.update(DocRef, "class_letter", binding.userClassLetterEdit.getText().toString());
                                            transaction.update(DocRef, "profile_image", url_profile_edit);

                                            getActivity().runOnUiThread(new Runnable() {
                                                public void run() {
                                                    binding.progressBarAccountFragment.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(AccountFragment.this.getActivity(), "Изменено", Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                            return null;
                                        }
                                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "Transaction success!");
                                            binding.progressBarAccountFragment.setVisibility(View.INVISIBLE);
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Transaction failure.", e);
                                                    binding.progressBarAccountFragment.setVisibility(View.INVISIBLE);
                                                }
                                            });
                                }
                            });
                        }
                    });
                } else {
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
                                    binding.progressBarAccountFragment.setVisibility(View.INVISIBLE);
                                    Toast.makeText(AccountFragment.this.getActivity(), "Изменено", Toast.LENGTH_SHORT).show();
                                }
                            });

                            return null;
                        }
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Transaction success!");
                            binding.progressBarAccountFragment.setVisibility(View.INVISIBLE);
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Transaction failure.", e);
                                    binding.progressBarAccountFragment.setVisibility(View.INVISIBLE);
                                }
                            });
                }
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

                        Glide.with(AccountFragment.this.getContext())
                                .load((String) document.getData().get("profile_image"))
                                .into(binding.profileImageEdit);

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(imageReturnedIntent);
            if (resultCode == RESULT_OK) {
                profileUriEdit = result.getUri();
                binding.profileImageEdit.setImageURI(profileUriEdit);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private static class RandomString {
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
}