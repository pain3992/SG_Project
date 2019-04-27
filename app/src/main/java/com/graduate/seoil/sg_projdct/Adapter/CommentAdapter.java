package com.graduate.seoil.sg_projdct.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.graduate.seoil.sg_projdct.GroupActivity;
import com.graduate.seoil.sg_projdct.Model.Comment;
import com.graduate.seoil.sg_projdct.Model.User;
import com.graduate.seoil.sg_projdct.R;

import java.util.List;

/**
 * Created by baejanghun on 13/04/2019.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private Context mContext;
    private List<Comment> mComment;

    private FirebaseUser fuser;

    public CommentAdapter(Context mContext, List<Comment> mComment) {
        this.mContext = mContext;
        this.mComment = mComment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_item, viewGroup, false);
        return new CommentAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        final Comment comment = mComment.get(i);
        long regTime = comment.getRegistDate();
        long curTime = System.currentTimeMillis();
        long diffTime = (curTime - regTime) / 1000;

        viewHolder.comment.setText(comment.getComment());

        getUserInfo(viewHolder.image_profile, viewHolder.username, comment.getPublisher());
        if (diffTime < TIME_MAXIMUM.SEC) {
            viewHolder.registDate.setText("방금 전");
        } else if ((diffTime /= TIME_MAXIMUM.SEC) < TIME_MAXIMUM.MIN) {
            viewHolder.registDate.setText(diffTime + "분 전");
        } else if ((diffTime /= TIME_MAXIMUM.MIN) < TIME_MAXIMUM.HOUR) {
            viewHolder.registDate.setText(diffTime + "시간 전");
        } else if ((diffTime /= TIME_MAXIMUM.HOUR) < TIME_MAXIMUM.DAY) {
            viewHolder.registDate.setText(diffTime + "일 전");
        } else if ((diffTime /= TIME_MAXIMUM.DAY) < TIME_MAXIMUM.MONTH) {
            viewHolder.registDate.setText(diffTime + "달 전");
        } else {
            viewHolder.registDate.setText(diffTime + "년 전");
        }

//        viewHolder.comment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext, GroupActivity.class);
//                intent.putExtra("publisherid", comment.getPublisher());
//                mContext.startActivity(intent);
//            }
//        });
//
//        viewHolder.image_profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(mContext, GroupActivity.class);
//                intent.putExtra("publisherid", comment.getPublisher());
//                mContext.startActivity(intent);
//            }
//        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image_profile;

        public TextView username, comment, registDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.comment_item_image_profile);
            username = itemView.findViewById(R.id.comment_item_username);
            comment = itemView.findViewById(R.id.comment_item_comment);
            registDate = itemView.findViewById(R.id.comment_item_registerDate);
        }

    }
    private void getUserInfo(final ImageView imageView, final TextView username, final String publisherid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(publisherid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Activity activity = (Activity) mContext;
                if (activity.isFinishing())
                    return;
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                Glide.with(mContext).load(user.getImageURL()).into(imageView);
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mComment.size();
    }

    private static class TIME_MAXIMUM {
        public static final int SEC = 60;
        public static final int MIN = 60;
        public static final int HOUR = 24;
        public static final int DAY = 30;
        public static final int MONTH = 12;
    }
}
