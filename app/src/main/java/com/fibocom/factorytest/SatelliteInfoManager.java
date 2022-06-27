package com.fibocom.factorytest;

import android.location.GnssStatus;
import android.location.GnssStatus;

import java.util.ArrayList;
import java.util.Collections;
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
            0xffff0000, //red
            0xff00ff00, //green
            0xFFBB86FC, //purple
            0xff909090 //gray
    };

    List<SatelliteInfo> mSatelInfoList;

    /**
     * Construction function.
     */
    public SatelliteInfoManager() {
        mSatelInfoList = new ArrayList<>();
    }

    void updateSatelliteInfo(GnssStatus gnssStatus) {
        if (mSatelInfoList != null) {
            mSatelInfoList.clear();
        } else {
            mSatelInfoList = new ArrayList<>();
        }

        int maxSatellites = gnssStatus.getSatelliteCount();
        for(int i = 0; i < maxSatellites; i++) {
            SatelliteInfo satInfo = new SatelliteInfo();
            satInfo.mPrn = gnssStatus.getSvid(i);
            satInfo.mSnr = gnssStatus.getCn0DbHz(i);
            satInfo.mElevation = gnssStatus.getElevationDegrees(i);
            satInfo.mAzimuth = gnssStatus.getAzimuthDegrees(i);
            satInfo.mUsedInFix = gnssStatus.usedInFix(i);
            switch (gnssStatus.getConstellationType(i)) {
                case GnssStatus.CONSTELLATION_GPS:
                    satInfo.mColor = SATEL_COLOR[0];
                    break;
                case GnssStatus.CONSTELLATION_SBAS:
                    satInfo.mColor = SATEL_COLOR[1];
                    satInfo.mPrn -= 87;
                    break;
                case GnssStatus.CONSTELLATION_GLONASS:
                    satInfo.mColor = SATEL_COLOR[2];
                    satInfo.mPrn += 64;
                    break;
                case GnssStatus.CONSTELLATION_QZSS:
                    satInfo.mColor = SATEL_COLOR[3];
                    break;
                case GnssStatus.CONSTELLATION_BEIDOU:
                    satInfo.mColor = SATEL_COLOR[4];
                    satInfo.mPrn += 200;
                    break;
                case GnssStatus.CONSTELLATION_GALILEO:
                    satInfo.mColor = SATEL_COLOR[5];
                    satInfo.mPrn += 300;
                    break;
                case GnssStatus.CONSTELLATION_IRNSS:
                    satInfo.mColor = SATEL_COLOR[6];
                    satInfo.mPrn += 900;
                    break;
                default:
                    satInfo.mColor = SATEL_COLOR[7];
                    break;
            }
            mSatelInfoList.add(satInfo);
        }
        Collections.sort(mSatelInfoList);
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
