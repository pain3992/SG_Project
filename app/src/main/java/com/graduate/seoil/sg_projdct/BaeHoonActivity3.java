package com.graduate.seoil.sg_projdct;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class BaeHoonActivity3 extends AppCompatActivity {
    private TextView _text;
    private CountDownTimer _timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bae_hoon3);
        Intent intent = new Intent(this.getIntent());
        _text = (TextView)findViewById(R.id.tvMsg);

        _timer = new CountDownTimer(600*1000, 1000) {
            public void onTick(long millisUntilFinished) {
                _text.setText("카운트: " + millisUntilFinished/1000);
            }

            public void onFinish() {
                _text.setText("finshed");
            }
        };

        Button btnStart = (Button)findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                _timer.start();
            }
        });

        Button btnEnd = (Button)findViewById(R.id.btnStop);
        btnEnd.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                _timer.cancel();
            }
        });
    }
}
