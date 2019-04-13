package com.graduate.seoil.sg_projdct.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    public NotificationAdapter(Context mContext, List<GroupNotification> groupNotifications) {
        this.mContext = mContext;
        this.groupNotifications = groupNotifications;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView content, date, writer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.notification_item_content);
            date = itemView.findViewById(R.id.notification_item_date);
            writer = itemView.findViewById(R.id.notification_item_writer);
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
        GroupNotification notifications = groupNotifications.get(i);
        System.out.println("postshot --> " + notifications);
        viewHolder.content.setText(notifications.getContent());
        viewHolder.writer.setText(notifications.getWriter());
        viewHolder.date.setText(notifications.getRegistDate());
    }

    @Override
    public int getItemCount() {
    if (groupNotifications.size() < 3) {
            return groupNotifications.size();
        } else {
            return 2;
        }
    }
}
