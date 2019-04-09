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
public class MessageUserListAdapter extends RecyclerView.Adapter<MessageUserListAdapter.ViewHolder> {
    private static final String TAG = "GroupInformationAdapter";

    // vars
    private List<GroupUserList> groupUserLists;
    private Context mContext;

    public MessageUserListAdapter(Context mContext, List<GroupUserList> groupUserLists) {
        this.mContext = mContext;
        this.groupUserLists = groupUserLists;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView image;
        TextView name;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.preview_url);
            name = itemView.findViewById(R.id.preview_name);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d(TAG, "onCreateViewHolder : called");
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_preview_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {
        Log.d(TAG, "onBindViewHolder : called");
        final GroupUserList groupUserList = groupUserLists.get(i);

        viewHolder.name.setText(groupUserList.getUsername());
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
}
