package com.fibocom.factorytest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkFragment extends Fragment {

    private static final String TAG = "NetworkFragment";
    private TextView mTypeTv;
    private TextView mRssiTv;
    private TextView mRssiTv2;
    private ConnectivityManager mConnectivityManager;
    private TelephonyManager mTelephonyManager;
    private SubscriptionManager mSubscriptionManager;
    private List<MyPhoneStateListener> myPhoneStateListener;
    private Map<MyPhoneStateListener, TelephonyManager> myPhoneStateListenerTelephonyManagerMap;

    public NetworkFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConnectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        mTelephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        mSubscriptionManager = SubscriptionManager.from(getContext());
        myPhoneStateListener = new ArrayList<>();
        myPhoneStateListenerTelephonyManagerMap = new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_network, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTypeTv = view.findViewById(R.id.typeTv);
        mRssiTv = view.findViewById(R.id.rssiTv);
        mRssiTv2 = view.findViewById(R.id.rssiTv2);

        List<TextView> textList = new ArrayList<>();
        textList.add(mRssiTv);
        textList.add(mRssiTv2);

        int index = 0;
        @SuppressLint("MissingPermission") List<SubscriptionInfo> list = mSubscriptionManager.getActiveSubscriptionInfoList();
        if (list != null && !list.isEmpty()) {
            boolean useSeparator = false;
            StringBuilder builder = new StringBuilder();
            for (SubscriptionInfo subInfo : list) {
                if (isSubscriptionInService(subInfo.getSubscriptionId())) {
                    TelephonyManager telephonyManager = mTelephonyManager.createForSubscriptionId(subInfo.getSubscriptionId());
                    MyPhoneStateListener listener = new MyPhoneStateListener(textList.get(index++));
                    myPhoneStateListener.add(listener);
                    myPhoneStateListenerTelephonyManagerMap.put(listener, telephonyManager);

                    if (useSeparator) builder.append(", ");
                    builder.append(getNetworkOperatorName(subInfo.getSubscriptionId()));
                    useSeparator = true;
                }
            }
            mTypeTv.setText(builder.toString());
        } else {
            //mSummary = MobileNetworkUtils.getCurrentCarrierNameForDisplay(mContext);
            mTypeTv.setText(mTelephonyManager.getNetworkOperatorName());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onResume() {
        super.onResume();
        //mTelephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        for (MyPhoneStateListener listener : myPhoneStateListener) {
            TelephonyManager telephonyManager = myPhoneStateListenerTelephonyManagerMap.get(listener);
            telephonyManager.listen(listener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //mTelephonyManager.listen(myPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        for (MyPhoneStateListener listener : myPhoneStateListener) {
            TelephonyManager telephonyManager = myPhoneStateListenerTelephonyManagerMap.get(listener);
            telephonyManager.listen(listener, PhoneStateListener.LISTEN_NONE);
        }
    }

    public String getNetworkOperatorName(int subId) {
        try {
            Method method = mTelephonyManager.getClass().getMethod("getNetworkOperatorName", int.class);
            String name = (String) method.invoke(mTelephonyManager, subId);
            return name;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ServiceState getServiceStateForSubscriber(int subId) {
        try {
            Method method = mTelephonyManager.getClass().getMethod("getServiceStateForSubscriber", int.class);
            ServiceState state = (ServiceState) method.invoke(mTelephonyManager, subId);
            return state;
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isSubscriptionInService(int subId) {
        if (mTelephonyManager != null) {
            if (getServiceStateForSubscriber(subId).getState()
                    == ServiceState.STATE_IN_SERVICE) {
                return true;
            }
        }
        return false;
    }

    class MyPhoneStateListener extends PhoneStateListener {
        TextView rssiTv;

        public MyPhoneStateListener(TextView rssiTv) {
            super();
            this.rssiTv = rssiTv;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);

            try {
                Method method = signalStrength.getClass().getMethod("getDbm");
                int dbm = (int) method.invoke(signalStrength);
                rssiTv.setText(dbm + " dbm");
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}