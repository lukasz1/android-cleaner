package co.qualityc.cleaner.scan.cache.service;

import android.content.Intent;
import android.content.pm.ApplicationInfo;

import java.util.ArrayList;
import java.util.List;

import co.qualityc.cleaner.PackageCache;
import co.qualityc.cleaner.StorageItem;
import co.qualityc.cleaner.scan.cache.task.CacheScanTask;
import co.qualityc.cleaner.scan.service.ScanService;


public class CacheScanService extends ScanService implements CacheScanTask.OnProgressListener {
    public static final String TAG = CacheScanService.class.getName();
    public static final String ACTION_SCAN_IN_PROGRESS = "cleaner.scan.cache.inProgress";
    public static final String ACTION_SCAN_FINISHED = "cleaner.scan.cache.finished";

    public static final String KEY_APP_INFO = "appInfo";
    public static final String KEY_PACKAGE_CACHES = "packageCaches";
    public static final String KEY_TOTAL_CACHE_SIZE = "totalCacheSize";


    public CacheScanService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        CacheScanTask task = new CacheScanTask(getPackageManager());
        task.setProgressListener(this);
        scan(task);
    }

    @Override
    protected void onStarted() {

    }

    @Override
    public void onProgress(ApplicationInfo applicationInfo, int i, int size) {
        announceScanInProgress(applicationInfo, i, size);
    }

    @Override
    protected void onFinished(List<? extends StorageItem> items) {
        announceScanFinished(new ArrayList<PackageCache>((List<? extends PackageCache>) items));
    }

    private void announceScanInProgress(ApplicationInfo applicationInfo, int count, int size) {
        Intent intent = new Intent(ACTION_SCAN_IN_PROGRESS);
        intent.putExtra(KEY_APP_INFO, applicationInfo.packageName);
        intent.putExtra(KEY_COUNT, count);
        intent.putExtra(KEY_NO_OF_FILES, size);

        sendBroadcast(intent);
    }

    private void announceScanFinished(List<PackageCache> caches) {
        Intent intent = new Intent(ACTION_SCAN_FINISHED);
        intent.putExtra(KEY_PACKAGE_CACHES, new ArrayList<PackageCache>(caches));
        intent.putExtra(KEY_TOTAL_CACHE_SIZE, calculateTotalCacheSize(caches));

        sendBroadcast(intent);
    }

    private long calculateTotalCacheSize(List<PackageCache> caches) {
        long size = 0;
        for (PackageCache cache : caches) {
            size += cache.getCacheSize();
        }

        return size;
    }
}
