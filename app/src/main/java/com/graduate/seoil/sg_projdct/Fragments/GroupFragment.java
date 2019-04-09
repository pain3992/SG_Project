package com.graduate.seoil.sg_projdct.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.graduate.seoil.sg_projdct.Adapter.PostAdapter;
import com.graduate.seoil.sg_projdct.ChatActivity;
import com.graduate.seoil.sg_projdct.GStartActivity;
import com.graduate.seoil.sg_projdct.GroupActivity;
import com.graduate.seoil.sg_projdct.GroupInformationInner;
import com.graduate.seoil.sg_projdct.GroupRegistActivity;
import com.graduate.seoil.sg_projdct.IndexActivity;
import com.graduate.seoil.sg_projdct.MainActivity;
import com.graduate.seoil.sg_projdct.MessageActivity;
import com.graduate.seoil.sg_projdct.Model.Post;
import com.graduate.seoil.sg_projdct.PostAddActivity;
import com.graduate.seoil.sg_projdct.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class GroupFragment extends Fragment  {
    private CircleImageView userImageURL;
    private String str_userName, str_userImageURL, group_title;
    private RelativeLayout post_write;
    private ImageView back_button;
    private ImageButton group_feed, group_chat, group_info;
    private TextView title;

    private RecyclerView recyclerView;
    private FrameLayout fl_group_information;
    private PostAdapter postAdapter;
    private List<Post> postLists;

    private List<String> groupUserList;

    FirebaseUser fuser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_group, container, false);
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        if (getArguments() != null) {
            group_title = getArguments().getString("group_title");
            str_userName = getArguments().getString("userName");
            str_userImageURL = getArguments().getString("userImageURL");
        }

        group_feed = view.findViewById(R.id.group_feed);
        group_chat = view.findViewById(R.id.group_chat);
        group_info = view.findViewById(R.id.group_info);

        userImageURL = view.findViewById(R.id.post_write_default);
        post_write = view.findViewById(R.id.post_relative);
        back_button = view.findViewById(R.id.group_backButton);
        title = view.findViewById(R.id.group_toolbar_title);

        Glide.with(view).load(str_userImageURL).into(userImageURL);
        title.setText(group_title);


        recyclerView = view.findViewById(R.id.post_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        postLists = new ArrayList<>();
        postAdapter = new PostAdapter(getContext(), postLists);
        recyclerView.setAdapter(postAdapter);

        fl_group_information = view.findViewById(R.id.group_information);




        checkFollowing();


        post_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PostAddActivity.class);
                intent.putExtra("group_title", group_title);
                intent.putExtra("userName", str_userName);
                intent.putExtra("userImageURL", str_userImageURL);
                startActivity(intent);
            }
        });

        group_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        group_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MessageActivity.class);
                intent.putExtra("userid", fuser.getUid());
                intent.putExtra("userImageURL", str_userImageURL);
                intent.putExtra("group_title", group_title);
                Objects.requireNonNull(getContext()).startActivity(intent);
            }
        });

        group_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), GroupInformationInner.class);
                intent.putExtra("group_title", group_title);
                Objects.requireNonNull(getContext()).startActivity(intent);
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }
        });



        return view;
    }

    private void checkFollowing() {
        groupUserList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Group")
                .child(group_title)
                .child("userList");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupUserList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    groupUserList.add(snapshot.getKey());
                }

                readPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readPosts() {
        Query query = FirebaseDatabase.getInstance().getReference("Posts").child(group_title).orderByChild("registDate");
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(group_title);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postLists.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    for (String id : groupUserList) {
                        if (post.getPublisher().equals(id)) {
                            postLists.add(post);
                        }
                    }
                }
                Collections.reverse(postLists);
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
