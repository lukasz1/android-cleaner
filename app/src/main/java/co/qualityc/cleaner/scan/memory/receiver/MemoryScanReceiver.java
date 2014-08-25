package co.qualityc.cleaner.scan.memory.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import co.qualityc.cleaner.scan.memory.MemoryScanResult;
import co.qualityc.cleaner.scan.memory.MemoryScanService;


public class MemoryScanReceiver extends BroadcastReceiver {


    public interface MemoryScanListener {
        void onMemoryScanFinished(MemoryScanResult scanResult);
    }

    private MemoryScanListener listener;

    private Intent pendingIntent;


    @Override
    public void onReceive(Context context, Intent intent) {
        if (listener != null) {
            handleIntent(intent);
        } else {
            pendingIntent = intent;
        }
    }

    private void handleIntent(Intent intent) {
        String action = intent.getAction();
        if (action.equals(MemoryScanService.ACTION_SCAN_IN_PROGRESS)) {

        } else if (action.equals(MemoryScanService.ACTION_SCAN_FINISHED)) {
            MemoryScanResult scanResult = intent.getParcelableExtra(MemoryScanService.KEY_RESULT);

            listener.onMemoryScanFinished(scanResult);
        }
    }

    public void unregisterListener() {
        listener = null;
    }

    public void registerListener(MemoryScanListener listener) {
        this.listener = listener;

        if (pendingIntent != null) {
            handleIntent(pendingIntent);
            pendingIntent = null;
        }
    }

    public void register(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(MemoryScanService.ACTION_SCAN_IN_PROGRESS);
        filter.addAction(MemoryScanService.ACTION_SCAN_FINISHED);

        context.registerReceiver(this, filter);
    }

    public void unregister(Context context) {
        context.unregisterReceiver(this);
    }
}
