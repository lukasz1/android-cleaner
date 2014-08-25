package co.qualityc.cleaner.scan.cache.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.List;

import co.qualityc.cleaner.PackageCache;
import co.qualityc.cleaner.scan.cache.service.CacheScanService;


public class CacheScanReceiver extends BroadcastReceiver {

    public interface CacheScanListener {
        void onCacheScanInProgress(String packageName, int count, int size);

        void onCacheScanFinished(List<PackageCache> packageCaches, long totalCacheSize);
    }

    private CacheScanListener listener;


    @Override
    public void onReceive(Context context, Intent intent) {

        if (listener != null) {
            String action = intent.getAction();
            if (action.equals(CacheScanService.ACTION_SCAN_IN_PROGRESS)) {
                String packageName = intent.getStringExtra(CacheScanService.KEY_APP_INFO);
                int count = intent.getIntExtra(CacheScanService.KEY_COUNT, 0);
                int noOfAllFiles = intent.getIntExtra(CacheScanService.KEY_NO_OF_FILES, 0);

                listener.onCacheScanInProgress(packageName, count, noOfAllFiles);
            } else if (action.equals(CacheScanService.ACTION_SCAN_FINISHED)) {
                List<PackageCache> packageCaches = (List<PackageCache>)
                        intent.getSerializableExtra(CacheScanService.KEY_PACKAGE_CACHES);
                long totalSize = intent.getLongExtra(CacheScanService.KEY_TOTAL_CACHE_SIZE, 0);

                listener.onCacheScanFinished(packageCaches, totalSize);
            }

        }
    }

    public void unregisterListener() {
        listener = null;
    }

    public void registerListener(CacheScanListener listener) {
        this.listener = listener;
    }

    public void register(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(CacheScanService.ACTION_SCAN_IN_PROGRESS);
        filter.addAction(CacheScanService.ACTION_SCAN_FINISHED);

        context.registerReceiver(this, filter);
    }

    public void unregister(Context context) {
        context.unregisterReceiver(this);
    }
}
