package com.chenxujie.mobileoa.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chenxujie.mobileoa.R;
import com.chenxujie.mobileoa.model.Missive;
import com.chenxujie.mobileoa.model.User;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class WriteMissiveFragment extends Fragment {
    public static final int SELECT_FILE = 2;
    private EditText titleEdt;
    private EditText contentEdt;
    private TextView personTv;
    private Button publicBtn;
    private EditText receiver;
    private Button attachmentBtn = null;
    private Activity activity;
    private WriteAnnounceFragment.CallBack callBack;
    private File file;

    public WriteMissiveFragment(WriteAnnounceFragment.CallBack cb) {
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
        View view = inflater.inflate(R.layout.fragment_write_missive, null);
        receiver = (EditText) view.findViewById(R.id.write_missive_receiver);
        attachmentBtn = (Button) view.findViewById(R.id.btn_attachment);
        titleEdt = (EditText) view.findViewById(R.id.txt_title);
        contentEdt = (EditText) view.findViewById(R.id.txt_content);
        personTv = (TextView) view.findViewById(R.id.txt_person);
        publicBtn = (Button) view.findViewById(R.id.btn_publish);

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
                String receiverString = receiver.getText().toString();
                if (TextUtils.isEmpty(title)) {
                    Toast.makeText(container.getContext(), "标题不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(content)) {
                    Toast.makeText(container.getContext(), "内容不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(receiverString)) {
                    Toast.makeText(container.getContext(), "审核人不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                final Missive missive = new Missive();
                missive.setTitle(title);
                missive.setContent(content);
                missive.setReceiver(receiverString);
                missive.setSender(BmobUser.getCurrentUser(User.class).getName());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                missive.setTime(simpleDateFormat.format(new Date()));
                missive.setWritten(false);

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
                                missive.setFilePath(bmobFile.getFileUrl());
                                missive.setFileName(bmobFile.getFilename());
                                missive.save(new SaveListener<String>() {
                                    @Override
                                    public void done(String s, BmobException e) {
                                        if (e == null) {
                                            Toast.makeText(container.getContext(), "公文发布成功！", Toast.LENGTH_SHORT).show();
                                            callBack.onSucess();
                                        } else {
                                            Toast.makeText(container.getContext(), "公文发布失败，请重试", Toast.LENGTH_SHORT).show();
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
                    missive.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                Toast.makeText(container.getContext(), "公文发布成功！", Toast.LENGTH_SHORT).show();
                                callBack.onSucess();
                            } else {
                                Toast.makeText(container.getContext(), "公文发布失败，请重试", Toast.LENGTH_SHORT).show();
                                Log.e("通知发布失败", e.getErrorCode() + " " + e.getMessage());
                            }
                        }
                    });
                }
            }
        });
        return view;

    }


    public void setAttachment(String filename, String filePath) {
        file = new File(filePath);
        attachmentBtn.setText(filename);
    }
}


