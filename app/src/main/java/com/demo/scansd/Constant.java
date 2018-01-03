package com.demo.scansd;

/**
 * Created by Scott on 2016/5/26.
 */
public class Constant {
    public static final int MSG_START_PROGRESS = 1000;
    public static final int MSG_STOP_PROGRESS = 1001;
    public static final int MSG_UPDATE_PROGRESS = 1002;
    public static final int MSG_UPDATE_AVERAGE_FRAGMENT = 1003;
    public static final int MSG_UPDATE_BIGGEST_FRAGMENT = 1004;
    public static final int MSG_UPDATA_EXTENSION_FRAGMENT = 1005;
    public static final int MSG_SCAN_FINISHED= 1006;

    public static final long PROGRESS_INTERVAL = 7000;
    public static final long START_DELAY = 2000;
    public static final long PROGRESS_UPDATE_INTERVAL = PROGRESS_INTERVAL / 100;

    public static final int STATUS_START = 100;
    public static final int STATUS_PAUSE = 101;
    public static final int STATUS_STOP = 102;

    public static final int SCAN_STEP = 100;
}
