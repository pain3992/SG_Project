package com.graduate.seoil.sg_projdct;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.graduate.seoil.sg_projdct.Adapter.GroupInformationAdapter;
import com.graduate.seoil.sg_projdct.Model.Group;
import com.graduate.seoil.sg_projdct.Model.GroupUserList;

import java.util.ArrayList;
import java.util.List;

public class GroupInformation extends AppCompatActivity {
    FirebaseUser fuser;
    DatabaseReference reference;
    TextView title, registDate, currentUser, currentUser2, maxUser, dayCycle, planTime, announce;
    private List<GroupUserList> mGroupUserList;
    String str_title;

    RecyclerView recyclerView;
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_information);

        Intent intent = getIntent();
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        // 그룹 리스트에서 던져준 그룹 정보 데이터 받기.
        title = findViewById(R.id.tv_groupInformation_title);
        registDate = findViewById(R.id.tv_groupInformation_registDate);
        currentUser = findViewById(R.id.tv_groupInformation_currentUser);
        currentUser2 = findViewById(R.id.tv_groupInformation_currentUser2);
        maxUser = findViewById(R.id.tv_groupInformation_maxUser);
        dayCycle = findViewById(R.id.tv_groupInformation_dayCycle);
        planTime = findViewById(R.id.tv_groupInformation_planTime);
        announce = findViewById(R.id.group_announce);

        str_title = intent.getStringExtra("group_title");
        title.setText(intent.getStringExtra("group_title"));
        registDate.setText(intent.getStringExtra("group_registDate"));
        currentUser.setText(intent.getStringExtra("group_currentUser") + "명");
        currentUser2.setText(intent.getStringExtra("group_currentUser"));
        maxUser.setText(intent.getStringExtra("group_maxUser") + "명");
        planTime.setText(intent.getStringExtra("group_planTime"));
        dayCycle.setText(intent.getStringExtra("group_dayCycle"));
        announce.setText(intent.getStringExtra("group_announce"));
        // ~ 그룹 리스트에서 던져준 그룹 정보 데이터 받기.

        readGroupList();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        GroupInformationAdapter groupInformationAdapter = new GroupInformationAdapter(this, mGroupUserList);

        recyclerView = findViewById(R.id.rv_groupListProfileImage);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(groupInformationAdapter);
    }

    private void readGroupList() {
        Log.d("GroupInformation str_title : ", str_title);
        reference = FirebaseDatabase.getInstance().getReference("Group").child(str_title).child("userList");

        mGroupUserList = new ArrayList<>();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GroupUserList groupUserList = snapshot.getValue(GroupUserList.class);
                    mGroupUserList.add(groupUserList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
