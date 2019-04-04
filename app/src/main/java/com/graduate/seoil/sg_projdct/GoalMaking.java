package com.graduate.seoil.sg_projdct;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.graduate.seoil.sg_projdct.Model.Goal;

public class GoalMaking extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private Button btnCreate;
    private EditText etTitle, etContent;

    FirebaseUser fuser;

    private String username;
    private String userImageURL;

    String goalname;

    String goaltext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_making);
        Intent myintent = getIntent();


        etTitle = (EditText) findViewById(R.id.new_note_title);
        etContent = (EditText)findViewById(R.id.new_note_content);
        btnCreate = (Button) findViewById(R.id.new_note_btn);
        firebaseDatabase = FirebaseDatabase.getInstance();


        databaseReference = firebaseDatabase.getReference("Goal");
        fuser = FirebaseAuth.getInstance().getCurrentUser();




        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    goalname = etTitle.getText().toString();
                    goaltext = etContent.getText().toString();

                    if (TextUtils.isEmpty(goalname)) {
                        Toast.makeText(getApplicationContext(), "목표 제목을 입력 하세요.", Toast.LENGTH_SHORT).show();
                    } else {
                        Goal goal = new Goal(databaseReference.push().getKey(), goalname, goaltext);
                        databaseReference.child(goalname).setValue(goal);

                        Toast.makeText(getApplicationContext(), "추가 되었습니다.", Toast.LENGTH_SHORT).show();


                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "" + e, Toast.LENGTH_SHORT).show();
                }
                finish();
            }

        });





    }

}