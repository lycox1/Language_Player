package com.e4deen.bean_player.view.player_view.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.e4deen.bean_player.data.Constants;
import com.e4deen.bean_player.R;
import com.e4deen.bean_player.data.LogUtil;
import com.e4deen.bean_player.util.Vibe;
import com.e4deen.bean_player.view.player_view.activity.MainActivity;

import org.w3c.dom.Text;

/**
 * Created by user on 2016-12-21.
 */
public class CircleButton extends RelativeLayout {

    static String LOG_TAG = "BeanPlayer_CircleTouchEvent";
    int normalBitmapId = R.drawable.ic_circle_release;
    int size = 0; // 휠 전체 사이즈 (가로 세로 대칭)
    double proportion_of_startButton = 0.381578; // 전체에서 start/pause 버튼의 비율. 정가운데를 0으로 놓은 비율
    int jog_drag_state, vibe_count = 0;
    int old_deg, new_deg, unit_mSec, shiftTime = 0;
    int backStepDeg = 0;
    int backStepLevel = 30;
    float prev_shift_time, this_shift_time;
    double radToDegree = 180 / Math.PI;
    Context mContext;
    int mLayoutWidth, mLayoutHeight, mRotatorWidth, mRotatorHeight;
    private ImageView ivRotor;
    private Bitmap bmpRotorOn;
    int accum_Deg, accum_mSec;
    boolean backStep = false;
    public TextView tv_ShiftTime;
    RelativeLayout RL_Circle;

    public CircleButton(Context context, int layoutWidth, int layoutHeight) {
        super(context);
        mContext = context;
        //vibe = new Vibe(mContext);

        RL_Circle = (RelativeLayout) ((MainActivity)mContext).findViewById(R.id.RL_Circle);

        mLayoutWidth = layoutWidth;
        mLayoutHeight = layoutHeight;

        mRotatorWidth = (layoutWidth > layoutHeight) ? layoutHeight:layoutWidth;
        mRotatorHeight = (layoutWidth > layoutHeight) ? layoutHeight:layoutWidth;

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

        setUnitSec(4);
    }

/*
    public void setMediaPlayerController(MediaPlayerController mediaPlayerController) {
        mMediaPlayerController = mediaPlayerController;
    }
*/
    public void init() {

        //Log.d(LOG_TAG, "dialerHeight " + dialerHeight + ", dialerWidth " + dialerWidth + ", imageOriginal.getWidth() " + imageOriginal.getWidth() + ", imageOriginal.getHeight()" + imageOriginal.getHeight() );

        setUnitSec(4);
    }

    public void setUnitSec(int sec) {
//        unit_mSec = (int)((float)((float)sec / 90) * 1000);
//        Log.d(LOG_TAG, "setUnitSec sec " + sec + ", unit_mSec " + unit_mSec);

        unit_mSec = (int)( ( ((float)1/(float)360) * (float)1000) * sec )  ;
        //Log.d(LOG_TAG, "setUnitSec (float)(1/360) " + ( (float) 1/ (float)360 ) + ", test " + ( (float) 1/ (float)360 ) * (float)1000);
        //Log.d(LOG_TAG, "setUnitSec sec " + sec + ", unit_mSec " + unit_mSec);
    }

    public boolean isPlayPauseButton(double x, double y) {

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

    public int degCalc(double x, double y) {
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
        LogUtil.logD(LOG_TAG, "result_deg : " + result_deg + ", x " + x + ", y " + y);
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
                case MotionEvent.ACTION_DOWN:
                    accum_mSec = 0;
                    shiftTime = 0;
                    prev_shift_time = 0;
                    this_shift_time = 0;
                    backStep = false;
                    tv_ShiftTime = new TextView(mContext);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                    //params.addRule(RelativeLayout.CENTER_HORIZONTAL);
                    params.addRule(RelativeLayout.ALIGN_LEFT);
                    params.setMargins(0,0,0,0);

                    tv_ShiftTime.setLayoutParams(params);
                    tv_ShiftTime.setPadding(0,0,0,0);

                    RL_Circle.addView(tv_ShiftTime, params);
                    old_deg = degCalc(event.getX(), event.getY());
                    //Vibe.Vibe_Start();
                    break;

                case MotionEvent.ACTION_MOVE:
                    //if( jog_drag_state == Constants.JOG_DRAG_START) {
                    if(true) { // just for debug


                        //new_deg = degCalc(x, y);
                        new_deg = degCalc(event.getX(), event.getY());
                        shiftTime = getShifTime(old_deg, new_deg);

                        //vibe.vibration(80);

                        accum_Deg += (new_deg - old_deg);
                        accum_mSec += shiftTime;

                        this_shift_time =  (accum_mSec / 300);
                        Log.d(LOG_TAG, "accum_mSec " + accum_mSec + "circle this_shift_time " + this_shift_time + ", prev_shift_time " + prev_shift_time);
                        if(this_shift_time != prev_shift_time) {
                            Log.d(LOG_TAG, "run vibe");
                            prev_shift_time = this_shift_time;
                            Vibe.vibration(300);
                        }

                        //String round_shift_time = accum_mSec/1000.0;
                        //String.format("%.1f", (float)accum_mSec/1000.0f);
                        String text_shift_time;

                        if(accum_mSec >= 0) {
                            text_shift_time = "               " + String.format("%.1f", (float)accum_mSec/1000.0f) + " Sec FF >>";
                        } else {
                            text_shift_time = "<< Rew " + String.format("%.1f", (float)accum_mSec/1000.0f) + " Sec      ";
                        }
                        tv_ShiftTime.setText(text_shift_time);

                        //Log.d(LOG_TAG, "MotionEvent.ACTION_MOVE accum_Deg " + accum_Deg + ", accum_mSec " + accum_mSec);
                        //LogUtil.logD(LOG_TAG, "MotionEvent.ACTION_MOVE accum_Deg " + accum_Deg + ", accum_mSec " + accum_mSec);
                        if(Constants.FILE_READY_STATUS == Constants.FILE_READY) {
                            if (shiftTime < 0) {
                                shiftTime = -shiftTime;
                                MediaPlayerController.sController.rewPlay(shiftTime);
                            } else if (shiftTime >= 0) {
                                MediaPlayerController.sController.ffPlay(shiftTime);
                            } else {
                                //LogUtil.logD(LOG_TAG, "MotionEvent.ACTION_MOVE same degree old_deg " + old_deg + ", new_deg " + new_deg);
                            }
                        }
                        old_deg = new_deg;
                        LogUtil.logD(LOG_TAG, "MotionEvent.ACTION_MOVE new_deg " + new_deg );
                        Matrix matrix=new Matrix();
                        ivRotor.setScaleType(ImageView.ScaleType.MATRIX);
                        matrix.postRotate((float) new_deg, mRotatorWidth/2, mRotatorHeight/2);//getWidth()/2, getHeight()/2);
                        ivRotor.setImageMatrix(matrix);
                    }
                    break;

                case MotionEvent.ACTION_UP:
                    tv_ShiftTime.setText("");
                    //Vibe.Vibe_Stop();
                    backStep = true;

                    if(shiftTime > 0) {
                        backStepDeg = new_deg / backStepLevel;
                    } else {
                        backStepDeg = (360 - new_deg) / backStepLevel;
                    }

                    if(backStepDeg <= 0) {
                        backStepDeg = 10;
                    }
/*
                    Thread myThread = new Thread(new Runnable() {
                        public void run() {
                            while ( backStep == true) {
                                try {
                                    LogUtil.logD(LOG_TAG, "CircleButton backStepDeg " + backStepDeg + ", new_deg " + new_deg);
                                    if(shiftTime > 0) {  // FF 에서 back 하는 경우
                                        new_deg -= backStepDeg;
                                        if(new_deg < 0) {
                                            backStep = false;
                                            new_deg = 0;
                                        }
                                        Matrix matrix=new Matrix();
                                        ivRotor.setScaleType(ImageView.ScaleType.MATRIX);
                                        matrix.postRotate((float) new_deg, mRotatorWidth/2, mRotatorHeight/2);
                                        ivRotor.setImageMatrix(matrix);
                                    } else {
                                        new_deg += backStepDeg;
                                        if(new_deg > 360) {
                                            backStep = false;
                                            new_deg = 0;
                                        }
                                        Matrix matrix=new Matrix();
                                        ivRotor.setScaleType(ImageView.ScaleType.MATRIX);
                                        matrix.postRotate((float) new_deg, mRotatorWidth/2, mRotatorHeight/2);
                                        ivRotor.setImageMatrix(matrix);
                                    }
                                    Thread.sleep(1);
                                } catch (Throwable t) {
                                }
                            }
                        }
                    });
                    myThread.start();
*/

                    ((MainActivity) mContext).runOnUiThread(new Runnable()
                     {
                         @Override
                         public void run()
                         {
                             while ( backStep == true) {
                                 try {
                                     LogUtil.logD(LOG_TAG, "CircleButton backStepDeg " + backStepDeg + ", new_deg " + new_deg);
                                     if(shiftTime > 0) {  // FF 에서 back 하는 경우
                                         new_deg -= backStepDeg;
                                         if(new_deg < 0) {
                                             backStep = false;
                                             new_deg = 0;
                                         }
                                         Matrix matrix=new Matrix();
                                         ivRotor.setScaleType(ImageView.ScaleType.MATRIX);
                                         matrix.postRotate((float) new_deg, mRotatorWidth/2, mRotatorHeight/2);
                                         ivRotor.setImageMatrix(matrix);
                                     } else {
                                         new_deg += backStepDeg;
                                         if(new_deg > 360) {
                                             backStep = false;
                                             new_deg = 0;
                                         }
                                         Matrix matrix=new Matrix();
                                         ivRotor.setScaleType(ImageView.ScaleType.MATRIX);
                                         matrix.postRotate((float) new_deg, mRotatorWidth/2, mRotatorHeight/2);
                                         ivRotor.setImageMatrix(matrix);
                                     }

                                     if(new_deg == 0) {
                                         backStep = false;
                                     }
                                     Thread.sleep(3);
                                 } catch (Throwable t) {
                                 }
                             }
                         }
                    });
                    break;
            }
            return true;
            }


    public int getShifTime(int old_deg, int new_deg) {
        int shift_mSec = 0;
        LogUtil.logD(LOG_TAG, "getShiftTime old_deg " + old_deg + ", new_deg " + new_deg);
        if( old_deg > new_deg) { // 반시계방향으로 회전하는 경우. Rew
            if( (old_deg - new_deg) > 300) {  // 1도에서 359도로 드래그한 케이스.
                shift_mSec = (360 - old_deg) + new_deg ;
            } else {
                shift_mSec = -(old_deg - new_deg);
            }
        } else if ( new_deg > old_deg ) { // 시계방향으로 회전하는 경우. FF
            if( (new_deg - old_deg) > 300 ) { // 359도에서 1로 드래그한 케이스. 별도 처리 필요함.
                shift_mSec = -((360 - new_deg) + old_deg);
            } else {
                shift_mSec = new_deg - old_deg;
            }
        }
        shift_mSec = shift_mSec * unit_mSec;
        LogUtil.logD(LOG_TAG, "getShiftTime shift_mSec " + shift_mSec + ", old_deg " + old_deg + ", new_deg " + new_deg + ", unit_mSec " + unit_mSec);
        return shift_mSec; // 위에서는 차이나는 각도를 계산한것이고 여기서 단위시간과 곱해서 리턴
    }
}
