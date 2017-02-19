package com.e4deen.bobplayer;

/**
 * Created by user on 2016-12-29.
 */
public class Constants {
    static final int AXIS_X = 0;
    static final int AXIS_Y = 1;
    static final int JOG_DRAG_END = 0;
    static final int JOG_DRAG_START = 1;

    static final int PLAYER_STATUS_PAUSE = 0;
    static final int PLAYER_STATUS_PLAY = 1;
    static final int PLAYER_STATUS_STOP = 2;

    static int PLAYER_STATUS = PLAYER_STATUS_STOP;

    static final int FILE_NOT_READY = 0;
    static final int FILE_READY = 1;

    static int FILE_READY_STATUS = FILE_NOT_READY;

    static int NOT_REPEAT = 0;
    static int REPEAT = 1;
    static int REPEAT_STATE = NOT_REPEAT;

    static int NOT_SUFFLE = 0;
    static int SHUFFLE = 1;
    static int SHUFFLE_STATE = NOT_SUFFLE;

    static int BOOKMARK_A = 0;
    static int BOOKMARK_B = 1;
    static int PROGRESS_BAR_TIME = 2;

    static int SEC = 1000;
}
