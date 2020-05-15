package com.example.schoolnews;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.schoolnews.databinding.MainActivityBinding;
import com.example.schoolnews.fragments.AccountForGuestFragment;
import com.example.schoolnews.fragments.AccountFragment;
import com.example.schoolnews.fragments.AddForGuestFragment;
import com.example.schoolnews.fragments.AddFragment;
import com.example.schoolnews.fragments.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private MainActivityBinding binding;

    private HomeFragment homeFragment;
    private AddFragment addFragment;
    private AccountFragment accountFragment;
    private AccountForGuestFragment accountForGuestFragment;
    private AddForGuestFragment addForGuestFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = MainActivityBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        mAuth = FirebaseAuth.getInstance();

        homeFragment = new HomeFragment();
        addFragment = new AddFragment();
        accountFragment = new AccountFragment();
        accountForGuestFragment = new AccountForGuestFragment();
        addForGuestFragment = new AddForGuestFragment();

        setFragment(homeFragment);

        binding.nav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        setFragment(homeFragment);
                        return true;
                    case R.id.nav_add:
                        if (mAuth.getCurrentUser() == null) {
                            setFragment(addForGuestFragment);
                        } else setFragment(addFragment);
                        return true;
                    case R.id.nav_account:
                        if (mAuth.getCurrentUser() == null) {
                            setFragment(accountForGuestFragment);
                        } else setFragment(accountFragment);
                        return true;
                    default:
                        return false;
                }
            }
        });

        setContentView(view);
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(binding.frame.getId(), fragment);
        fragmentTransaction.commit();
    }
}