package com.example.schoolnews.comments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.schoolnews.databinding.ActivityCommentBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {

    List<Comment> commentList;
    CommentRecyclerAdapter commentRecyclerAdapter;

    private ActivityCommentBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private String news_id;
    private String current_user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommentBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        news_id = getIntent().getStringExtra("news_id");

        //Отображение комментов в ресайкл
        commentList = new ArrayList<>();
        commentRecyclerAdapter = new CommentRecyclerAdapter(commentList);
        binding.commentsList.setHasFixedSize(true);
        binding.commentsList.setLayoutManager(new LinearLayoutManager(this));
        binding.commentsList.setAdapter(commentRecyclerAdapter);

        firebaseFirestore.collection("News/" + news_id + "/Comments").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (!documentSnapshots.isEmpty()){
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()){
                        if (doc.getType() == DocumentChange.Type.ADDED){

                            String commentId = doc.getDocument().getId();
                            Comment comment = doc.getDocument().toObject(Comment.class);
                            commentList.add(comment);
                        }
                    }

                    Collections.sort(commentList, new CommentRecyclerAdapter.CommentDateComparator());
                    commentRecyclerAdapter.notifyDataSetChanged();
                }
            }
        });

        binding.pushComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.commentEdit.getText().toString().trim().isEmpty()) {
                    Toast.makeText(CommentActivity.this, "Комментарий не может быть пустым", Toast.LENGTH_SHORT).show();
                } else {
                    binding.progressBarCommentActivity.setVisibility(View.VISIBLE);
                    current_user_id = firebaseAuth.getCurrentUser().getUid();
                    final Date date = new Date();
                    Map<String, Object> commentsMap = new HashMap<>();
                    commentsMap.put("user_id", current_user_id);
                    commentsMap.put("comment", binding.commentEdit.getText().toString());
                    commentsMap.put("timestamp", date);
                    firebaseFirestore.collection("News/" + news_id + "/Comments").add(commentsMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (!task.isSuccessful()) {
                                binding.progressBarCommentActivity.setVisibility(View.INVISIBLE);
                                Toast.makeText(CommentActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                binding.progressBarCommentActivity.setVisibility(View.INVISIBLE);
                                Toast.makeText(CommentActivity.this, "Комментарий опубликован", Toast.LENGTH_SHORT).show();
                                binding.commentEdit.setText("");
                            }
                        }
                    });
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
}