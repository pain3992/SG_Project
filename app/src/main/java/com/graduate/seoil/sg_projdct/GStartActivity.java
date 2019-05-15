package com.graduate.seoil.sg_projdct;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.snapshot.Index;

public class GStartActivity extends AppCompatActivity {

    Button login, register;

    FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();
//        FirebaseAuth.getInstance().signOut();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        System.out.println("fuser : " + firebaseUser);

        // Check if user is null
        if (firebaseUser != null) {
            System.out.println("==========================");
            Intent intent = new Intent(GStartActivity.this, IndexActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gstart);

        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GStartActivity.this, LoginActivity.class));
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GStartActivity.this, RegisterActivity.class));
            }
        });
    }
}
