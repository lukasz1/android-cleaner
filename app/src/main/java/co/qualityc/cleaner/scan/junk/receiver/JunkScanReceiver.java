package co.qualityc.cleaner.scan.junk.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.io.File;
import java.util.List;

import co.qualityc.cleaner.scan.junk.JunkFile;
import co.qualityc.cleaner.scan.junk.service.JunkScanService;


public class JunkScanReceiver extends BroadcastReceiver {

    public interface JunkScanListener {
        void onJunkScanInProgress(File file);

        void onJunkScanFinished(List<JunkFile> junkFiles, int totalJunkSize);
    }

    private JunkScanListener listener;


    @Override
    public void onReceive(Context context, Intent intent) {

        if (listener != null) {
            String action = intent.getAction();
            if (action.equals(JunkScanService.ACTION_SCAN_IN_PROGRESS)) {
                File file = (File) intent.getSerializableExtra(JunkScanService.KEY_FILE);

                listener.onJunkScanInProgress(file);
            } else if (action.equals(JunkScanService.ACTION_SCAN_FINISHED)) {
                List<JunkFile> files = (List<JunkFile>)
                        intent.getSerializableExtra(JunkScanService.KEY_JUNK_FILES);
                int totalSize = intent.getIntExtra(JunkScanService.KEY_TOTAL_JUNK, 0);

                listener.onJunkScanFinished(files, totalSize);
            }

        }
    }

    public void unregisterListener() {
        listener = null;
    }

    public void registerListener(JunkScanListener listener) {
        this.listener = listener;
    }

    public void register(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(JunkScanService.ACTION_SCAN_IN_PROGRESS);
        filter.addAction(JunkScanService.ACTION_SCAN_FINISHED);

        context.registerReceiver(this, filter);
    }

    public void unregister(Context context) {
        context.unregisterReceiver(this);
    }
}
