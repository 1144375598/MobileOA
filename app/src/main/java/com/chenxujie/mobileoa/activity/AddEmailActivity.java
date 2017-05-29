package com.chenxujie.mobileoa.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.chenxujie.mobileoa.R;
import com.chenxujie.mobileoa.model.Email;
import com.chenxujie.mobileoa.model.User;
import com.chenxujie.mobileoa.util.ActivityManager;
import com.chenxujie.mobileoa.util.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.http.bean.Init;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class AddEmailActivity extends Activity implements View.OnClickListener {
    private ImageButton back;
    private TextView sender;
    private EditText receiver;
    private TextView date;
    private EditText title;
    private EditText content;
    private Button send;
    private List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.addActivity(this);
        setContentView(R.layout.activity_add_email);

        back = (ImageButton) findViewById(R.id.btn_back);
        sender = (TextView) findViewById(R.id.tv_email_sender);
        receiver = (EditText) findViewById(R.id.et_email_receiver);
        date = (TextView) findViewById(R.id.tv_email_date);
        title = (EditText) findViewById(R.id.et_email_title);
        content = (EditText) findViewById(R.id.et_email_content);
        send = (Button) findViewById(R.id.btn_send);
        init();
        queryUsers();
    }

    private void init() {
        if (getIntent().getExtras().getBoolean("isReply")) {
            title.setText("Re:" + getIntent().getExtras().getString("title"));
            receiver.setText(getIntent().getExtras().getString("receiver"));
        } else {
            sender.setText(BmobUser.getCurrentUser(User.class).getUsername());
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        date.setText(simpleDateFormat.format(new Date()));

        back.setOnClickListener(this);
        send.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.btn_send:
                sendEmail();
        }
    }

    private void sendEmail() {
        if (TextUtils.isEmpty(receiver.getText().toString())) {
            Toast.makeText(AddEmailActivity.this, getString(R.string.receiver_is_wrong), Toast
                    .LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(title.getText().toString())) {
            Toast.makeText(AddEmailActivity.this, getString(R.string.title_is_null), Toast.LENGTH_SHORT)
                    .show();
            return;
        } else if (TextUtils.isEmpty(content.getText().toString())) {
            Toast.makeText(AddEmailActivity.this, getString(R.string.content_is_null), Toast.LENGTH_SHORT)
                    .show();
            return;
        } else {
            String receiverString = receiver.getText().toString();
            Boolean flag = false;//收件人是否存在
            for (User user : users) {
                if (user.getUsername().equals(receiverString)) {
                    flag = true;
                    break;
                }
            }
            if (flag == false) {
                Toast.makeText(AddEmailActivity.this, "不存在该收件人！", Toast.LENGTH_SHORT).show();
                return;
            }

            Email email = new Email();
            email.setSender(BmobUser.getCurrentUser(User.class).getUsername());
            email.setTitle(title.getText().toString());
            email.setDate(date.getText().toString());
            email.setContent(content.getText().toString());
            email.setReceiver(receiver.getText().toString());
            email.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        Toast.makeText(AddEmailActivity.this, getString(R.string.add_email_success), Toast
                                .LENGTH_SHORT).show();
                        onBackPressed();
                    } else {
                        Toast.makeText(AddEmailActivity.this, getString(R.string.add_email_fail), Toast
                                .LENGTH_SHORT).show();
                        Log.e("邮件发送失败", e.getErrorCode() + " " + e.getMessage());
                    }
                }
            });
        }

    }

    private void queryUsers() {
        BmobQuery<User> query = new BmobQuery<>();
        query.setLimit(1000);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    users = list;
                } else {
                    Toast.makeText(AddEmailActivity.this, "收件人信息加载失败", Toast.LENGTH_SHORT).show();
                    Log.e("收件人信息加载失败", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
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
