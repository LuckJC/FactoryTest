package com.fibocom.factorytest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GpsFragment extends Fragment {

    private static final String TAG = "GpsFragment";

    private LocationManager mLocationManager;
    private SatelSignalChartView mSignalView;
    private SatelliteInfoManager mSatelInfoManager = null;
    private String mProvider = "";
    private String mStatus = "";
    private TextView tvProvider;
    private TextView tvTime;
    private TextView tvDate;
    private TextView tvStatus;
    private TextView tvLat;
    private TextView tvLon;
    private TextView tvAlt;
    private TextView tvAcc;
    private TextView tvSpeed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSatelInfoManager = new SatelliteInfoManager();
        mLocationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        mStatus = getString(R.string.gps_status_unknown);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSignalView = view.findViewById(R.id.signal_view);
        mSignalView.requestUpdate(mSatelInfoManager);

        tvProvider = view.findViewById(R.id.tv_provider);
        tvProvider.setOnClickListener(v -> {
            if (!mLocationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        tvTime = view.findViewById(R.id.tv_time);
        tvDate = view.findViewById(R.id.tv_date);
        tvStatus = view.findViewById(R.id.tv_status);
        tvLat = view.findViewById(R.id.tv_latitude);
        tvLon = view.findViewById(R.id.tv_longitude);
        tvAlt = view.findViewById(R.id.tv_altitude);
        tvAcc = view.findViewById(R.id.tv_accuracy);
        tvSpeed = (TextView) view.findViewById(R.id.tv_speed);

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onResume() {
        super.onResume();
        if (mLocationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            mProvider = getString(
                    R.string.provider_status_enabled,
                    LocationManager.GPS_PROVIDER);
        } else {
            mProvider = getString(
                    R.string.provider_status_disabled,
                    LocationManager.GPS_PROVIDER);
        }
        tvProvider.setText(mProvider);
        tvStatus.setText(mStatus);
        mLocationManager.addGpsStatusListener(myGpsStatusListener);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, myLocationListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        mLocationManager.removeGpsStatusListener(myGpsStatusListener);
        mLocationManager.removeUpdates(myLocationListener);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private GpsStatus.Listener myGpsStatusListener = new GpsStatus.Listener() {
        @Override
        public void onGpsStatusChanged(int event) {
            switch (event) {
                case GpsStatus.GPS_EVENT_STARTED:
                    mStatus = getString(R.string.gps_status_started);
                    break;
                case GpsStatus.GPS_EVENT_STOPPED:
                    mStatus = getString(R.string.gps_status_stopped);
                    break;
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    mStatus = getString(R.string.gps_status_first_fix);
                    break;
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    @SuppressLint("MissingPermission") GpsStatus gpsStatus = mLocationManager.getGpsStatus(null);
                    mSatelInfoManager.updateSatelliteInfo(gpsStatus);
                    mSignalView.requestUpdate(mSatelInfoManager);
                    boolean isFixed = mSatelInfoManager.isUsedInFix(SatelliteInfoManager.PRN_ANY);
                    if (!isFixed) {
                        mStatus = getString(R.string.gps_status_unavailable);
                    } else {
                        mStatus = getString(R.string.gps_status_available);
                    }
                    break;
                default:
                    break;
            }
            tvStatus.setText(mStatus);
        }
    };

    LocationListener myLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(@NonNull Location location) {
            Date d = new Date(location.getTime());
            SimpleDateFormat dateFormat = new SimpleDateFormat("z yyyy/MM/dd");
            String date = dateFormat.format(d);
            tvDate.setText(date);

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            String time = timeFormat.format(d);
            tvTime.setText(time);

            tvLat.setText(String.valueOf(location.getLatitude()));
            tvLon.setText(String.valueOf(location.getLongitude()));
            tvAlt.setText(String.valueOf(location.getAltitude()));
            tvAcc.setText(String.valueOf(location.getAccuracy()));
            tvSpeed.setText(String.valueOf(location.getSpeed()));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            mProvider = getString(
                    R.string.provider_status_enabled,
                    LocationManager.GPS_PROVIDER);
            tvProvider.setText(mProvider);
            mStatus = getString(R.string.gps_status_unknown);
            tvStatus.setText(mStatus);
        }

        @Override
        public void onProviderDisabled(String provider) {
            mProvider = getString(
                    R.string.provider_status_disabled,
                    LocationManager.GPS_PROVIDER);
            tvProvider.setText(mProvider);
            mStatus = getString(R.string.gps_status_unavailable);
            tvStatus.setText(mStatus);
        }
    };
}