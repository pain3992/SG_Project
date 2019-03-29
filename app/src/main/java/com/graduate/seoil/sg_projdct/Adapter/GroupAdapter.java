package com.graduate.seoil.sg_projdct.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.graduate.seoil.sg_projdct.ChatActivity;
import com.graduate.seoil.sg_projdct.Model.Group;
import com.graduate.seoil.sg_projdct.R;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by baejanghun on 28/03/2019.
 */
public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder> {
    private Context mContext;
    private List<Group> mGroups;

    public GroupAdapter(Context mContext, List<Group> mGroups) {
        this.mGroups = mGroups;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.group_item, viewGroup, false);
        return new GroupAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Group group = mGroups.get(i);
        viewHolder.group_title.setText(group.getTitle());
        viewHolder.group_planTime.setText(String.valueOf(group.getPlanTime()));
        viewHolder.group_dayCycle.setText(group.getDayCycle());
        viewHolder.group_minCount.setText(String.valueOf(group.getUser_min_count()));
        viewHolder.group_maxCount.setText(String.valueOf(group.getUesr_max_count()));

        if (group.getImageURL().equals("default")) {
            viewHolder.group_profile_image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(group.getImageURL()).into(viewHolder.group_profile_image);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ChatActivity.class);
                mContext.startActivity(intent);
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
        public TextView group_minCount;
        public TextView group_maxCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            group_title = itemView.findViewById(R.id.tv_groupList_title);
            group_profile_image = itemView.findViewById(R.id.groupList_profile_image);
            group_planTime = itemView.findViewById(R.id.tv_groupList_hashTime);
            group_dayCycle = itemView.findViewById(R.id.tv_groupList_hashCycle);
            group_minCount = itemView.findViewById(R.id.tv_groupList_minCount);
            group_maxCount = itemView.findViewById(R.id.tv_groupList_maxCount);
        }
    }

}
