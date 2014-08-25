package co.qualityc.cleaner.clean.task;

import android.app.ActivityManager;
import android.content.Context;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.util.List;

import co.qualityc.cleaner.OnProgressListener;
import co.qualityc.cleaner.PackageInfo;
import co.qualityc.cleaner.clean.cache.task.InternalCacheCleanTask;
import co.qualityc.cleaner.scan.ProcessInfo;


public class CleanTask implements Runnable {
    private static final String TAG = CleanTask.class.getName();

    private final Context context;
    private final List<ProcessInfo> appProcessInfos;
    private final OnProgressListener onProgressListener;
    private final List<PackageInfo> junkPackages;


    public CleanTask(Context context,
                     OnProgressListener onProgressListener,
                     List<ProcessInfo> appProcessInfos,
                     List<PackageInfo> junkPackages) {
        this.context = context;
        this.appProcessInfos = appProcessInfos;
        this.onProgressListener = onProgressListener;
        this.junkPackages = junkPackages;
    }


    @Override
    public void run() {
        onProgressListener.onCleanStarted();


        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (appProcessInfos != null) {
            killAppProcesses(appProcessInfos);
        }

        if (junkPackages != null) {
            deleteJunk(junkPackages);
        }

        new InternalCacheCleanTask(context.getPackageManager()).run();


        onProgressListener.onCleanFinished();
    }

    private void killAppProcesses(List<ProcessInfo> runningAppProcesses) {
        for (ProcessInfo processInfo : runningAppProcesses) {
            killBackgroundProcess(processInfo.getProcessName());
        }
    }

    private void killBackgroundProcess(String packageName) {

        ActivityManager manager = (ActivityManager)
                context.getSystemService(Context.ACTIVITY_SERVICE);
        manager.killBackgroundProcesses(packageName);
    }

    private void deleteJunk(List<PackageInfo> packagesInfo) {
        for (PackageInfo packageInfo : packagesInfo) {
            try {
                FileUtils.deleteDirectory(packageInfo.getDir());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
