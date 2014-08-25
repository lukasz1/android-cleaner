package co.qualityc.cleaner.clean.service;

import android.app.IntentService;
import android.util.Log;

import co.qualityc.cleaner.StorageItem;
import co.qualityc.cleaner.clean.task.StorageCleanTask;


public abstract class CleanService extends IntentService
        implements StorageCleanTask.OnProgressListener {
    public CleanService(String name) {
        super(name);
    }


    protected void clean(StorageCleanTask task) {
        task.setProgressListener(this);

        onCleanStarted();
        task.run();
        onCleanFinished();
    }

    protected abstract void onCleanStarted();

    protected abstract void onCleanFinished();

    @Override
    public void onProgress(StorageItem file, int i, int size) {
    }
}
