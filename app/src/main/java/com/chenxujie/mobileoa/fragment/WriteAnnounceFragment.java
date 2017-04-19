package com.chenxujie.mobileoa.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.chenxujie.mobileoa.model.Notification;
import com.chenxujie.mobileoa.model.User;

import java.util.Date;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;


public class WriteAnnounceFragment extends Fragment {
    public static final int SELECT_FILE= 1;

    private EditText titleEdt;
    private EditText contentEdt;
    private TextView personTv;
    private Button publicBtn;
    private String filePath;
    private Spinner spinner = null;
    private Button attachmentBtn = null;
    private String[] items;
    private Activity activity;

    public WriteAnnounceFragment() {

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
                openFile();
            }
        });

        personTv.setText(BmobUser.getCurrentUser(User.class).getName());
        publicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleEdt.getText().toString();
                String content = contentEdt.getText().toString();
                String level = items[spinner.getSelectedItemPosition()];
                String attachment = attachmentBtn.getText().toString();
                if (TextUtils.isEmpty(title)) {
                    Toast.makeText(container.getContext(), "标题不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(container.getContext(), "内容不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                Notification notification = new Notification();
                notification.setTitle(title);
                notification.setLevel(level);
                notification.setContent(content);
                notification.setSender(BmobUser.getCurrentUser(User.class));
                notification.setTime(new BmobDate(new Date()));
                if (!TextUtils.isEmpty(attachment) && !attachment.equals("选择附件")) {
                    notification.setFilename(attachment);
                    notification.setFilepath(filePath);
                }
                notification.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            Toast.makeText(container.getContext(), "发布成功！", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(container.getContext(), "发布失败，请重试", Toast.LENGTH_SHORT).show();
                            Log.e("通知发布失败", e.getErrorCode() + " " + e.getMessage());
                        }
                    }
                });

            }
        });
        return view;

    }

    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        activity.startActivityForResult(intent,SELECT_FILE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                Uri uri = data.getData();
                Log.e("文件路径",uri.getPath().toString());

            }
        }
    }
    public void setAttachmentText(String text, String path) {
        filePath = path;
        attachmentBtn.setText(text);
    }
}
