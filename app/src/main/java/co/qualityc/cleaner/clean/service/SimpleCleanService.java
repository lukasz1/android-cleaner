package co.qualityc.cleaner.clean.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.List;

import co.qualityc.cleaner.OnProgressListener;
import co.qualityc.cleaner.PackageInfo;
import co.qualityc.cleaner.clean.task.CleanTask;
import co.qualityc.cleaner.scan.ProcessInfo;
import co.qualityc.cleaner.scan.StorageScanResult;
import co.qualityc.cleaner.scan.memory.MemoryScanResult;
import co.qualityc.cleaner.scan.memory.MemoryScanService;
import co.qualityc.cleaner.scan.memory.receiver.MemoryScanReceiver;
import co.qualityc.cleaner.scan.packages.receiver.StorageScanReceiver;
import co.qualityc.cleaner.scan.storage.service.StorageScanService;
import co.qualityc.cleaner.scan.utils.ScanUtils;

public class SimpleCleanService extends Service implements OnProgressListener {
    private static final String TAG = SimpleCleanService.class.getName();

    public static final String ACTION_FINISHED = "cleaner.clean.finished";
    public static final String KEY_APP_PROCESSES = "appProcesses";
    public static final String KEY_JUNK_PACKAGES = "junkPackages";
    public static final String KEY_MEMORY_RESULT = "memoryResult";
    public static final String KEY_STORAGE_RESULT = "storageResult";

    private StorageScanResult storageScanResult;
    private MemoryScanResult memoryScanResult;

    private StorageScanReceiver packagesScanReceiver = new StorageScanReceiver();
    private StorageScanReceiver.StorageScanListener packagesScanListener =
            new StorageScanReceiver.StorageScanListener() {
                @Override
                public void onPackagesScanFinished(List<PackageInfo> packagesInfo, long totalInternalCacheSize) {
                    storageScanResult = new StorageScanResult.Builder()
                            .junkPackagesInfo(packagesInfo)
                            .availableStorage(ScanUtils.getAvailableStorage())
                            .totalStorage(ScanUtils.getTotalStorageSize())
                            .totalInternalCacheSize(totalInternalCacheSize)
                            .build();

                    onScanFinished();
                }
            };

    private MemoryScanReceiver memoryScanReceiver = new MemoryScanReceiver();
    private MemoryScanReceiver.MemoryScanListener memoryScanListener =
            new MemoryScanReceiver.MemoryScanListener() {

                @Override
                public void onMemoryScanFinished(MemoryScanResult scanResult) {
                    memoryScanResult = scanResult;

                    startService(new Intent(SimpleCleanService.this, StorageScanService.class));
                }
            };


    @Override
    public void onCreate() {
        super.onCreate();

        memoryScanReceiver.register(this);
        memoryScanReceiver.registerListener(memoryScanListener);

        packagesScanReceiver.register(this);
        packagesScanReceiver.registerListener(packagesScanListener);
    }

    @Override
    public void onDestroy() {
        memoryScanReceiver.unregister(this);
        memoryScanReceiver.registerListener(memoryScanListener);

        packagesScanReceiver.unregister(this);
        packagesScanReceiver.registerListener(packagesScanListener);

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        List<ProcessInfo> appProcessInfos
                = (List<ProcessInfo>) intent.getSerializableExtra(KEY_APP_PROCESSES);
        List<PackageInfo> junkPackages
                = (List<PackageInfo>) intent.getSerializableExtra(KEY_JUNK_PACKAGES);

        new Thread(new CleanTask(this, this, appProcessInfos, junkPackages)).start();

        return START_NOT_STICKY;
    }

    @Override
    public void onCleanStarted() {

    }

    @Override
    public void onCleanFinished() {
        startService(new Intent(this, MemoryScanService.class));
    }

    private void onScanFinished() {
        announceCleanFinished();
        stopSelf();
    }

    private void announceCleanFinished() {
        Intent intent = new Intent(ACTION_FINISHED);
        intent.putExtra(KEY_MEMORY_RESULT, memoryScanResult);
        intent.putExtra(KEY_STORAGE_RESULT, storageScanResult);

        sendStickyBroadcast(intent);
    }
}
