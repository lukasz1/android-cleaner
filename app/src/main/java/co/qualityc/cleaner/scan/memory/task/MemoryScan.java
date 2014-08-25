package co.qualityc.cleaner.scan.memory.task;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import co.qualityc.cleaner.scan.ProcessInfo;
import co.qualityc.cleaner.scan.memory.MemoryFreedPredication;


public class MemoryScan implements Runnable {
    public interface OnProgressListener {
        void onStarted();

        void onFinished(long availableRAM, long totalRAM,
                        List<ProcessInfo> runningAppProcessInfos,
                        List<ProcessInfo> runningServiceInfos);
    }

    private final Context context;
    private final OnProgressListener listener;


    public MemoryScan(Context context, OnProgressListener onFinishListener) {
        this.context = context;
        this.listener = onFinishListener;
    }

    @Override
    public void run() {
        listener.onStarted();

        ActivityManager activityManager = (ActivityManager)
                context.getSystemService(Activity.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos =
                filterProcesses(activityManager.getRunningAppProcesses());

        List<ActivityManager.RunningServiceInfo> runningServiceInfos =
                filterServices(activityManager.getRunningServices(100));

        List<ProcessInfo> appProcessesInfos = createProcessInfosFromRunningApps(runningAppProcessInfos);
        List<ProcessInfo> serviceProcessesInfos = createProcessInfosFromRunningServices(runningServiceInfos);

        long availableRAM = calculateAvailableRAM();
        long totalRAM = calculateTotalRAM();

        listener.onFinished(
                availableRAM,
                totalRAM,
                appProcessesInfos,
                new ArrayList<ProcessInfo>());
    }

    private List<ProcessInfo> createProcessInfosFromRunningApps(List<ActivityManager
            .RunningAppProcessInfo>
                                                                        processInfos) {
        MemoryFreedPredication predication = MemoryFreedPredication.getInstance(context);

        List<ProcessInfo> processes = new ArrayList<ProcessInfo>();
        for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
            ProcessInfo process = new ProcessInfo(processInfo);
            process.setMemoryUsage(predication.calculateMemoryUsage(processInfo.pid));

            processes.add(process);
        }

        return processes;
    }

    private List<ProcessInfo> createProcessInfosFromRunningServices(List<ActivityManager.RunningServiceInfo>
                                                         serviceInfos) {
        MemoryFreedPredication predication = MemoryFreedPredication.getInstance(context);

        List<ProcessInfo> processes = new ArrayList<ProcessInfo>();
        for (ActivityManager.RunningServiceInfo processInfo : serviceInfos) {
            ProcessInfo process = new ProcessInfo(processInfo);
            process.setMemoryUsage(predication.calculateMemoryUsage(processInfo.pid));

            processes.add(process);
        }

        return processes;
    }

    public long calculateTotalRAM() {
        String str1 = "/proc/meminfo";
        String str2;
        String[] arrayOfString;

        long initial_memory = 0;
        try {
            FileReader localFileReader = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
            str2 = localBufferedReader.readLine();//meminfo
            arrayOfString = str2.split("\\s+");
            for (String num : arrayOfString) {
                Log.i(str2, num + "\t");
            }
            //total Memory
            initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024;
            localBufferedReader.close();
            return initial_memory;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    private long calculateAvailableRAM() {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Activity
                .ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);

        return mi.availMem;
    }

    private List<ActivityManager.RunningAppProcessInfo> filterProcesses(
            List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos) {

        List<ActivityManager.RunningAppProcessInfo> result = new ArrayList<ActivityManager
                .RunningAppProcessInfo>();

        for (ActivityManager.RunningAppProcessInfo processInfo : runningAppProcessInfos) {
            try {
                ApplicationInfo applicationInfo = getApplicationInfo(processInfo.processName);
                // remove system processes
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) continue;
                // remove SpeedChecker app process
                if (applicationInfo.packageName.equals(context.getPackageName())) continue;

                result.add(processInfo);
            } catch (PackageManager.NameNotFoundException e) {
            }
        }

        return result;
    }

    private List<ActivityManager.RunningServiceInfo> filterServices(List<ActivityManager
            .RunningServiceInfo> runningServices) {
        List<ActivityManager.RunningServiceInfo> result = new ArrayList<ActivityManager
                .RunningServiceInfo>();

        for (ActivityManager.RunningServiceInfo runningService : runningServices) {
            if (!runningService.process.contains("com.google")
                    && !runningService.process.contains("com.android")) {
                result.add(runningService);
            }
        }

        return result;
    }

    private ApplicationInfo getApplicationInfo(String packageName) throws PackageManager
            .NameNotFoundException {
        return context.getPackageManager().getApplicationInfo(packageName, 0);
    }
}
