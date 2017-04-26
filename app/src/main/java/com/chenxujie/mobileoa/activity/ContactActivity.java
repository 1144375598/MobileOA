package com.chenxujie.mobileoa.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.chenxujie.mobileoa.R;
import com.chenxujie.mobileoa.model.Contact;
import com.chenxujie.mobileoa.model.User;
import com.chenxujie.mobileoa.util.ActivityManager;
import com.chenxujie.mobileoa.util.Test;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class ContactActivity extends Activity {

    private TextView name;
    private EditText telephone;
    private EditText email;
    private Button update;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.addActivity(this);
        setContentView(R.layout.activity_contact);
        name = (TextView) findViewById(R.id.edtname);
        telephone = (EditText) findViewById((R.id.edtTelphone));
        email = (EditText) findViewById(R.id.edtEmail);
        update = (Button) findViewById(R.id.btn_update);

        if (getIntent().getExtras().getBoolean("isAdd") == false) {
            name.setText(getIntent().getExtras().getString("name"));
            telephone.setText(getIntent().getExtras().getString("telephone"));
            email.setText(getIntent().getExtras().getString("email"));
        } else {
            update.setText(R.string.save);
        }

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(name.getText().toString()) || TextUtils.isEmpty(telephone.getText().toString())) {
                    Toast.makeText(ContactActivity.this, getString(R.string.have_no_telephone),
                            Toast.LENGTH_SHORT).show();
                } else if (!Test.testPhone(telephone.getText().toString())) {
                    Toast.makeText(ContactActivity.this, "请输入正确的电话号码",
                            Toast.LENGTH_SHORT).show();
                } else {
                    updateContact();
                }
            }
        });
    }

    private void updateContact() {
        if (getIntent().getExtras().getBoolean("isAdd") == false) {
            String contactID = getIntent().getExtras().getString("id");
            Contact contact = new Contact();
            contact.setTelephone(telephone.getText().toString());
            contact.setEmail(email.getText().toString());
            contact.setName(name.getText().toString());
            contact.update(contactID, new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        Toast.makeText(ContactActivity.this, getString(R.string.update_success),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.update_fail),
                                Toast.LENGTH_SHORT).show();
                        Log.i("联系人更新失败", "更新失败：" + e.getMessage() + "," + e.getErrorCode());
                    }
                }
            });
        } else {
            Contact contact = new Contact();
            contact.setTelephone(telephone.getText().toString());
            contact.setEmail(email.getText().toString());
            contact.setName(name.getText().toString());
            contact.setUser(BmobUser.getCurrentUser(User.class));
            contact.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        Toast.makeText(ContactActivity.this, getString(R.string.add_contact_success),
                                Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(ContactActivity.this, getString(R.string.add_contact_fail),
                                Toast.LENGTH_SHORT).show();
                        Log.i("联系人添加失败", "添加失败：" + e.getMessage() + "," + e.getErrorCode());
                    }
                }
            });
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.removeActivity(this);
    }
}
