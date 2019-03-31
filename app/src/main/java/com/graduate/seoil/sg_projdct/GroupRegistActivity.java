package com.graduate.seoil.sg_projdct;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.snapshot.Index;
import com.graduate.seoil.sg_projdct.Adapter.ExpandableListAdapter;
import com.graduate.seoil.sg_projdct.Model.Group;
import com.graduate.seoil.sg_projdct.Model.User;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class GroupRegistActivity extends AppCompatActivity {
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser fuser;

    private String username;
    private String userImageURL;

    RecyclerView recyclerview;
    EditText et_title, et_minCount, et_maxCount, et_planTime, et_announce;
    CheckBox[] chkBoxs;
    Integer[] chkBoxIds = {R.id.ckbox_mon, R.id.ckbox_tue, R.id.ckbox_wed, R.id.ckbox_thu, R.id.ckbox_fri, R.id.ckbox_sat, R.id.ckbox_sun};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupregister);

        Intent intent = getIntent();

        Toolbar toolbar = (Toolbar)findViewById(R.id.groupRegister_toolbar);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Group");
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        et_title = (EditText) findViewById(R.id.et_groupTitle);
        et_minCount = (EditText) findViewById(R.id.et_minCount);
        et_maxCount = (EditText) findViewById(R.id.et_maxCount);
        et_planTime = (EditText) findViewById(R.id.et_plan_time);
        et_announce = (EditText) findViewById(R.id.et_group_announce);

        chkBoxs = new CheckBox[chkBoxIds.length];

        username = intent.getStringExtra("str_userName");
        userImageURL = intent.getStringExtra("str_userImageURL");

        Log.d("이미지URL 못불러옴", userImageURL);

        toolbar.findViewById(R.id.groupRegister_create).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = et_title.getText().toString();
                String announce = et_announce.getText().toString();
                int minCount = Integer.parseInt(et_minCount.getText().toString());
                int maxCount = Integer.parseInt(et_maxCount.getText().toString());
                int planTime = Integer.parseInt(et_planTime.getText().toString());
                String checked_days = "";

                long now = System.currentTimeMillis();
                Date date = new Date(now);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String getTime = sdf.format(date);

                for(int i = 0; i < chkBoxIds.length; i++) {
                    chkBoxs[i] = (CheckBox) findViewById(chkBoxIds[i]);
                    if (chkBoxs[i].isChecked()) {
                        checked_days += (chkBoxs[i].getText().toString());
                    }
                }

                // TODO : [3월 29일] 승연이가 그룹목표 넣으면 "default"를 바꿔줘야함.
                // TODO : [3월 29일] 그룹은 하나 이상 못만들게 해야함.
                // TODO : [3월 30일] 프로필 사진 변경시, 그룹의 유저리스트의 imageURL도 변경되어야 함.

                Group group = new Group(title, announce, "default", userImageURL, getTime, fuser.getUid(), checked_days, planTime, minCount, maxCount);
                databaseReference.child(title).setValue(group);

                // userList 코딩 ~
                databaseReference = firebaseDatabase.getReference("Group").child(title).child("userList");
                User user = new User(fuser.getUid(), username, "admin", userImageURL);
                databaseReference.child(fuser.getUid()).setValue(user);

                finish();
            }
        });

//        recyclerview = (RecyclerView) findViewById(R.id.expanded_recyclerview);
//        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
//        List<ExpandableListAdapter.Item> data = new ArrayList<>();
//
//
//        ExpandableListAdapter.Item places1 = new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "Fruits");
//        places1.invisibleChildren = new ArrayList<>();
//        places1.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "Apple"));
//        places1.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "Orange"));
//        places1.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "Banana"));
////        data.add(new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "Fruits"));
////        data.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "Apple"));
////        data.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "Orange"));
////        data.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "Banana"));
//        ExpandableListAdapter.Item places2 = new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "Places");
//        places2.invisibleChildren = new ArrayList<>();
//        places2.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "Kerala"));
//        places2.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "Tamil Nadu"));
//        places2.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "Karnataka"));
//        places2.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "Maharashtra"));
//
//
//        ExpandableListAdapter.Item places3 = new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "Cars");
//        places3.invisibleChildren = new ArrayList<>();
//        places3.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "Cadillac"));
//        places3.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "Audi"));
//        places3.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "Benz"));
//        places3.invisibleChildren.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "KIA"));
//        data.add(new ExpandableListAdapter.Item(ExpandableListAdapter.HEADER, "Cars"));
//        data.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "Audi"));
//        data.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "Aston Martin"));
//        data.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "BMW"));
//        data.add(new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, "Cadillac"));
//
//        data.add(places1);
//        data.add(places2);
//        data.add(places3);
//
//        recyclerview.setAdapter(new ExpandableListAdapter(data)); RecyclerView
    }
}
