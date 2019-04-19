package com.graduate.seoil.sg_projdct;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.google.firebase.database.ValueEventListener;
import com.graduate.seoil.sg_projdct.Adapter.CommentAdapter;
import com.graduate.seoil.sg_projdct.Fragments.GroupListSearchFragment;
import com.graduate.seoil.sg_projdct.Model.Comment;
import com.graduate.seoil.sg_projdct.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CommentsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    EditText add_comment;
    ImageView image_profile;
    ImageView back_button;
    TextView post;

    String postid;
    String publisher_id;
    String group_title;

    FirebaseUser fuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        recyclerView = findViewById(R.id.comments_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(CommentsActivity.this));
        commentList = new ArrayList<>();
        commentAdapter = new CommentAdapter(this, commentList);
        recyclerView.setAdapter(commentAdapter);

        add_comment = findViewById(R.id.comments_add_comment);
        image_profile = findViewById(R.id.comments_image_profile);
        post = findViewById(R.id.comments_post);
        back_button = findViewById(R.id.comments_backButton);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        postid = intent.getStringExtra("postid");
        publisher_id = intent.getStringExtra("publisherid");
        group_title = intent.getStringExtra("group_title");

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (add_comment.getText().toString().equals("")) {
                    Toast.makeText(CommentsActivity.this, "응원 메세지를 가득 채워주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    addComment();
                }
            }
        });

        getImage();
        readComments();
    }

    private void addComment() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(group_title).child(postid);
        HashMap<String, Object> hashMap = new HashMap<>();

        long cur_time = System.currentTimeMillis();
        hashMap.put("comment", add_comment.getText().toString());
        hashMap.put("publisher", fuser.getUid());
        hashMap.put("registDate", cur_time);

        reference.push().setValue(hashMap);
        add_comment.setText("");
    }

    private void getImage() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImageURL()).into(image_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readComments() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(group_title).child(postid);

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
}
