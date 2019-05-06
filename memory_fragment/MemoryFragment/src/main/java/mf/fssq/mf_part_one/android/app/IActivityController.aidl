package android.app;

import android.content.Intent;

/**
 * Testing interface to monitor what is happening in the activity manager
 * while tests are running.  Not for normal application development.
 * {@hide}
 */
interface IActivityController
{
    /**
     * The system is trying to start an activity.  Return true to allow
     * it to be started as normal, or false to cancel/reject this activity.
     */
    boolean activityStarting(in Intent intent, String pkg);
   
    /**
     * The system is trying to return to an activity.  Return true to allow
     * it to be resumed as normal, or false to cancel/reject this activity.
     */
      //www.2cto.com
    boolean activityResuming(String pkg);
   
    /**
     * An application process has crashed (in Java).  Return true for the
     * normal error recovery (app crash dialog) to occur, false to kill
     * it immediately.
     */
    boolean appCrashed(String processName, int pid,
            String shortMsg, String longMsg,
            long timeMillis, String stackTrace);
   
    /**
     * Early call as soon as an ANR is detected.
     */
    int appEarlyNotResponding(String processName, int pid, String annotation);

    /**
     * An application process is not responding.  Return 0 to show the "app
     * not responding" dialog, 1 to continue waiting, or -1 to kill it
     * immediately.
     */
    int appNotResponding(String processName, int pid, String processStats);
}