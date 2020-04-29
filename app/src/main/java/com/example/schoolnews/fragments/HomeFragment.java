package com.example.schoolnews.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.schoolnews.R;
import com.example.schoolnews.news.News;
import com.example.schoolnews.news.NewsAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference newsRef;

    private NewsAdapter newsAdapter;

    RecyclerView mRecyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mRecyclerView = view.findViewById(R.id.news_list_view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseFirestore = FirebaseFirestore.getInstance();
        newsRef = firebaseFirestore.collection("News");

        setUpRecyclerView();
    }

    @Override
    public void onStart() {
        super.onStart();
        newsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        newsAdapter.stopListening();
    }

    private void setUpRecyclerView() {
        Query query = newsRef.orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<News> options = new FirestoreRecyclerOptions.Builder<News>().setQuery(query,News.class).build();

        newsAdapter = new NewsAdapter(options);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(HomeFragment.this.getActivity()));
        mRecyclerView.setAdapter(newsAdapter);
    }
}