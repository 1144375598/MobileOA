package com.chenxujie.mobileoa.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chenxujie.mobileoa.R;
import com.chenxujie.mobileoa.fragment.ContactsFragment;
import com.chenxujie.mobileoa.fragment.EmailFragment;
import com.chenxujie.mobileoa.fragment.NotificationFragment;
import com.chenxujie.mobileoa.fragment.PersonInfoFragment;
import com.chenxujie.mobileoa.fragment.ScheduleFragment;
import com.chenxujie.mobileoa.fragment.WaitingMissiveFragment;
import com.chenxujie.mobileoa.fragment.WriteAnnounceFragment;
import com.chenxujie.mobileoa.fragment.WriteMissiveFragment;
import com.chenxujie.mobileoa.fragment.WrittenMissiveFragment;
import com.chenxujie.mobileoa.util.ActivityManager;
import com.chenxujie.mobileoa.util.GetPathFromUri;
import com.chenxujie.mobileoa.view.MenuScrollView;

import net.simonvt.widget.MenuDrawer;
import net.simonvt.widget.MenuDrawerManager;


public class MainActivity extends FragmentActivity implements
        View.OnClickListener, WriteAnnounceFragment.CallBack, PersonInfoFragment.personCallBack {

    private static final String STATE_MENUDRAWER = "com.chenxujie.mobileoa.activity.MainActivity.menuDrawer";
    private static final String STATE_ACTIVE_VIEW_ID = "com.chenxujie.mobileoa.activity.MainActivity.activeViewId";


    private MenuDrawerManager menuDrawer;
    private TextView contentTextView;

    private int activeViewId;

    private int screenWidth;
    private RelativeLayout searchLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager.addActivity(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        // 窗口的宽度
        screenWidth = dm.widthPixels;

        if (savedInstanceState != null) {
            activeViewId = savedInstanceState.getInt(STATE_ACTIVE_VIEW_ID);
        }

        menuDrawer = new MenuDrawerManager(this, MenuDrawer.MENU_DRAG_WINDOW);
        menuDrawer.setContentView(R.layout.activity_main);
        menuDrawer.setMenuView(R.layout.menu_scrollview);

        MenuScrollView msv = (MenuScrollView) menuDrawer.getMenuView();
        msv.setOnScrollChangedListener(new MenuScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                menuDrawer.getMenuDrawer().invalidate();
            }
        });

        searchLayout = (RelativeLayout) findViewById(R.id.searchLayout);

        contentTextView = (TextView) findViewById(R.id.contentText);

        findViewById(R.id.tv_write_announce).setOnClickListener(this);
        findViewById(R.id.tv_waiting_missive).setOnClickListener(this);
        findViewById(R.id.tv_written_missive).setOnClickListener(this);
        findViewById(R.id.tv_notification).setOnClickListener(this);
        findViewById(R.id.tv_email).setOnClickListener(this);
        findViewById(R.id.tv_contacts).setOnClickListener(this);
        findViewById(R.id.tv_schedule).setOnClickListener(this);
        findViewById(R.id.tv_person_info).setOnClickListener(this);
        findViewById(R.id.tv_write_missive).setOnClickListener(this);
        ImageButton imageButton = (ImageButton) findViewById(R.id.btn_menu);
        imageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int drawerState = menuDrawer.getDrawerState();
                if (drawerState != MenuDrawer.STATE_OPEN
                        && drawerState != MenuDrawer.STATE_OPENING) {
                    menuDrawer.openMenu();
                    return;
                }

            }
        });

        TextView activeView = (TextView) findViewById(activeViewId);
        if (activeView != null) {
            menuDrawer.setActiveView(activeView);
            contentTextView.setText(activeView.getText());
        }

        // This will animate the drawer open and closed until the user manually
        // drags it. Usually this would only be
        // called on first launch.
        menuDrawer.getMenuDrawer().peekDrawer();
        menuDrawer.getMenuDrawer().setDropShadowEnabled(false);
        findViewById(R.id.tv_waiting_missive).performClick();
    }

    @Override
    protected void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);
        menuDrawer.onRestoreDrawerState(inState
                .getParcelable(STATE_MENUDRAWER));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(STATE_MENUDRAWER,
                menuDrawer.onSaveDrawerState());
        outState.putInt(STATE_ACTIVE_VIEW_ID, activeViewId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                menuDrawer.toggleMenu();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        final int drawerState = menuDrawer.getDrawerState();
        if (drawerState == MenuDrawer.STATE_OPEN
                || drawerState == MenuDrawer.STATE_OPENING) {
            menuDrawer.closeMenu();
            return;
        }
        super.onBackPressed();
    }


    @Override
    public void onClick(View v) {
        menuDrawer.setActiveView(v);
        contentTextView.setText(((TextView) v).getText());
        menuDrawer.closeMenu();
        activeViewId = v.getId();
        Fragment newContent = null;
        switch (activeViewId) {
            case R.id.tv_write_announce:
                //写通知
                setSearchVisibility(false);
                newContent = new WriteAnnounceFragment(this);
                break;
            case R.id.tv_write_missive:
                //写通知
                setSearchVisibility(false);
                newContent = new WriteMissiveFragment(this);
                break;
            case R.id.tv_waiting_missive:
                //代办公文
                setSearchVisibility(false);
                newContent = new WaitingMissiveFragment();
                break;
            case R.id.tv_written_missive:
                //已阅公文
                setSearchVisibility(false);
                newContent = new WrittenMissiveFragment();
                break;
            case R.id.tv_notification:
                //通知公告
                setSearchVisibility(false);
                newContent = new NotificationFragment();
                break;
            case R.id.tv_email:
                //电子邮件
                setSearchVisibility(false);
                newContent = new EmailFragment();
                break;
            case R.id.tv_schedule:
                //日程
                setSearchVisibility(false);
                newContent = new ScheduleFragment();
                break;
            case R.id.tv_person_info:
                //个人信息
                setSearchVisibility(false);
                newContent = new PersonInfoFragment(this);
                break;
            case R.id.tv_contacts:
                //通讯录
                setSearchVisibility(false);
                newContent = new ContactsFragment();
                break;
            case R.id.btn_menu:
                int drawerState = menuDrawer.getDrawerState();
                if (drawerState != MenuDrawer.STATE_OPEN
                        && drawerState != MenuDrawer.STATE_OPENING) {
                    // mMenuDrawer.closeMenu();
                    menuDrawer.openMenu();
                    return;
                }
                break;

            default:
                break;
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, newContent).commit();
    }

    private void setSearchVisibility(boolean show) {
        searchLayout.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void openFile(int code) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, code);
    }

    @Override
    public void onSucess() {
        findViewById(R.id.tv_waiting_missive).performClick();
    }

    @Override
    public void openCamera(Uri uri) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, PersonInfoFragment.TAKE_PHOTO); // 启动相机程序
    }

    @Override
    public void openGallery() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, PersonInfoFragment.SELECT_PHOTO);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri uri;
        String path;
        String filename;
        switch (requestCode) {
            case WriteAnnounceFragment.SELECT_FILE:
                if (resultCode == RESULT_OK) {
                    uri = data.getData();
                    path = GetPathFromUri.getPath(this, uri);
                    filename = path.substring(path.lastIndexOf("/") + 1, path.length());
                    ((WriteAnnounceFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.content_frame))
                            .setAttachment(filename, path);
                }
                break;
            case WriteMissiveFragment.SELECT_FILE:
                if (resultCode == RESULT_OK) {
                    uri = data.getData();
                    path = GetPathFromUri.getPath(this, uri);
                    filename = path.substring(path.lastIndexOf("/") + 1, path.length());
                    ((WriteMissiveFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.content_frame))
                            .setAttachment(filename, path);
                }
                break;
            case PersonInfoFragment.TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    uri = ((PersonInfoFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.content_frame))
                            .getImageUri();
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(uri, "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(intent, PersonInfoFragment.CROP_PHOTO); // 启动裁剪程序
                }
                break;
            case PersonInfoFragment.SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri imageUri = ((PersonInfoFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.content_frame))
                            .getImageUri();
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(data.getData(), "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, PersonInfoFragment.CROP_PHOTO);
                }
                break;
            case PersonInfoFragment.CROP_PHOTO:
                Log.e("crop photo", "enter");
                if (resultCode == RESULT_OK) {
                    ((PersonInfoFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.content_frame))
                            .setHeadPicture();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityManager.removeActivity(this);
    }
}
