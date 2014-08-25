package co.qualityc.cleaner.scan.cache.task;

import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Build;
import android.os.RemoteException;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import co.qualityc.cleaner.PackageCache;
import co.qualityc.cleaner.StorageItem;
import co.qualityc.cleaner.scan.task.ScanTask;


public class CacheScanTask extends ScanTask {
    public interface OnProgressListener {
        void onProgress(ApplicationInfo applicationInfo, int i, int size);
    }

    protected OnProgressListener progressListener;


    public CacheScanTask(PackageManager packageManager) {
        super(packageManager);
    }

    public void setProgressListener(OnProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    public List<? extends StorageItem> run() {
        final List<PackageCache> caches = new ArrayList<PackageCache>();

        final Semaphore semaphore = new Semaphore(1);

        final List<ApplicationInfo> installedApplications = getInstalledApplications();
        for (int i = 0; i <= installedApplications.size(); i++) {
            try {
                semaphore.acquire();
                if (i == installedApplications.size()) {
                    // todo
                    Thread.sleep(100);
                    break;
                }

                try {
                    final ApplicationInfo applicationInfo = installedApplications.get(i);

                    final int finalI = i;
                    getPackageSizeInfoMethod().invoke(packageManager, applicationInfo.packageName,
                            new IPackageStatsObserver.Stub() {
                                public void onGetStatsCompleted(PackageStats pStats, boolean success)
                                        throws RemoteException {
                                    onProgress(applicationInfo, finalI + 1, installedApplications.size());

                                    long externalCacheSize = 0;
                                    if (Build.VERSION.SDK_INT >= 11) {
                                        externalCacheSize += pStats.externalCacheSize;
                                    }

                                    if (pStats.cacheSize > 0 || externalCacheSize > 0) {
                                        PackageCache packageCache = new PackageCache(applicationInfo.packageName, pStats.cacheSize);
                                        packageCache.setExternalCacheSize(externalCacheSize);

                                        caches.add(packageCache);
                                    }

                                    semaphore.release();
                                }
                            }
                    );
                } catch (Exception e) {
                    e.printStackTrace(System.err);
                    semaphore.release();
                }

            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
                semaphore.release();
            }
        }


        return caches;
    }

    private Method getPackageSizeInfoMethod() throws NoSuchMethodException {
        return packageManager.getClass().getMethod("getPackageSizeInfo",
                String.class,
                IPackageStatsObserver.class);
    }

    protected void onProgress(ApplicationInfo applicationInfo, int i, int size) {
        if (progressListener != null) {
            progressListener.onProgress(applicationInfo, i, size);
        }
    }
}
