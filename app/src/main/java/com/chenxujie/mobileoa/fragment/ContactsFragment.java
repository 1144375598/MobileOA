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
import com.chenxujie.mobileoa.activity.ContactActivity;
import com.chenxujie.mobileoa.adapter.ContactsAdapter;
import com.chenxujie.mobileoa.model.Contact;
import com.chenxujie.mobileoa.model.User;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;


public class ContactsFragment extends Fragment {
    private ListView listView;
    private ContactsAdapter contactsAdapter;
    private List<Contact> contactList;
    private Activity activity;
    private Button addContact;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (Activity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, null);
        listView = (ListView) view.findViewById(R.id.listview_contacts);
        addContact = (Button) view.findViewById(R.id.add_contact);
        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ContactActivity.class);
                intent.putExtra("isAdd", true);
                activity.startActivity(intent);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact contact = contactList.get(position);
                Bundle bundle = new Bundle();
                bundle.putString("name", contact.getName());
                bundle.putString("telephone", contact.getTelephone());
                bundle.putString("email", contact.getEmail());
                bundle.putString("id", contact.getObjectId());
                bundle.putBoolean("isAdd", false);
                Intent intent = new Intent(activity,
                        ContactActivity.class);
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
        BmobQuery<Contact> bmobQuery = new BmobQuery<Contact>();
        bmobQuery.addWhereEqualTo("user", new BmobPointer(user));
        bmobQuery.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        bmobQuery.findObjects(new FindListener<Contact>() {
            @Override
            public void done(List<Contact> list, BmobException e) {
                dialog.dismiss();
                if (e == null) {
                    contactList = list;
                    contactsAdapter = new ContactsAdapter(activity, R.layout.item_contact, list);
                    listView.setAdapter(contactsAdapter);
                } else {
                    if (e.getErrorCode() == 9009) {
                        Toast.makeText(activity, getString(R.string.no_contact), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(activity, getString(R.string.query_contact_fail), Toast.LENGTH_SHORT).show();
                    }
                    Log.e("query contact fail", e.toString());
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
                TextView position = (TextView) view.findViewById(R.id.position);
                final Integer pos = new Integer(position.getText().toString()) - 1;
                final Contact temp = contactList.get(pos);
                Contact contact1 = new Contact();
                contact1.setObjectId(temp.getObjectId());
                contact1.delete(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Toast.makeText(activity, getString(R.string.delete_success), Toast.LENGTH_SHORT).show();
                            contactList.remove(temp);
                            contactsAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(activity, getString(R.string.delete_fail), Toast.LENGTH_SHORT).show();
                            Log.e("删除联系人失败", "失败：" + e.getMessage() + "," + e.getErrorCode());
                        }
                    }
                });
                break;
        }
        return super.onContextItemSelected(item);
    }

}
