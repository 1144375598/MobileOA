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
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.DownloadFileListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;


public class PersonInfoFragment extends Fragment {
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;

    File outputImage = new File(Environment.getExternalStorageDirectory(), "output_image.jpg");
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

    public PersonInfoFragment() {
        // Required empty public constructor
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
            age.setText(user.getAge());
        }
        if (user.getSex() != null) {
            sex.setText(user.getSex() ? "男" : "女");
        }
        address.setText(user.getAddress());
        educationLevel.setText(user.getEducation());
        department.setText(user.getDepartment());
        position.setText(user.getPosition());
        if (user.getHiredate() != null) {
            hiredate.setText(user.getHiredate().toString());
        }

        if (outputImage.exists()) {
            Log.e("未下载", "1");
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(imageUri));
                photo.setImageBitmap(bitmap); // 将裁剪后的照片显示出来
            } catch (FileNotFoundException f) {
                f.printStackTrace();
            }
        } else {
            Log.e("开始下载", "1");
            BmobFile bmobFile = new BmobFile(user.getPhotoName(), "", user.getPhotoUrl());
            bmobFile.download(outputImage, new DownloadFileListener() {
                @Override
                public void onStart() {
                    Log.e("开始下载图片", "download");
                }

                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        try {
                            Bitmap bitmap = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(imageUri));
                            photo.setImageBitmap(bitmap); // 将裁剪后的照片显示出来
                        } catch (FileNotFoundException f) {
                            f.printStackTrace();
                        }
                    } else {
                        Toast.makeText(activity, "头像下载失败", Toast.LENGTH_SHORT).show();
                        Log.e("头像下载失败", e.getErrorCode() + " " + e.getMessage());
                    }
                }

                @Override
                public void onProgress(Integer integer, long l) {

                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (outputImage.exists()) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(imageUri));
                photo.setImageBitmap(bitmap); // 将裁剪后的照片显示出来
            } catch (FileNotFoundException f) {
                f.printStackTrace();
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, 0, 0, "重新拍一张");
        //menu.add(0, 1, 0, "从相册选取");
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

        Intent intent;
        switch (item.getItemId()) {
            case 0:
                intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO); // 启动相机程序
                break;
           /* case 1:
                intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image*//*");
                startActivityForResult(intent, TAKE_PHOTO);
                break;*/
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == activity.RESULT_OK) {
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri, "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CROP_PHOTO); // 启动裁剪程序
                }
                break;
            case CROP_PHOTO:
                if (resultCode == activity.RESULT_OK) {
                    String path = outputImage.getAbsolutePath();
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    PhotoUtil.compressImageByQuality(bitmap, Environment.getExternalStorageDirectory().getPath() + "/output_image.jpg");
                    photo.setImageBitmap(bitmap); // 将裁剪后的照片显示出来
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
                                        Toast.makeText(activity, "头像上传成功", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                Toast.makeText(activity, "头像上传失败", Toast.LENGTH_SHORT).show();
                                Log.e("头像上传失败", e.getErrorCode() + " " + e.getMessage());

                            }
                        }
                    });

                }
                break;
            default:
                break;
        }
    }
}
