package com.graduate.seoil.sg_projdct;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.snapshot.Index;
import com.graduate.seoil.sg_projdct.Fragments.ChatFragment;
import com.graduate.seoil.sg_projdct.Fragments.GroupFragment;
import com.graduate.seoil.sg_projdct.Fragments.GroupListFragment;
import com.graduate.seoil.sg_projdct.Fragments.HomeFragment;
import com.graduate.seoil.sg_projdct.Fragments.ProfileFragment;
import com.graduate.seoil.sg_projdct.Fragments.StatisticsFragment;
import com.graduate.seoil.sg_projdct.Fragments.UsersFragment;
import com.graduate.seoil.sg_projdct.Model.User;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class IndexActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username, toolbar_title;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        toolbar_title = findViewById(R.id.toolbar_title);


        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_home);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit(); // 이니시 프래그먼트 설정
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_group:
                    selectedFragment = new GroupListFragment();
                    break;
                case R.id.navigation_chart:
                    selectedFragment = new StatisticsFragment();
                    break;
                case R.id.navigation_home:
                    selectedFragment = new HomeFragment();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        }
    };
}
