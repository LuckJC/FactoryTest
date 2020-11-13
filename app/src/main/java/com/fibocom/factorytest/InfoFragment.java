package com.fibocom.factorytest;

import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Method;

public class InfoFragment extends Fragment {

    private Context mContext;
    private boolean mIsMultiSim;
    private TelephonyManager mTelephonyManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = getContext();
        if (mContext == null) return;
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
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new MyItemRecyclerViewAdapter(MainContent.ITEMS, 1));
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainContent.ITEMS.clear();

        int phoneCount = mTelephonyManager.getPhoneCount();
        for (int simSlotNumber = 0; simSlotNumber < phoneCount;
             simSlotNumber++) {
            MainContent.addMainItem(MainContent.createMainItem(
                    getTitleForGsmPhone(simSlotNumber).toString(),
                    mTelephonyManager.getImei(simSlotNumber)));
            MainContent.addMainItem(MainContent.createMainItem(
                    getTitleForCdmaPhone(simSlotNumber).toString(),
                    mTelephonyManager.getMeid(simSlotNumber)));
        }
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
            MainContent.addMainItem(MainContent.createMainItem(
                    getTitleForCdmaPhone(slotNumber).toString(),
                    mTelephonyManager.getMeid(slotNumber)));
        } else {
            MainContent.addMainItem(MainContent.createMainItem(
                    getTitleForGsmPhone(slotNumber).toString(),
                    mTelephonyManager.getImei(slotNumber)));
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