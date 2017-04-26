package com.chenxujie.mobileoa.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chenxujie.mobileoa.R;
import com.chenxujie.mobileoa.util.ActivityManager;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;

public class NotificationDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView title;
    private TextView content;
    private TextView date;
    private TextView sender;
    private TextView level;
    private Button attachment;
    private ImageButton back;
    private Boolean hasAttach = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.addActivity(this);
        setContentView(R.layout.activity_notification_detail);
        title = (TextView) findViewById(R.id.txt_title);
        content = (TextView) findViewById(R.id.txt_content);
        date = (TextView) findViewById(R.id.txt_date);
        sender = (TextView) findViewById(R.id.txt_jinbanren);
        level = (TextView) findViewById(R.id.spinner_level);
        attachment = (Button) findViewById(R.id.btn_attachment);
        back = (ImageButton) findViewById(R.id.btn_back);

        init();
    }

    private void init() {
        title.setText(getIntent().getExtras().getString("title"));
        content.setText(getIntent().getExtras().getString("content"));
        date.setText(getIntent().getExtras().getString("date"));
        sender.setText(getIntent().getExtras().getString("sender"));
        level.setText(getIntent().getExtras().getString("level"));
        hasAttach = !TextUtils.isEmpty(getIntent().getExtras().getString("filename"));
        if (hasAttach) {
            attachment.setText(getIntent().getExtras().getString("filename"));
            attachment.setOnClickListener(this);
        } else {
            attachment.setVisibility(View.GONE);
        }
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.btn_attachment:
                downloadFile();
                break;
        }
    }

    private void downloadFile() {
        final ProgressDialog progressDialog = new ProgressDialog(NotificationDetailActivity.this);
        BmobFile bmobFile = new BmobFile(getIntent().getExtras().getString("filename"), "", getIntent().getExtras().getString("filepath"));
        File saveFile=new File(Environment.getExternalStorageDirectory()+"/MobileOA", bmobFile.getFilename());
        bmobFile.download(saveFile,new DownloadFileListener() {
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
                    Toast.makeText(NotificationDetailActivity.this, "附件下载成功，请到"+s+"查看", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NotificationDetailActivity.this, "附件下载失败", Toast.LENGTH_SHORT).show();
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
