package com.example.schoolnews.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.schoolnews.databinding.FragmentAddBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class AddFragment extends Fragment {

    private static final int MAX_LENGTH = 100;

    public AddFragment() {
        // Required empty public constructor
    }

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;

    private FragmentAddBinding binding;

    private String TAG = "MyLog";
    private String User_id;

    private Uri ImageUri = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        User_id = mAuth.getCurrentUser().getUid();

        binding.newsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(1024, 512)
                        .setAspectRatio(2, 1)
                        .start(getContext(), AddFragment.this);
            }
        });

        binding.btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(binding.newsName.toString()) && !TextUtils.isEmpty(binding.newsText.toString()) && ImageUri != null) {

                    final StorageReference filepath = storageReference.child("news_images").child(RandomString.getAlphaNumericString(28) + ".jpg");
                    filepath.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String url = uri.toString();

                                    String User_id = mAuth.getCurrentUser().getUid();

                                    Map<String, Object> newsMap = new HashMap<>();
                                    newsMap.put("news_image", url);
                                    newsMap.put("user_id", User_id);
                                    newsMap.put("timestamp", FieldValue.serverTimestamp());
                                    newsMap.put("news_name", binding.newsName.getText().toString());
                                    newsMap.put("news_text", binding.newsText.getText().toString());

                                    firebaseFirestore.collection("News").document(RandomString.getAlphaNumericString(28)).set(newsMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot successfully written!");

                                            Toast.makeText(AddFragment.this.getActivity(), "Опубликовано", Toast.LENGTH_SHORT).show();

                                            binding.newsName.setText("");
                                            binding.newsText.setText("");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error writing document", e);
                                        }
                                    });
                                }
                            });
                        }
                    });
                }

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                ImageUri = result.getUri();
                binding.newsImage.setImageURI(ImageUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
