package com.graduate.seoil.sg_projdct;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
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
import com.google.firebase.database.snapshot.Index;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.graduate.seoil.sg_projdct.Adapter.ExpandableListAdapter;
import com.graduate.seoil.sg_projdct.Fragments.GroupListFragment;
import com.graduate.seoil.sg_projdct.Fragments.TimePickerFragment;
import com.graduate.seoil.sg_projdct.Model.Group;
import com.graduate.seoil.sg_projdct.Model.User;
import com.theartofdev.edmodo.cropper.CropImage;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class GroupRegistActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, NumberPicker.OnValueChangeListener {
    public final int GROUP_REGISTER_ACTIVITY = 0;
    FirebaseAuth auth;
    DatabaseReference reference;
    FirebaseDatabase firebaseDatabase;
    FirebaseUser fuser;

    Uri imageUri;
    String myUri = "";
    StorageTask uploadTask;
    StorageReference storageReference;

    private String username;
    private String userImageURL;

    TextView et_planTime, groupInner_title, tv_profile_added, group_invite_user_count, et_maxCount, group_regist, category;
    EditText et_title, et_minCount, et_announce;
    ImageView profile_image, iv_profile_added, back_button;
    CheckBox[] chkBoxs;
    Integer[] chkBoxIds = {R.id.ckbox_mon, R.id.ckbox_tue, R.id.ckbox_wed, R.id.ckbox_thu, R.id.ckbox_fri, R.id.ckbox_sat, R.id.ckbox_sun};

    RelativeLayout group_category;

    private String mUri;

    int categoryCountTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupregister);

        Intent intent = getIntent();

        firebaseDatabase = FirebaseDatabase.getInstance();
        reference = firebaseDatabase.getReference("Group");
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        tv_profile_added = findViewById(R.id.tv_profile_added);
        group_regist = findViewById(R.id.groupRegister_create);

        et_title = findViewById(R.id.tv_title); // 타이틀
        et_announce = findViewById(R.id.et_group_announce); // 그룹 소개
        et_planTime = findViewById(R.id.et_group_regist_plan_time); // 목표 시간
        profile_image = findViewById(R.id.iv_group_regist); // 프로필 이미지
        iv_profile_added = findViewById(R.id.iv_profile_added); // 프로필 이미지 적용후
        group_category = findViewById(R.id.group_category); // 카테고리 RelativeLayout
        category = findViewById(R.id.et_category); // 카테고리
        group_invite_user_count = findViewById(R.id.group_invite_user_count); // 모집 인원
        back_button = findViewById(R.id.groupRegister_backButton);

        chkBoxs = new CheckBox[chkBoxIds.length];

        reference = FirebaseDatabase.getInstance().getReference("CategoryCountt");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                categoryCountTotal = (int)dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        username = IndexActivity.spref.getString("str_userName", "default");
        userImageURL = IndexActivity.spref.getString("str_userImageURL", "default");

        Log.d("이미지URL 못불러옴", userImageURL); // TODO : Error -> username, userimageURL 읽기전에 프래그먼트 이동시에.

        et_planTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(2, 1)
                        .start(GroupRegistActivity.this);
            }
        });

        iv_profile_added.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(2, 1)
                        .start(GroupRegistActivity.this);
            }
        });

        group_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupRegistActivity.this, CategoryActivity.class);
                startActivityForResult(intent, GROUP_REGISTER_ACTIVITY);
            }
        });


        group_invite_user_count.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerShow();
            }
        });

        group_regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_title.getText().equals("") || category.getText().toString().equals("") || et_planTime.getText().toString().equals("") || et_announce.getText().toString().equals("")) {
                    Toast.makeText(GroupRegistActivity.this, "항목을 모두 입력해주세요!", Toast.LENGTH_SHORT).show();
                } else {
                    String str_count = group_invite_user_count.getText().toString();
                    int index_count = str_count.indexOf("명");
                    long now = System.currentTimeMillis();
                    Date date = new Date(now);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                    String title = et_title.getText().toString();
                    String announce = et_announce.getText().toString();
                    String str_time = et_planTime.getText().toString();
                    String checked_days = "";
                    String getTime = sdf.format(date);
                    final String categorys = category.getText().toString();
                    int maxCount = Integer.parseInt(str_count.substring(0, index_count));

                    int index = str_time.indexOf(":");
                    int hour = Integer.parseInt(str_time.substring(0, index)) * 60;
                    int minute = Integer.parseInt(str_time.substring(index + 1));
                    int time = hour + minute;

                    for (int i = 0; i < chkBoxIds.length; i++) {
                        chkBoxs[i] = findViewById(chkBoxIds[i]);
                        if (chkBoxs[i].isChecked()) {
                            switch (i) {
                                case 0:
                                    checked_days = "월";
                                    break;
                                case 1:
                                    checked_days += "화";
                                    break;
                                case 2:
                                    checked_days += "수";
                                    break;
                                case 3:
                                    checked_days += "목";
                                    break;
                                case 4:
                                    checked_days += "금";
                                    break;
                                case 5:
                                    checked_days += "토";
                                    break;
                                case 6:
                                    checked_days += "일";
                                    break;
                            }
                        }
                    }
                    cntSetting(categorys);


                    // TODO : [3월 29일] 승연이가 그룹목표 넣으면 "default"를 바꿔줘야함.
                    // TODO : [3월 29일] 그룹은 하나 이상 못만들게 해야함.
                    // TODO : [3월 30일] 프로필 사진 변경시, 그룹의 유저리스트의 imageURL도 변경되어야 함.

                    HashMap<String, Object> userList = new HashMap<String, Object>();
                    HashMap<String, Object> userList_under = new HashMap<String, Object>();
                    userList_under.put("id", fuser.getUid());
                    userList_under.put("imageURL", userImageURL);
                    userList_under.put("level", "admin");
                    userList_under.put("username", username);
                    userList_under.put("registDate", System.currentTimeMillis());
                    userList.put(fuser.getUid(), userList_under);


                    Group group = new Group(title, announce, categorys, mUri, System.currentTimeMillis(), username, checked_days, time, 1, maxCount, userList);
                    reference = FirebaseDatabase.getInstance().getReference("Group");
                    reference.child(title).setValue(group);

                    // User테이블에 가입한 그룹 추가하기.
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid()).child("groupList");
                    HashMap<String, Object> groupList = new HashMap<>();
                    groupList.put("title", title);
                    groupList.put("registDate", System.currentTimeMillis());
                    reference.child(title).setValue(groupList);

                    // 그룹 가입시 기본 포스트 하나 들어가기
                    reference = FirebaseDatabase.getInstance().getReference("Posts");
                    String postid = reference.push().getKey();

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("postid", postid);
                    hashMap.put("postimage", "https://firebasestorage.googleapis.com/v0/b/sg-project-chat.appspot.com/o/posts%2F1555002256229.null?alt=media&token=dd479c7b-4d73-402e-8cb6-00b36660ed34");
                    hashMap.put("description", "서로간에 계획실천 인증사진을 올려보세요!");
                    hashMap.put("publisher", "JiQeRTHwbpSRSl7OVIc4hGx531l2");
                    hashMap.put("registDate", System.currentTimeMillis());

                    reference.child(title).child(postid).setValue(hashMap);




//                reference = FirebaseDatabase.getInstance().getReference("CategoryCount");
//                reference.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        boolean hasCategory = false;
//                        long curVal = 0;
//                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                            if (snapshot.getKey().equals(categorys)) {
//                                System.out.println("snapshot : " + snapshot.getKey() + ", " + snapshot.getValue());
//                                curVal = (long) snapshot.getValue();
//                                hasCategory = true;
//                                break;
//                            }
//                        }
//                        HashMap<String, Object> hashMap = new HashMap<>();
//                        if (!hasCategory)
//                            hashMap.put(categorys, 1);
//                        else
//                            hashMap.put(categorys, curVal + 1);
//                        reference.updateChildren(hashMap);
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });

                    IndexActivity.GROUP_COUNT += 1;
                    GroupListFragment.recyclerView.setVisibility(View.VISIBLE);
                    GroupListFragment.outsider_view.setVisibility(View.GONE);

                    Intent intent = new Intent(GroupRegistActivity.this, GroupActivity.class);
                    intent.putExtra("group_title", title);
                    intent.putExtra("userName", username);
                    intent.putExtra("userImageURL", userImageURL);
                    finish();
                    startActivity(intent);
                }
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
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

//        groupInner_title.setTextColor(Color.parseColor("#313F47"));
//        cardView_before.setVisibility(View.GONE);

        if (imageUri != null) {
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

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
                        mUri = downloadUri.toString();

//                        reference = FirebaseDatabase.getInstance().getReference("Group").child(et_title.getText().toString());
//                        HashMap<String, Object> map = new HashMap<>();
//                        map.put("imageURL", mUri);
//                        reference.updateChildren(map);

                        profile_image.setVisibility(View.GONE);
                        Glide.with(GroupRegistActivity.this).load(mUri).into(iv_profile_added);
                        iv_profile_added.setVisibility(View.VISIBLE);

//                        group_profile_image.setColorFilter(Color.parseColor("#BDBDBD"), PorterDuff.Mode.MULTIPLY);
//                        cardView_after.setVisibility(View.VISIBLE);
//                        groupInner_title.setVisibility(View.VISIBLE);
//                        groupInner_title.setTextColor(Color.parseColor("#FFFFFF"));

                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)tv_profile_added.getLayoutParams();
                        params.addRule(RelativeLayout.BELOW, R.id.iv_profile_added);

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

            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(getApplicationContext(), "Upload in progess", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        } else if (requestCode == GROUP_REGISTER_ACTIVITY) {
            if (resultCode != RESULT_OK) {
                category.setText("Data Error");
            }
            if (resultCode == RESULT_OK) {
                String result = data.getExtras().getString(CategoryActivity.RESULT_DATA);
                category.setText(result);
            }
        } else {
            Toast.makeText(this, "이미지 안올림.", Toast.LENGTH_SHORT).show();
        }


    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        TextView textView = findViewById(R.id.et_group_regist_plan_time);
        if (minute != 0)
            textView.setText(hourOfDay + ":" + minute);
        else
            textView.setText(hourOfDay + ":" + minute + "0");
    }

    public void timePickerShow()
    {
        final Dialog d = new Dialog(GroupRegistActivity.this);
        d.setTitle("NumberPicker");
        d.setContentView(R.layout.dialog);
        ImageView b1 = d.findViewById(R.id.button1);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(20);
        np.setMinValue(2);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                group_invite_user_count.setText(String.valueOf(np.getValue()) + "명");
                d.dismiss();
            }
        });
        d.show();


    }
    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

    }

    private void cntSetting(final String category) {
        Query query = FirebaseDatabase.getInstance().getReference("CategoryCountt").orderByChild("name").startAt(category).endAt(category + "\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reference = FirebaseDatabase.getInstance().getReference("CategoryCountt");
                final HashMap<String, Object> hashMap = new HashMap<>();
                if (dataSnapshot.getChildrenCount() == 0) {
                    hashMap.put("name", category);
                    hashMap.put("value", 1);
                    reference.child(String.valueOf(categoryCountTotal)).setValue(hashMap);
                } else {
                    System.out.println("dataSnapshot.key : " + dataSnapshot.getValue());
                    for (final DataSnapshot postShot : dataSnapshot.getChildren()) {
                        final String key = postShot.getKey();
                        DatabaseReference referenceVal = FirebaseDatabase.getInstance().getReference("CategoryCountt").child(key).child("value");
                        referenceVal.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                long val = (long)dataSnapshot.getValue() + 1;
                                hashMap.put("name", category);
                                hashMap.put("value", val);
                                reference.child(Objects.requireNonNull(key)).updateChildren(hashMap);
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
