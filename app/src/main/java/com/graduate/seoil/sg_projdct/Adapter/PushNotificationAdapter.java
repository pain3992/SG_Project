package com.graduate.seoil.sg_projdct.Adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.graduate.seoil.sg_projdct.Model.PushNotification;
import com.graduate.seoil.sg_projdct.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PushNotificationAdapter extends RecyclerView.Adapter<PushNotificationAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<PushNotification> pushNotifications;

    public PushNotificationAdapter(Context mContext, ArrayList<PushNotification> pushNotifications) {
        this.mContext = mContext;
        this.pushNotifications = pushNotifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_push_notification, viewGroup, false);


        return new PushNotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PushNotificationAdapter.ViewHolder viewHolder, int i) {
        PushNotification push = pushNotifications.get(i);
        Glide.with(mContext).load(push.getSender_url()).into(viewHolder.circleImageView);

        long regTime = push.getTimestamp();
        long curTime = System.currentTimeMillis();
        long diffTime = (curTime - regTime) / 1000;
        if (diffTime < TIME_MAXIMUM.SEC) {
            viewHolder.date.setText("방금 전");
        } else if ((diffTime /= TIME_MAXIMUM.SEC) < TIME_MAXIMUM.MIN) {
            viewHolder.date.setText(diffTime + "분 전");
        } else if ((diffTime /= TIME_MAXIMUM.MIN) < TIME_MAXIMUM.HOUR) {
            viewHolder.date.setText(diffTime + "시간 전");
        } else if ((diffTime /= TIME_MAXIMUM.HOUR) < TIME_MAXIMUM.DAY) {
            viewHolder.date.setText(diffTime + "일 전");
        } else if ((diffTime /= TIME_MAXIMUM.DAY) < TIME_MAXIMUM.MONTH) {
            viewHolder.date.setText(diffTime + "달 전");
        } else {
            viewHolder.date.setText(diffTime + "년 전");
        }

        viewHolder.sender_name.setText(push.getSender_name());
        viewHolder.title.setText(push.getTitle());
        viewHolder.content.setText(push.getContent());

    }

    @Override
    public int getItemCount() {
        return pushNotifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView circleImageView;
        private TextView sender_name;
        private TextView title;
        private TextView content;
        private TextView date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            circleImageView = itemView.findViewById(R.id.item_push_url);
            sender_name = itemView.findViewById(R.id.item_push_sender_name);
            title = itemView.findViewById(R.id.item_push_title);
            content = itemView.findViewById(R.id.item_push_content);
            date = itemView.findViewById(R.id.item_push_date);
        }
    }

    private static class TIME_MAXIMUM {
        public static final int SEC = 60;
        public static final int MIN = 60;
        public static final int HOUR = 24;
        public static final int DAY = 30;
        public static final int MONTH = 12;
    }
}
