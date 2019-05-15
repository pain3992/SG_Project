package com.graduate.seoil.sg_projdct.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.graduate.seoil.sg_projdct.Model.GroupUserList;
import com.graduate.seoil.sg_projdct.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by baejanghun on 30/03/2019.
 */
public class GroupInformationInnerAdapter extends RecyclerView.Adapter<GroupInformationInnerAdapter.ViewHolder> {
    private static final String TAG = "GroupInformationAdapter";

    // vars
    private List<GroupUserList> groupUserLists;
    private Context mContext;

    public GroupInformationInnerAdapter(Context mContext, List<GroupUserList> groupUserLists) {
        this.mContext = mContext;
        this.groupUserLists = groupUserLists;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView image;
        TextView name, registDate, level;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.preview_url);
            name = itemView.findViewById(R.id.preview_name);
            registDate = itemView.findViewById(R.id.groupInner_item_registDate);
            level = itemView.findViewById(R.id.groupInner_item_level);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d(TAG, "onCreateViewHolder : called");
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.group_inner_preview_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder : called");
        final GroupUserList groupUserList = groupUserLists.get(i);

        long regTime = groupUserList.getRegistDate();
        long curTime = System.currentTimeMillis();
        long diffTime = (curTime - regTime) / 1000;

        Log.d(TAG, "groupUserList's registDate : " + groupUserList.getRegistDate());

        viewHolder.name.setText(groupUserList.getUsername());
        if (diffTime < TIME_MAXIMUM.SEC) {
            viewHolder.registDate.setText("방금 전 가입");
        } else if ((diffTime /= TIME_MAXIMUM.SEC) < TIME_MAXIMUM.MIN) {
            viewHolder.registDate.setText("활동일 : " + diffTime + "분");
        } else if ((diffTime /= TIME_MAXIMUM.MIN) < TIME_MAXIMUM.HOUR) {
            viewHolder.registDate.setText("활동일 : " + diffTime + "시간");
        } else if ((diffTime /= TIME_MAXIMUM.HOUR) < TIME_MAXIMUM.DAY) {
            viewHolder.registDate.setText("활동일 : " + diffTime + "일");
        } else if ((diffTime /= TIME_MAXIMUM.DAY) < TIME_MAXIMUM.MONTH) {
            viewHolder.registDate.setText("활동일 : " + diffTime + "달");
        } else {
            viewHolder.registDate.setText("활동일 : " + diffTime + "년");
        }


        String level = groupUserList.getLevel();
        if (level.equals("admin"))
            viewHolder.level.setText("그룹장");
        else if (level.equals("manager"))
            viewHolder.level.setText("매니저");
        else
            viewHolder.level.setText("그룹원");
        if (groupUserList.getImageURL().equals("default")) {
            viewHolder.image.setImageResource(R.mipmap.ic_launcher);
        } else {
            Glide.with(mContext).load(groupUserList.getImageURL()).into(viewHolder.image);
        }
//        viewHolder.image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "onClick : clicked on an image");
//                Toast.makeText(mContext, "호냐", Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return groupUserLists.size();
    }

    private static class TIME_MAXIMUM {
        public static final int SEC = 60;
        public static final int MIN = 60;
        public static final int HOUR = 24;
        public static final int DAY = 30;
        public static final int MONTH = 12;
    }
}
