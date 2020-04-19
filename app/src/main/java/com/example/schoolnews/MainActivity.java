package com.example.schoolnews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.schoolnews.databinding.MainActivityBinding;
import com.example.schoolnews.fragments.AccountFragment;
import com.example.schoolnews.fragments.AddFragment;
import com.example.schoolnews.fragments.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private MainActivityBinding binding;

    private HomeFragment homeFragment;
    private AddFragment addFragment;
    private AccountFragment accountFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MainActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        homeFragment = new HomeFragment();
        addFragment = new AddFragment();
        accountFragment = new AccountFragment();

        setFragment(homeFragment);

        binding.nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home :
                        setFragment(homeFragment);
                        return true;
                    case R.id.nav_add :
                        setFragment(addFragment);
                        return true ;
                    case R.id.nav_account :
                        setFragment(accountFragment);
                        return true ;
                    default :
                        return false;
                }
            }
        });

        setContentView(view);
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }
}