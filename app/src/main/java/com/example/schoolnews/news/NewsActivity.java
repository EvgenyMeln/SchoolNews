package com.example.schoolnews.news;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.example.schoolnews.R;
import com.example.schoolnews.databinding.NewsActivityBinding;
import com.example.schoolnews.gallery.GalleryAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class NewsActivity extends AppCompatActivity {

    private NewsActivityBinding binding;
    private FirebaseFirestore firebaseFirestore;

    private GalleryAdapter galleryAdapter;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = NewsActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        mRecyclerView = view.findViewById(R.id.news_recycler_activity);

        firebaseFirestore = FirebaseFirestore.getInstance();

        Bundle arguments = getIntent().getExtras();
        Bundle args = getIntent().getBundleExtra("BUNDLE");
        List<String> newsImages  = (List<String>) args.getSerializable("newsImages");

        if(arguments!=null){
            String news_name = arguments.get("news_name_activity").toString();
            String news_text = arguments.get("news_text_activity").toString();
            String news_time = arguments.get("news_time_activity").toString();
            String user_id = arguments.get("user_id").toString();

            firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){

                        String userName = task.getResult().getString("name");
                        String userSchool = task.getResult().getString("school");
                        String userClassNumber = task.getResult().getString("class_number");
                        String userClassLetter = task.getResult().getString("class_letter");

                        binding.userNameActivity.setText(userName);
                        binding.userSchoolActivity.setText(userSchool);
                        binding.userClassNumberActivity.setText(userClassNumber);
                        binding.userClassLetterActivity.setText(userClassLetter);

                    } else{
                        //
                    }
                }
            });

            binding.newsNameActivity.setText(news_name);
            binding.newsTextActivity.setText(news_text);
            binding.newsTimeActivity.setText(news_time);
        }
        galleryAdapter = new GalleryAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(NewsActivity.this, LinearLayoutManager.HORIZONTAL, false));
        galleryAdapter.setUrls(newsImages);
        mRecyclerView.setAdapter(galleryAdapter);
        closeContextMenu();
        setContentView(view);
    }
}
