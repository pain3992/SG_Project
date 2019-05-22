package com.graduate.seoil.sg_projdct.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.graduate.seoil.sg_projdct.Adapter.UserAdapter;
import com.graduate.seoil.sg_projdct.Model.Chat;
import com.graduate.seoil.sg_projdct.Model.Chatlist;
import com.graduate.seoil.sg_projdct.Model.GroupUserList;
import com.graduate.seoil.sg_projdct.Model.User;
import com.graduate.seoil.sg_projdct.Notification.Token;
import com.graduate.seoil.sg_projdct.R;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatFragment extends Fragment {
    private RecyclerView recyclerView;
    private TextView tv_userName;
    private CircleImageView civ_userImageURL;

    private UserAdapter userAdapter;
    private List<User> mUsers;

    FirebaseUser fuser;
    DatabaseReference reference;

    private List<GroupUserList> mUserList;
    private String str_userName, str_userImageURL, group_title;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

//        recyclerView = view.findViewById(R.id.group_recycler_view);
//        tv_userName = view.findViewById(R.id.group_userName);
//        civ_userImageURL = view.findViewById(R.id.group_userProfileImage);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        if (getArguments() != null) {
            group_title = getArguments().getString("group_title");
            str_userName = getArguments().getString("userName");
            str_userImageURL = getArguments().getString("userImageURL");
        }

        tv_userName.setText(str_userName);
        if (str_userImageURL == null) {
            civ_userImageURL.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(view).load(str_userImageURL).into(civ_userImageURL);
        }




        mUserList = new ArrayList<>();

//        reference = FirebaseDatabase.getInstance().getReference("Chatlist").child(fuser.getUid());
        reference = FirebaseDatabase.getInstance().getReference("Group").child(group_title).child("userList"); // TODO : 4/2 방금에러 패스스트링
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (getActivity() == null) {
                    return;
                }

                mUserList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GroupUserList userList = snapshot.getValue(GroupUserList.class);
                    if (!userList.getId().equals(fuser.getUid())) { // 자기 자신은 채팅유저에 안뜨게
                        mUserList.add(userList);
                    }
                }

                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        updateToken(FirebaseInstanceId.getInstance().getToken());

        return view;
    }

//    private void updateToken(String token) {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
//        Token token1 = new Token(token);
//        reference.child(fuser.getUid()).setValue(token1);
//    }

    private void chatList() {
        mUsers = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    for (GroupUserList userlist : mUserList) {
                        if (user.getId().equals(userlist.getId())) {
                            mUsers.add(user);
                        }
                    }
                }
                userAdapter = new UserAdapter(getContext(), mUsers, true);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
