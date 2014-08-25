package co.qualityc.cleaner.scan.memory;

import android.content.Intent;

import java.io.File;
import java.util.List;

import co.qualityc.cleaner.SCIntent;
import co.qualityc.cleaner.StorageItem;
import co.qualityc.cleaner.scan.ProcessInfo;
import co.qualityc.cleaner.scan.memory.task.MemoryScan;
import co.qualityc.cleaner.scan.service.ScanService;


public class MemoryScanService extends ScanService
        implements MemoryScan.OnProgressListener {
    private static final String TAG = MemoryScanService.class.getName();
    public static final String ACTION_SCAN_IN_PROGRESS = "cleaner.scan.memory.inProgress";
    public static final String ACTION_SCAN_FINISHED = "cleaner.scan.memory.finished";

    public static final String KEY_RESULT = "result";


    public MemoryScanService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);

        new MemoryScan(this, this).run();
    }

    @Override
    public void onStarted() {
    }

    @Override
    protected void onFinished(List<? extends StorageItem> items) {
    }

    @Override
    public void onFinished(long availableRAM, long totalRAM,
                           List<ProcessInfo> runningAppProcessInfos,
                           List<ProcessInfo> runningServiceInfos) {
        announceScanFinished(availableRAM, totalRAM, runningAppProcessInfos, runningServiceInfos);
    }

    private void announceScanFinished(long availableRAM, long totalRAM,
                                      List<ProcessInfo> runningAppProcessInfos,
                                      List<ProcessInfo> runningServiceInfos) {

        MemoryScanResult scanResult = new MemoryScanResult();
        scanResult.setRunningAppProcesses(runningAppProcessInfos);
        scanResult.setRunningAppServices(runningServiceInfos);
        scanResult.setAvailableRAM(availableRAM);
        scanResult.setTotalRAM(totalRAM);

        Intent intent = new Intent(ACTION_SCAN_FINISHED);
        intent.putExtra(SCIntent.KEY_BACKGROUND_SCAN, isBackgroundScan);
        intent.putExtra(KEY_RESULT, scanResult);

        sendBroadcast(intent);
    }

    private void announceScanInProgress(File file) {
        Intent intent = new Intent(ACTION_SCAN_IN_PROGRESS);

        sendBroadcast(intent);
    }
}
