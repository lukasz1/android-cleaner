package co.qualityc.cleaner.scan.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import co.qualityc.cleaner.fragment.ScanFragment;
import co.qualityc.cleaner.scan.StorageScanResult;
import co.qualityc.cleaner.scan.memory.MemoryScanResult;


public class ScanReceiver extends BroadcastReceiver {

    public interface ScanListener {
        void onScanFinished(MemoryScanResult memoryScanResult, StorageScanResult storageScanResult);
    }

    private ScanListener listener;


    @Override
    public void onReceive(Context context, Intent intent) {
        if (listener != null) {
            handleIntent(intent, context);
        }
    }

    private void handleIntent(Intent intent, Context context) {
        context.removeStickyBroadcast(intent);

        String action = intent.getAction();
        if (action.equals(ScanFragment.ACTION_SCAN_FINISHED)) {
            MemoryScanResult memoryScanResult =
                    intent.getParcelableExtra(ScanFragment.KEY_MEMORY_SCAN_RESULT);
            StorageScanResult storageScanResult =
                    (StorageScanResult) intent.getSerializableExtra(ScanFragment.KEY_STORAGE_SCAN_RESULT);

            listener.onScanFinished(memoryScanResult, storageScanResult);
        }
    }

    public void register(Context context, ScanListener listener) {
        this.listener = listener;

        IntentFilter filter = new IntentFilter();
        filter.addAction(ScanFragment.ACTION_SCAN_FINISHED);

        context.registerReceiver(this, filter);
    }

    public void unregister(Context context) {
        this.listener = null;

        context.unregisterReceiver(this);
    }
}
