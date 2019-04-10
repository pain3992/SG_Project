package com.graduate.seoil.sg_projdct;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.graduate.seoil.sg_projdct.Fragments.GroupFragment;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView title, username;
    Toolbar toolbar;

    DatabaseReference reference;

    String str_userName, str_userImageURL, str_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        Intent intent = getIntent();
        str_title = intent.getStringExtra("group_title");
        str_userName = intent.getStringExtra("userName");
        str_userImageURL = intent.getStringExtra("userImageURL");

        // 이니시 프래그먼트 설정
        Fragment initFragment = new GroupFragment();

        Bundle bundle = new Bundle();
        bundle.putString("group_title", str_title);
        bundle.putString("userName", str_userName);
        bundle.putString("userImageURL", str_userImageURL);
        initFragment.setArguments(bundle); // 그룹 타이틀 넘기기

        getSupportFragmentManager().beginTransaction().replace(R.id.group_fragment_container, initFragment).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}

