package com.e4deen.bean_player.data;

/**
 * Created by user on 2016-12-29.
 */
public class Constants {
    public static boolean FAIL = false;
    public static boolean SUCCESS = true;

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
    public static int frag_plm_state = IDX_PLM_PLAYLIST;

    public static String newPlaylistName;

    public static final float[] NEGATIVE = {
            -1.0f,     0,     0,    0, 255, // red
            0, -1.0f,     0,    0, 255, // green
            0,     0, -1.0f,    0, 255, // blue
            0,     0,     0, 1.0f,   0  // alpha
    };

    public static int mNewPlaylistIdx = 0;
    public static int mCurrentPlaylistIdx = 0;



    public static String FRAG_MAIN_TAB_FILELIST = "frag_main_tab_filelist";
    public static String FRAG_MAIN_TAB_SHADOWING = "frag_main_tab_shadowing";
    public static String FRAG_MAIN_TAB_SCRIPT = "frag_main_tab_script";
    public static int IDX_MAIN_TAB_FILELIST = 0;
    public static int IDX_MAIN_TAB_SHADOWING = 1;
    public static int IDX_MAIN_TAB_SCRIPT = 2;

    public static int STATE_OFF = 0;
    public static int STATE_ON = 1;
    public static int frag_main_tab_state = IDX_MAIN_TAB_FILELIST;
    public static int SHADOWING_MY_VOICE_ONOFF_STATE = STATE_OFF;
    public static int SHADOWING_PLAYING_FILE_ONOFF_STATE = STATE_ON;
}
