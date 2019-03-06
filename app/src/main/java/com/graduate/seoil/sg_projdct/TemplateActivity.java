package com.graduate.seoil.sg_projdct;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class TemplateActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_group:
                    Toast.makeText(getApplicationContext(), "그룹페이지", Toast.LENGTH_SHORT);
                    return true;
                case R.id.navigation_chart:
                    Toast.makeText(getApplicationContext(), "통계페이지", Toast.LENGTH_SHORT);
                    return true;
                case R.id.navigation_home:
                    Toast.makeText(getApplicationContext(), "홈페이지", Toast.LENGTH_SHORT);
                    return true;
                case R.id.navigation_notifications:
                    Toast.makeText(getApplicationContext(), "알림페이지", Toast.LENGTH_SHORT);
                    return true;
                case R.id.navigation_setting:
                    Toast.makeText(getApplicationContext(), "세팅페이지", Toast.LENGTH_SHORT);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
