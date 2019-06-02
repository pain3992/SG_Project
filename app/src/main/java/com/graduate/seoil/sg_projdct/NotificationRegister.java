package com.graduate.seoil.sg_projdct;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.graduate.seoil.sg_projdct.Fragments.APIService;
import com.graduate.seoil.sg_projdct.Model.User;
import com.graduate.seoil.sg_projdct.Notification.Client;
import com.graduate.seoil.sg_projdct.Notification.Data;
import com.graduate.seoil.sg_projdct.Notification.MyResponse;
import com.graduate.seoil.sg_projdct.Notification.Sender;
import com.graduate.seoil.sg_projdct.Notification.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationRegister extends AppCompatActivity {
    EditText content;
    TextView add;
    ImageView backButton;
    String group_title;
    String user_name;
    FirebaseUser fuser;
    APIService apiService;
    boolean notify = false;
    DatabaseReference reference,reference2;
    private List<String> mUid;
    String userid;
    private String str_userImageURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_register);

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        str_userImageURL = IndexActivity.spref.getString("str_userImageURL", "default");
        mUid = new ArrayList<>();
        fuser = FirebaseAuth.getInstance().getCurrentUser();

        Intent intent = getIntent();
        group_title = intent.getStringExtra("group_title");
        userid = intent.getStringExtra("userid");
        add = findViewById(R.id.notification_register_add);
        backButton = findViewById(R.id.notification_register_backButton);
        content = findViewById(R.id.notification_register_content);

        reference2 = FirebaseDatabase.getInstance().getReference("Group").child(group_title).child("userList");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    mUid.add(childSnapshot.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reference2.addListenerForSingleValueEvent(eventListener);

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                user_name = user.getUsername();
                String con = content.getText().toString();
                if(notify){
                    for (int i = 0; i < mUid.size(); i++) {
                        System.out.println("mUid.get(i) : " + mUid.get(i));
                        sendNotification(mUid.get(i),user_name,con);
                        reference = FirebaseDatabase.getInstance().getReference("PushNotifications");
                        HashMap<String, Object> hashMap_push = new HashMap<>();
                        hashMap_push.put("uid",mUid.get(i));
                        hashMap_push.put("title", user_name + " 님이 공지를 작성하였습니다.");
                        hashMap_push.put("content", con);
                        hashMap_push.put("timestamp", System.currentTimeMillis());
                        hashMap_push.put("sender_name", user_name);
                        hashMap_push.put("sender_url", str_userImageURL);
                        hashMap_push.put("category", "공지");
                        reference.child(mUid.get(i)).push().updateChildren(hashMap_push);
                    }
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference = FirebaseDatabase.getInstance().getReference("Group").child(group_title).child("notifications");

                String noty_id = reference.push().getKey();
                long curtime = System.currentTimeMillis();

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("content", content.getText().toString());
                hashMap.put("noty_id", noty_id);
                hashMap.put("registDate", curtime);
                hashMap.put("writer", user_name);
                hashMap.put("writer_id", fuser.getUid());

                reference.child(noty_id).setValue(hashMap);

                GroupNotificationActivity.no_noty.setVisibility(View.GONE);
                GroupNotificationActivity.noty_add.setVisibility(View.GONE);
                GroupNotificationActivity.recyclerView.setVisibility(View.VISIBLE);
                finish();
                notify = true;
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void sendNotification(String receiver, final String userName, final String content){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(),R.drawable.logo, content,userName+"님이 공지를 작성 했습니다.",userid);

                    Sender sender = new Sender(data,token.getToken());

                    apiService.sendNotification(sender)
                            .enqueue(new Callback<MyResponse>() {
                                @Override
                                public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                    if(response.code()==200){
                                        if(response.body().success!=1){
                                            //Toast.makeText(PostAddActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyResponse> call, Throwable t) {

                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
