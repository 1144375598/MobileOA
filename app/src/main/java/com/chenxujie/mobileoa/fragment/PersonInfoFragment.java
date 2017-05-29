package com.chenxujie.mobileoa.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chenxujie.mobileoa.R;
import com.chenxujie.mobileoa.activity.LoginActivity;
import com.chenxujie.mobileoa.model.User;
import com.chenxujie.mobileoa.util.ActivityManager;
import com.chenxujie.mobileoa.util.PhotoUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;


public class PersonInfoFragment extends Fragment {
    public static final int TAKE_PHOTO = 5;
    public static final int CROP_PHOTO = 6;
    public static final int SELECT_PHOTO = 7;

    private File outputImage = new File(Environment.getExternalStorageDirectory() +
            "/MobileOA/headPicture", "headPicture.jpg");
    private Uri imageUri = Uri.fromFile(outputImage);

    private TextView username;
    private TextView name;
    private TextView age;
    private TextView sex;
    private TextView address;
    private TextView department;
    private TextView position;
    private TextView educationLevel;
    private TextView hiredate;
    private ImageView photo;
    private Button logout;
    private User user;
    private Activity activity;
    private personCallBack personCallBack;
    private Button resetPwd;


    public Uri getImageUri() {
        return imageUri;
    }


    public PersonInfoFragment(personCallBack cb) {
        super();
        personCallBack = cb;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_person_info, null);
        photo = (ImageView) view.findViewById(R.id.headPicture);
        username = (TextView) view.findViewById(R.id.tv_username);
        name = (TextView) view.findViewById(R.id.tv_name);
        age = (TextView) view.findViewById(R.id.tv_age);
        sex = (TextView) view.findViewById(R.id.tv_sex);
        address = (TextView) view.findViewById(R.id.tv_address);
        department = (TextView) view.findViewById(R.id.tv_department);
        position = (TextView) view.findViewById(R.id.tv_position);
        educationLevel = (TextView) view.findViewById(R.id.tv_educationlevel);
        hiredate = (TextView) view.findViewById(R.id.tv_hiredate);
        logout = (Button) view.findViewById(R.id.exit_app);
        resetPwd = (Button) view.findViewById(R.id.reset_pwd);

        resetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(activity)
                        .setMessage(R.string.sure_exit)
                        .setTitle(R.string.prompt)
                        .setPositiveButton(R.string.confirm_button,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                        BmobUser.logOut();   //清除缓存用户对象
                                        Intent intent = new Intent(
                                                activity,
                                                LoginActivity.class);
                                        startActivity(intent);
                                        ActivityManager.finishAll();
                                    }
                                })
                        .setNegativeButton(R.string.cancel,
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
            }
        });
        init();
        registerForContextMenu(photo);
        return view;
    }

    private void init() {
        user = BmobUser.getCurrentUser(User.class);
        username.setText(user.getUsername());
        name.setText(user.getName());
        if (user.getAge() != null) {
            age.setText(user.getAge().toString());
        }
        if (user.getSex() != null) {
            sex.setText(user.getSex() ? "男" : "女");
        }
        address.setText(user.getAddress());
        educationLevel.setText(user.getEducation());
        department.setText(user.getDepartment());
        position.setText(user.getPosition());
        if (user.getHiredate() != null) {
            hiredate.setText(user.getHiredate().getDate());
        }

        if (outputImage.exists()) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream
                        (imageUri));
                photo.setImageBitmap(bitmap); // 将裁剪后的照片显示出来
            } catch (FileNotFoundException f) {
                f.printStackTrace();
            }
        } else {
            if (!TextUtils.isEmpty(user.getPhotoUrl())) {
                BmobFile bmobFile = new BmobFile(user.getPhotoName(), "", user.getPhotoUrl());
                bmobFile.download(outputImage, new DownloadFileListener() {

                    @Override
                    public void onProgress(Integer integer, long l) {

                    }

                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
                            try {
                                Bitmap bitmap = BitmapFactory.decodeStream(activity.getContentResolver()
                                        .openInputStream(imageUri));
                                photo.setImageBitmap(bitmap); // 将裁剪后的照片显示出来
                            } catch (FileNotFoundException f) {
                                f.printStackTrace();
                            }
                        } else {
                            Toast.makeText(activity, "头像下载失败", Toast.LENGTH_SHORT).show();
                            Log.e("头像下载失败", e.getErrorCode() + " " + e.getMessage());
                        }
                    }

                });
            }

        }
    }

    private void showDialog() {
        View view = View.inflate(activity.getApplicationContext(), R.layout.dialog_reset_pwd, null);
        final EditText oldPwd = (EditText) view.findViewById(R.id.old_pwd);
        final EditText newPwd = (EditText) view.findViewById(R.id.new_pwd);
        final EditText confirmNewPwd = (EditText) view.findViewById(R.id.confirm_new_pwd);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style
                .Theme_picker);
        builder.setView(view);
        builder.setTitle("重置密码");
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //通过反射使密码输入错误时dialog不消失
                try {
                    Field field = dialog.getClass().getSuperclass()
                            .getDeclaredField("mShowing");
                    field.setAccessible(true);
                    field.set(dialog, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                String oldPwdString = oldPwd.getText().toString();
                String newPwdString = newPwd.getText().toString();
                String confirmPwdString = confirmNewPwd.getText().toString();
                if (TextUtils.isEmpty(oldPwdString)) {
                    oldPwd.setError("请输入旧密码");
                } else if (TextUtils.isEmpty(newPwdString) || TextUtils.isEmpty
                        (confirmPwdString)) {

                    newPwd.setError("请输入新密码");
                } else if (!TextUtils.equals(newPwdString, confirmPwdString)) {
                    newPwd.requestFocus();
                    newPwd.setError("密码不一致");
                    confirmNewPwd.setText("");
                } else {
                    try {
                        Field field = dialog.getClass().getSuperclass()
                                .getDeclaredField("mShowing");
                        field.setAccessible(true);
                        field.set(dialog, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    BmobUser.updateCurrentUserPassword(oldPwdString, newPwdString, new UpdateListener() {


                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Toast.makeText(activity, "密码修改成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(activity, "密码修改失败" + " " + e.getMessage(), Toast.LENGTH_SHORT)
                                        .show();
                                Log.e("密码修改失败", e.getErrorCode() + " " + e.getMessage());
                            }
                        }
                    });
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    Field field = dialog.getClass().getSuperclass()
                            .getDeclaredField("mShowing");
                    field.setAccessible(true);
                    field.set(dialog, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (outputImage.exists()) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream
                        (imageUri));
                photo.setImageBitmap(bitmap); // 将照片显示出来
            } catch (FileNotFoundException f) {
                f.printStackTrace();
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, 0, 0, "重新拍一张");
        menu.add(0, 1, 0, "从相册选取");
    }

    public interface personCallBack {
        void openCamera(Uri uri);

        void openGallery();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        switch (item.getItemId()) {
            case 0:
                personCallBack.openCamera(imageUri);
                break;
            case 1:
                personCallBack.openGallery();
                break;
        }
        return super.onContextItemSelected(item);
    }


    public void setHeadPicture() {
        String path = outputImage.getAbsolutePath();
        final Bitmap picture = BitmapFactory.decodeFile(path);
        PhotoUtil.compressImageByQuality(picture, path);

        if (!TextUtils.isEmpty(BmobUser.getCurrentUser(User.class).getPhotoUrl())) {
            BmobFile file = new BmobFile();
            file.setUrl(BmobUser.getCurrentUser(User.class).getPhotoUrl());
            file.delete(new UpdateListener() {

                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        uploadPicture(picture);
                    } else {
                        Log.e("文件删除失败：", e.getErrorCode() + "," + e.getMessage());
                    }
                }
            });
        } else {
            uploadPicture(picture);
        }

    }

    private void uploadPicture(final Bitmap picture) {
        final BmobFile bmobFile = new BmobFile(outputImage);
        bmobFile.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    User user = new User();
                    user.setPhotoUrl(bmobFile.getFileUrl());
                    user.setPhotoName(bmobFile.getFilename());
                    user.update(BmobUser.getCurrentUser(User.class).getObjectId(), new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                photo.setImageBitmap(picture); // 将裁剪后的照片显示出来
                                Toast.makeText(activity, "头像上传成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(activity, "头像更新失败", Toast.LENGTH_SHORT).show();
                                Log.e("头像更新失败", e.getErrorCode() + " " + e.getMessage());
                            }
                        }
                    });
                } else {
                    Toast.makeText(activity, "头像上传失败", Toast.LENGTH_SHORT).show();
                    Log.e("头像上传失败", e.getErrorCode() + " " + e.getMessage());
                }
            }
        });
    }
}


