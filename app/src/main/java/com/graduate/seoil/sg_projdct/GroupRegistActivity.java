package com.graduate.seoil.sg_projdct;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

public class GroupRegistActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_group:
                    return true;
                case R.id.navigation_chart:
                    Toast.makeText(getApplicationContext(), "통계페이지", Toast.LENGTH_SHORT);
                    return true;
                case R.id.navigation_home:
                    Intent intent = new Intent(GroupRegistActivity.this, IndexActivity.class);
                    startActivity(intent);
                    return true;
                case R.id.navigation_notifications:
                    Toast.makeText(getApplicationContext(), "알림", Toast.LENGTH_SHORT);
                    return true;
                case R.id.navigation_setting:
                    Toast.makeText(getApplicationContext(), "세팅", Toast.LENGTH_SHORT);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupregister);
    }
}
