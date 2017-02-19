package com.e4deen.bobplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

/**
 * Created by user on 2016-12-21.
 */
public class CircleButton extends ImageView {

    static String LOG_TAG = "Jog_Player_CircleTouchEvent";
    int normalBitmapId = R.drawable.ic_circle_release;
    int size = 0; // 휠 전체 사이즈 (가로 세로 대칭)
    double proportion_of_startButton = 0.381578; // 전체에서 start/pause 버튼의 비율. 정가운데를 0으로 놓은 비율
    int jog_drag_state, vibe_count = 0;
    int old_deg, new_deg, unit_mSec = 0;
    double radToDegree = 180 / Math.PI;
    MediaPlayerController mMediaPlayerController;
    Context mContext;
    Vibe vibe;
    SeekBar seekBar;
    private static Bitmap imageOriginal, imageScaled;
    private static Matrix matrix;
    private int dialerHeight, dialerWidth;

    int accum_Deg, accum_mSec;

    public CircleButton(Context context) {
        super(context);
        mContext = context;
        vibe = new Vibe(mContext);
    }

    public CircleButton(Context context, AttributeSet atts) {
        super(context, atts);
        mContext = context;
        vibe = new Vibe(mContext);

//        setBackgroundResource(normalBitmapId);
    }

    void setMediaPlayerController(MediaPlayerController mediaPlayerController) {
        mMediaPlayerController = mediaPlayerController;
    }

    void init() {

        imageOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.center_circle);
        matrix = new Matrix();

        dialerHeight = this.getHeight();
        dialerWidth = this.getWidth();

        Log.d(LOG_TAG, "dialerHeight " + dialerHeight + ", dialerWidth " + dialerWidth + ", imageOriginal.getWidth() " + imageOriginal.getWidth() + ", imageOriginal.getHeight()" + imageOriginal.getHeight() );

        Matrix resize = new Matrix();
        resize.postScale((float)Math.min(dialerWidth, dialerHeight) / (float)imageOriginal.getWidth(), (float)Math.min(dialerWidth, dialerHeight) / (float)imageOriginal.getHeight());
        imageScaled = Bitmap.createBitmap(imageOriginal, 0, 0, imageOriginal.getWidth(), imageOriginal.getHeight(), resize, false);

        setUnitSec(2);
    }



    void setUnitSec(int sec) {

        unit_mSec = (int)((float)((float)sec / 90) * 1000);
        Log.d(LOG_TAG, "setUnitSec sec " + sec + ", unit_mSec " + unit_mSec);
    }

    double convertAxis(float point, int axis) {

        double modifiedPoint = (double)(point - size/2);

        if(axis == Constants.AXIS_X) {
            return modifiedPoint;
        } else {
            return -modifiedPoint;
        }
    }

    boolean isPlayPauseButton(double x, double y) {

        boolean is_x_PlayPauseButton, is_y_PlayPauseButton =  false;

        x = Math.abs(x);
        y = Math.abs(y);

        is_x_PlayPauseButton = x < ((size/2) * proportion_of_startButton);
        is_y_PlayPauseButton = y < ((size/2) * proportion_of_startButton);

//        Log.d(LOG_TAG, "isPlayPauseButton play button layer x : " + ((size/2) * proportion_of_startButton) );

        if(is_x_PlayPauseButton && is_y_PlayPauseButton) {
            return true;
        } else {
            return false;
        }
    }

    int degCalc(double x, double y) {
//        Log.d(LOG_TAG, "calcDeg x : " + x + ", y : " + y);
        double result_rad = 0;
        int result_deg = 0;

        if( (x > 0) && (y > 0) ) {  // 1사분면
            result_rad = Math.atan2(y,x);
            result_deg = (int)(result_rad * radToDegree);
        } else if( (x < 0) && (y > 0) ) {  // 2사분면
            result_rad = Math.atan2(y,-x);
            result_deg = (int)(result_rad * radToDegree);
            result_deg = 180 - result_deg;
        } else if( (x < 0) && (y < 0) ) {  // 3사분면
            result_rad = Math.atan2(-y,-x);
            result_deg = (int)(result_rad * radToDegree);
            result_deg = 180 + result_deg;
        } else if ( (x > 0) && (y < 0) ) {  // 4사분면
            result_rad = Math.atan2(-y,x);
            result_deg = (int)(result_rad * radToDegree);
            result_deg = 360 - result_deg;
        }

//        Log.d(LOG_TAG, "calcDeg x : " + x + ", y : " + y + ", deg " + result_deg);
//        Log.d(LOG_TAG, "calcDeg deg " + result_deg);

        return result_deg;
    }

    @Override
        public boolean onTouchEvent(MotionEvent event) {
            // TODO Auto-generated method stub

            int action = event.getAction();
            double x, y;

            switch (action) {
                /*
                case MotionEvent.ACTION_UP:
                    x = convertAxis(event.getX(), Constants.AXIS_X);
                    y = convertAxis(event.getY(), Constants.AXIS_Y);
                    if( isPlayPauseButton(x,y) ) {
                        if (Constants.PLAYER_STATUS == Constants.PLAYER_STATUS_PLAY) {
                            setImageResource(R.drawable.ic_circle_release);
                        } else {
                            setImageResource(R.drawable.ic_circle_release);
                        }
                    } else{
                        if (Constants.PLAYER_STATUS == Constants.PLAYER_STATUS_PLAY) {
                            setImageResource(R.drawable.ic_circle_release);
                        } else {
                            setImageResource(R.drawable.ic_circle_release);
                        }
                    }
                    jog_drag_state = Constants.JOG_DRAG_END;
                    Log.d(LOG_TAG, "MotionEvent.ACTION_UP accum_Deg " + accum_Deg + ", accum_mSec " + accum_mSec);
                    vibe.Vibe_Stop();
//                    setBackgroundResource(normalBitmapId);
                    break;

                case MotionEvent.ACTION_DOWN:
                    x = convertAxis(event.getX(), Constants.AXIS_X);
                    y = convertAxis(event.getY(), Constants.AXIS_Y);
    //                setBackgroundResource(clickedBitmapId);
                    accum_Deg = 0;
                    accum_mSec = 0;
                    if( isPlayPauseButton(x,y) ) {
                        vibe.vibration(80);
                        //Play Pause 동작
                        if(Constants.PLAYER_STATUS == Constants.PLAYER_STATUS_PAUSE || Constants.PLAYER_STATUS == Constants.PLAYER_STATUS_STOP) {
                            setImageResource(R.drawable.ic_circle_release);
                            if(Constants.FILE_READY_STATUS == Constants.FILE_READY) {
                                mMediaPlayerController.startPlay();
                                Constants.PLAYER_STATUS = Constants.PLAYER_STATUS_PLAY;
                            }
                            else {
                                setImageResource(R.drawable.ic_circle_release);
                                Log.d(LOG_TAG, "Playing file is not ready.");
                                Toast.makeText( mContext , "Playing file is not ready.", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if(Constants.PLAYER_STATUS == Constants.PLAYER_STATUS_PLAY) {
                            mMediaPlayerController.pausePlay();
                            Constants.PLAYER_STATUS = Constants.PLAYER_STATUS_PAUSE;
                        } else {
                            Log.d(LOG_TAG, "Constants.STATUS is wrong STATUS : " + Constants.PLAYER_STATUS );
                        }

                        Log.d(LOG_TAG, "onTouchEvent x : " + x + ", y : " + y + ", This touch point is PlayPause Button");
                    } else {
                        if(Constants.PLAYER_STATUS == Constants.PLAYER_STATUS_PLAY) {
                            setImageResource(R.drawable.ic_circle_release);
                        } else {
                            setImageResource(R.drawable.ic_circle_release);
                        }
                        jog_drag_state = Constants.JOG_DRAG_START;
                        old_deg = degCalc(x, y);
                        vibe.Vibe_Start();
                        Log.d(LOG_TAG, "onTouchEvent x : " + x + ", y : " + y + "This touch point is drag layer.");
                    }
                    break;
*/

                case MotionEvent.ACTION_MOVE:
                    //if( jog_drag_state == Constants.JOG_DRAG_START) {
                    if(true) { // just for debug
                        x = convertAxis(event.getX(), Constants.AXIS_X);
                        y = convertAxis(event.getY(), Constants.AXIS_Y);

                        new_deg = degCalc(x, y);
                        int shiftTime = getShifTime(old_deg, new_deg);

                        vibe.vibration(80);

                        accum_Deg += (new_deg - old_deg);
                        accum_mSec += shiftTime;
//                        Log.d(LOG_TAG, "MotionEvent.ACTION_MOVE accum_Deg " + accum_Deg + ", accum_mSec " + accum_mSec);
                        if(shiftTime < 0) {
                            //mMediaPlayerController.rewPlay( -shiftTime );
                        } else if ( shiftTime >= 0) {
                            //mMediaPlayerController.ffPlay( shiftTime );
                        } else {
                            Log.d(LOG_TAG, "MotionEvent.ACTION_MOVE same degree old_deg " + old_deg + ", new_deg " + new_deg);
                        }
                        old_deg = new_deg;

                        matrix.postRotate(new_deg);
                        setImageBitmap(Bitmap.createBitmap(imageScaled, 0, 0, imageScaled.getWidth(), imageScaled.getHeight(), matrix, true));
                    }
                    break;
            }
            //invalidate();
            return true;
            }


    int getShifTime(int old_deg, int new_deg) {
        int shift_mSec = 0;
        if( old_deg > new_deg) { // 일반적으로 위에서 아래로 내려오는 드래그 : FF Play , FF 는 양수를 리턴
            if( (old_deg - new_deg) > 300) {  // 예외상황. 359도 에서 1도로 드래그한 케이스. 역방이기때문에 따로 처리 필요함.
                shift_mSec = -( ( 360 - old_deg ) + new_deg ) ;
            } else {
                shift_mSec = old_deg - new_deg;
            }
        } else if ( new_deg > old_deg ) { // 일반적으로 아래에서 위로 올리는 드래그 : Rew Play, Rew 는 음수를 리턴
            if( (new_deg - old_deg) > 300 ) { // 1도에서 359로 드래그한 케이스. 역방향이기 때문에 별도 처리 필요함.
                shift_mSec = old_deg + (360 - new_deg);
            } else {
                shift_mSec = -( new_deg - old_deg );
            }
        }
        return shift_mSec = shift_mSec * unit_mSec; // 위에서는 차이나는 각도를 계산한것이고 여기서 단위시간과 곱해서 리턴
    }
}