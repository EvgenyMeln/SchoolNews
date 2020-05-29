package com.example.schoolnews.news;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.schoolnews.R;
import com.example.schoolnews.authentication.LoginActivity;
import com.example.schoolnews.comments.CommentActivity;
import com.example.schoolnews.databinding.NewsActivityBinding;
import com.example.schoolnews.databinding.UserLayoutBinding;
import com.example.schoolnews.gallery.GalleryAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.relex.circleindicator.CircleIndicator2;

public class NewsActivity extends AppCompatActivity {

    private NewsActivityBinding binding;
    private UserLayoutBinding binding_user;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    private GalleryAdapter galleryAdapter;
    private RecyclerView mRecyclerView;

    private String user_id;
    private String news_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = NewsActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        binding_user = binding.userLayout;
        mRecyclerView = view.findViewById(R.id.news_recycler_activity);

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        Bundle arguments = getIntent().getExtras();
        Bundle args = getIntent().getBundleExtra("BUNDLE");
        List<String> newsImages  = (List<String>) args.getSerializable("newsImages");

        if(arguments!=null){
            String news_name = arguments.get("news_name_activity").toString();
            String news_text = arguments.get("news_text_activity").toString();
            String news_time = arguments.get("news_time_activity").toString();
            user_id = arguments.get("user_id").toString();
            news_id = arguments.get("news_id").toString();

            firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){

                        String userName = task.getResult().getString("name");
                        String userSchool = task.getResult().getString("school");
                        String userClassNumber = task.getResult().getString("class_number");
                        String userClassLetter = task.getResult().getString("class_letter");
                        String userProfile = task.getResult().getString("profile_image");

                        binding_user.cvName.setText(userName);
                        binding_user.cvSchool.setText("Школа №"+ userSchool);
                        binding_user.cvClassNumber.setText(userClassNumber);
                        binding_user.cvClassNumber.setText("Класс: " + userClassNumber +" " + userClassLetter);


                        Glide.with(NewsActivity.this)
                                .load(userProfile)
                                .error(R.drawable.default_profile_image)
                                .into(binding_user.cvProfileImage);

                    } else{
                        //
                    }
                }
            });

            binding.newsNameActivity.setText(news_name);
            binding.newsTextActivity.setText(news_text);
            binding_user.cvNewsTime.setText(news_time);
        }
        galleryAdapter = new GalleryAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(NewsActivity.this, LinearLayoutManager.HORIZONTAL, false));
        galleryAdapter.setUrls(newsImages);
        mRecyclerView.setAdapter(galleryAdapter);

        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(mRecyclerView);
        CircleIndicator2 indicator = view.findViewById(R.id.indicator);
        indicator.attachToRecyclerView(mRecyclerView, pagerSnapHelper);
        galleryAdapter.registerAdapterDataObserver(indicator.getAdapterDataObserver());

        //Удаление публикации
        if (firebaseAuth.getCurrentUser() != null) {
            String cur_user_id = firebaseAuth.getCurrentUser().getUid();
            if (cur_user_id.equals(user_id)){
                binding.deleteNews.setVisibility(View.VISIBLE);
            }
        }
        binding.deleteNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(NewsActivity.this);
                builder.setTitle("Внимание!")
                        .setMessage("Вы уверены, что хотите удалить публикацию?")
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                firebaseFirestore.collection("News").document(news_id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(NewsActivity.this, "Ваша публикация удалена", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        })
                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        //Подсчет комментов
        firebaseFirestore.collection("News/" + news_id + "/Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {

                    int comments = documentSnapshots.size();
                    updateCommentCount(comments);

                } else {
                    updateCommentCount(0);
                }

            }
        });

        //Коммент
        binding.commentNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth.getCurrentUser() != null) {
                    Intent commentIntent = new Intent(NewsActivity.this, CommentActivity.class);
                    commentIntent.putExtra("news_id", news_id);
                    NewsActivity.this.startActivity(commentIntent);
                }else {
                    Toast.makeText(NewsActivity.this, "Гостям нельзя смотреть комментарии", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Подсчет лайков
        firebaseFirestore.collection("News/" + news_id + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {

                    int likes = documentSnapshots.size();
                    updateLikeCount(likes);

                } else {
                    updateLikeCount(0);
                }

            }
        });

        //Получение лайков
        if (firebaseAuth.getCurrentUser() != null) {
            String current_user_id = firebaseAuth.getCurrentUser().getUid();
            firebaseFirestore.collection("News/" + news_id + "/Likes").document(current_user_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                    if (documentSnapshot.exists()) {
                        binding.likeNews.setImageDrawable(NewsActivity.this.getDrawable(R.drawable.like_active));
                    } else {
                        binding.likeNews.setImageDrawable(NewsActivity.this.getDrawable(R.drawable.outline_favorite_border_24));
                    }

                }
            });
        } else {
            binding.likeNews.setImageDrawable(NewsActivity.this.getDrawable(R.drawable.outline_favorite_border_24));
        }

        //Лайк фича
        binding.likeNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (firebaseAuth.getCurrentUser() != null) {

                    final String current_user_id = firebaseAuth.getCurrentUser().getUid();
                    firebaseFirestore.collection("News/" + news_id + "/Likes").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (!task.getResult().exists()) {
                                Map<String, Object> likesMap = new HashMap<>();
                                likesMap.put("timestamp", FieldValue.serverTimestamp());

                                firebaseFirestore.collection("News/" + news_id + "/Likes").document(current_user_id).set(likesMap);
                            } else {
                                firebaseFirestore.collection("News/" + news_id + "/Likes").document(current_user_id).delete();
                            }

                        }
                    });

                } else {
                    Toast.makeText(NewsActivity.this, "Гостям нельзя ставить лайки", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Просмотрено
        if (firebaseAuth.getCurrentUser() != null) {
            final String current_user_id = firebaseAuth.getCurrentUser().getUid();
            Map<String, Object> viewsMap = new HashMap<>();
            viewsMap.put("timestamp", FieldValue.serverTimestamp());
            firebaseFirestore.collection("News/" + news_id + "/Views").document(current_user_id).set(viewsMap);
        } else{
            Map<String, Object> viewsGuestMap = new HashMap<>();
            viewsGuestMap.put("timestamp", FieldValue.serverTimestamp());
            firebaseFirestore.collection("News/" + news_id + "/Views").document(LoginActivity.getGuest_id()).set(viewsGuestMap);
        }

        //Подсчет просмотрено
        firebaseFirestore.collection("News/" + news_id + "/Views").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {

                    int views = documentSnapshots.size();
                    updateViewCount(views);

                } else {
                    updateViewCount(0);
                }

            }
        });

        setContentView(view);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateCommentCount(int comment) {
        binding.commentCountNews.setText("" + comment + "");
    }

    public void updateLikeCount(int like) {
        binding.likeCountNews.setText("" + like + "");
    }

    public void updateViewCount(int view) {
        binding.viewCountNews.setText("" + view + "");
    }
}