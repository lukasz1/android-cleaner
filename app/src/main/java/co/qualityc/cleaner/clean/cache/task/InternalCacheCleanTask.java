package co.qualityc.cleaner.clean.cache.task;

import android.content.pm.PackageManager;

import java.lang.reflect.Method;

import co.qualityc.cleaner.clean.task.StorageCleanTask;


public class InternalCacheCleanTask extends StorageCleanTask {
    private static final long DESIRED_CACHE_STORAGE = (long) Math.pow(1024, 4); // 1024 GB


    private final PackageManager packageManager;

    public InternalCacheCleanTask(PackageManager packageManager) {
        super(null);
        this.packageManager = packageManager;
    }

    @Override
    public void run() {
        freeStorage(DESIRED_CACHE_STORAGE);
    }

    private void freeStorage(long desiredStorage) {
        Method[] methods = packageManager.getClass().getDeclaredMethods();
        for (Method m : methods) {
            if (m.getName().equals("freeStorage")) {
                try {
                    m.invoke(packageManager, desiredStorage, null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }
}
