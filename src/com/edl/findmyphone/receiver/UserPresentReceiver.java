package com.edl.findmyphone.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.edl.findmyphone.service.CoreService;

import java.util.List;

public class UserPresentReceiver extends BroadcastReceiver {
    private static final String TAG = "UserPresentReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "receiver UserPresent broadcast>>>>");

        if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)
              /*&& !isProcessRunning(context)*/) {
            //Toast.makeText(context, "User Present!", Toast.LENGTH_LONG).show();
            context.startService(new Intent(context.getApplicationContext(), CoreService.class));

            /*intent.setClass(context, CoreService.class);
            intent.setPackage(context.getPackageName());
            PendingIntent restartServicePendingIntent = PendingIntent
                    .getService(context.getApplicationContext(), 1, intent, PendingIntent.FLAG_ONE_SHOT);
            AlarmManager alarmService = (AlarmManager) context.getApplicationContext()
                    .getSystemService(Context.ALARM_SERVICE);
            alarmService.set(
                    AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + 3000,
                    restartServicePendingIntent);*/
        }
        Log.i(TAG, "receiver completed-->>>started CoreService");
    }

    public boolean isBackgroundRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = context.getPackageName();
        List<ActivityManager.RunningTaskInfo> list = activityManager.getRunningTasks(100);
        if (list == null && list.size() == 0)
            return false;
        for (ActivityManager .RunningTaskInfo info : list) {
            if (info.topActivity.getPackageName().equals(packageName) || info.baseActivity.getPackageName().equals(packageName)) {
                return true;
            }
        }
        return false;
    }


    public boolean isProcessRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE );
        List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        for(int i = 0; i < procInfos.size(); i++){
            if (procInfos.get(i).processName.equals("com.edl.findmyphone.soreService")) {
                Log.i("equal", procInfos.get(i).processName);
                break;
            }
        /*    else {
                Log.i("Not equal", procInfos.get(i).processName);
                return false;
            }*/
        }
        return true;
    }
}
