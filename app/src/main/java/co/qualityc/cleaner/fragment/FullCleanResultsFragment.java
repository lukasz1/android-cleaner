package co.qualityc.cleaner.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import co.qualityc.cleaner.R;
import co.qualityc.cleaner.scan.StorageScanResult;
import co.qualityc.cleaner.scan.memory.MemoryScanResult;
import co.qualityc.cleaner.utils.MathUtils;


public class FullCleanResultsFragment extends CleanResultsFragment {
    public static final String TAG = FullCleanResultsFragment.class.getName();

    public static FullCleanResultsFragment newInstance(MemoryScanResult memoryScanResult,
                                                       StorageScanResult storageScanResult) {
        FullCleanResultsFragment fragment = new FullCleanResultsFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_MEMORY_RESULT, memoryScanResult);
        args.putSerializable(KEY_STORAGE_RESULT, storageScanResult);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prepareResults();
    }

    private void prepareResults() {
        memoryScanResult.clearRunningProcesses();

        storageScanResult.getJunkPackagesInfo().clear();
        storageScanResult.setTotalInternalCacheSize(0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_clean_results, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateView();
    }

    private void updateView() {
        ((TextView) getView().findViewById(R.id.tv_apps_to_clean_2)).setText("" + memoryScanResult.getRunningProcesses().size());
        ((TextView) getView().findViewById(R.id.tv_available_memory_2)).setText(String.format(
                "%.2f%%",
                memoryScanResult.getAvailableRAMPercentage()));

        ((TextView) getView().findViewById(R.id.tv_available_storage_2)).setText(String.format(
                "%.2f%%",
                storageScanResult.getAvailableStoragePercentage()));
        ((TextView) getView().findViewById(R.id.tv_junk_2)).setText(String.format(
                "%.2fMB",
                MathUtils.convertBytesToMB(storageScanResult.calculateTotalJunk())));
    }
}
