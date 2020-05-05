package com.example.schoolnews.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.schoolnews.R;
import com.example.schoolnews.databinding.FragmentAddBinding;
import com.example.schoolnews.gallery.GalleryAdapter;
import com.example.schoolnews.news.News;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class AddFragment extends Fragment {

    public AddFragment() {
        // Required empty public constructor
    }

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;

    private FragmentAddBinding binding;

    private GalleryAdapter galleryAdapter;
    private RecyclerView mRecyclerView;

    private List<String> urls;
    private List<String> urls_storage;
    private List<Uri> uris = new ArrayList<>();

    private final int Pick_image = 1;
    private Uri PreviewUri = null;

    private String TAG = "MyLog";
    private String url_preview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        mRecyclerView = view.findViewById(R.id.gallery_recycler);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        setUpGalleryRecyclerView();

        binding.addAPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(1024, 512)
                        .setAspectRatio(2, 1)
                        .start(getContext(), AddFragment.this);
            }
        });

        binding.addAPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, Pick_image);
            }
        });

        binding.btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.btnPublish.setEnabled(false);
                binding.progressBarAddFragment.setVisibility(View.VISIBLE);
                urls_storage = new ArrayList<>();
                final Date date = new Date();

                if (PreviewUri != null) {
                    final StorageReference filepath = storageReference.child("news_preview_images").child(RandomString.getAlphaNumericString(28) + ".jpg");
                    filepath.putFile(PreviewUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    url_preview = uri.toString();

                                }
                            });
                        }
                    });
                }

                for (final Uri uri : uris) {
                    final StorageReference filepath = storageReference.child("news_images").child(RandomString.getAlphaNumericString(28) + ".jpg");
                    filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    urls_storage.add(uri.toString());

                                    if (urls_storage.size() == uris.size()) {
                                        News news = new News(
                                                mAuth.getCurrentUser().getUid()
                                                , binding.newsName.getText().toString()
                                                , binding.newsText.getText().toString()
                                                , urls_storage
                                                , date
                                                , url_preview);

                                        firebaseFirestore.collection("News").document(RandomString.getAlphaNumericString(28)).set(news).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "DocumentSnapshot successfully written!");

                                                binding.progressBarAddFragment.setVisibility(View.INVISIBLE);
                                                Toast.makeText(AddFragment.this.getActivity(), "Опубликовано", Toast.LENGTH_SHORT).show();
                                                binding.btnPublish.setEnabled(true);

                                                binding.newsName.setText("");
                                                binding.newsText.setText("");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error writing document", e);
                                                binding.progressBarAddFragment.setVisibility(View.INVISIBLE);
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (requestCode == Pick_image) {
            if (resultCode == RESULT_OK) {
                Uri imageUri = imageReturnedIntent.getData();
                uris.add(imageUri);
                urls.add(imageUri.toString());
                galleryAdapter.notifyDataSetChanged();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                // Exception error = result.getError();
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(imageReturnedIntent);
            if (resultCode == RESULT_OK) {
                PreviewUri = result.getUri();
                binding.newsImage.setImageURI(PreviewUri);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void setUpGalleryRecyclerView() {
        galleryAdapter = new GalleryAdapter();
        urls = new ArrayList<>();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(AddFragment.this.getActivity(), LinearLayoutManager.HORIZONTAL, false));
        galleryAdapter.setUrls(urls);
        mRecyclerView.setAdapter(galleryAdapter);

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