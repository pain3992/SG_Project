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
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.graduate.seoil.sg_projdct.Adapter.ExpandableListAdapter;
import com.graduate.seoil.sg_projdct.Model.Group;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class GroupRegistActivity extends AppCompatActivity {
    FirebaseAuth auth;
    DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;

    RecyclerView recyclerview;
    EditText et_title, et_minCount, et_maxCount, et_planTime, et_announce;
    CheckBox[] chkBoxs;
    Integer[] chkBoxIds = {R.id.ckbox_mon, R.id.ckbox_tue, R.id.ckbox_wed, R.id.ckbox_thu, R.id.ckbox_fri, R.id.ckbox_sat, R.id.ckbox_sun};

    String key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupregister);
        Toolbar toolbar = (Toolbar)findViewById(R.id.groupRegister_toolbar);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Group");
        key = databaseReference.child("Group").push().getKey();

        et_title = (EditText) findViewById(R.id.et_groupTitle);
        et_minCount = (EditText) findViewById(R.id.et_minCount);
        et_maxCount = (EditText) findViewById(R.id.et_maxCount);
        et_planTime = (EditText) findViewById(R.id.et_plan_time);
        et_announce = (EditText) findViewById(R.id.et_group_announce);

        chkBoxs = new CheckBox[chkBoxIds.length];

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

                Group group = new Group(title, announce, "default", "default", getTime, minCount, maxCount);
                databaseReference.child(key).setValue(group);

                Toast.makeText(getApplicationContext(), "Submitted", Toast.LENGTH_SHORT).show();
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

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_group:
                    return true;
                case R.id.navigation_chart:
                    Toast.makeText(getApplicationContext(), "통계페이지", Toast.LENGTH_SHORT);
                    return true;
                case R.id.navigation_home:
                    Intent intent = new Intent(GroupRegistActivity.this, IndexActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_notifications:
                    Toast.makeText(getApplicationContext(), "알림", Toast.LENGTH_SHORT);
                    return true;
                case R.id.navigation_setting:
                    Toast.makeText(getApplicationContext(), "세팅", Toast.LENGTH_SHORT);
                    return true;
            }
            return false;
        }
    };
}
