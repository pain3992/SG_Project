package com.graduate.seoil.sg_projdct.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.graduate.seoil.sg_projdct.GroupActivity;
import com.graduate.seoil.sg_projdct.GroupInformation;
import com.graduate.seoil.sg_projdct.Model.Group;
import com.graduate.seoil.sg_projdct.R;

import java.util.HashMap;
import java.util.List;

import static com.graduate.seoil.sg_projdct.IndexActivity.GROUP_ACTIVITY;

/**
 * Created by baejanghun on 28/03/2019.
 */
public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
    private Context mContext;
    private List<Group> mGroups;

    private String userName;
    private String userImageURL;

    FirebaseUser fuser;

    public GroupAdapter(Context mContext, List<Group> mGroups, String userName, String str_userImageURL) {
        this.mGroups = mGroups;
        this.mContext = mContext;
        this.userName = userName;
        this.userImageURL = str_userImageURL;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.group_item, viewGroup, false);

        return new GroupAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Group group = mGroups.get(i);
        viewHolder.group_title.setText(group.getTitle());
        viewHolder.group_dayCycle.setText(String.format("#") + group.getDayCycle());
        viewHolder.group_current_user.setText(String.valueOf(group.getcurrent_user()));
        viewHolder.group_maxCount.setText(String.valueOf(group.getUser_max_count()));
        viewHolder.group_planTime.setText(String.format("#") + String.valueOf(group.getPlanTime() / 60) + String.format("시간"));

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        final HashMap<String, Object> userList = group.getUserList();

        // 그룹 정원 다 차면 참여하기 --> 마감
        if (group.getcurrent_user() == group.getUser_max_count()) {
            viewHolder.group_join.setText("마감");
            viewHolder.group_join.setTextColor(Color.parseColor("#F44336"));
        }

        if (userList.get(fuser.getUid()) != null) {
            viewHolder.group_join.setText("입장하기");
            viewHolder.group_join.setTextColor(Color.parseColor("#D81B60"));
        }

        // 그룹 리스트 프로필 사진.
        // TODO : [3월 30일 에러] 앱 내에서 DB 읽기도 전에 액션을 하면 뻑 나는 경우 있음.
        if (group.getImageURL().equals("default")) {
            viewHolder.group_profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(group.getImageURL()).into(viewHolder.group_profile_image);
        }

        // 그룹 리스트 클릭시 그룹 이동(그룹 가입 X시 그룹 정보로 이동)
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userList.get(fuser.getUid()) != null) {
                    // userList : {4aZRiZY7wtWaiJ8IM70VXhUPhLM2={level=user, imageURL=imageURL, id=4aZRiZY7wtWaiJ8IM70VXhUPhLM2, registDate=2019-04-02, username=얍얍}
                    Intent intent = new Intent(mContext, GroupActivity.class);

                    Bundle bundle = new Bundle();
                    bundle.putString("group_title", group.getTitle());
                    bundle.putString("userName", userName);
                    bundle.putString("userImageURL", userImageURL);
//                    bundle.putString(GroupActivity.SEND_DATA, "GroupListFragment");
                    intent.putExtras(bundle);

//                    mContext.startActivity(intent);
                    ((Activity)mContext).startActivityForResult(intent, GROUP_ACTIVITY);
//                    fragment.setArguments(bundle);
//                    FragmentManager fragmentManager = ((IndexActivity)mContext).getSupportFragmentManager();
//                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                    fragmentTransaction.add(R.id.fragment_container, fragment);
//                    fragmentTransaction.commit();

                } else {
                    Intent intent = new Intent(mContext, GroupInformation.class);

                    // 그룹 리스트에서 그룹 정보 액티비티로 데이터 전달
                    Bundle bundle = new Bundle();
                    bundle.putString("group_id", group.getAdminName());
                    bundle.putString("group_title", group.getTitle());
                    bundle.putString("group_registDate", group.getRegistDate());
                    bundle.putString("group_currentUser", String.valueOf(group.getcurrent_user()));
                    bundle.putString("group_maxUser", String.valueOf(group.getUser_max_count()));
                    bundle.putString("group_planTime", String.valueOf(group.getPlanTime() / 60));
                    bundle.putString("group_dayCycle", group.getDayCycle());
                    bundle.putString("group_announce", group.getContent());
                    bundle.putString("userName", userName);
                    bundle.putString("userImageURL", userImageURL);

                    intent.putExtras(bundle);

                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mGroups.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView group_title;
        public ImageView group_profile_image;
        public TextView group_planTime;
        public TextView group_dayCycle;
        public TextView group_current_user;
        public TextView group_maxCount;
        public TextView group_join;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            group_title = itemView.findViewById(R.id.tv_groupList_title);
            group_profile_image = itemView.findViewById(R.id.groupList_profile_image);
            group_planTime = itemView.findViewById(R.id.tv_groupList_hashTime);
            group_dayCycle = itemView.findViewById(R.id.tv_groupList_hashCycle);
            group_current_user = itemView.findViewById(R.id.tv_groupList_currentUser);
            group_maxCount = itemView.findViewById(R.id.tv_groupList_maxCount);
            group_join = itemView.findViewById(R.id.tv_group_join);
        }
    }

}
