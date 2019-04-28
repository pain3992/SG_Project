package com.graduate.seoil.sg_projdct;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.graduate.seoil.sg_projdct.Fragments.GroupFragment;

import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupActivity extends AppCompatActivity {

    public static final String RESULT_DATA = "GroupListFragment";

    CircleImageView profile_image;
    TextView title, username;
    Toolbar toolbar;

    DatabaseReference reference;

    String str_userName, str_userImageURL, str_title, receive;

    FirebaseUser fuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        str_userImageURL = IndexActivity.spref.getString("str_userImageURL", "default");
        str_userName =  IndexActivity.spref.getString("str_userName", "default");

        Intent intent = getIntent();
        if (intent != null) {
            str_title = intent.getStringExtra("group_title");
        }

        Bundle bundles = Objects.requireNonNull(getIntent()).getExtras();
        if (bundles != null) {
            String publisher = intent.getStringExtra("publisherid");

            SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
            editor.putString("profileid", publisher);
            editor.apply();
        }
//        receive = intent.getStringExtra(SEND_DATA);

        // 이니시 프래그먼트 설정
        Fragment initFragment = new GroupFragment();

        Bundle bundle = new Bundle();
        bundle.putString("group_title", str_title);
        bundle.putString("userName", str_userName);
        bundle.putString("userImageURL", str_userImageURL);
        bundle.putString("receive", receive);
        initFragment.setArguments(bundle); // 그룹 타이틀 넘기기

        getSupportFragmentManager().beginTransaction().replace(R.id.group_fragment_container, initFragment).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void status(String status) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

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

