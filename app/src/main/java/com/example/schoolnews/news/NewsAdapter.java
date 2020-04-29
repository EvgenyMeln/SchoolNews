package com.example.schoolnews.news;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.schoolnews.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class NewsAdapter extends FirestoreRecyclerAdapter<News, NewsAdapter.NewsHolder> {

    public Context context;

    private FirebaseFirestore firebaseFirestore;

    public NewsAdapter(@NonNull FirestoreRecyclerOptions<News> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final NewsHolder newsHolder, int i, @NonNull News news) {
        newsHolder.tv_news_name.setText(news.getNews_name());
        newsHolder.tv_news_text.setText(news.getNews_text());
        newsHolder.setNewsImage(news.getNews_image());
        newsHolder.tv_timestamp.setText(news.getTimestamp().toString());

        String user_id = news.getUser_id();
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){

                    String userName = task.getResult().getString("name");
                    String userSchool = task.getResult().getString("school");
                    String userClassNumber = task.getResult().getString("class_number");
                    String userClassLetter = task.getResult().getString("class_letter");

                    newsHolder.setUserData(userName, userSchool, userClassNumber, userClassLetter);

                } else{
                    //
                }
            }
        });

    }

    @NonNull
    @Override
    public NewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_news, parent,false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        return new NewsHolder(v);
    }

    public class NewsHolder extends RecyclerView.ViewHolder{
        TextView tv_user_name;
        TextView tv_user_school;
        TextView tv_user_class_number;
        TextView tv_user_class_letter;
        TextView tv_news_name;
        TextView tv_news_text;
        TextView tv_timestamp;
        ImageView tv_news_image;

        public NewsHolder(@NonNull View itemView) {
            super(itemView);

            tv_news_name = itemView.findViewById(R.id.cv_news_name);
            tv_news_text = itemView.findViewById(R.id.cv_news_text);
            tv_timestamp = itemView.findViewById(R.id.cv_news_time);
        }

        public void setNewsImage(String downloadUri){
            tv_news_image= itemView.findViewById(R.id.cv_image_news);
            Glide.with(context).load(downloadUri).into(tv_news_image);
        }

        public void setUserData(String userName, String userSchool, String userClassNumber, String userClassLetter){

            tv_user_name = itemView.findViewById(R.id.cv_name);
            tv_user_school = itemView.findViewById(R.id.cv_school);
            tv_user_class_number = itemView.findViewById(R.id.cv_class_number);
            tv_user_class_letter = itemView.findViewById(R.id.cv_class_letter);

            tv_user_name.setText(userName);
            tv_user_school.setText(userSchool);
            tv_user_class_number.setText(userClassNumber);
            tv_user_class_letter.setText(userClassLetter);

        }

    }
}