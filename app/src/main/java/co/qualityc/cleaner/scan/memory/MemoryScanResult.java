package co.qualityc.cleaner.scan.memory;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

import co.qualityc.cleaner.scan.ProcessInfo;


public class MemoryScanResult implements Parcelable {
    private List<ProcessInfo> runningAppProcesses = new ArrayList<ProcessInfo>();
    private List<ProcessInfo> runningAppServices = new ArrayList<ProcessInfo>();
    private long availableRAM;
    private long totalRAM;


    public MemoryScanResult() {}

    public MemoryScanResult(Parcel parcel) {
        readFromParcel(parcel);
    }

    public static final Creator CREATOR = new Creator() {
        public MemoryScanResult createFromParcel(Parcel in) {
            return new MemoryScanResult(in);
        }

        public MemoryScanResult[] newArray(int size) {
            return new MemoryScanResult[size];
        }
    };


    public void setRunningAppProcesses(List<ProcessInfo> runningAppProcesses) {
        this.runningAppProcesses = runningAppProcesses;
    }

    public void setRunningAppServices(List<ProcessInfo> runningAppServices) {
        this.runningAppServices = runningAppServices;
    }

    public List<ProcessInfo> getRunningProcesses() {
        List<ProcessInfo> processes = new ArrayList<ProcessInfo>();
        processes.addAll(runningAppProcesses);
        processes.addAll(runningAppServices);

        return processes;
    }

    public long calculateMemoryUsage() {
        long usage = 0;
        for (ProcessInfo processInfo : getRunningProcesses()) {
            usage += processInfo.getSize();
        }

        return usage;
    }

    public void clearRunningProcesses() {
        runningAppProcesses.clear();
        runningAppServices.clear();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(runningAppProcesses);
        dest.writeList(runningAppServices);
        dest.writeLong(availableRAM);
        dest.writeLong(totalRAM);
    }

    private void readFromParcel(Parcel parcel) {
        parcel.readList(runningAppProcesses, null);
        parcel.readList(runningAppServices, null);
        availableRAM = parcel.readLong();
        totalRAM = parcel.readLong();
    }

    public void setAvailableRAM(long availableRAM) {
        this.availableRAM = availableRAM;
    }

    public long getAvailableRAM() {
        return availableRAM;
    }

    public void setTotalRAM(long totalRAM) {
        this.totalRAM = totalRAM;
    }

    public long getTotalRAM() {
        return totalRAM;
    }

    public float getAvailableRAMPercentage() {
        return availableRAM / (float) totalRAM * 100;
    }


}
