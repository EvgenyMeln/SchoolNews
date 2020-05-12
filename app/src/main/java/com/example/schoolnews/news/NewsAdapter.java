package com.example.schoolnews.news;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.schoolnews.R;
import com.example.schoolnews.authentication.LoginActivity;
import com.example.schoolnews.comments.CommentActivity;
import com.example.schoolnews.fragments.HomeFragment;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsAdapter extends FirestoreRecyclerAdapter<News, NewsAdapter.NewsHolder> {

    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public NewsAdapter(@NonNull FirestoreRecyclerOptions<News> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final NewsHolder newsHolder, int i, @NonNull final News news) {
        final String news_id = news.getNews_id();
        final String news_name = news.getNews_name();
        final String news_text = news.getNews_text();
        final Date news_time = news.getTimestamp();
        final String user_id = news.getUser_id();
        final List<String> newsImages = news.getNewsImages();

        newsHolder.tv_news_name.setText(news_name);
        if (!newsImages.isEmpty())
            newsHolder.setNewsImage(news.getNews_image());
        String stringDate = DateFormat.getDateTimeInstance().format(news_time);
        if (!stringDate.isEmpty()) {
            newsHolder.tv_timestamp.setText(stringDate);
        } else {
            newsHolder.tv_timestamp.setText("");
        }

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    String userName = task.getResult().getString("name");
                    String userSchool = task.getResult().getString("school");
                    String userClassNumber = task.getResult().getString("class_number");
                    String userClassLetter = task.getResult().getString("class_letter");

                    newsHolder.setUserData(userName, userSchool, userClassNumber, userClassLetter);

                } else {
                    //
                }
            }
        });

        newsHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, NewsActivity.class);
                intent.putExtra("news_name_activity", news_name);
                intent.putExtra("user_id", user_id);
                intent.putExtra("news_text_activity", news_text);
                intent.putExtra("user_id", user_id);
                intent.putExtra("news_time_activity", news_time);
                Bundle args = new Bundle();
                args.putSerializable("newsImages", (Serializable) newsImages);
                intent.putExtra("BUNDLE", args);
                context.startActivity(intent);
            }
        });

        //Подсчет лайков
        firebaseFirestore.collection("News/" + news_id + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {

                    int likes = documentSnapshots.size();
                    newsHolder.updateLikeCount(likes);

                } else {
                    newsHolder.updateLikeCount(0);
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
                        newsHolder.like.setImageDrawable(context.getDrawable(R.drawable.like_accent));
                    } else {
                        newsHolder.like.setImageDrawable(context.getDrawable(R.drawable.like_grey));
                    }

                }
            });
        } else {
            newsHolder.like.setImageDrawable(context.getDrawable(R.drawable.like_grey));
        }

        //Лайк фича
        newsHolder.like.setOnClickListener(new View.OnClickListener() {
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
                    Toast.makeText(context, "Гостям нельзя ставить лайки", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Подсчет комментов
        firebaseFirestore.collection("News/" + news_id + "/Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if (!documentSnapshots.isEmpty()) {

                    int comments = documentSnapshots.size();
                    newsHolder.updateCommentCount(comments);

                } else {
                    newsHolder.updateCommentCount(0);
                }

            }
        });

        //Коммент
        newsHolder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth.getCurrentUser() != null) {
                    Intent commentIntent = new Intent(context, CommentActivity.class);
                    commentIntent.putExtra("news_id", news_id);
                    context.startActivity(commentIntent);
                }else {
                    Toast.makeText(context, "Гостям нельзя смотреть комментарии", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @NonNull
    @Override
    public NewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cards_news, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new NewsHolder(v);
    }

    public class NewsHolder extends RecyclerView.ViewHolder {
        TextView tv_user_name;
        TextView tv_user_school;
        TextView tv_user_class_number;
        TextView tv_user_class_letter;
        TextView tv_news_name;
        TextView tv_timestamp;
        ImageView tv_news_image;

        ImageView like;
        TextView like_count;

        ImageView comment;
        TextView comment_count;

        View itemView;

        public NewsHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            tv_news_image = itemView.findViewById(R.id.cv_image_news);
            tv_news_name = itemView.findViewById(R.id.cv_news_name);
            tv_timestamp = itemView.findViewById(R.id.cv_news_time);
            like = itemView.findViewById(R.id.like);
            comment = itemView.findViewById(R.id.comment);
        }

        public void setNewsImage(String downloadUri) {
            Picasso.get()
                    .load(downloadUri)
                    .error(R.drawable.birthday)
                    .into(tv_news_image);
        }

        public void setUserData(String userName, String userSchool, String userClassNumber, String userClassLetter) {

            tv_user_name = itemView.findViewById(R.id.cv_name);
            tv_user_school = itemView.findViewById(R.id.cv_school);
            tv_user_class_number = itemView.findViewById(R.id.cv_class_number);
            tv_user_class_letter = itemView.findViewById(R.id.cv_class_letter);

            tv_user_name.setText(userName);
            tv_user_school.setText(userSchool);
            tv_user_class_number.setText(userClassNumber);
            tv_user_class_letter.setText(userClassLetter);

        }

        public void updateLikeCount(int like) {
            like_count = itemView.findViewById(R.id.like_count);
            like_count.setText("" + like + "");
        }

        public void updateCommentCount(int comment) {
            comment_count = itemView.findViewById(R.id.comment_count);
            comment_count.setText("" + comment + "");
        }
    }
}