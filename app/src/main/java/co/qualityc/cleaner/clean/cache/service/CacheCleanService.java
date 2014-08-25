package co.qualityc.cleaner.clean.cache.service;

import android.content.Intent;

import co.qualityc.cleaner.clean.cache.task.InternalCacheCleanTask;
import co.qualityc.cleaner.clean.service.CleanService;


public class CacheCleanService extends CleanService {
    private static final String TAG = CacheCleanService.class.getName();


    public CacheCleanService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        clean(new InternalCacheCleanTask(getPackageManager()));
    }

    @Override
    protected void onCleanStarted() {

    }

    @Override
    protected void onCleanFinished() {

    }
}
