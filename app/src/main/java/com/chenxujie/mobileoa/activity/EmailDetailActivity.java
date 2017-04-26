package com.chenxujie.mobileoa.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chenxujie.mobileoa.R;
import com.chenxujie.mobileoa.model.User;
import com.chenxujie.mobileoa.util.ActivityManager;

import cn.bmob.v3.BmobUser;

public class EmailDetailActivity extends AppCompatActivity implements View.OnClickListener{
    private ImageButton back;
    private TextView sender;
    private EditText receiver;
    private TextView date;
    private EditText title;
    private EditText content;
    private Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.addActivity(this);
        setContentView(R.layout.activity_email_detail);

        back = (ImageButton) findViewById(R.id.btn_back);
        sender = (TextView) findViewById(R.id.tv_email_sender);
        receiver = (EditText) findViewById(R.id.et_email_receiver);
        date = (TextView) findViewById(R.id.tv_email_date);
        title = (EditText) findViewById(R.id.et_email_title);
        content = (EditText) findViewById(R.id.et_email_content);
        send = (Button) findViewById(R.id.btn_send);
        init();
    }
    private void init() {
        sender.setText(getIntent().getExtras().getString("sender"));
        receiver.setText(BmobUser.getCurrentUser(User.class).getUsername());
        date.setText(getIntent().getExtras().getString("date"));
        title.setText(getIntent().getExtras().getString("title"));
        content.setText(getIntent().getExtras().getString("content"));

        back.setOnClickListener(this);
        send.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.btn_reply:
                Intent intent=new Intent(EmailDetailActivity.this,AddEmailActivity.class);
                intent.putExtra("isReply",true);
                intent.putExtra("title",title.getText().toString());
                intent.putExtra("receiver",sender.getText().toString());
                startActivity(intent);
        }
    }
    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.removeActivity(this);
    }
}
