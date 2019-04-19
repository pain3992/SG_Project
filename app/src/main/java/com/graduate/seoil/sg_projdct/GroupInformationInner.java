package com.graduate.seoil.sg_projdct;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.UploadTask;
import com.graduate.seoil.sg_projdct.Adapter.GroupInformationAdapter;
import com.graduate.seoil.sg_projdct.Adapter.GroupInformationInnerAdapter;
import com.graduate.seoil.sg_projdct.Adapter.NotificationAdapter;
import com.graduate.seoil.sg_projdct.Adapter.PostAdapter;
import com.graduate.seoil.sg_projdct.Adapter.UserAdapter;
import com.graduate.seoil.sg_projdct.Model.Group;
import com.graduate.seoil.sg_projdct.Model.GroupNotification;
import com.graduate.seoil.sg_projdct.Model.GroupUserList;
import com.graduate.seoil.sg_projdct.Model.User;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class GroupInformationInner extends AppCompatActivity {
    Uri imageUri;
    String myUri = "";
    StorageTask uploadTask;
    StorageReference storageReference;

    FirebaseUser fuser;
    DatabaseReference reference;

    ImageView group_profile, backButton;
    TextView title, usercount, registDate, category, time, days, notification_more;
    String group_title, admin_id;
    RecyclerView notifications, recyclerView_userList;

    List<GroupNotification> mNotifications;
    List<GroupUserList> mUser;
    NotificationAdapter notiAdapter;
    GroupInformationInnerAdapter groupInformationAdapter;

    int noty_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_information_inner);

        Intent intent = getIntent();
        group_title = intent.getStringExtra("group_title");

        fuser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        group_profile = findViewById(R.id.setting_account_profile_image);
        title = findViewById(R.id.groupInner_title);
        usercount = findViewById(R.id.groupInner_usercount);
        registDate = findViewById(R.id.groupInner_registDate);
        backButton = findViewById(R.id.groupInner_backButton);
        category = findViewById(R.id.groupInner_category);
        time = findViewById(R.id.groupInner_times);
        days = findViewById(R.id.groupInner_days);
        notification_more = findViewById(R.id.groupInner_notification_more);

        notifications = findViewById(R.id.groupInner_notification);
        notifications.setHasFixedSize(true);
        notifications.setLayoutManager(new LinearLayoutManager(GroupInformationInner.this));
        mNotifications = new ArrayList<>();
        notiAdapter = new NotificationAdapter(getApplicationContext(), mNotifications, group_title);
        notifications.setAdapter(notiAdapter);

        recyclerView_userList = findViewById(R.id.groupInner_rv_userList);
        recyclerView_userList.setHasFixedSize(true);
        recyclerView_userList.setLayoutManager(new LinearLayoutManager(GroupInformationInner.this));
        mUser = new ArrayList<>();
        groupInformationAdapter = new GroupInformationInnerAdapter(getApplicationContext(), mUser);
        recyclerView_userList.setAdapter(groupInformationAdapter);


        reference = FirebaseDatabase.getInstance().getReference("Group").child(group_title);
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Group group = dataSnapshot.getValue(Group.class);
                Glide.with(getApplicationContext()).load(group.getImageURL()).into(group_profile);
                group_profile.setColorFilter(Color.parseColor("#BDBDBD"), PorterDuff.Mode.MULTIPLY);

                title.setText(group.getTitle());
                usercount.setText("그룹 인원 " + String.valueOf(group.getcurrent_user()) + "명");
                registDate.setText("|  개설일 " + group.getRegistDate());
                category.setText("#" + group.getGoal());
                time.setText("#" + String.valueOf(group.getPlanTime() / 60) + "시간");
                days.setText("#" + group.getDayCycle());
                admin_id = group.getAdminName();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // 공지사항 긁어오기.
//        reference = FirebaseDatabase.getInstance().getReference("Group").child(group_title).child("notifications");
        Query query = FirebaseDatabase.getInstance().getReference("Group").child(group_title).child("notifications").orderByChild("registDate")
                .limitToLast(2);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                noty_count = (int) dataSnapshot.getChildrenCount();
                mNotifications.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GroupNotification group = snapshot.getValue(GroupNotification.class);
                    mNotifications.add(group);
                }
                Collections.reverse(mNotifications);
                notiAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // 유저 긁어오기
        reference = FirebaseDatabase.getInstance().getReference("Group").child(group_title).child("userList"); // TODO : 4/2 방금에러 패스스트링
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GroupUserList userList = snapshot.getValue(GroupUserList.class);
                    mUser.add(userList);
                }
                notiAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // 전체 공지사항 보기
        notification_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupInformationInner.this, GroupNotificationActivity.class);
                intent.putExtra("group_title", group_title);
                intent.putExtra("noty_count", noty_count);
                startActivity(intent);
            }
        });

        group_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fuser.getUid().equals(admin_id)) {
                    CropImage.activity()
                            .setAspectRatio(2, 1)
                            .start(GroupInformationInner.this);
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImage() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Uploading");
        pd.show();

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
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
                        String mUri = downloadUri.toString();

                        reference = FirebaseDatabase.getInstance().getReference("Group").child(group_title);
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", mUri);
                        reference.updateChildren(map);

                        pd.dismiss();
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "No Image selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();

//            group_profile.setImageURI(imageUri);

            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(getApplicationContext(), "Upload in progess", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        } else {
            Toast.makeText(this, "이미지 안올림.", Toast.LENGTH_SHORT).show();
        }
    }
}
