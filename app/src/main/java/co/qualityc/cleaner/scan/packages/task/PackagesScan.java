package co.qualityc.cleaner.scan.packages.task;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Environment;
import android.os.RemoteException;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import co.qualityc.cleaner.PackageInfo;

public class PackagesScan implements Runnable {
    private static final String TAG = PackagesScan.class.getName();
    private static final String CACHE_DIR = "cache";

    public interface OnProgressListener {
        void onPackageScanStarted();

        void onPackageScanFinished(List<PackageInfo> packageInfoList, long totalInternalCacheSize);
    }

    private final OnProgressListener listener;
    private final PackageManager packageManager;


    public PackagesScan(PackageManager packageManager, OnProgressListener listener) {
        this.packageManager = packageManager;
        this.listener = listener;
    }

    @Override
    public void run() {
        listener.onPackageScanStarted();

        List<PackageInfo> packageInfoList = new ArrayList<PackageInfo>();

        if (isExternalStorageAvailable()) {

            File[] packageFiles = getPackagesDir().listFiles();
            if (packageFiles != null) {
                for (File packageFile : packageFiles) {
                    if (packageFile.isDirectory()) {
                        PackageInfo packageInfo = new PackageInfo(packageFile.getName());
                        packageInfo.setDir(packageFile);
                        packageInfo.setCacheSize(calculatePackageCache(packageFile));
                        packageInfo.setJunkSize(calculatePackageJunkSize(packageFile));

                        packageInfoList.add(packageInfo);
                    }
                }
            }
        }

        long totalInternalCacheSize = calculateTotalInternalCacheSize();

        listener.onPackageScanFinished(packageInfoList, totalInternalCacheSize);
    }

    private long calculateTotalInternalCacheSize() {
        final Semaphore codeSizeSemaphore = new Semaphore(1, true);

        final long[] size = {0};

        for (final ApplicationInfo appInfo : packageManager.getInstalledApplications(PackageManager.GET_META_DATA)) {
            try {
                codeSizeSemaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace(System.err);
            }

            try {
                getPackageSizeInfoMethod().invoke(packageManager, appInfo.packageName,
                        new IPackageStatsObserver.Stub() {
                            public void onGetStatsCompleted(PackageStats pStats, boolean succeedded)
                                    throws RemoteException {

                                size[0] += pStats.cacheSize;
                                codeSizeSemaphore.release();
                            }
                        }
                );
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }

        return size[0];
    }

    private Method getPackageSizeInfoMethod() throws NoSuchMethodException {
        return packageManager.getClass().getMethod("getPackageSizeInfo",
                String.class,
                IPackageStatsObserver.class);
    }

    private long calculatePackageJunkSize(File packageDir) {
        long size = 0;

        if (packageDir.exists()) {
            final File[] dirFiles = packageDir.listFiles();
            File cacheDir = new File(packageDir, CACHE_DIR);

            for (File dirFile : dirFiles) {
                if (!dirFile.equals(cacheDir)) {
                    if (dirFile.isDirectory()) {
                        size += FileUtils.sizeOfDirectory(dirFile);
                    } else {
                        size += dirFile.length();
                    }
                }
            }
        }

        return size;
    }

    private long calculatePackageCache(File packageFile) {
        File cacheDir = new File(packageFile, CACHE_DIR);
        if (cacheDir.exists() && cacheDir.isDirectory()) {
            return FileUtils.sizeOfDirectory(cacheDir);
        } else {
            return 0;
        }
    }

    private boolean isExternalStorageAvailable() {
        final String state = Environment.getExternalStorageState();

        return Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    private File getCacheDir(String packageName) {
        return new File(getPackageDir(packageName), CACHE_DIR);
    }

    private File getPackageDir(String packageName) {
        return new File(getPackagesDir(), packageName);
    }

    private File getPackagesDir() {
        return new File(Environment.getExternalStorageDirectory(), "Android/data");
    }

    public static boolean isPackageInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
