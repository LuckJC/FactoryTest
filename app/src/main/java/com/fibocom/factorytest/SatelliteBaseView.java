package com.fibocom.factorytest;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;


/**
 * Base class for satellites view.
 */
public class SatelliteBaseView extends View {

    /**
     * Constructor function.
     *
     * @param context Context for view running in
     */
    public SatelliteBaseView(Context context) {
        this(context, null, 0);
        // TODO Auto-generated constructor stub
    }

    /**
     * Constructor function.
     *
     * @param context Context for view running in
     * @param attrs   The attributes of the XML tag that is inflating the view
     */
    public SatelliteBaseView(Context context, AttributeSet attrs) {
        this(context, null, 0);
    }

    /**
     * Constructor function.
     *
     * @param context  Context for view running in
     * @param attrs    The attributes of the XML tag that is inflating the view
     * @param defStyle An attribute in the current theme that contains a reference to
     *                 a style resource that supplies default values for the view
     */
    public SatelliteBaseView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    //private static final String TAG = "SatelliteBaseView";
    private SatelliteInfoManager mSiManager = null;


    /**
     * Force to update.
     *
     * @param manager SatelliteInfoManager to use
     */
    public void requestUpdate(SatelliteInfoManager manager) {
        mSiManager = manager;
        this.postInvalidate();
    }

    protected SatelliteInfoManager getSatelliteInfoManager() {
        return mSiManager;
    }

}
