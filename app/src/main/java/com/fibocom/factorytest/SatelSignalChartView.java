package com.fibocom.factorytest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.TypedValue;

import java.util.List;

/**
 * View for show satellites signal information.
 */
public class SatelSignalChartView extends SatelliteBaseView {

    private static final float PERCENT_80 = 0.8f;
    private static final float PERCENT_75 = 0.75f;
    private static final float PERCENT_50 = 0.5f;
    private static final float PERCENT_25 = 0.25f;
    private static final float RATIO_BASE = 100;
    private static final int BASE_LINE_OFFSET = 6;
    private static final int TEXT_OFFSET = 10;
    private float TEXT_SIZE;
    private int BAR_WIDTH;
    private int satelliteCount = 0;
    private Paint mLinePaint;
    private Paint mLine2Paint;
    private Paint mRectPaint;
    private Paint mRectLinePaint;
    private Paint mTextPaint;
    private Paint mBgPaint;

    /**
     * Constructor function.
     *
     * @param context Context for view running in
     */
    public SatelSignalChartView(Context context) {
        this(context, null, 0);
    }

    /**
     * Constructor function.
     *
     * @param context Context for view running in
     * @param attrs   The attributes of the XML tag that is inflating the view
     */
    public SatelSignalChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * Constructor function.
     *
     * @param context  Context for view running in
     * @param attrs    The attributes of the XML tag that is inflating the view
     * @param defStyle An attribute in the current theme that contains a reference to
     *                 a style resource that supplies default values for the view
     */
    public SatelSignalChartView(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
        onCreateView();
    }

    private void onCreateView() {
        TEXT_SIZE = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());
        BAR_WIDTH = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());

        mRectPaint = new Paint();
        mRectPaint.setAntiAlias(false);
        mRectPaint.setStrokeWidth(2.0f);

        mLinePaint = new Paint();
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(getResources().getColor(R.color.sigview_line_color, null));
        mLinePaint.setStyle(Style.STROKE);
        mLinePaint.setStrokeWidth(1.0f);

        mLine2Paint = new Paint(mLinePaint);
        mLine2Paint.setStrokeWidth(0.5f);

        mRectLinePaint = new Paint(mRectPaint);
        mRectLinePaint.setColor(getResources().getColor(R.color.bar_outline, null));
        mRectLinePaint.setStyle(Style.STROKE);

        mTextPaint = new Paint();
        mTextPaint.setTextAlign(Align.CENTER);
        mTextPaint.setTextSize(TEXT_SIZE);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(getResources().getColor(R.color.sigview_text_color, null));

        mBgPaint = new Paint();
        mBgPaint.setColor(getResources().getColor(R.color.sigview_background, null));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float viewWidth = getWidth();
        float viewHeight = getHeight();
        float sigRectMaxHeight = (float) Math.floor(viewHeight * PERCENT_80);
        float baseLineY = sigRectMaxHeight + BASE_LINE_OFFSET;
        float rectRatio = sigRectMaxHeight / RATIO_BASE;

        canvas.drawPaint(mBgPaint);
        canvas.drawLine(0, baseLineY, viewWidth, baseLineY, mLinePaint);
        float[] line2YArr = {baseLineY - sigRectMaxHeight * PERCENT_25,
                baseLineY - sigRectMaxHeight * PERCENT_50,
                baseLineY - sigRectMaxHeight * PERCENT_75,
                baseLineY - sigRectMaxHeight};
        for (int i = 0; i < line2YArr.length; i++) {
            canvas.drawLine(0, line2YArr[i], viewWidth, line2YArr[i], mLine2Paint);
        }
        SatelliteInfoManager simgr = getSatelliteInfoManager();
        if (simgr != null) {
            List<SatelliteInfo> stInfoList = simgr.getSatelInfoList();
            float bgRectWidth = BAR_WIDTH;
            float barWidth = (float) Math.floor(bgRectWidth * PERCENT_75);
            float margin = (bgRectWidth - barWidth) / 2;
            int newSatelliteCount = stInfoList.size();
            for (int i = 0; i < newSatelliteCount; i++) {
                SatelliteInfo si = stInfoList.get(i);
                float barHeight = si.mSnr * rectRatio;
                float left = i * bgRectWidth + margin;
                float top = baseLineY - barHeight;
                float center = left + bgRectWidth / 2;
                canvas.drawRect(left, top, left + barWidth, baseLineY, getSigBarPaint(si, simgr));
                mRectLinePaint.setColor(si.mColor);
                canvas.drawRect(left, top, left + barWidth, baseLineY, mRectLinePaint);
                float textOffset = bgRectWidth - barWidth;
                canvas.drawText(String.valueOf(si.mPrn), center,
                        baseLineY + textOffset + TEXT_OFFSET, mTextPaint);
                String snrStr = String.format("%3.1f", si.mSnr);
                canvas.drawText(snrStr, center, top - textOffset, mTextPaint);
            }
            if (satelliteCount != newSatelliteCount) {
                satelliteCount = newSatelliteCount;
                requestLayout();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int minimumWidth = getSuggestedMinimumWidth();
        final int minimumHeight = getSuggestedMinimumHeight();
        int width = measureWidth(minimumWidth, widthMeasureSpec);
        int height = measureHeight(minimumHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureWidth(int defaultWidth, int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.AT_MOST:
                defaultWidth = specSize;
                break;
            case MeasureSpec.EXACTLY:
                defaultWidth = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                SatelliteInfoManager simgr = getSatelliteInfoManager();
                if (simgr != null) {
                    List<SatelliteInfo> stInfoList = simgr.getSatelInfoList();
                    defaultWidth = BAR_WIDTH * stInfoList.size();
                }
                defaultWidth = Math.max(defaultWidth, specSize);
        }
        return defaultWidth;
    }


    private int measureHeight(int defaultHeight, int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        switch (specMode) {
            case MeasureSpec.AT_MOST:
                defaultHeight = specSize;
                break;
            case MeasureSpec.EXACTLY:
                defaultHeight = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                defaultHeight = Math.max(defaultHeight, specSize);
                break;
        }
        return defaultHeight;
    }

    private Paint getSigBarPaint(SatelliteInfo info, SatelliteInfoManager manager) {
        if (!manager.isUsedInFix(SatelliteInfoManager.PRN_ANY)) {
            mRectPaint.setColor(getResources().getColor(R.color.bar_used));
            mRectPaint.setStyle(Style.STROKE);
        } else {
            if (manager.isUsedInFix(info.mPrn)) {
                mRectPaint.setColor(getResources().getColor(R.color.bar_used));
                mRectPaint.setStyle(Style.FILL);
            } else {
                mRectPaint.setColor(getResources().getColor(R.color.bar_unused));
                mRectPaint.setStyle(Style.FILL);
            }
        }
        return mRectPaint;
    }
}
