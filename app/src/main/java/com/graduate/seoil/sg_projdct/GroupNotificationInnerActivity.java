package com.graduate.seoil.sg_projdct;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.graduate.seoil.sg_projdct.Adapter.CommentAdapter;
import com.graduate.seoil.sg_projdct.Model.Comment;
import com.graduate.seoil.sg_projdct.Model.GroupNotification;
import com.graduate.seoil.sg_projdct.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupNotificationInnerActivity extends AppCompatActivity {
    DatabaseReference reference;
    FirebaseUser fuser;

    String notyid;
    String writerid;
    String group_title;

    TextView writer, registDate, content, post, title;
    EditText comment;
    ImageView back_button;
    CircleImageView profile;

    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_notification_inner);

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        group_title = intent.getStringExtra("group_title");
        notyid = intent.getStringExtra("notyid");
        writerid = intent.getStringExtra("writer_id");

        title = findViewById(R.id.group_notification_inner_title);
        writer = findViewById(R.id.group_notification_inner_writer);
        registDate = findViewById(R.id.group_notification_inner_date);
        content = findViewById(R.id.group_notification_inner_content);
        profile = findViewById(R.id.group_notification_inner_profile);
        back_button = findViewById(R.id.gruop_notification_inner_backButton);
        post = findViewById(R.id.group_notification_inner_comment_post);
        comment = findViewById(R.id.group_notification_inner_comment);

        title.setText(group_title);

        recyclerView = findViewById(R.id.recycler_noti_comment);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(GroupNotificationInnerActivity.this));
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList);
        recyclerView.setAdapter(commentAdapter);


        reference = FirebaseDatabase.getInstance().getReference("Group").child(group_title).child("notifications").child(notyid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                GroupNotification groupNotification = dataSnapshot.getValue(GroupNotification.class);

                assert groupNotification != null;
                long regTime = groupNotification.getRegistDate();
                long curTime = System.currentTimeMillis();
                long diffTime = (curTime - regTime) / 1000;

                writer.setText(groupNotification.getWriter());
                content.setText(groupNotification.getContent());
                if (diffTime < TIME_MAXIMUM.SEC) {
                    registDate.setText("방금 전");
                } else if ((diffTime /= TIME_MAXIMUM.SEC) < TIME_MAXIMUM.MIN) {
                    registDate.setText(diffTime + "분 전");
                } else if ((diffTime /= TIME_MAXIMUM.MIN) < TIME_MAXIMUM.HOUR) {
                    registDate.setText(diffTime + "시간 전");
                } else if ((diffTime /= TIME_MAXIMUM.HOUR) < TIME_MAXIMUM.DAY) {
                    registDate.setText(diffTime + "일 전");
                } else if ((diffTime /= TIME_MAXIMUM.DAY) < TIME_MAXIMUM.MONTH) {
                    registDate.setText(diffTime + "달 전");
                } else {
                    registDate.setText(diffTime + "년 전");
                }
                getUserInfo(profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        readComments();

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comment.getText().toString().equals("")) {
                    Toast.makeText(GroupNotificationInnerActivity.this, "응원 메세지를 가득 채워주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    addComment();
                }
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getUserInfo(final ImageView imageView) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(writerid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Activity activity = GroupNotificationInnerActivity.this;
                if (activity.isFinishing())
                    return;
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                Glide.with(getApplicationContext()).load(user.getImageURL()).into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addComment() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Group").child(group_title).child("notifications").child(notyid);

        long curTime = System.currentTimeMillis();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("comment", comment.getText().toString());
        hashMap.put("publisher", fuser.getUid());
        hashMap.put("registerDate", curTime);

        reference.child("Comments").push().setValue(hashMap);
        comment.setText("");
    }

    private void readComments() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Group").child(group_title).child("notifications").child(notyid).child("Comments");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment comment = snapshot.getValue(Comment.class);
                    commentList.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private static class TIME_MAXIMUM {
        public static final int SEC = 60;
        public static final int MIN = 60;
        public static final int HOUR = 24;
        public static final int DAY = 30;
        public static final int MONTH = 12;
    }
}
