package com.graduate.seoil.sg_projdct;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
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
import android.widget.TimePicker;
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
import com.graduate.seoil.sg_projdct.Fragments.TimePickerFragment;
import com.graduate.seoil.sg_projdct.Model.Group;
import com.graduate.seoil.sg_projdct.Model.User;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;



public class GroupRegistActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser fuser;

    private String username;
    private String userImageURL;

    private Button btn_study, btn_health, btn_etc;

    RecyclerView recyclerview;
    TextView et_planTime;
    EditText et_title, et_minCount, et_maxCount, et_announce;
    CheckBox[] chkBoxs;
    Integer[] chkBoxIds = {R.id.ckbox_mon, R.id.ckbox_tue, R.id.ckbox_wed, R.id.ckbox_thu, R.id.ckbox_fri, R.id.ckbox_sat, R.id.ckbox_sun};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupregister);

        Intent intent = getIntent();

        TextView group_regist = findViewById(R.id.groupRegister_create);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Group");
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        et_title = (EditText) findViewById(R.id.et_groupTitle);
        et_minCount = (EditText) findViewById(R.id.et_minCount);
        et_maxCount = (EditText) findViewById(R.id.et_maxCount);
        et_planTime = (TextView) findViewById(R.id.et_plan_time);
        et_announce = (EditText) findViewById(R.id.et_group_announce);

        btn_study = findViewById(R.id.btn_category_1);
        btn_health = findViewById(R.id.btn_category_2);
        btn_etc = findViewById(R.id.btn_category_3);

        chkBoxs = new CheckBox[chkBoxIds.length];

        username = intent.getStringExtra("str_userName");
        userImageURL = intent.getStringExtra("str_userImageURL");

        Log.d("이미지URL 못불러옴", userImageURL); // TODO : Error -> username, userimageURL 읽기전에 프래그먼트 이동시에.

        et_planTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        group_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = et_title.getText().toString();
                String announce = et_announce.getText().toString();
                int minCount = Integer.parseInt(et_minCount.getText().toString());
                int maxCount = Integer.parseInt(et_maxCount.getText().toString());
                String str_time = et_planTime.getText().toString();
                int index = str_time.indexOf(":");
                int hour = Integer.parseInt(str_time.substring(0, index)) * 60;
                int minute = Integer.parseInt(str_time.substring(index + 1));
                int time = hour + minute;

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

                HashMap<String, Object> userList = new HashMap<String, Object>();
                HashMap<String, Object> userList_under = new HashMap<String, Object>();
                userList_under.put("id", fuser.getUid());
                userList_under.put("imageURL", userImageURL);
                userList_under.put("level", "admin");
                userList_under.put("username", username);
                userList_under.put("registDate", getTime);
                userList.put(fuser.getUid(), userList_under);

                Group group = new Group(title, announce, "default", userImageURL, getTime, fuser.getUid(), checked_days, time, minCount, maxCount, userList);
                databaseReference.child(title).setValue(group);

                // User테이블에 가입한 그룹 추가하기.
                databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("groupList");
                HashMap<String, Object> groupList = new HashMap<>();
                groupList.put("title", title);
                groupList.put("registDate", getTime);
                databaseReference.child(title).setValue(groupList);

                finish();
            }
        });

        btn_study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupRegistActivity.this, Maincheck.class);
                intent.putExtra("category", btn_study.getText().toString());
                startActivity(intent);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        TextView textView = findViewById(R.id.et_plan_time);
        if (minute != 0)
            textView.setText(hourOfDay + ":" + minute);
        else
            textView.setText(hourOfDay + ":" + minute + "0");
    }
}
