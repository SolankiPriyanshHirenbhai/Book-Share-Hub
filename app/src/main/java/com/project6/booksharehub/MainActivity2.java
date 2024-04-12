package com.project6.booksharehub;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.jetbrains.annotations.NotNull;

public class MainActivity2 extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    HomeFragment homeFragment = new HomeFragment();
    ProfileFragment profileFragment = new ProfileFragment();
    ChatFragment chatFragment = new ChatFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        bottomNavigationView = findViewById(R.id.navigation_view);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NotNull MenuItem item) {
                int itemId = item.getItemId();
                if(itemId == R.id.navigation_profile){
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, profileFragment).commit();
                }else if(itemId == R.id.navigation_home){
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, homeFragment).commit();
                }else if(itemId == R.id.navigation_chat){
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, chatFragment).commit();
                }else{
                    Toast.makeText(MainActivity2.this, "Some Error Occured", Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });
    }
}