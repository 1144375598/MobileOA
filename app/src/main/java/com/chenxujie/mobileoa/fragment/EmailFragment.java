package com.chenxujie.mobileoa.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chenxujie.mobileoa.R;
import com.chenxujie.mobileoa.activity.AddEmailActivity;
import com.chenxujie.mobileoa.activity.EmailDetailActivity;
import com.chenxujie.mobileoa.adapter.EmailAdapter;
import com.chenxujie.mobileoa.model.Email;
import com.chenxujie.mobileoa.model.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;


public class EmailFragment extends Fragment {
    private ListView listView;
    private EmailAdapter emailAdapter;
    private List<Email> emailList;
    private Activity activity;
    private Button addEmail;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, null);
        listView = (ListView) view.findViewById(R.id.listview_email);
        addEmail = (Button) view.findViewById(R.id.add_email);
        addEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, AddEmailActivity.class);
                intent.putExtra("isReply",false);
                activity.startActivity(intent);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Email email = emailList.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("title", email.getTitle());
                bundle.putString("content", email.getContent());
                bundle.putString("date", email.getDate());
                bundle.putString("sender", email.getSender());
                bundle.putString("id", email.getObjectId());
                Intent intent = new Intent(activity,
                        EmailDetailActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        registerForContextMenu(listView);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        query();
    }

    private void query() {
        final ProgressDialog dialog = ProgressDialog.show(activity, "请稍等...", "正在打开...");
        User user = BmobUser.getCurrentUser(User.class);
        BmobQuery<Email> bmobQuery = new BmobQuery<Email>();
        bmobQuery.addWhereEqualTo("receiver", new BmobPointer(user));
        bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        bmobQuery.findObjects(new FindListener<Email>() {
            @Override
            public void done(List<Email> list, BmobException e) {
                dialog.dismiss();
                if (e == null) {
                    emailList = list;
                    emailAdapter = new EmailAdapter(activity, R.layout.item_email, list);
                    listView.setAdapter(emailAdapter);
                } else {
                    if (e.getErrorCode() == 9009) {
                        Toast.makeText(activity, getString(R.string.no_email), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(activity, getString(R.string.query_email_fail), Toast.LENGTH_SHORT).show();
                    }
                    Log.e("query email fail", e.getErrorCode()+" "+e.getMessage());
                }
            }
        });
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, 1, 0, "删除");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item
                        .getMenuInfo();
                View view = listView.getChildAt(menuInfo.position);
                TextView position = (TextView) view.findViewById(R.id.item_email_position);
                final Integer pos = new Integer(position.getText().toString()) - 1;
                final Email temp = emailList.get(pos);
                Email email1 = new Email();
                email1.setObjectId(temp.getObjectId());
                email1.delete(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Toast.makeText(activity, getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
                            emailList.remove(temp);
                            emailAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(activity, getString(R.string.delete_fail), Toast.LENGTH_SHORT).show();
                            Log.e("删除邮件失败", e.getMessage() + "," + e.getErrorCode());
                        }
                    }
                });
                break;
        }
        return super.onContextItemSelected(item);
    }
}
