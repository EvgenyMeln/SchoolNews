package com.example.schoolnews.comments;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentRecyclerAdapter.ViewHolder> {

    private FirebaseFirestore firebaseFirestore;

    public List<Comment> commentList;
    public Context context;

    public CommentRecyclerAdapter(List<Comment> commentList){
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comments_layout, parent, false);
        context = parent.getContext();
        firebaseFirestore = firebaseFirestore.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final  CommentRecyclerAdapter.ViewHolder holder, int position) {
        final String user_id = commentList.get(position).getUser_id();

        String commentMessage = commentList.get(position).getComment();
        holder.setComment_message(commentMessage);

        final Date news_time = commentList.get(position).getTimestamp();
        if (news_time != null){
            final String stringDate = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(news_time);
            holder.setNews_date(stringDate);
        }

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    String userName = task.getResult().getString("name");
                    String userSchool = task.getResult().getString("school");
                    String userClassNumber = task.getResult().getString("class_number");
                    String userClassLetter = task.getResult().getString("class_letter");
                    String userProfile = task.getResult().getString("profile_image");

                    holder.setUserData(userName, userSchool, userClassNumber, userClassLetter, userProfile);

                } else {
                    //
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if (commentList != null){
            return commentList.size();
        }else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private View mView;
        private TextView comment;

        TextView comment_user_name;
        TextView comment_user_school;
        TextView comment_user_class_number;
        TextView news_date;
        ImageView user_profile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setComment_message(String message){
            comment = mView.findViewById(R.id.comment);
            comment.setText(message);
        }

        public void setNews_date(String date){
            news_date = itemView.findViewById(R.id.cv_news_time);
            news_date.setText(date);
        }

        public void setUserData(String userName, String userSchool, String userClassNumber, String userClassLetter, String userProfile) {

            comment_user_name = itemView.findViewById(R.id.comment_name);
            comment_user_school = itemView.findViewById(R.id.cv_school);
            comment_user_class_number = itemView.findViewById(R.id.cv_class_number);
            user_profile = itemView.findViewById(R.id.cv_profile_image);

            comment_user_name.setText(userName);
            comment_user_school.setText("Школа №"+ userSchool);
            comment_user_class_number.setText("Класс: " + userClassNumber +" " + userClassLetter);

            Glide.with(context)
                    .load(userProfile)
                    .error(R.drawable.default_profile_image)
                    .into(user_profile);
        }
    }

    public static class CommentDateComparator implements Comparator<Comment>
    {
        public int compare(Comment left, Comment right) {
            return left.getTimestamp().compareTo(right.getTimestamp());
        }
    }
}