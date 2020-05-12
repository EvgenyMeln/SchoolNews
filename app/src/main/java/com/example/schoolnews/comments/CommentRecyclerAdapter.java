package com.example.schoolnews.comments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.schoolnews.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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

        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    String userName = task.getResult().getString("name");
                    String userSchool = task.getResult().getString("school");
                    String userClassNumber = task.getResult().getString("class_number");
                    String userClassLetter = task.getResult().getString("class_letter");

                    holder.setUserData(userName, userSchool, userClassNumber, userClassLetter);

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
        TextView comment_user_class_letter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setComment_message(String message){
            comment = mView.findViewById(R.id.comment);
            comment.setText(message);
        }

        public void setUserData(String userName, String userSchool, String userClassNumber, String userClassLetter) {

            comment_user_name = itemView.findViewById(R.id.comment_name);
            comment_user_school = itemView.findViewById(R.id.comment_school);
            comment_user_class_number = itemView.findViewById(R.id.comment_class_number);
            comment_user_class_letter = itemView.findViewById(R.id.comment_class_letter);

            comment_user_name.setText(userName);
            comment_user_school.setText(userSchool);
            comment_user_class_number.setText(userClassNumber);
            comment_user_class_letter.setText(userClassLetter);
        }
    }
}