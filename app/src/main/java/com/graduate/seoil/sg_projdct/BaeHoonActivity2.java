package com.graduate.seoil.sg_projdct;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class BaeHoonActivity2 extends AppCompatActivity {

    FirebaseUser fuser;
    DatabaseReference reference;
    TextView title,text;
    String str_title;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bae_hoon2);

        Intent intent = getIntent();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        title = findViewById(R.id.goal_name);
        text = findViewById(R.id.goal_content);

        title.setText(intent.getStringExtra("goal_title"));
        text.setText(intent.getStringExtra("goal_text"));


    }
}
