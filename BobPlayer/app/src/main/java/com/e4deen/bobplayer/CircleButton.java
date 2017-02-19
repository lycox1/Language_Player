package com.e4deen.bobplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

/**
 * Created by user on 2016-12-21.
 */
public class CircleButton extends RelativeLayout {

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
    int mLayoutWidth, mLayoutHeight, mRotatorWidth, mRotatorHeight;
    private ImageView ivRotor;
    private Bitmap bmpRotorOn;
    int test = 0;
    int accum_Deg, accum_mSec;

    public CircleButton(Context context, int layoutWidth, int layoutHeight) {
        super(context);
        mContext = context;
        vibe = new Vibe(mContext);

        mLayoutWidth = layoutWidth;
        mLayoutHeight = layoutHeight;

        mRotatorWidth = (layoutWidth > layoutHeight) ? layoutHeight:layoutWidth;
        mRotatorHeight = (layoutWidth > layoutHeight) ? layoutHeight:layoutWidth;

//        mLayoutWidth = (layoutWidth > layoutHeight) ? layoutHeight:layoutWidth;
//        mLayoutHeight = (layoutWidth > layoutHeight) ? layoutHeight:layoutWidth;

        int rotoron = R.drawable.center_circle;
        Bitmap srcon = BitmapFactory.decodeResource(mContext.getResources(), rotoron);

        float scaleWidth = ((float) mRotatorWidth) / srcon.getWidth();
        float scaleHeight = ((float) mRotatorHeight) / srcon.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        bmpRotorOn = Bitmap.createBitmap(
                srcon, 0, 0,
                srcon.getWidth(),srcon.getHeight() , matrix , true);

        Log.d(LOG_TAG, "mRotatorWidth " + mRotatorWidth + ", mRotatorHeight " + mRotatorHeight);

        ivRotor = new ImageView(context);
        ivRotor.setImageBitmap(bmpRotorOn);
        RelativeLayout.LayoutParams lp_ivKnob = new RelativeLayout.LayoutParams(mRotatorWidth,mRotatorHeight);//LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp_ivKnob.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(ivRotor, lp_ivKnob);
    }


    void setMediaPlayerController(MediaPlayerController mediaPlayerController) {
        mMediaPlayerController = mediaPlayerController;
    }

    void init() {

        //Log.d(LOG_TAG, "dialerHeight " + dialerHeight + ", dialerWidth " + dialerWidth + ", imageOriginal.getWidth() " + imageOriginal.getWidth() + ", imageOriginal.getHeight()" + imageOriginal.getHeight() );

        setUnitSec(2);
    }

    void setUnitSec(int sec) {

        unit_mSec = (int)((float)((float)sec / 90) * 1000);
        Log.d(LOG_TAG, "setUnitSec sec " + sec + ", unit_mSec " + unit_mSec);
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
        double result_deg = 0;
        //int result_deg = 0;

 /*   // 수식을 이용한 일반적인 삼각함수 각도 계산방식 - 3시방향 0도, 12시방향 90도, 9시 방향 180도 6시방향 270도
        if( (x > 0) && (y > 0) ) {  // 1사분면 result_rad = Math.atan2(y,x); result_deg = (int)(result_rad * radToDegree);
        } else if( (x < 0) && (y > 0) ) {  // 2사분면 result_rad = Math.atan2(y,-x); result_deg = (int)(result_rad * radToDegree); result_deg = 180 - result_deg;
        } else if( (x < 0) && (y < 0) ) {  // 3사분면 result_rad = Math.atan2(-y,-x); result_deg = (int)(result_rad * radToDegree); result_deg = 180 + result_deg;
        } else if ( (x > 0) && (y < 0) ) {  // 4사분면 result_rad = Math.atan2(-y,x); result_deg = (int)(result_rad * radToDegree); result_deg = 360 - result_deg;
        }
*/

        // x : 0 ~ mRotatorWidth(451px), y : 0 ~ mRotatorHeight(451px)  를 0~1 사이의 값으로 변환한다.
        // Log.d(LOG_TAG, "degCalc x : " + x + ", y : " + y + ", mRotatorWidth " + mRotatorWidth + ", mRotatorHeight " + mRotatorHeight);
        x = x / ((double) mRotatorWidth);
        y = y / ((double) mRotatorHeight);

        // 아래에서 0~1 사이의 값을 사용하던 좌표계를 -0.5~ + 0.5 를 사용하는 좌표계로 변환하기 위해 completely 대칭을 만들어 주기 위해 아래와 같이 대칭 처리를 해준다.
        x = 1 - x;
        y = 1 - y;

        result_deg = (double) -Math.toDegrees(Math.atan2(x - 0.5f, y - 0.5f));
        // Log.d(LOG_TAG, "result_deg : " + result_deg + ", x " + x + ", y " + y);
        if( result_deg < 0 ) {
            result_deg = 360 + result_deg;
        }
//        Log.d(LOG_TAG, "calcDeg x : " + x + ", y : " + y + ", deg " + (int)result_deg);
//        Log.d(LOG_TAG, "calcDeg deg " + result_deg);

        return (int)result_deg;
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

                        //new_deg = degCalc(x, y);
                        new_deg = degCalc(event.getX(), event.getY());
                        int shiftTime = getShifTime(old_deg, new_deg);

                        //vibe.vibration(80);

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
                        Log.d(LOG_TAG, "MotionEvent.ACTION_MOVE new_deg " + new_deg );
                        Matrix matrix=new Matrix();
                        ivRotor.setScaleType(ImageView.ScaleType.MATRIX);
                        matrix.postRotate((float) new_deg, mRotatorWidth/2, mRotatorHeight/2);//getWidth()/2, getHeight()/2);
                        ivRotor.setImageMatrix(matrix);
                    }
                    break;
            }
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
