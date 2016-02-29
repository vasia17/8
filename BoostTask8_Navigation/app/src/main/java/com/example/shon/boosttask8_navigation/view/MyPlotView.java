package com.example.shon.boosttask8_navigation.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.shon.boosttask8_navigation.MainActivity;
import com.example.shon.boosttask8_navigation.R;
import com.example.shon.boosttask8_navigation.entity.Sample;

import java.util.List;

public class MyPlotView extends View {

    private List<Sample> mSamples;

    private Paint mAxisX, mAxisY, mLineX, mLineY, mLineZ;

    public MyPlotView(Context context) {
        this(context, null);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public MyPlotView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mAxisX = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAxisX.setStyle(Paint.Style.STROKE);
        mAxisX.setColor(Color.rgb(255, 153, 51));
        mAxisX.setStrokeWidth(getResources().getDisplayMetrics().density * 2.0f);

        mAxisY = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAxisY.setStyle(Paint.Style.STROKE);
        mAxisY.setColor(Color.rgb(255, 153, 51));
        mAxisY.setStrokeWidth(getResources().getDisplayMetrics().density * 2.0f);

        mLineX = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLineX.setStyle(Paint.Style.STROKE);
        mLineX.setColor(Color.rgb(1, 168, 33));
        mLineX.setStrokeWidth(getResources().getDisplayMetrics().density * 2.0f);

        mLineY = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLineY.setStyle(Paint.Style.STROKE);
        mLineY.setColor(Color.rgb(220, 56, 71));
        mLineY.setStrokeWidth(getResources().getDisplayMetrics().density * 2.0f);

        mLineZ = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLineZ.setStyle(Paint.Style.STROKE);
        mLineZ.setColor(Color.rgb(88, 144, 255));
        mLineZ.setStrokeWidth(getResources().getDisplayMetrics().density * 2.0f);

        setBackground(getResources().getDrawable(R.drawable.grid));
    }

    public void setSamples(List<Sample> samples) {
        this.mSamples = samples;
    }

    public void onPlotDataChanged() {
        ViewCompat.postInvalidateOnAnimation(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mSamples == null || mSamples.isEmpty()) {
            return;
        }

        Log.d(MainActivity.MAIN_TAG, "MyPlotView: onDraw");

        float elementsOnScreen = 25;
        if (mSamples.size() > elementsOnScreen) {
            elementsOnScreen = mSamples.size() + 3;
        }

        float midHeight = getHeight() / 2.0f;
        float unitHeight = getHeight() / 22.0f;
        float unitWidth = (float) getWidth() / elementsOnScreen;

        canvas.drawLine(0, midHeight, getWidth(), midHeight, mAxisX);
        canvas.drawLine(2 * unitWidth, 0, 2 * unitWidth, getHeight(), mAxisY);
        canvas.drawText("+10", unitWidth, -unitHeight * 10 + midHeight, new Paint(Paint.ANTI_ALIAS_FLAG));
        canvas.drawText("  0", unitWidth, midHeight, new Paint(Paint.ANTI_ALIAS_FLAG));
        canvas.drawText("-10", unitWidth, -unitHeight * -10 + midHeight , new Paint(Paint.ANTI_ALIAS_FLAG));

        float startX = 2 * unitWidth, startY1 = midHeight, startY2 = midHeight, startY3 = midHeight,
                stopX, stopY1, stopY2, stopY3;

        for (int i = mSamples.size() - 1; i >= 0; i--) {
            stopX = startX + unitWidth;

            stopY1 = -unitHeight * mSamples.get(i).getX() + midHeight;
            canvas.drawLine(startX, startY1, stopX, stopY1, mLineX);

            stopY2 = -unitHeight * mSamples.get(i).getY() + midHeight;
            canvas.drawLine(startX, startY2, stopX, stopY2, mLineY);

            stopY3 = -unitHeight * mSamples.get(i).getZ() + midHeight;
            canvas.drawLine(startX, startY3, stopX, stopY3, mLineZ);

            startX = stopX;
            startY1 = stopY1;
            startY2 = stopY2;
            startY3 = stopY3;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }
}
