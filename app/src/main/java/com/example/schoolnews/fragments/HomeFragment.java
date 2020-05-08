package com.example.schoolnews.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.schoolnews.R;
import com.example.schoolnews.news.News;
import com.example.schoolnews.news.NewsAdapter;
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
    private RecyclerView mRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mRecyclerView = view.findViewById(R.id.news_list_view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onViewCreated(view, savedInstanceState);

        firebaseFirestore = FirebaseFirestore.getInstance();
        newsRef = firebaseFirestore.collection("News");

        setUpRecyclerViewDate();
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_sort, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.date_sort:
                newsAdapter.stopListening();
                setUpRecyclerViewDate();
                Toast.makeText(HomeFragment.this.getActivity(), "Отсортировано по дате", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.name_sort:
                newsAdapter.stopListening();
                setUpRecyclerViewName();
                Toast.makeText(HomeFragment.this.getActivity(), "Отсортировано по названию", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.user_sort:
                newsAdapter.stopListening();
                setUpRecyclerViewUser();
                Toast.makeText(HomeFragment.this.getActivity(), "Отсортировано по пользователю", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setUpRecyclerViewDate() {
        Query query = newsRef.orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<News> options = new FirestoreRecyclerOptions.Builder<News>().setQuery(query,News.class).build();
        newsAdapter = new NewsAdapter(options);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(HomeFragment.this.getActivity()));
        mRecyclerView.setAdapter(newsAdapter);
        newsAdapter.startListening();
    }

    private void setUpRecyclerViewName() {
        Query query = newsRef.orderBy("news_name");
        FirestoreRecyclerOptions<News> options = new FirestoreRecyclerOptions.Builder<News>().setQuery(query,News.class).build();
        newsAdapter = new NewsAdapter(options);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(HomeFragment.this.getActivity()));
        mRecyclerView.setAdapter(newsAdapter);
        newsAdapter.startListening();
    }

    private void setUpRecyclerViewUser() {
        Query query = newsRef.orderBy("user_id", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<News> options = new FirestoreRecyclerOptions.Builder<News>().setQuery(query,News.class).build();
        newsAdapter = new NewsAdapter(options);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(HomeFragment.this.getActivity()));
        mRecyclerView.setAdapter(newsAdapter);
        newsAdapter.startListening();
    }
}