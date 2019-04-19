package com.graduate.seoil.sg_projdct.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.graduate.seoil.sg_projdct.GroupNotificationInnerActivity;
import com.graduate.seoil.sg_projdct.Model.Group;
import com.graduate.seoil.sg_projdct.Model.GroupNotification;
import com.graduate.seoil.sg_projdct.R;

import java.util.List;

/**
 * Created by baejanghun on 12/04/2019.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<GroupNotification> groupNotifications;
    private Context mContext;
    private String group_title;

    public NotificationAdapter(Context mContext, List<GroupNotification> groupNotifications, String group_title) {
        this.mContext = mContext;
        this.groupNotifications = groupNotifications;
        this.group_title = group_title;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView content, date, writer;
        RelativeLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.notification_item_content);
            date = itemView.findViewById(R.id.notification_item_date);
            writer = itemView.findViewById(R.id.notification_item_writer);
            layout = itemView.findViewById(R.id.notification_layout);
        }
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.notification_item, viewGroup, false);
        return new NotificationAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final GroupNotification notifications = groupNotifications.get(i);

        long regTime = notifications.getRegistDate();
        long curTime = System.currentTimeMillis();
        long diffTime = (curTime - regTime) / 1000;

        viewHolder.content.setText(notifications.getContent());
        viewHolder.writer.setText(notifications.getWriter());
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

        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, GroupNotificationInnerActivity.class);
                intent.putExtra("group_title", group_title);
                intent.putExtra("notyid", notifications.getNoty_id());
                intent.putExtra("writer_id", notifications.getWriter_id());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
//    if (groupNotifications.size() < 3) {
//            return groupNotifications.size();
//        } else {
//            return 2;
//        }
//    }
        return groupNotifications.size();
    }

    private static class TIME_MAXIMUM {
        public static final int SEC = 60;
        public static final int MIN = 60;
        public static final int HOUR = 24;
        public static final int DAY = 30;
        public static final int MONTH = 12;
    }
}
