package com.graduate.seoil.sg_projdct.Adapter;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.graduate.seoil.sg_projdct.Fragments.GroupFragment;
import com.graduate.seoil.sg_projdct.Fragments.ProfileFragment;
import com.graduate.seoil.sg_projdct.Model.NotificationModel;
import com.graduate.seoil.sg_projdct.Model.Notifications;
import com.graduate.seoil.sg_projdct.Model.Post;
import com.graduate.seoil.sg_projdct.Model.User;
import com.graduate.seoil.sg_projdct.R;

import java.lang.ref.Reference;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
    private Context mcontext;
    private List<Notifications> mNotificcation;

    public NotificationsAdapter(Context context, List<Notifications> notification){
        mcontext = context;
        mNotificcation = notification;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mcontext).inflate(R.layout.notification_items,viewGroup,false);
        return new NotificationsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Notifications notification = mNotificcation.get(i);

        viewHolder.texts.setText(notification.getText());

        getUserInfo(viewHolder.image_profile,viewHolder.usernames, notification.getUserid());

//        if (notification.isIspost()) {
//            viewHolder.post_images.setVisibility(View.VISIBLE);
//            getPostImage(viewHolder.post_images, notification.getPostid());
//        } else {
//            viewHolder.post_images.setVisibility(View.GONE);
//        }
//        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (notification.isIspost()) {
//                    SharedPreferences.Editor editor = mcontext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
//                    editor.putString("postid", notification.getPostid());
//                    editor.apply();
//
//                    ((FragmentActivity)mcontext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                            new GroupFragment()).commit();
//                } else {
//                    SharedPreferences.Editor editor = mcontext.getSharedPreferences("PREFS", MODE_PRIVATE).edit();
//                    editor.putString("profileid", notification.getUserid());
//                    editor.apply();
//
//                    ((FragmentActivity)mcontext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
//                            new ProfileFragment()).commit();
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mNotificcation.size();
    }
    public  class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView image_profile, post_images;
        public TextView usernames, texts;
        public ViewHolder(@NonNull View itemView){
            super(itemView);

            image_profile = itemView.findViewById(R.id.image_profile);
            post_images = itemView.findViewById(R.id.post_images);
            usernames = itemView.findViewById(R.id.usernames);
            texts = itemView.findViewById(R.id.comments);
        }
    }
    private void getUserInfo(final ImageView imageView, final TextView usernames, String publisherid){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(publisherid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(mcontext).load(user.getImageURL()).into(imageView);
                usernames.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
//    private void getPostImage(final ImageView imageView, final String postid){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(grouptitle).child(postid);
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Post post = dataSnapshot.getValue(Post.class);
//                Glide.with(mcontext).load(post.getPostimage()).into(imageView);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
}
