package co.qualityc.cleaner.scan;

import android.app.ActivityManager;

import java.io.Serializable;

import co.qualityc.cleaner.StorageItem;


public class ProcessInfo extends StorageItem implements Serializable {
    private final int pid;
    private int memoryUsage;
    private final String processName;


    public ProcessInfo(ActivityManager.RunningAppProcessInfo processInfo) {
        this.pid = processInfo.pid;
        this.processName = processInfo.processName;
    }

    public ProcessInfo(ActivityManager.RunningServiceInfo processInfo) {
        this.pid = processInfo.pid;
        this.processName = processInfo.process;
    }

    @Override
    public long getSize() {
        return memoryUsage;
    }

    public void setMemoryUsage(int memoryUsage) {
        this.memoryUsage = memoryUsage;
    }

    public String getProcessName() {
        return processName;
    }

    public int getPid() {
        return pid;
    }
}
