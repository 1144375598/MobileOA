package com.chenxujie.mobileoa.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chenxujie.mobileoa.R;
import com.chenxujie.mobileoa.activity.NotificationDetailActivity;
import com.chenxujie.mobileoa.model.Notification;
import com.chenxujie.mobileoa.model.User;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;


public class WriteAnnounceFragment extends Fragment {
    public static final int SELECT_FILE = 1;

    private EditText titleEdt;
    private EditText contentEdt;
    private TextView personTv;
    private Button publicBtn;
    private String filePath;
    private Spinner spinner = null;
    private Button attachmentBtn = null;
    private String[] items;
    private Activity activity;
    private CallBack callBack;
    private File file;

    public WriteAnnounceFragment(CallBack cb) {
        super();
        callBack = cb;
    }

    @Override
    public void onAttach(Context context) {
        activity = (Activity) context;
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_write_announce, null);
        spinner = (Spinner) view.findViewById(R.id.spinner_level);
        attachmentBtn = (Button) view.findViewById(R.id.btn_attachment);
        titleEdt = (EditText) view.findViewById(R.id.txt_title);
        contentEdt = (EditText) view.findViewById(R.id.txt_content);
        personTv = (TextView) view.findViewById(R.id.txt_person);
        publicBtn = (Button) view.findViewById(R.id.btn_publish);
        // 建立数据源
        items = getResources().getStringArray(R.array.levelname);
        // 建立Adapter并且绑定数据源
        ArrayAdapter<String> _Adapter = new ArrayAdapter<String>(
                container.getContext(), android.R.layout.simple_spinner_item,
                items);
        // 绑定 Adapter到控件
        _Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(_Adapter);
        attachmentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callBack.openFile(SELECT_FILE);
            }
        });

        personTv.setText(BmobUser.getCurrentUser(User.class).getName());
        publicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEdt.getText().toString();
                String content = contentEdt.getText().toString();
                String level = items[spinner.getSelectedItemPosition()];
                if (TextUtils.isEmpty(title)) {
                    Toast.makeText(container.getContext(), "标题不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(container.getContext(), "内容不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                final Notification notification = new Notification();
                notification.setTitle(title);
                notification.setLevel(level);
                notification.setContent(content);
                notification.setSender(BmobUser.getCurrentUser(User.class).getName());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                notification.setDate(simpleDateFormat.format(new Date()));

                if (file != null) {
                    final ProgressDialog progressDialog = new ProgressDialog(activity);
                    final BmobFile bmobFile = new BmobFile(file);
                    bmobFile.uploadblock(new UploadFileListener() {
                        @Override
                        public void onStart() {
                            super.onStart();
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            progressDialog.setTitle("文件上传中");
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                        }

                        @Override
                        public void done(BmobException e) {
                            progressDialog.dismiss();
                            if (e == null) {
                                notification.setFilepath(bmobFile.getFileUrl());
                                notification.setFilename(bmobFile.getFilename());
                                notification.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        if (e == null) {
                                            Toast.makeText(container.getContext(), "发布成功！", Toast.LENGTH_SHORT).show();
                                            callBack.onSucess();
                                        } else {
                                            Toast.makeText(container.getContext(), "发布失败，请重试", Toast.LENGTH_SHORT).show();
                                            Log.e("通知发布失败", e.getErrorCode() + " " + e.getMessage());
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(activity, "文件上传失败", Toast.LENGTH_SHORT).show();
                                Log.e("文件上传失败", e.getErrorCode() + " " + e.getMessage());
                            }
                        }

                        @Override
                        public void onProgress(Integer value) {
                            progressDialog.setProgress(value);
                        }
                    });
                }else{
                    notification.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                Toast.makeText(container.getContext(), "发布成功！", Toast.LENGTH_SHORT).show();
                                callBack.onSucess();
                            } else {
                                Toast.makeText(container.getContext(), "发布失败，请重试", Toast.LENGTH_SHORT).show();
                                Log.e("通知发布失败", e.getErrorCode() + " " + e.getMessage());
                            }
                        }
                    });
                }
            }
        });
        return view;

    }


    public interface CallBack {
        void openFile(int code);

        void onSucess();
    }

    public void setAttachment(String filename, String filePath) {
        file = new File(filePath);
        attachmentBtn.setText(filename);
    }
}
