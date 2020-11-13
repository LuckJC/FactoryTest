package com.fibocom.factorytest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkFragment extends Fragment {

    //private static final String TAG = "NetworkFragment";
    private Context mContext;
    private MyItemRecyclerViewAdapter myAdapter;
    private TelephonyManager mTelephonyManager;
    private SubscriptionManager mSubscriptionManager;
    private List<MyPhoneStateListener> myPhoneStateListener;
    private Map<MyPhoneStateListener, TelephonyManager> myPhoneStateListenerTelephonyManagerMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        if (mContext == null) return;
        mTelephonyManager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        mSubscriptionManager = (SubscriptionManager) mContext.getSystemService(
                Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        myPhoneStateListener = new ArrayList<>();
        myPhoneStateListenerTelephonyManagerMap = new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_network_list, container, false);
        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            myAdapter = new MyItemRecyclerViewAdapter(MainContent.ITEMS, 1);
            recyclerView.setAdapter(myAdapter);
            DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
            recyclerView.setItemAnimator(defaultItemAnimator);
        }
        return view;
    }

    @SuppressLint("HardwareIds")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MainContent.ITEMS.clear();
        @SuppressLint("MissingPermission") List<SubscriptionInfo> list = mSubscriptionManager.getActiveSubscriptionInfoList();
        if (list != null && !list.isEmpty()) {
            for (SubscriptionInfo subInfo : list) {
                TelephonyManager telephonyManager = mTelephonyManager.createForSubscriptionId(subInfo.getSubscriptionId());
                //subInfo.getSubscriptionId();
                MainContent.addMainItem(MainContent.createMainItem(
                        getString(R.string.status_imsi_id),
                        telephonyManager.getSubscriberId()));

                MainContent.addMainItem(MainContent.createMainItem(
                        getString(R.string.status_icc_id),
                        telephonyManager.getSimSerialNumber()));

                MainContent.addMainItem(MainContent.createMainItem(
                        telephonyManager.getNetworkOperatorName(),
                        ""));

                if (isSubscriptionInService(subInfo.getSubscriptionId())) {
                    MainContent.addMainItem(MainContent.createMainItem(
                            getString(R.string.network_registered),
                            getString(R.string.yes)));
                    MyPhoneStateListener listener = new MyPhoneStateListener(MainContent.ITEMS.size() - 1);
                    myPhoneStateListener.add(listener);
                    myPhoneStateListenerTelephonyManagerMap.put(listener, telephonyManager);
                } else {
                    MainContent.addMainItem(MainContent.createMainItem(
                            getString(R.string.network_registered),
                            getString(R.string.no)));
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //mTelephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        for (MyPhoneStateListener listener : myPhoneStateListener) {
            TelephonyManager telephonyManager = myPhoneStateListenerTelephonyManagerMap.get(listener);
            if (telephonyManager != null) {
                telephonyManager.listen(listener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //mTelephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        for (MyPhoneStateListener listener : myPhoneStateListener) {
            TelephonyManager telephonyManager = myPhoneStateListenerTelephonyManagerMap.get(listener);
            if (telephonyManager != null) {
                telephonyManager.listen(listener, PhoneStateListener.LISTEN_NONE);
            }
        }
    }

    public ServiceState getServiceStateForSubscriber(int subId) {
        try {
            Method method = mTelephonyManager.getClass().getMethod("getServiceStateForSubscriber", int.class);
            return (ServiceState) method.invoke(mTelephonyManager, subId);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isSubscriptionInService(int subId) {
        if (mTelephonyManager != null) {
            return getServiceStateForSubscriber(subId).getState()
                    == ServiceState.STATE_IN_SERVICE;
        }
        return false;
    }

    class MyPhoneStateListener extends PhoneStateListener {
        private final int position;
        private final MainContent.MainItem mainItem;

        public MyPhoneStateListener(int position) {
            super();
            this.position = position;
            mainItem = MainContent.ITEMS.get(position);
        }

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);

            try {
                Method method = signalStrength.getClass().getMethod("getDbm");
                int dbm = (int) method.invoke(signalStrength);
                if (mainItem != null) {
                    mainItem.details = dbm + " dmb";
                    myAdapter.notifyItemChanged(position);
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        }
    }
}