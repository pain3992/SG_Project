package com.graduate.seoil.sg_projdct.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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
import com.graduate.seoil.sg_projdct.Adapter.GroupAdapter;
import com.graduate.seoil.sg_projdct.Adapter.GroupInformationAdapter;
import com.graduate.seoil.sg_projdct.Adapter.UserAdapter;
import com.graduate.seoil.sg_projdct.GroupInformation;
import com.graduate.seoil.sg_projdct.GroupRegistActivity;
import com.graduate.seoil.sg_projdct.Model.Group;
import com.graduate.seoil.sg_projdct.Model.GroupUserList;
import com.graduate.seoil.sg_projdct.Model.User;
import com.graduate.seoil.sg_projdct.Notification.Data;
import com.graduate.seoil.sg_projdct.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GroupListSearchFragment extends Fragment {
    private RecyclerView recyclerView;
    private GroupAdapter groupAdapter;

    private TextView create_group;
    private String str_userName;
    private String str_userImageURL;
    private List<Group> mGroup;

    private EditText search_group;

    private FirebaseUser fuser;
    private DatabaseReference reference;

    private ArrayList<String> groupList;

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_list_search, container, false);

        recyclerView = view.findViewById(R.id.groupSearch_rv_groupList);
        create_group = view.findViewById(R.id.groupSearch_groupRegister_create);

        recyclerView.setHasFixedSize(true); // item이 추가되거나 삭제될 때 RecyclerView의 크기가 변경될 수도 있고, 그렇게 되면 다른 View크기가 변경될 가능성이 있기 때문에 고정시켜버린다.
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        groupList = new ArrayList<>();

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        // 현재 유저의 가입한 그룹 리스트 불러오기.
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("groupList");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    groupList.add(snapshot.getKey());
                }
            }
                        @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mGroup = new ArrayList<>();

        Bundle bundle = getArguments();
        assert bundle != null;
        str_userName = bundle.getString("str_userName"); // TODO : Error null object reference
        str_userImageURL = bundle.getString("str_userImageURL");

        TabLayout tabLayout = view.findViewById(R.id.groupSearch_tab_layout);

        Objects.requireNonNull(tabLayout.getTabAt(1)).select(); // 그룹 검색에서는 "그룹검색"이 선텍되어있게.
        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selectedFragment = null;
                selectedFragment = new GroupListFragment();

                Bundle bundle = new Bundle();
                bundle.putString("str_userName", str_userName);
                bundle.putString("str_userImageURL", str_userImageURL);
                selectedFragment.setArguments(bundle);

                assert getFragmentManager() != null;
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        create_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), GroupRegistActivity.class);
                intent.putExtra("str_userName", str_userName);
                intent.putExtra("str_userImageURL", str_userImageURL);
                startActivity(intent);
            }
        });

        readGroupList();

        search_group = view.findViewById(R.id.search_groups);
        search_group.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search_group(s.toString().toLowerCase());
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

    private void readGroupList() {

        reference = FirebaseDatabase.getInstance().getReference("Group");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mGroup.clear();
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
                                mGroup.add(group);
                            }
                            check_num = 0;
                        }
                    }

                }
                groupAdapter = new GroupAdapter(getContext(), mGroup, str_userName, str_userImageURL);
                recyclerView.setAdapter(groupAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void search_group(String s) {
        Query query = FirebaseDatabase.getInstance().getReference("Group").orderByChild("title")
                .startAt(s)
                .endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mGroup.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Group group = snapshot.getValue(Group.class);
                    mGroup.add(group);
                }
                groupAdapter = new GroupAdapter(getContext(), mGroup, str_userName, str_userImageURL);
                recyclerView.setAdapter(groupAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
