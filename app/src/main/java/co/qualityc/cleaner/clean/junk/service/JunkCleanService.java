package co.qualityc.cleaner.clean.junk.service;

import android.content.Intent;
import android.util.Log;

import java.util.List;

import co.qualityc.cleaner.clean.junk.task.JunkCleanTask;
import co.qualityc.cleaner.clean.service.CleanService;
import co.qualityc.cleaner.scan.junk.JunkFile;
import co.qualityc.cleaner.scan.junk.service.JunkScanService;


public class JunkCleanService extends CleanService {
    private static final String TAG = JunkScanService.class.getName();
    private static final String ARG_JUNK_FILES = "ARG_JUNK_FILES";


    public JunkCleanService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        clean(new JunkCleanTask((List<JunkFile>) intent.getSerializableExtra(ARG_JUNK_FILES)));
    }


    @Override
    protected void onCleanStarted() {

    }

    @Override
    protected void onCleanFinished() {
        announceCleanFinished();
    }

    private void announceCleanFinished() {
        Log.d(TAG, "Junk files clean finished");
    }

    private void announceCleanInProgress(JunkFile file, int i, int size) {
    }
}
