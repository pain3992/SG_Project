package com.graduate.seoil.sg_projdct.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.graduate.seoil.sg_projdct.Adapter.GoalAdapter;
import com.graduate.seoil.sg_projdct.BaeHoonActivity2;
import com.graduate.seoil.sg_projdct.BaeHoonActivity3;
import com.graduate.seoil.sg_projdct.GoalMaking;
import com.graduate.seoil.sg_projdct.Model.Goal;
import com.graduate.seoil.sg_projdct.R;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements View.OnClickListener {
    DatabaseReference mDatabase;
    FirebaseDatabase firebaseDatabase;
    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab, fab1, fab2;
    private RecyclerView recyclerView;
    private List<Goal> listItems = new ArrayList<>();
    private ListView database_listview;
    private GoalAdapter adapter;
    private String str_userName;
    private String str_userImageURL;
    String key;
    Goal goal;
    int clickCounter = 0;

    String goalname;

    String goaltext;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.goal_list);

        recyclerView.setHasFixedSize(true); // item이 추가되거나 삭제될 때 RecyclerView의 크기가 변경될 수도 있고, 그렇게 되면 다른 View크기가 변경될 가능성이 있기 때문에 고정시켜버린다.
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (getArguments() != null) {
            str_userName = getArguments().getString("str_Username");
            str_userImageURL = getArguments().getString("str_userImageURL");
        }


        fab_open = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_close);

        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab1 = (FloatingActionButton) view.findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) view.findViewById(R.id.fab2);

        fab.setOnClickListener((View.OnClickListener) this);
        fab1.setOnClickListener((View.OnClickListener) this);
        fab2.setOnClickListener((View.OnClickListener) this);
        readGoalList();
        return  view;

    }
    private void readGoalList() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Goal");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listItems.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Goal goal = snapshot.getValue(Goal.class);
                    listItems.add(goal);
                }
                adapter = new GoalAdapter(getContext(), listItems);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab:
                anim();
                Toast.makeText(getContext(), "Floating Action Button", Toast.LENGTH_SHORT).show();
                break;
            case R.id.fab1:
                anim();
                Intent intent = new Intent(getContext(), BaeHoonActivity3.class);
                startActivity(intent);
                break;
            case R.id.fab2:
                anim();
                Intent myintent = new Intent(getContext(), GoalMaking.class);
                startActivity(myintent);
                break;
        }

    }

    public void anim() {

        if (isFabOpen) {
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;
        } else {
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
        }
    }
}
