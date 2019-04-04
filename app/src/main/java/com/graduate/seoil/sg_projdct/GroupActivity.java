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
import com.graduate.seoil.sg_projdct.Fragments.GroupListFragment;
import com.graduate.seoil.sg_projdct.Fragments.HomeFragment;
import com.graduate.seoil.sg_projdct.Fragments.ProfileFragment;
import com.graduate.seoil.sg_projdct.Fragments.SettingFragment;
import com.graduate.seoil.sg_projdct.Fragments.StatisticsFragment;
import com.graduate.seoil.sg_projdct.Fragments.UsersFragment;
import com.graduate.seoil.sg_projdct.GStartActivity;
import com.graduate.seoil.sg_projdct.Model.Chat;
import com.graduate.seoil.sg_projdct.Model.User;
import com.graduate.seoil.sg_projdct.R;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupActivity extends AppCompatActivity {

//    CircleImageView profile_image;
//    TextView username;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    String str_userName, str_userImageURL, str_title;

    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

//        profile_image = findViewById(R.id.profile_image);
//        username = findViewById(R.id.username);
        Intent intent = getIntent();
        str_title = intent.getStringExtra("group_title");
        str_userName = intent.getStringExtra("userName");
        str_userImageURL = intent.getStringExtra("userImageURL");


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
//                username.setText(user.getUsername());
                str_userName = user.getUsername();
                str_userImageURL = user.getImageURL();

//                if (str_userImageURL.equals("default")) {
//                    profile_image.setImageResource(R.mipmap.ic_launcher);
//                } else {
//                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // 이니시 프래그먼트 설정
        Fragment fragment = new ChatFragment();

        Bundle bundle = new Bundle();
        bundle.putString("group_title", str_title);
        bundle.putString("userName", str_userName);
        bundle.putString("userImageURL", str_userImageURL);
        fragment.setArguments(bundle); // 그룹 타이틀 넘기기

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.group_fragment_container, fragment)
                .commit();

        BottomNavigationView navigation = findViewById(R.id.group_bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
//        navigation.setSelectedItemId(R.id.navigation_home);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            // Session 역할 : 유저이름과 프로필사진 담기.
            Bundle bundle = new Bundle();
            bundle.putString("str_userName", str_userName);
            bundle.putString("str_userImageURL", str_userImageURL);

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
                case R.id.navigation_setting:
//                    SharedPreferences.Editor editor = (SharedPreferences.Editor) getSharedPreferences("PREFS", MODE_PRIVATE);
//                    editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
//                    editor.apply();
                    selectedFragment = new SettingFragment();
                    break;
            }
            assert selectedFragment != null;
            selectedFragment.setArguments(bundle); // userName 넘기기
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.group_fragment_container, selectedFragment)
                    .commit();
            return true;
        }
    };

    private void status(String status) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}

