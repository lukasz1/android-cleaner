package co.qualityc.cleaner.scan.junk.service;

import android.content.Intent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import co.qualityc.cleaner.StorageItem;
import co.qualityc.cleaner.scan.junk.JunkFile;
import co.qualityc.cleaner.scan.junk.task.JunkScanTask;
import co.qualityc.cleaner.scan.service.ScanService;


public class JunkScanService extends ScanService implements JunkScanTask.OnProgressListener {
    public static final String ACTION_SCAN_IN_PROGRESS = "cleaner.scan.junk.inProgress";
    public static final String ACTION_SCAN_FINISHED = "cleaner.scan.junk.finished";

    public static final String KEY_FILE = "file";
    public static final String KEY_JUNK_FILES = "junkFiles";
    public static final String KEY_TOTAL_JUNK = "totalJunk";


    public JunkScanService() {
        super(JunkScanService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        JunkScanTask task = new JunkScanTask(getPackageManager());
        task.setProgressListener(this);
        scan(task);
    }

    private int calculateTotalJunkSize(List<JunkFile> junkFiles) {
        int size = 0;
        for (JunkFile file : junkFiles) {
            size += file.getFile().length();
        }

        return size;
    }


    @Override
    protected void onStarted() {

    }

    @Override
    public void onProgress(File file) {
        announceScanInProgress(file);
    }

    @Override
    protected void onFinished(List<? extends StorageItem> items) {
        announceScanFinished(new ArrayList<JunkFile>((List<? extends JunkFile>) items));
    }

    private void announceScanInProgress(File file) {
        Intent intent = new Intent(ACTION_SCAN_IN_PROGRESS);
        intent.putExtra(KEY_FILE, file);

        sendBroadcast(intent);
    }

    private void announceScanFinished(List<JunkFile> files) {
        Intent intent = new Intent(ACTION_SCAN_FINISHED);
        intent.putExtra(KEY_JUNK_FILES, new ArrayList<JunkFile>(files));
        intent.putExtra(KEY_TOTAL_JUNK, calculateTotalJunkSize(files));

        sendBroadcast(intent);
    }
}
