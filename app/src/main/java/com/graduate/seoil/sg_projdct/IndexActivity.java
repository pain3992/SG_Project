package com.graduate.seoil.sg_projdct;

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
import com.graduate.seoil.sg_projdct.Model.User;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class IndexActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username, toolbar_title, groupCreate;

    FirebaseUser firebaseUser;
    DatabaseReference reference;

    public static String str_userName;
    public static String str_userImageURL;
    private String toss_selectFragment;

    public static final int GROUP_ACTIVITY = 1;
    public static boolean IS_TRUN;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);

        IS_TRUN = false;

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        // 유저네임, 프로필URL 불러오기.
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                str_userName = user.getUsername();
                str_userImageURL = user.getImageURL();
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

                BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
                if (result.equals("GroupListFragment")) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new GroupListFragment()).commit();
                    navigation.setSelectedItemId(R.id.navigation_group);
                }
            }
        }

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
                    selectedFragment.setArguments(bundle);
                    break;
                case R.id.navigation_chart:
                    selectedFragment = new StatisticsFragment();
                    selectedFragment.setArguments(bundle);
                    break;
                case R.id.navigation_home:
                    selectedFragment = new HomeFragment();
                    selectedFragment.setArguments(bundle);
                    break;
                case R.id.navigation_setting:
//                    SharedPreferences.Editor editor = (SharedPreferences.Editor) getSharedPreferences("PREFS", MODE_PRIVATE);
//                    editor.putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid());
//                    editor.apply();

//                     SharedPreference prefs = getContext().getSharedPreference("PREFS", Context.MODE_PRIVATE);
//                     profileId = prefs.getString("profileid", "none");
                    selectedFragment = new SettingFragment();
                    selectedFragment.setArguments(bundle);
                    break;
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }



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
