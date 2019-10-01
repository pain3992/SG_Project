package com.graduate.seoil.sg_projdct;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class    GroupInformation extends AppCompatActivity {
    FirebaseUser fuser;
    DatabaseReference reference;
    TextView title, registDate, currentUser, currentUser2, maxUser, dayCycle, planTime, announce;
    TextView enter_group;
    private List<GroupUserList> mGroupUserList;
    String str_title, userName, userImageURL;
    int user_count;

    GroupInformationAdapter groupInformationAdapter;

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
        enter_group = findViewById(R.id.group_entrance);

        str_title = intent.getStringExtra("group_title");
        user_count = Integer.parseInt(intent.getStringExtra("group_currentUser")); // 그룹 현재 인원수
        title.setText(intent.getStringExtra("group_title"));

        Date dates = new Date(intent.getLongExtra("group_registDate", 0));
        SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd");
        final String getTimes = sdfs.format(dates);

        registDate.setText(getTimes);
        currentUser.setText(intent.getStringExtra("group_currentUser") + "명");
        currentUser2.setText(intent.getStringExtra("group_currentUser"));
        maxUser.setText(intent.getStringExtra("group_maxUser") + "명");
        planTime.setText(intent.getStringExtra("group_planTime") + "시간");
        dayCycle.setText(intent.getStringExtra("group_dayCycle"));
        announce.setText(intent.getStringExtra("group_announce"));

        userImageURL = IndexActivity.spref.getString("str_userImageURL", "default");
        userName =  IndexActivity.spref.getString("str_userName", "default");
        // ~ 그룹 리스트에서 던져준 그룹 정보 데이터 받기.

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final String getTime = sdf.format(date);

        readGroupList();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        groupInformationAdapter = new GroupInformationAdapter(this, mGroupUserList);

        recyclerView = findViewById(R.id.rv_groupListProfileImage);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(groupInformationAdapter);

        // 그룹 입장하기.
        enter_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 그룹 정원이 가득찼으면 정보만 확인하고 가입은 못함.
                if (!currentUser.getText().toString().equals(maxUser.getText().toString())) {
                    // 그룹 테이블에 유저리스트 업데이트
                    reference = FirebaseDatabase.getInstance().getReference("Group").child(str_title).child("userList");

                    HashMap<String, Object> userList = new HashMap<>();
                    HashMap<String, Object> userList_under = new HashMap<>();
                    userList_under.put("id", fuser.getUid());
                    userList_under.put("imageURL", userImageURL);
                    userList_under.put("level", "user");
                    userList_under.put("username", userName);
                    userList_under.put("registDate", System.currentTimeMillis());
                    userList.put(fuser.getUid(), userList_under);

                    reference.updateChildren(userList);

                    reference = FirebaseDatabase.getInstance().getReference("Group").child(str_title).child("current_user");
                    reference.setValue(user_count + 1); // 그룹 현재 인원수 + 1
                    // ~ 그룹 테이블에 유저리스트 업데이트

                    // 유저 테이블에 그룹리스트 업데이트
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("groupList").child(str_title);
                    HashMap<String, Object> groupList = new HashMap<>();
                    groupList.put("title", str_title);
                    groupList.put("registDate", getTime);
                    reference.updateChildren(groupList);
                    // ~ 유저 테이블에 그룹리스트 업데이트

                    Intent intent = new Intent(GroupInformation.this, GroupActivity.class);
                    intent.putExtra("group_title", str_title);
                    intent.putExtra("userName", userName);
                    intent.putExtra("userImageURL", userImageURL);

                    startActivity(intent);
                    IndexActivity.GROUP_COUNT += 1;
                    finish();
                } else {
                    Toast.makeText(GroupInformation.this, "그룹 정원이 가득 찼습니다.", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private void readGroupList() {
        reference = FirebaseDatabase.getInstance().getReference("Group").child(str_title).child("userList");

        mGroupUserList = new ArrayList<>();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GroupUserList groupUserList = snapshot.getValue(GroupUserList.class);
                    mGroupUserList.add(groupUserList);
                }
                groupInformationAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
