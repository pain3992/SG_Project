package com.graduate.seoil.sg_projdct.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.snapshot.Index;
import com.graduate.seoil.sg_projdct.Adapter.GroupAdapter;
import com.graduate.seoil.sg_projdct.ChatActivity;
import com.graduate.seoil.sg_projdct.GroupRegistActivity;
import com.graduate.seoil.sg_projdct.IndexActivity;
import com.graduate.seoil.sg_projdct.Model.Group;
import com.graduate.seoil.sg_projdct.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GroupListFragment extends Fragment {
    private RecyclerView recyclerView;
    private GroupAdapter groupAdapter;

    private TextView create_group;
    private String str_userName;
    private String str_userImageURL;
    private List<Group> mGroup;
    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_list, container, false);
        create_group = view.findViewById(R.id.groupRegister_create);
        recyclerView = view.findViewById(R.id.rv_groupList);

        recyclerView.setHasFixedSize(true); // item이 추가되거나 삭제될 때 RecyclerView의 크기가 변경될 수도 있고, 그렇게 되면 다른 View크기가 변경될 가능성이 있기 때문에 고정시켜버린다.
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mGroup = new ArrayList<>();

        if (getArguments() != null) {
            str_userName = getArguments().getString("str_Username");
            str_userImageURL = getArguments().getString("str_userImageURL");
        }
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

        return view;
    }
//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) { // 프래그먼트 별로 메뉴바 생성 옵션 #2
//        inflater.inflate(R.menu.fragment_toolbar, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }

    private void readGroupList() {
       // final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Group");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mGroup.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Group group = snapshot.getValue(Group.class);
                    mGroup.add(group);
                }

                groupAdapter = new GroupAdapter(getContext(), mGroup);
                recyclerView.setAdapter(groupAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
