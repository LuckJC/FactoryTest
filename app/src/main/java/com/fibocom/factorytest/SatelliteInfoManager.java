package com.fibocom.factorytest;

import android.location.GpsSatellite;
import android.location.GpsStatus;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Class for satellites list information management.
 */
public class SatelliteInfoManager {

    public static final int PRN_ANY = -1;
    public static final int PRN_ALL = -2;

    private static final int[] SATEL_COLOR = {
            0xff00ffff, //cyan
            0xffffff00, //yellow
            0xffffffff, //white
            0xff0000ff, //blue
            0xff00ff00, //green
            0xffff0000 //red
    };

    List<SatelliteInfo> mSatelInfoList;

    /**
     * Construction function.
     */
    public SatelliteInfoManager() {
        mSatelInfoList = new ArrayList<>();
    }

    void updateSatelliteInfo(GpsStatus gpsStatus) {
        if (mSatelInfoList != null) {
            mSatelInfoList.clear();
        } else {
            mSatelInfoList = new ArrayList<>();
        }

        int maxSatellites = gpsStatus.getMaxSatellites();
        Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
        int count = 0;
        while (iters.hasNext() && count <= maxSatellites) {
            count++;
            GpsSatellite s = iters.next();
            SatelliteInfo satInfo = new SatelliteInfo();
            satInfo.mPrn = s.getPrn();
            satInfo.mSnr = s.getSnr();
            satInfo.mElevation = s.getElevation();
            satInfo.mAzimuth = s.getAzimuth();
            satInfo.mUsedInFix = s.usedInFix();
            if (satInfo.mPrn >= 1 && satInfo.mPrn <= 32) { // GPS
                satInfo.mColor = SATEL_COLOR[0];
            } else if (satInfo.mPrn >= 65 && satInfo.mPrn <= 96) { // GLONASS
                satInfo.mColor = SATEL_COLOR[1];
            } else if (satInfo.mPrn >= 201 && satInfo.mPrn <= 237) { // BDS
                satInfo.mColor = SATEL_COLOR[2];
            } else if (satInfo.mPrn >= 401 && satInfo.mPrn <= 436) { // GALIEO
                satInfo.mColor = SATEL_COLOR[3];
            } else if (satInfo.mPrn >= 193 && satInfo.mPrn <= 197) { // QZSS
                satInfo.mColor = SATEL_COLOR[4];
            } else if (satInfo.mPrn >= 33 && satInfo.mPrn <= 64) { // SBAS
                satInfo.mColor = SATEL_COLOR[5];
            }
            mSatelInfoList.add(satInfo);
        }
    }

    public List<SatelliteInfo> getSatelInfoList() {
        return mSatelInfoList;
    }

    SatelliteInfo getSatelliteInfo(int prn) {
        for (SatelliteInfo si : mSatelInfoList) {
            if (si.mPrn == prn) {
                return si;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{Satellite Count:").append(mSatelInfoList.size());
        for (SatelliteInfo info : mSatelInfoList) {
            builder.append(info.toString());
        }
        builder.append("}");
        return builder.toString();
    }

    void clearSatelInfos() {
        mSatelInfoList.clear();
    }

    boolean isUsedInFix(int prn) {
        boolean result = false;
        if (prn == PRN_ALL && mSatelInfoList.size() > 0) {
            result = true;
        }
        for (SatelliteInfo si : mSatelInfoList) {
            if (prn == PRN_ALL) {
                if (!si.mUsedInFix) {
                    result = false;
                    break;
                }
            } else if (prn == PRN_ANY) {
                if (si.mUsedInFix) {
                    result = true;
                    break;
                }
            } else if (prn == si.mPrn) {
                result = si.mUsedInFix;
                break;
            }
        }
        return result;
    }
}
