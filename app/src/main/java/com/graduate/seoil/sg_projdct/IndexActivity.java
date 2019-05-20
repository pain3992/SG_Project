package com.graduate.seoil.sg_projdct;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.Chart;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.snapshot.Index;
import com.graduate.seoil.sg_projdct.Adapter.GroupAdapter;
import com.graduate.seoil.sg_projdct.Fragments.ChatFragment;
import com.graduate.seoil.sg_projdct.Fragments.GroupFragment;
import com.graduate.seoil.sg_projdct.Fragments.GroupListFragment;
import com.graduate.seoil.sg_projdct.Fragments.HomeFragment;
import com.graduate.seoil.sg_projdct.Fragments.ProfileFragment;
import com.graduate.seoil.sg_projdct.Fragments.SettingFragment;
import com.graduate.seoil.sg_projdct.Fragments.StatisticsFragment;
import com.graduate.seoil.sg_projdct.Fragments.UsersFragment;
import com.graduate.seoil.sg_projdct.Model.Group;
import com.graduate.seoil.sg_projdct.Model.User;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import durdinapps.rxfirebase2.RxFirebaseDatabase;
import durdinapps.rxfirebase2.RxFirebaseQuery;

public class IndexActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username, toolbar_title, groupCreate;

    FirebaseUser firebaseUser;
    DatabaseReference reference;
    RxFirebaseDatabase rxFirebaseDatabase;
    RxFirebaseQuery rxFirebaseQuery;

    public static String str_userName;
    public static String str_userImageURL;
    int join_group_cnt;
    private String toss_selectFragment;

    public static final int GROUP_ACTIVITY = 1;
    public static boolean IS_TRUN;

    public static int GROUP_COUNT;

    private Fragment GroupListFragment, StatisticsFragment, HomeFragment, NotificationFragment, SettingFragment;

    public static SharedPreferences spref;
    public static SharedPreferences.Editor editor;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        IS_TRUN = false;

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        // 유저네임, 프로필URL 불러오기.
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("CommitPrefEdits")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                str_userName = user.getUsername();
                str_userImageURL = user.getImageURL();

                spref = getSharedPreferences("gpref", MODE_PRIVATE);
                editor = spref.edit();
                editor.putString("str_userImageURL", str_userImageURL);
                editor.putString("str_userName", str_userName);
                editor.apply();

                reference.child("groupList").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        GROUP_COUNT = (int) dataSnapshot.getChildrenCount();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if ( savedInstanceState == null && !IS_TRUN) { // 이니시 프래그먼트 설정
            navigation.setSelectedItemId(R.id.navigation_home);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IS_TRUN = true;
        if (requestCode == GROUP_ACTIVITY) {
            if (resultCode != RESULT_OK) {

            }
            if (resultCode == RESULT_OK) {
                String result = data.getExtras().getString(GroupActivity.RESULT_DATA);

                GroupListFragment = null;
                StatisticsFragment = null;
                SettingFragment = null;
                HomeFragment = null;

                BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
                if (result.equals("GroupListFragment")) {
                    navigation.setSelectedItemId(R.id.navigation_group);
                }

            }
        }

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_group:
                    if (GroupListFragment == null) {
                        GroupListFragment = new GroupListFragment();
                        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, GroupListFragment).commit();
                    }

                    if (GroupListFragment != null) getSupportFragmentManager().beginTransaction().show(GroupListFragment).commit();
                    if (StatisticsFragment != null) getSupportFragmentManager().beginTransaction().hide(StatisticsFragment).commit();
                    if (HomeFragment != null) getSupportFragmentManager().beginTransaction().hide(HomeFragment).commit();
                    if (SettingFragment != null) getSupportFragmentManager().beginTransaction().hide(SettingFragment).commit();

                    break;
                case R.id.navigation_chart:
                    if (StatisticsFragment == null) {
                        StatisticsFragment = new StatisticsFragment();
                        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, StatisticsFragment).commit();
                    }

                    if (GroupListFragment != null) getSupportFragmentManager().beginTransaction().hide(GroupListFragment).commit();
                    if (StatisticsFragment != null) getSupportFragmentManager().beginTransaction().show(StatisticsFragment).commit();
                    if (HomeFragment != null) getSupportFragmentManager().beginTransaction().hide(HomeFragment).commit();
                    if (SettingFragment != null) getSupportFragmentManager().beginTransaction().hide(SettingFragment).commit();

                    break;
                case R.id.navigation_home:
                    if (HomeFragment == null) {
                        HomeFragment = new HomeFragment();
                        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, HomeFragment).commit();
                    }
                    if (GroupListFragment != null) getSupportFragmentManager().beginTransaction().hide(GroupListFragment).commit();
                    if (StatisticsFragment != null) getSupportFragmentManager().beginTransaction().hide(StatisticsFragment).commit();
                    if (HomeFragment != null) getSupportFragmentManager().beginTransaction().show(HomeFragment).commit();
                    if (SettingFragment != null) getSupportFragmentManager().beginTransaction().hide(SettingFragment).commit();

                    break;
                case R.id.navigation_setting:
                    if (SettingFragment == null) {
                        SettingFragment = new SettingFragment();
                        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, SettingFragment).commit();
                    }

                    if (GroupListFragment != null) getSupportFragmentManager().beginTransaction().hide(GroupListFragment).commit();
                    if (StatisticsFragment != null) getSupportFragmentManager().beginTransaction().hide(StatisticsFragment).commit();
                    if (HomeFragment != null) getSupportFragmentManager().beginTransaction().hide(HomeFragment).commit();
                    if (SettingFragment != null) getSupportFragmentManager().beginTransaction().show(SettingFragment).commit();

                    break;
            }

            return true;
        }
    };

//    private void status(String status) {
//        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
//
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("status", status);
//
//        reference.updateChildren(hashMap);
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        status("online");
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        status("offline");
//    }
}
