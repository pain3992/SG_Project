package com.graduate.seoil.sg_projdct;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.gson.Gson;
import com.graduate.seoil.sg_projdct.Adapter.MessageAdapter;
import com.graduate.seoil.sg_projdct.Adapter.MessageUserListAdapter;
import com.graduate.seoil.sg_projdct.Fragments.APIService;
import com.graduate.seoil.sg_projdct.Model.Chat;
import com.graduate.seoil.sg_projdct.Model.Group;
import com.graduate.seoil.sg_projdct.Model.GroupUserList;
import com.graduate.seoil.sg_projdct.Model.NotificationModel;
import com.graduate.seoil.sg_projdct.Model.Post;
import com.graduate.seoil.sg_projdct.Model.User;
import com.graduate.seoil.sg_projdct.Notification.Client;
import com.graduate.seoil.sg_projdct.Notification.Data;
import com.graduate.seoil.sg_projdct.Notification.MyResponse;
import com.graduate.seoil.sg_projdct.Notification.Sender;
import com.graduate.seoil.sg_projdct.Notification.Token;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostAddActivity extends AppCompatActivity {
    MessageAdapter messageAdapter;
    MessageUserListAdapter messageUserListAdapter;
    List<Chat> mchat;
    List<Post> postedd;
    List<GroupUserList> mUserList;

    private List<String> mUid;
    DatabaseReference reference,reference2;
    Uri imageUri;
    String myUri = "";
    StorageTask uploadTask;
    StorageReference storageReference;

    private static final int IMAGE_REQUEST = 1;

    ImageView close, image_added;
    TextView post;
    EditText description;

    String group_title, userName, userImageURL, userid, receiver, receivers;

    FirebaseUser fuser;

    APIService apiService;


    boolean notify = false;

    private Post destinationPostModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_add);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        Intent intent = getIntent();

        group_title = intent.getStringExtra("group_title");
        System.out.println("userid : " + userid);
        userid = intent.getStringExtra("userid");
        userImageURL = intent.getStringExtra("userImageURL");
        userName =  IndexActivity.spref.getString("str_userName", "default");

        fuser = FirebaseAuth.getInstance().getCurrentUser();


        close = findViewById(R.id.postAdd_close);
        image_added = findViewById(R.id.postAdd_image_added);
        post = findViewById(R.id.postAdd_post);
        description = findViewById(R.id.post_description);

        storageReference = FirebaseStorage.getInstance().getReference("posts");


        mUid = new ArrayList<>();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(PostAddActivity.this, GroupActivity.class);
//                intent.putExtra("group_title ", group_title);
//                intent.putExtra("userName", userName);
//                intent.putExtra("userImageURL", userImageURL);
//                startActivity(intent);
                finish();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                uploadImage();
                notify = true;
            }
        });

        CropImage.activity()
                .setAspectRatio(2, 1)
                .start(PostAddActivity.this);

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

        receiver = FirebaseAuth.getInstance().getCurrentUser().getUid();

        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                String con = description.getText().toString();
                if(notify){
                    for (int i = 0; i < mUid.size(); i++) {
                        System.out.println("mUid.get(i) : " + mUid.get(i));
                        sendNotification(mUid.get(i),user.getUsername(),con);
                        reference = FirebaseDatabase.getInstance().getReference("PushNotifications");
                        HashMap<String, Object> hashMap_push = new HashMap<>();
                        hashMap_push.put("uid",mUid.get(i));
                        hashMap_push.put("title", userName + " 님이 피드를 작성하였습니다.");
                        hashMap_push.put("content", description.getText().toString());
                        hashMap_push.put("timestamp", System.currentTimeMillis());
                        hashMap_push.put("sender_name", userName);
                        hashMap_push.put("sender_url", userImageURL);
                        hashMap_push.put("category", "피드");
                        reference.child(mUid.get(i)).push().updateChildren(hashMap_push);
                    }
                }
                notify = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }





    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
//        CropImage.activity()
//                .setAspectRatio(1, 1)
//                .start(AccountSetting.this);
    }

    private String getfileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("올라가는 중..");
        progressDialog.show();

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
            + "." + getfileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        myUri = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
                        String postid = reference.push().getKey();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("postid", postid);
                        hashMap.put("postimage", myUri);
                        hashMap.put("description", description.getText().toString());
                        hashMap.put("publisher", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        hashMap.put("registDate", System.currentTimeMillis());

//                        reference.child(group_title).child(fuser.getUid()).child(postid).setValue(hashMap);
                        reference.child(group_title).child(postid).setValue(hashMap);

                        progressDialog.dismiss();

//                        Intent intent = new Intent(PostAddActivity.this, GroupActivity.class);
//                        intent.putExtra("group_title", group_title);
//                        intent.putExtra("userName", userName);
//                        intent.putExtra("userImageURL", userImageURL);
//                        startActivity(intent);

                        finish();
                    } else {
                        Toast.makeText(PostAddActivity.this, "실패!", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostAddActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            } else {
            Toast.makeText(this, "이미지 선택이 안됨.", Toast.LENGTH_SHORT).show();
        }
    }
    private void sendNotification(String receiver, final String userName, final String content){
        DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
        Query query = tokens.orderByKey().equalTo(receiver);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Token token = snapshot.getValue(Token.class);
                    Data data = new Data(fuser.getUid(),R.drawable.logo, content,userName+"님이 피드를 작성 했습니다.",userid);

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
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

            image_added.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "이미지 안올림.", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(PostAddActivity.this, GroupActivity.class);
//            intent.putExtra("group_title", group_title);
//            intent.putExtra("userName", userName);
//            intent.putExtra("userImageURL", userImageURL);
//            startActivity(intent);
            finish();
        }
    }
}
