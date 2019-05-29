package com.graduate.seoil.sg_projdct;

import android.content.Intent;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.graduate.seoil.sg_projdct.Adapter.GroupInformationAdapter;
import com.graduate.seoil.sg_projdct.Adapter.MessageAdapter;
import com.graduate.seoil.sg_projdct.Adapter.MessageUserListAdapter;
import com.graduate.seoil.sg_projdct.Fragments.APIService;
import com.graduate.seoil.sg_projdct.Model.Chat;
import com.graduate.seoil.sg_projdct.Model.GroupUserList;
import com.graduate.seoil.sg_projdct.Model.User;
import com.graduate.seoil.sg_projdct.Notification.Client;
import com.graduate.seoil.sg_projdct.Notification.Data;
import com.graduate.seoil.sg_projdct.Notification.MyResponse;
import com.graduate.seoil.sg_projdct.Notification.Sender;
import com.graduate.seoil.sg_projdct.Notification.Token;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username, toolbar_title;

    FirebaseUser fuser;
    DatabaseReference reference;

    ImageButton btn_send;
    ImageView back_button;
    EditText text_send;

    MessageAdapter messageAdapter;
    MessageUserListAdapter messageUserListAdapter;
    List<Chat> mchat;
    List<GroupUserList> mUserList;

    RecyclerView recyclerView, recyclerView_userList;

    Intent intent;

    ValueEventListener seenListener;

    String userid, group_title, userImageURL;

    APIService apiService;

    boolean notify = false;

    String str_userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        Toolbar toolbar = findViewById(R.id.message_toolbar);
        back_button = findViewById(R.id.message_backButton);
        toolbar_title = findViewById(R.id.message_title);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        str_userName =  IndexActivity.spref.getString("str_userName", "default");

        recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send= findViewById(R.id.text_send);

        intent = getIntent();
        group_title = intent.getStringExtra("group_title");
        userid = intent.getStringExtra("userid");
        userImageURL = intent.getStringExtra("userImageURL");

        toolbar_title.setText(group_title);

        fuser = FirebaseAuth.getInstance().getCurrentUser();


        // mUserList : 그룹 유저들 불러오기.
//        reference = FirebaseDatabase.getInstance().getReference("Group").child(group_title).child("userList");
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (getApplicationContext() == null) return;
//
//                mUserList.clear();
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    GroupUserList userList = snapshot.getValue(GroupUserList.class);
//                    if (!userList.getId().equals(fuser.getUid())) { // 자기 자신은 채팅유저에 안뜨게
//                        mUserList.add(userList);
//                    }
//                }
//                messageUserListAdapter.notifyDataSetChanged();
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });


//        recyclerView_userList = findViewById(R.id.rv_message_userList);
//        recyclerView_userList.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        recyclerView_userList.setLayoutManager(linearLayoutManager);
//        mUserList = new ArrayList<>();
//        messageUserListAdapter = new MessageUserListAdapter(this, mUserList);
//        recyclerView_userList.setAdapter(messageUserListAdapter);

        // 그룹 채팅 일어오기.
        readMessage();


//        reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                User user = dataSnapshot.getValue(User.class);
//                username.setText(user.getUsername());
//
//                if (user.getImageURL().equals("default")) {
//                    profile_image.setImageResource(R.mipmap.ic_launcher);
//                } else {
//                    Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile_image);
//                }
////                readMessage(fuser.getUid(), mUserList);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String msg = text_send.getText().toString();
                if (!msg.equals("")) {
                    sendMessage(fuser.getUid(), msg);
                } else {
                    Toast.makeText(MessageActivity.this, "You can't send empty message", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });

//        seenMessage(userid);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void sendMessage(String sender, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        Date today = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("a hh:mm");

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("message", message);
        hashMap.put("isseen", false);
        hashMap.put("sender_imageUrl", userImageURL);
        hashMap.put("sender_name", str_userName);
        hashMap.put("send_date", simpleDateFormat.format(today));

        // 채팅 꽂아버리기
        reference.child("Chats").child(group_title).push().setValue(hashMap);

//        final DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
//                .child(group_title)
//                .child(fuser.getUid());
//
//        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (!dataSnapshot.exists()) {
//                    chatRef.child("id").setValue(receiver);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        final String msg = message;

        // 메세지 알림
//        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                User user = dataSnapshot.getValue(User.class);
//                if (notify) {
//                    sendNotification(receiver, user.getUsername(), msg);
//                }
//                notify = false;
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

//    private void seenMessage(final String userid) {
//        reference = FirebaseDatabase.getInstance().getReference("Chats");
//        seenListener = reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Chat chat = snapshot.getValue(Chat.class);
//                    if (chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid)) {
//                        HashMap<String, Object> hashMap = new HashMap<>();
//                        hashMap.put("isseen", true);
//                        snapshot.getRef().updateChildren(hashMap);
//                    }
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
    private void readMessage() {
        mchat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats").child(group_title);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);
                    assert chat != null;
                    mchat.add(chat);

                }
                messageAdapter = new MessageAdapter(MessageActivity.this, mchat);
                recyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

//    private void sendNotification(String receiver, final String username, final String message) {
//        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
//        Query query = tokens.orderByKey().equalTo(receiver);
//        query.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Token token = snapshot.getValue(Token.class);
//
//                    Data data = new Data(fuser.getUid(), R.mipmap.ic_launcher, username+": " + message, "New Message",
//                            userid);
//
//                    Sender sender = new Sender(data, token.getToken());
//
//                    apiService.sendNotification(sender)
//                            .enqueue(new Callback<MyResponse>() {
//                                @Override
//                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
//                                    if (response.code() == 200) {
//                                        assert response.body() != null;
//                                        if (response.body().success != 1) {
//                                            Toast.makeText(MessageActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                                }
//
//                                @Override
//                                public void onFailure(Call<MyResponse> call, Throwable t) {
//
//                                }
//                            });
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void status(String status) {
        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}
