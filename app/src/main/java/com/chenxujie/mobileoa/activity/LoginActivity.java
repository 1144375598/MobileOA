package com.chenxujie.mobileoa.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.chenxujie.mobileoa.R;
import com.chenxujie.mobileoa.model.User;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends Activity {
    private static final String applicationID = "f422f9953797634e2f6f60f357a4c21b";
    private EditText etpassword;
    private EditText etusername;
    private Button login;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bmob.initialize(this, applicationID);
        setContentView(R.layout.activity_login);

        etusername = (EditText) findViewById(R.id.at_account);
        etpassword = (EditText) findViewById(R.id.et_password);
        login = (Button) findViewById(R.id.btn_login);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etusername.getText().toString().trim();
                String password = etpassword.getText().toString().trim();
                if (username == null || username.isEmpty()) {
                    etusername.requestFocus();
                    etusername.setError(new StringBuffer(
                            getString(R.string.username_is_null)));
                    return;
                }
                if (password == null || password.isEmpty()) {
                    etpassword.requestFocus();
                    etpassword.setError(new StringBuffer(
                            getString(R.string.password_is_null)));
                    return;
                }
                final ProgressDialog dialog = ProgressDialog.show(LoginActivity.this, "请稍等...", "正在登录...");

                user = new User();
                user.setUsername(username);
                user.setPassword(password);
                user.login(new SaveListener<BmobUser>() {
                    @Override
                    public void done(BmobUser bmobUser, BmobException e) {
                        dialog.dismiss();
                        if (e == null) {
                            Toast.makeText(LoginActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, getString(R.string.login_fail), Toast.LENGTH_SHORT).show();
                            Log.e("login_fail", e.toString());
                        }
                    }
                });
            }
        });
        user = BmobUser.getCurrentUser(User.class);
        if (user != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
