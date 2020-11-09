package com.fibocom.factorytest;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A fragment representing a list of Items.
 */
public class SmsFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;

    public SmsFragment() {
    }

    public static SmsFragment newInstance(int columnCount) {
        SmsFragment fragment = new SmsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sms_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MySmsRecyclerViewAdapter(SmsContent.ITEMS));
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SmsContent.ITEMS.clear();
        SmsContent.ITEM_MAP.clear();
        getSmsInPhone();
    }

    private void getSmsInPhone() {
        final String SMS_URI_ALL = "content://sms/";
        try {
            ContentResolver cr = getContext().getContentResolver();
            String[] projection = new String[]{"_id", "address",
                    "body", "date", "type"};
            String[] args = new String[]{"?=1"};
            Uri uri = Uri.parse(SMS_URI_ALL);
            Cursor cur = cr.query(uri, projection, "type=1", null, "date desc");

            if (cur.moveToFirst()) {
                String phoneNumber;
                String smsBoby;
                String date;
                int id = 1;

                int phoneNumberColumn = cur.getColumnIndex("address");
                Log.d("MyLog", "phoneNumberColumn = " + phoneNumberColumn);
                int smsbodyColumn = cur.getColumnIndex("body");
                int dateColumn = cur.getColumnIndex("date");
                int typeColumn = cur.getColumnIndex("type");

                do {
                    phoneNumber = cur.getString(phoneNumberColumn);
                    Log.d("MyLog", "phoneNumber = " + phoneNumber);
                    smsBoby = cur.getString(smsbodyColumn);
                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss");
                    Date d = new Date(Long.parseLong(cur.getString(dateColumn)));
                    date = dateFormat.format(d);
                    int typeId = cur.getInt(typeColumn);
                    if (typeId == 1) {
                        SmsContent.addItem(SmsContent.createDummyItem(id++, phoneNumber, smsBoby, date));
                    }
                    if (smsBoby == null) smsBoby = "";
                } while (cur.moveToNext());
            }
            cur.close();
            cur = null;
        } catch (SQLiteException ex) {
            Log.e("MyLog", "SQLiteException in getSmsInPhone: " + ex.getMessage());
        }

    }
}