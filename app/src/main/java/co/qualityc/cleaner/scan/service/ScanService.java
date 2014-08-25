package co.qualityc.cleaner.scan.service;

import android.app.IntentService;
import android.content.Intent;

import java.util.List;

import co.qualityc.cleaner.SCIntent;
import co.qualityc.cleaner.StorageItem;
import co.qualityc.cleaner.scan.task.ScanTask;


public abstract class ScanService extends IntentService  {
    public static final String KEY_COUNT = "count";
    public static final String KEY_NO_OF_FILES = "noOfFiles";

    protected boolean isBackgroundScan = false;


    public ScanService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        isBackgroundScan = intent.getBooleanExtra(SCIntent.KEY_BACKGROUND_SCAN, false);
    }

    protected void scan(ScanTask task) {
        onStarted();
        onFinished(task.run());
    }

    protected abstract void onStarted();

    protected abstract void onFinished(List<? extends StorageItem> items);
}
