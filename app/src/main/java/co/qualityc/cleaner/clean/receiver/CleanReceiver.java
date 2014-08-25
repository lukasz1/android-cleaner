package co.qualityc.cleaner.clean.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import co.qualityc.cleaner.clean.service.SimpleCleanService;
import co.qualityc.cleaner.scan.StorageScanResult;
import co.qualityc.cleaner.scan.memory.MemoryScanResult;


public class CleanReceiver extends BroadcastReceiver {

    public interface CleanListener {
        void onCleanFinished(MemoryScanResult memoryScanResult, StorageScanResult storageScanResult);
    }

    private CleanListener listener;


    @Override
    public void onReceive(Context context, Intent intent) {
        if (listener != null) {
            handleIntent(intent, context);
        }
    }

    private void handleIntent(Intent intent, Context context) {
        context.removeStickyBroadcast(intent);

        String action = intent.getAction();
        if (action.equals(SimpleCleanService.ACTION_FINISHED)) {
            MemoryScanResult memoryScanResult =
                    intent.getParcelableExtra(SimpleCleanService.KEY_MEMORY_RESULT);
            StorageScanResult storageScanResult =
                    (StorageScanResult) intent.getSerializableExtra(SimpleCleanService.KEY_STORAGE_RESULT);

            listener.onCleanFinished(memoryScanResult, storageScanResult);
        }
    }

    public void register(Context context, CleanListener listener) {
        this.listener = listener;

        IntentFilter filter = new IntentFilter();
        filter.addAction(SimpleCleanService.ACTION_FINISHED);

        context.registerReceiver(this, filter);
    }

    public void unregister(Context context) {
        this.listener = null;

        context.unregisterReceiver(this);
    }
}
