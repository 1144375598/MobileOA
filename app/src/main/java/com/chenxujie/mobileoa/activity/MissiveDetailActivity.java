package com.chenxujie.mobileoa.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chenxujie.mobileoa.R;
import com.chenxujie.mobileoa.model.Missive;
import com.chenxujie.mobileoa.model.User;
import com.chenxujie.mobileoa.util.ActivityManager;

import java.io.File;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.UpdateListener;

public class MissiveDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView title;
    private TextView content;
    private TextView time;
    private TextView sender;
    private EditText comment;
    private Button confirm;
    private Button attachment;
    private ImageButton back;
    private TextView status;
    private LinearLayout confirmLayout;
    private TextView receiver;
    private Boolean hasAttach = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.addActivity(this);
        setContentView(R.layout.activity_missive_detail);
        init();
    }

    private void init() {
        title = (TextView) findViewById(R.id.missive_title);
        content = (TextView) findViewById(R.id.missive_content);
        time = (TextView) findViewById(R.id.missive_date);
        sender = (TextView) findViewById(R.id.missive_sender);
        comment = (EditText) findViewById(R.id.missive_comment);
        confirm = (Button) findViewById(R.id.missive_send);
        attachment = (Button) findViewById(R.id.missive_attachment);
        back = (ImageButton) findViewById(R.id.btn_back);
        status = (TextView) findViewById(R.id.missive_status);
        confirmLayout = (LinearLayout) findViewById(R.id.line);
        receiver = (TextView) findViewById(R.id.missive_receiver);

        title.setText(getIntent().getExtras().getString("title"));
        content.setText(getIntent().getExtras().getString("content"));
        time.setText(getIntent().getExtras().getString("time"));
        sender.setText(getIntent().getExtras().getString("sender"));
        receiver.setText(getIntent().getExtras().getString("receiver"));
        hasAttach = !TextUtils.isEmpty(getIntent().getExtras().getString("filename"));
        if (hasAttach) {
            attachment.setText(getIntent().getExtras().getString("filename"));
            attachment.setOnClickListener(this);
        } else {
            attachment.setVisibility(View.GONE);
        }
        if (getIntent().getExtras().getString("sender").equals(BmobUser.getCurrentUser(User.class)
                .getUsername())) {
            if (getIntent().getExtras().getBoolean("isWritten")) {
                comment.setText(getIntent().getExtras().getString("comment"));
                comment.setFocusable(false);
                confirmLayout.setVisibility(View.GONE);
                status.setText("已批阅");
            } else {
                status.setText("未批阅");
                comment.setFocusable(false);
                confirmLayout.setVisibility(View.GONE);
            }
        } else if (getIntent().getExtras().getBoolean("isWritten")) {
            comment.setText(getIntent().getExtras().getString("comment"));
            status.setText("已批阅");
            comment.setFocusable(false);
            confirmLayout.setVisibility(View.GONE);
        } else {
            status.setText("未批阅");
            confirm.setOnClickListener(this);
        }
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.missive_attachment:
                downloadFile();
                break;
            case R.id.missive_send:
                save();
                break;
        }
    }

    private void save() {
        if (TextUtils.isEmpty(comment.getText().toString())) {
            Toast.makeText(MissiveDetailActivity.this, "审核意见不能为空", Toast.LENGTH_SHORT).show();
        } else {
            Missive missive = new Missive();
            missive.setComment(comment.getText().toString());
            missive.setWritten(true);
            missive.update(getIntent().getExtras().getString("id"), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        Toast.makeText(MissiveDetailActivity.this, "公文批阅完成", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    } else {
                        Toast.makeText(MissiveDetailActivity.this, "公文上传失败，请重试", Toast.LENGTH_SHORT).show();
                        Log.e("公文上传失败", e.getErrorCode() + " " + e.getMessage());
                    }
                }
            });
        }
    }

    private void downloadFile() {
        final ProgressDialog progressDialog = new ProgressDialog(MissiveDetailActivity.this);
        BmobFile bmobFile = new BmobFile(getIntent().getExtras().getString("filename"), "", getIntent()
                .getExtras().getString("filepath"));
        File saveFile = new File(Environment.getExternalStorageDirectory() + "/MobileOA", bmobFile
                .getFilename());
        bmobFile.download(saveFile, new DownloadFileListener() {
            @Override
            public void onStart() {
                super.onStart();
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setTitle("文件下载中");
                progressDialog.setCancelable(true);
                progressDialog.show();
            }

            @Override
            public void done(String s, BmobException e) {
                progressDialog.dismiss();
                if (e == null) {
                    Toast.makeText(MissiveDetailActivity.this, "附件下载成功，请到" + s + "查看", Toast.LENGTH_SHORT)
                            .show();
                } else {
                    Toast.makeText(MissiveDetailActivity.this, "附件下载失败", Toast.LENGTH_SHORT).show();
                    Log.e("附件下载失败", e.getErrorCode() + " " + e.getMessage());
                }
            }

            @Override
            public void onProgress(Integer integer, long l) {
                progressDialog.setProgress(integer);
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
