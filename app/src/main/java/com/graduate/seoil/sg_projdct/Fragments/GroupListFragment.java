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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
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
    private RecyclerView recyclerView;
    private GroupAdapter groupAdapter;

    private TextView create_group;
    private String str_userName;
    private String str_userImageURL;
    private List<Group> mGroup;

    private int joinGroup_count = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_list, container, false);

        recyclerView = view.findViewById(R.id.rv_groupList);
        create_group = view.findViewById(R.id.groupRegister_create);

        if (getArguments() != null) {
            str_userName = getArguments().getString("str_userName");
            str_userImageURL = getArguments().getString("str_userImageURL");
        }

        recyclerView.setHasFixedSize(true); // item이 추가되거나 삭제될 때 RecyclerView의 크기가 변경될 수도 있고, 그렇게 되면 다른 View크기가 변경될 가능성이 있기 때문에 고정시켜버린다.
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mGroup = new ArrayList<>();

        System.out.println("유저네임, URL : " + str_userImageURL + ", " + str_userName);
        readGroupList();
        // 그룹 가입한 거 없으면 다른 페이지 뛰우기.
//        if (joinGroup_count == 0) {
//            Fragment selectedFragment;
//            selectedFragment = new GroupListZero();
//
//            Bundle bundle = new Bundle();
//            bundle.putString("str_userName", str_userName);
//            bundle.putString("str_userImageURL", str_userImageURL);
//            selectedFragment.setArguments(bundle);
//
//            assert getFragmentManager() != null;
//            getFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.fragment_container, selectedFragment)
//                    .commit();
//        }

        TabLayout tabLayout = view.findViewById(R.id.groupList_tab_layout);
        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selectedFragment;
                selectedFragment = new GroupListSearchFragment();

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

        return view;
    }

    private void readGroupList() {
        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        assert fuser != null;

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Group");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mGroup.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        if (Objects.equals(postSnapshot.getKey(), "userList")) {
                            for (DataSnapshot underSnapshot : postSnapshot.getChildren()) {
                                if (Objects.equals(underSnapshot.getKey(), fuser.getUid())) {
                                    joinGroup_count += 1;
                                    Group group = snapshot.getValue(Group.class);
                                    mGroup.add(group);
                                }
                            }
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
}
