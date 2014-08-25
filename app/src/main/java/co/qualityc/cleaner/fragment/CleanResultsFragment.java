package co.qualityc.cleaner.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import co.qualityc.cleaner.scan.StorageScanResult;
import co.qualityc.cleaner.scan.memory.MemoryScanResult;


public abstract class CleanResultsFragment extends Fragment {
    public static final String KEY_MEMORY_RESULT = "memoryResult";
    public static final String KEY_STORAGE_RESULT = "storageResult";


    protected MemoryScanResult memoryScanResult;
    protected StorageScanResult storageScanResult;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        memoryScanResult = getArguments().getParcelable(KEY_MEMORY_RESULT);
        storageScanResult = (StorageScanResult) getArguments().getSerializable(KEY_STORAGE_RESULT);
    }
}
