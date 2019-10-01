package com.graduate.seoil.sg_projdct.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.graduate.seoil.sg_projdct.Adapter.PushNotificationAdapter;
import com.graduate.seoil.sg_projdct.Model.PushNotification;
import com.graduate.seoil.sg_projdct.R;

import java.util.ArrayList;
import java.util.Collections;

public class NotificationFragment extends Fragment {

    private RecyclerView recyclerView;
    private PushNotificationAdapter pushNotificationAdapter;
    private ArrayList<PushNotification> mPush = new ArrayList<>();

    DatabaseReference reference;

    private TextView push_delete, push_no_item;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);

        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();

        push_delete = view.findViewById(R.id.push_delete);
        push_no_item = view.findViewById(R.id.push_no_item);

        mPush = new ArrayList<>();
        recyclerView = view.findViewById(R.id.push_lists);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        pushNotificationAdapter = new PushNotificationAdapter(getContext(), mPush);
        recyclerView.setAdapter(pushNotificationAdapter);

        reference = FirebaseDatabase.getInstance().getReference("PushNotifications").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mPush.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PushNotification push = snapshot.getValue(PushNotification.class);
                    mPush.add(push);
                }
                Collections.reverse(mPush);
                pushNotificationAdapter.notifyDataSetChanged();

                if(dataSnapshot.getChildrenCount() == 0) {
                    recyclerView.setVisibility(View.GONE);
                    push_no_item.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    push_no_item.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        push_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference = FirebaseDatabase.getInstance().getReference("PushNotifications").child(fuser.getUid());
                reference.removeValue();
            }
        });

        return view;
    }

}
