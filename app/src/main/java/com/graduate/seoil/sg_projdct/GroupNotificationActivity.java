package com.graduate.seoil.sg_projdct;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.graduate.seoil.sg_projdct.Adapter.NotificationAdapter;
import com.graduate.seoil.sg_projdct.Model.GroupNotification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroupNotificationActivity extends AppCompatActivity {
    List<GroupNotification> mNotification;
    NotificationAdapter notiAdapter;

    String group_title;
    ImageView back_button, write;

    public static RecyclerView recyclerView;
    public static TextView no_noty;
    public static Button noty_add;

    DatabaseReference reference;
    FirebaseUser fuser;


    int noty_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_notification);

        Intent intent = getIntent();
        group_title = intent.getStringExtra("group_title");
        noty_count = intent.getIntExtra("noty_count", 0);

        noty_add = findViewById(R.id.not_notification_add);
        no_noty  = findViewById(R.id.not_notification);
        back_button = findViewById(R.id.gruop_notification_backButton);
        recyclerView = findViewById(R.id.recycler_notification);
        write = findViewById(R.id.group_notification_write);

        if (noty_count == 0) {
            recyclerView.setVisibility(View.GONE);
            noty_add.setVisibility(View.VISIBLE);
            no_noty.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            noty_add.setVisibility(View.GONE);
            no_noty.setVisibility(View.GONE);
        }

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(GroupNotificationActivity.this));
        mNotification = new ArrayList<>();
        notiAdapter = new NotificationAdapter(getApplicationContext(), mNotification, group_title);
        recyclerView.setAdapter(notiAdapter);

        // 공지사항 긁어오기.
//        reference = FirebaseDatabase.getInstance().getReference("Group").child(group_title).child("notifications");
        Query query = FirebaseDatabase.getInstance().getReference("Group").child(group_title).child("notifications").orderByChild("registDate");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mNotification.clear();
                noty_count = (int) dataSnapshot.getChildrenCount();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GroupNotification group = snapshot.getValue(GroupNotification.class);
                    mNotification.add(group);
                }
                Collections.reverse(mNotification);
                notiAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupNotificationActivity.this, NotificationRegister.class);
                intent.putExtra("group_title", group_title);
                startActivity(intent);
            }
        });

        noty_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupNotificationActivity.this, NotificationRegister.class);
                intent.putExtra("group_title", group_title);
                startActivity(intent);
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}
