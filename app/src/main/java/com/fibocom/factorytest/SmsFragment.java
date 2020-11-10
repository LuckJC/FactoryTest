package com.fibocom.factorytest;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SmsFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private List<MySmsRecyclerViewAdapter.SmsItem> ITEMS = new ArrayList<>();
    private IntentFilter mReceiveFilter;
    private MessageReceiver mMessageReceiver;
    private MySmsRecyclerViewAdapter mySmsRecyclerViewAdapter;

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
            mySmsRecyclerViewAdapter = new MySmsRecyclerViewAdapter(ITEMS);
            recyclerView.setAdapter(mySmsRecyclerViewAdapter);
            DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
            recyclerView.setItemAnimator(defaultItemAnimator);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getSmsInPhone();
        initReceiverSms();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getContext().unregisterReceiver(mMessageReceiver);
    }

    private void getSmsInPhone() {
        final String SMS_URI_ALL = "content://sms/";
        try {
            ContentResolver cr = getContext().getContentResolver();
            String[] projection = new String[]{"_id", "address",
                    "body", "date", "type"};
            String[] args = new String[]{"?=1"};
            Uri uri = Uri.parse(SMS_URI_ALL);
            Cursor cur = cr.query(uri, projection, "type=1", null, "date asc");
            if (cur.moveToFirst()) {
                String phoneNumber;
                String smsBoby;
                String date;
                int phoneNumberColumn = cur.getColumnIndex("address");
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
                        mySmsRecyclerViewAdapter.addItem(phoneNumber, smsBoby, date);
                    }
                } while (cur.moveToNext());
            }
            cur.close();
        } catch (SQLiteException ex) {
            Log.e("MyLog", "SQLiteException in getSmsInPhone: " + ex.getMessage());
        }
    }

    private void initReceiverSms() {
        mReceiveFilter = new IntentFilter();
        mReceiveFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        mMessageReceiver = new MessageReceiver();
        getContext().registerReceiver(mMessageReceiver, mReceiveFilter);

    }

    private class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
                Bundle bundle = intent.getExtras();
                //使用pdu秘钥来提取一个pdus数组
                Object[] pdus = (Object[]) bundle.get("pdus");
                SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < messages.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                //获取发送方号码
                String address = messages[0].getOriginatingAddress();
                //获取短信内容
                String fullMessage = "";
                for (SmsMessage message : messages) {
                    fullMessage += message.getMessageBody();
                }
                Log.d("MyLog", "address: " + address + ", body: " + fullMessage);
                mySmsRecyclerViewAdapter.addItem(address, fullMessage, "now");
            }
        }
    }
}