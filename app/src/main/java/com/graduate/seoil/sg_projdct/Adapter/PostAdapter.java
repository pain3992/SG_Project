package com.graduate.seoil.sg_projdct.Adapter;

import android.app.Activity;
import android.content.Context;
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
import com.graduate.seoil.sg_projdct.Model.Post;
import com.graduate.seoil.sg_projdct.Model.User;
import com.graduate.seoil.sg_projdct.R;

import java.util.List;

/**
 * Created by baejanghun on 03/04/2019.
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    public Context mContext;
    public List<Post> mPost;

    private FirebaseUser fuser;

    public PostAdapter(Context mContext, List<Post> mPost) {
        this.mContext = mContext;
        this.mPost = mPost;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.post_item, viewGroup, false);

        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        Post post = mPost.get(i);
        Glide.with(mContext).load(post.getPostimage()).into(viewHolder.post_image);
        viewHolder.post_date.setText(post.getregistDate());

        if (post.getDescription().equals("")) {
            viewHolder.description.setVisibility(View.GONE);
        } else {
            viewHolder.description.setVisibility(View.VISIBLE);
            viewHolder.description.setText(post.getDescription());
        }

        publisherInfo(viewHolder.image_profile, viewHolder.username, viewHolder.publisher, post.getPublisher());
    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image_profile, post_image, like, comment, save;
        public TextView username, likes, publisher, description, comments, post_date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image_profile = itemView.findViewById(R.id.post_image_profile);
            post_image = itemView.findViewById(R.id.post_image);
            comments = itemView.findViewById(R.id.post_comments);
            like = itemView.findViewById(R.id.post_like);
            comment = itemView.findViewById(R.id.post_comment);
            save = itemView.findViewById(R.id.post_save);
            username = itemView.findViewById(R.id.post_username);
            likes = itemView.findViewById(R.id.post_likes);
            publisher = itemView.findViewById(R.id.post_publisher);
            description = itemView.findViewById(R.id.post_description);
            post_date = itemView.findViewById(R.id.post_date);
        }
    }

    private void publisherInfo(final ImageView image_profile, final TextView username, final TextView publisher, String userid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Activity activity = (Activity) mContext;
                if (activity.isFinishing())
                    return;
                Glide.with(mContext).load(user.getImageURL()).into(image_profile); // TODO : 에러 발생 4/8 - you cannot start a load for a destroyed activity / 그룹 검색 할떄 발생
                username.setText(user.getUsername());
                publisher.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}