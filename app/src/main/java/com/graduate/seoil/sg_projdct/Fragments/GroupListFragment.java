package com.graduate.seoil.sg_projdct.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Path;
import com.google.firebase.database.snapshot.Index;
import com.graduate.seoil.sg_projdct.Adapter.GroupAdapter;
import com.graduate.seoil.sg_projdct.ChatActivity;
import com.graduate.seoil.sg_projdct.GroupRegistActivity;
import com.graduate.seoil.sg_projdct.IndexActivity;
import com.graduate.seoil.sg_projdct.Model.Group;
import com.graduate.seoil.sg_projdct.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GroupListFragment extends Fragment {
    private FirebaseUser fuser;
    private DatabaseReference reference;

    private RecyclerView recyclerView, recyclerView_search;
    private RecyclerView recyclerView_groupInfo, recyclerView_groupFeed;
    private RelativeLayout outsider_view;
    private GroupAdapter groupAdapter;
    private ImageButton btn_join_list, btn_search_list;

    private TextView toolbar_title ,create_group;
    private EditText et_search_group;
    private String str_userName;
    private String str_userImageURL;
    private List<Group> mGroup, sGroup, iGroup;
    private List<String> invite_title;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_list, container, false);

        if (getArguments() != null) {
            str_userName = getArguments().getString("str_userName");
            str_userImageURL = getArguments().getString("str_userImageURL");
        }

        btn_join_list = view.findViewById(R.id.groupList_join_list);
        btn_search_list = view.findViewById(R.id.groupList_search_list);
        et_search_group = view.findViewById(R.id.et_search_groups);
        create_group = view.findViewById(R.id.gl_groupRegister_create);
        toolbar_title = view.findViewById(R.id.groupList_toolbar_title);

        recyclerView = view.findViewById(R.id.rv_groupList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mGroup = new ArrayList<>();
        groupAdapter = new GroupAdapter(getContext(), mGroup, str_userName, str_userImageURL);
        recyclerView.setAdapter(groupAdapter);

        recyclerView_search = view.findViewById(R.id.rv_groupList_Search);
        recyclerView_search.setHasFixedSize(true);
        recyclerView_search.setLayoutManager(new LinearLayoutManager(getContext()));
        sGroup = new ArrayList<>();
        groupAdapter = new GroupAdapter(getContext(), sGroup, str_userName, str_userImageURL);
        recyclerView_search.setAdapter(groupAdapter);

        iGroup = new ArrayList<>();
        outsider_view = view.findViewById(R.id.outsider_layout);

        invite_title = new ArrayList<>();

        readGroupList();
        readSearchList();

        recyclerView.setVisibility(View.VISIBLE);
        recyclerView_search.setVisibility(View.GONE);
        et_search_group.setVisibility(View.GONE);
        outsider_view.setVisibility(View.GONE);

        // 그룹 만들기
        create_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), GroupRegistActivity.class);
                intent.putExtra("str_userName", str_userName);
                intent.putExtra("str_userImageURL", str_userImageURL);
                startActivity(intent);
            }
        });

        // 그룹 검색
        et_search_group.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) { // 검색창 지우면 기존의 readSearchList 어댑터 장착
                    groupAdapter = new GroupAdapter(getContext(), sGroup, str_userName, str_userImageURL);
                    recyclerView_search.setAdapter(groupAdapter);
                } else {
                    search_group(s.toString().toLowerCase());
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 가입한 그룹 리스트 선택.
        btn_join_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView_search.setVisibility(View.GONE);
                et_search_group.setVisibility(View.GONE);
                outsider_view.setVisibility(View.GONE);
                toolbar_title.setText("가입한 그룹");
            }
        });

        // 그룹 리스트 검색.
        btn_search_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.GONE);
                recyclerView_search.setVisibility(View.VISIBLE);
                et_search_group.setVisibility(View.VISIBLE);
                outsider_view.setVisibility(View.GONE);
                toolbar_title.setText("그룹 검색");
            }
        });

        return view;
    }

    private void readGroupList() {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Group");

        // 가입한 그룹 리스트만 뛰우는 로직.
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mGroup.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        if (Objects.equals(postSnapshot.getKey(), "userList")) {
                            for (DataSnapshot underSnapshot : postSnapshot.getChildren()) {
                                if (Objects.equals(underSnapshot.getKey(), fuser.getUid())) {
                                    Group group = snapshot.getValue(Group.class);
                                    mGroup.add(group);
                                    invite_title.add(group.getTitle());
                                }
                            }
                        }
                    }
                }
                groupAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void readSearchList() {
        reference = FirebaseDatabase.getInstance().getReference("Group");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                sGroup.clear();
                long cur_user = 0;
                long check_num = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        if (postSnapshot.getKey().equals("current_user"))
                            cur_user = (long) postSnapshot.getValue();
                        if (Objects.equals(postSnapshot.getKey(), "userList")) {
                            for (DataSnapshot underSnapshot : postSnapshot.getChildren()) {
                                if (!Objects.equals(underSnapshot.getKey(), fuser.getUid()))
                                    check_num += 1;
                            }
                            if (cur_user == check_num) {
                                Group group = snapshot.getValue(Group.class);
                                sGroup.add(group);
                            }
                            check_num = 0;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void search_group(String s) {
        iGroup.clear();
        for (Group list : sGroup) {
            if (list.getTitle().toLowerCase().startsWith(s)) {
                iGroup.add(list);
            }
        }
        groupAdapter = new GroupAdapter(getContext(), iGroup, str_userName, str_userImageURL);
        recyclerView_search.setAdapter(groupAdapter);
    }
}
