package com.e4deen.bean_player.data;

/**
 * Created by user on 2016-12-29.
 */
public class Constants {
    public static boolean FAIL = false;
    public static boolean SUCCESS = true;

    public static final int AXIS_X = 0;
    public static final int AXIS_Y = 1;
    public static final int JOG_DRAG_END = 0;
    public static final int JOG_DRAG_START = 1;

    public static final int PLAYER_STATUS_PAUSE = 0;
    public static final int PLAYER_STATUS_PLAY = 1;
    public static final int PLAYER_STATUS_STOP = 2;

    public static int PLAYER_STATUS = PLAYER_STATUS_STOP;

    public static final int FILE_NOT_READY = 0;
    public static final int FILE_READY = 1;

    public static int FILE_READY_STATUS = FILE_NOT_READY;

    public static int NOT_REPEAT = 0;
    public static int REPEAT = 1;
    public static int REPEAT_STATE = NOT_REPEAT;

    public static int NOT_SUFFLE = 0;
    public static int SHUFFLE = 1;
    public static int SHUFFLE_STATE = NOT_SUFFLE;

    public static int BOOKMARK_A = 0;
    public static int BOOKMARK_B = 1;
    public static int PROGRESS_BAR_TIME = 2;

    public static int SEC = 1000;

    public static String FRAG_PLM_FILELIST = "frag_plm_filelist";
    public static String FRAG_PLM_PLAYLIST = "frag_plm_playlist";
    public static String FRAG_PLM_PLAYLIST_FILES = "frag_plm_playlist_files";
    public static int IDX_PLM_PLAYLIST = 0;
    public static int IDX_PLM_PLAYLIST_FILES = 1;
    public static int IDX_PLM_FILELIST = 2;
    public static int plm_state = IDX_PLM_PLAYLIST;

    public static String newPlaylistName;

    public static final float[] NEGATIVE = {
            -1.0f,     0,     0,    0, 255, // red
            0, -1.0f,     0,    0, 255, // green
            0,     0, -1.0f,    0, 255, // blue
            0,     0,     0, 1.0f,   0  // alpha
    };

    public static int mNewPlaylistIdx = 0;
    public static int mCurrentPlaylistIdx = 0;
}
