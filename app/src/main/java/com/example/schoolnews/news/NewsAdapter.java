package com.example.schoolnews.news;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolnews.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class NewsAdapter extends FirestoreRecyclerAdapter<News, NewsAdapter.NewsHolder> {

    public NewsAdapter(@NonNull FirestoreRecyclerOptions<News> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NewsHolder newsHolder, int i, @NonNull News news) {
        newsHolder.tv_user_name.setText(news.getAuthor_name());
        newsHolder.tv_user_school.setText(news.getAuthor_school());
        newsHolder.tv_user_class_number.setText(news.getAuthor_class_number());
        newsHolder.tv_user_class_letter.setText(news.getAuthor_class_letter());
        newsHolder.tv_news_name.setText(news.getNews_name());
        newsHolder.tv_news_text.setText(news.getNews_text());
    }

    @NonNull
    @Override
    public NewsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_news, parent,false);
        return new NewsHolder(v);
    }

    class NewsHolder extends RecyclerView.ViewHolder{
        TextView tv_user_name;
        TextView tv_user_school;
        TextView tv_user_class_number;
        TextView tv_user_class_letter;
        TextView tv_news_name;
        TextView tv_news_text;

        public NewsHolder(@NonNull View itemView) {
            super(itemView);

            tv_user_name = itemView.findViewById(R.id.cv_name);
            tv_user_school = itemView.findViewById(R.id.cv_school);
            tv_user_class_number = itemView.findViewById(R.id.cv_class_number);
            tv_user_class_letter = itemView.findViewById(R.id.cv_class_letter);
            tv_news_name = itemView.findViewById(R.id.cv_news_name);
            tv_news_text =itemView.findViewById(R.id.cv_news_text);
        }
    }
}
