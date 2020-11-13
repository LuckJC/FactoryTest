package com.fibocom.factorytest;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Method;
import java.util.List;

public class InfoFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private boolean mIsMultiSim;
    private TelephonyManager mTelephonyManager;
    private SubscriptionManager mSubscriptionManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }

        mTelephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        mIsMultiSim = mTelephonyManager.getPhoneCount() > 1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyItemRecyclerViewAdapter(MainContent.INFO_ITEMS, 1));
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        MainContent.INFO_ITEMS.clear();
        MainContent.INFO_ITEMS_MAP.clear();
        mSubscriptionManager = (SubscriptionManager) getContext().getSystemService(
                Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        //int defaultDataSub[] = mSubscriptionManager.getSubscriptionIds(1);
        List<SubscriptionInfo> subscriptionInfoList =
                mSubscriptionManager.getActiveSubscriptionInfoList();
        if (subscriptionInfoList != null) {
            for (SubscriptionInfo info : subscriptionInfoList) {
                Log.d("MyLog", "info: " + info);
            }
        }

        int phoneCount = mTelephonyManager.getPhoneCount();
        getIMSI();
        Log.d("MyLog", "phoneCount = " + phoneCount);
        for (int simSlotNumber = 0; simSlotNumber < phoneCount;
             simSlotNumber++) {
            MainContent.addInfoItem(MainContent.createInfoItem(simSlotNumber + 1,
                    getTitleForGsmPhone(simSlotNumber).toString(),
                    mTelephonyManager.getImei(simSlotNumber)));
            MainContent.addInfoItem(MainContent.createInfoItem(simSlotNumber + 2,
                    getTitleForCdmaPhone(simSlotNumber).toString(),
                    mTelephonyManager.getMeid(simSlotNumber)));
        }

        MainContent.addInfoItem(MainContent.createInfoItem(2 * phoneCount + 1,
                getString(R.string.status_imsi_id).toString(),
                mTelephonyManager.getSubscriberId()));
        MainContent.addInfoItem(MainContent.createInfoItem(2 * phoneCount + 2,
                getString(R.string.status_icc_id).toString(),
                mTelephonyManager.getSimSerialNumber()));
    }

    public static int getCurrentPhoneType(TelephonyManager telephonyManager, int subid) {
        Class<?> classType;
        int type = 0;
        try {
            classType = Class.forName("android.telephony.TelephonyManager");
            Method method = classType.getMethod("getCurrentPhoneType", int.class);
            type = (int) method.invoke(telephonyManager, new Object[]{subid});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return type;
    }

    private void getIMEIOrEMID(int slotNumber, boolean isCDMAPhone) {
        if (isCDMAPhone) {
            getTitleForCdmaPhone(slotNumber);
            MainContent.addInfoItem(MainContent.createInfoItem(1,
                    getTitleForCdmaPhone(slotNumber).toString(),
                    mTelephonyManager.getMeid(slotNumber)));
        } else {
            MainContent.addInfoItem(MainContent.createInfoItem(1,
                    getTitleForGsmPhone(slotNumber).toString(),
                    mTelephonyManager.getImei(slotNumber)));
        }
    }

    public String getIMSI() {
        try {
            String imsi = mTelephonyManager.getSubscriberId();
            if (null == imsi) {
                imsi = "";
            }
            Log.d("MyLog", "IMSI: " + imsi);
            return imsi;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    private CharSequence getTitleForGsmPhone(int simSlot) {
        return mIsMultiSim ? getString(R.string.imei_multi_sim, simSlot + 1)
                : getString(R.string.status_imei);
    }

    private CharSequence getTitleForCdmaPhone(int simSlot) {
        return mIsMultiSim ? getString(R.string.meid_multi_sim, simSlot + 1)
                : getString(R.string.status_meid_number);
    }
}