package com.graduate.seoil.sg_projdct;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.graduate.seoil.sg_projdct.Model.User;

import java.util.HashMap;

public class NotificationRegister extends AppCompatActivity {
    EditText content;
    TextView add;
    ImageView backButton;
    String group_title;
    String user_name;
    FirebaseUser fuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_register);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        group_title = intent.getStringExtra("group_title");

        add = findViewById(R.id.notification_register_add);
        backButton = findViewById(R.id.notification_register_backButton);
        content = findViewById(R.id.notification_register_content);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                user_name = user.getUsername();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Group").child(group_title).child("notifications");

                String noty_id = reference.push().getKey();
                long curtime = System.currentTimeMillis();

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("content", content.getText().toString());
                hashMap.put("noty_id", noty_id);
                hashMap.put("registDate", curtime);
                hashMap.put("writer", user_name);
                hashMap.put("writer_id", fuser.getUid());

                reference.child(noty_id).setValue(hashMap);

                GroupNotificationActivity.no_noty.setVisibility(View.GONE);
                GroupNotificationActivity.noty_add.setVisibility(View.GONE);
                GroupNotificationActivity.recyclerView.setVisibility(View.VISIBLE);

                finish();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
