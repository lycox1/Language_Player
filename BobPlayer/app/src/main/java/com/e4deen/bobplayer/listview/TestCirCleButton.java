package com.e4deen.bobplayer.listview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.e4deen.bobplayer.R;

/**
 * Created by user on 2017-02-18.
 */

public class TestCirCleButton extends RelativeLayout implements GestureDetector.OnGestureListener {

    static String LOG_TAG = "Jog_Player_TestCirCleButton";
    int mLayoutWidth, mLayoutHeight;
    Context mContext;
    private Bitmap bmpRotorOn;
    private ImageView ivRotor;
    private GestureDetector 	gestureDetector;

    public TestCirCleButton(Context context, int layoutWidth, int layoutHeight) {
        super(context);

        mContext = context;
        mLayoutWidth = (layoutWidth > layoutHeight) ? layoutHeight:layoutWidth;
        mLayoutHeight = (layoutWidth > layoutHeight) ? layoutHeight:layoutWidth;

        int rotoron = R.drawable.center_circle;
        Bitmap srcon = BitmapFactory.decodeResource(mContext.getResources(), rotoron);

        float scaleWidth = ((float) mLayoutWidth) / srcon.getWidth();
        float scaleHeight = ((float) mLayoutHeight) / srcon.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        bmpRotorOn = Bitmap.createBitmap(
                srcon, 0, 0,
                srcon.getWidth(),srcon.getHeight() , matrix , true);

        Log.d(LOG_TAG, "mLayoutWidth " + mLayoutWidth + ", mLayoutHeight " + mLayoutHeight);

        ivRotor = new ImageView(context);
        ivRotor.setImageBitmap(bmpRotorOn);
        RelativeLayout.LayoutParams lp_ivKnob = new RelativeLayout.LayoutParams(mLayoutWidth,mLayoutHeight);//LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp_ivKnob.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(ivRotor, lp_ivKnob);

        gestureDetector = new GestureDetector(getContext(), this);
    }

    private float cartesianToPolar(float x, float y) {
        return (float) -Math.toDegrees(Math.atan2(x - 0.5f, y - 0.5f));
    }


    public void setRotorPosAngle(float deg) {
        Log.d(LOG_TAG, "setRotorPosAngle deg " + deg);
        if (deg >= 210 || deg <= 150) {
            if (deg > 180) deg = deg - 360;
            Matrix matrix=new Matrix();
            ivRotor.setScaleType(ImageView.ScaleType.MATRIX);
            matrix.postRotate((float) deg, mLayoutWidth/2, mLayoutHeight/2);//getWidth()/2, getHeight()/2);
            ivRotor.setImageMatrix(matrix);
        }
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        float x = e2.getX() / ((float) getWidth());
        float y = e2.getY() / ((float) getHeight());

        float rotDegrees = cartesianToPolar(1 - x, 1 - y);// 1- to correct our custom axis direction

        Log.d(LOG_TAG, "rotDegrees " + rotDegrees + ", mLayoutWidth " + mLayoutWidth + ", mLayoutHeight " + mLayoutHeight);

        if (! Float.isNaN(rotDegrees)) {
            // instead of getting 0-> 180, -180 0 , we go for 0 -> 360
            float posDegrees = rotDegrees;
            if (rotDegrees < 0) posDegrees = 360 + rotDegrees;

            // deny full rotation, start start and stop point, and get a linear scale
            if (posDegrees > 210 || posDegrees < 150) {
                // rotate our imageview
                setRotorPosAngle(posDegrees);
                // get a linear scale
                float scaleDegrees = rotDegrees + 150; // given the current parameters, we go from 0 to 300
                // get position percent
                int percent = (int) (scaleDegrees / 3);
                //if (m_listener != null) m_listener.onRotate(percent);
                return true; //consumed
            } else
                return false;
        } else
            return false; // not consumed
    }

    @Override public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) return true;
        else return super.onTouchEvent(event);
    }

    public boolean onDown(MotionEvent event) {
        float x = event.getX() / ((float) getWidth());
        float y = event.getY() / ((float) getHeight());

        return true;
    }

    public boolean onSingleTapUp(MotionEvent e) {  return true;  }
    public void onShowPress(MotionEvent e) {}

    public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) { return false; }

    public void onLongPress(MotionEvent e) {	}
}
