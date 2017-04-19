package com.chenxujie.mobileoa.adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chenxujie.mobileoa.R;
import com.chenxujie.mobileoa.model.Contact;

import java.util.List;


public class ContactsAdapter extends ArrayAdapter<Contact> {
    private int resourceId;

    public ContactsAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Contact> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Contact contact = getItem(position);
        View view;
        final ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.name);
            viewHolder.dial = (ImageButton) view.findViewById(R.id.dial);
            viewHolder.position=(TextView)view.findViewById(R.id.position);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.name.setText(contact.getName());
        viewHolder.position.setText(new Integer(position+1).toString());
        final String phone_number = contact.getTelephone();
        if (phone_number != null && !phone_number.equals("")) {
            viewHolder.dial.setVisibility(View.VISIBLE);
            viewHolder.dial.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("tel:" + phone_number);
                    Intent intent = new Intent(Intent.ACTION_CALL, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (ActivityCompat.checkSelfPermission(v.getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    v.getContext().startActivity(intent);
                }
            });
        } else
            viewHolder.dial.setVisibility(View.INVISIBLE);
        return view;
    }

    class ViewHolder {
        TextView name;
        ImageButton dial;
        TextView position;
    }
}
