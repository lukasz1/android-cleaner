package co.qualityc.cleaner.scan.packages.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.List;

import co.qualityc.cleaner.PackageInfo;
import co.qualityc.cleaner.scan.storage.service.StorageScanService;

public class StorageScanReceiver extends BroadcastReceiver {

    public interface StorageScanListener {

        void onPackagesScanFinished(List<PackageInfo> packagesInfo, long totalInternalCacheSize);
    }

    private StorageScanListener listener;


    @Override
    public void onReceive(Context context, Intent intent) {

        if (listener != null) {
            String action = intent.getAction();
            if (action.equals(StorageScanService.ACTION_SCAN_IN_PROGRESS)) {

            } else if (action.equals(StorageScanService.ACTION_SCAN_FINISHED)) {
                List<PackageInfo> packageCaches = (List<PackageInfo>)
                        intent.getSerializableExtra(StorageScanService.KEY_PACKAGES_INFO);
                long totalInternalCacheSize = intent.getLongExtra(StorageScanService.KEY_INTERNAL_CACHE_SIZE, 0);
                listener.onPackagesScanFinished(packageCaches, totalInternalCacheSize);
            }

        }
    }

    public void unregisterListener() {
        listener = null;
    }

    public void registerListener(StorageScanListener listener) {
        this.listener = listener;
    }

    public void register(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(StorageScanService.ACTION_SCAN_IN_PROGRESS);
        filter.addAction(StorageScanService.ACTION_SCAN_FINISHED);

        context.registerReceiver(this, filter);
    }

    public void unregister(Context context) {
        context.unregisterReceiver(this);
    }
}
