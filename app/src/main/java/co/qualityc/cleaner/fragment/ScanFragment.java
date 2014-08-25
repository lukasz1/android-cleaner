package co.qualityc.cleaner.fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewAnimator;

import java.io.File;
import java.util.List;

import co.qualityc.cleaner.PackageCache;
import co.qualityc.cleaner.PackageInfo;
import co.qualityc.cleaner.R;
import co.qualityc.cleaner.activity.CleanerActivity;
import co.qualityc.cleaner.scan.StorageScanResult;
import co.qualityc.cleaner.scan.cache.receiver.CacheScanReceiver;
import co.qualityc.cleaner.scan.cache.service.CacheScanService;
import co.qualityc.cleaner.scan.junk.JunkFile;
import co.qualityc.cleaner.scan.junk.receiver.JunkScanReceiver;
import co.qualityc.cleaner.scan.junk.service.JunkScanService;
import co.qualityc.cleaner.scan.memory.MemoryScanResult;
import co.qualityc.cleaner.scan.memory.MemoryScanService;
import co.qualityc.cleaner.scan.memory.receiver.MemoryScanReceiver;
import co.qualityc.cleaner.scan.packages.receiver.StorageScanReceiver;
import co.qualityc.cleaner.scan.storage.service.StorageScanService;
import co.qualityc.cleaner.scan.utils.ScanUtils;
import co.qualityc.cleaner.utils.MathUtils;


public class ScanFragment extends Fragment {
    public static final String TAG = ScanFragment.class.getName();

    private static final long DEFAULT_SCAN_LENGTH = 3000;
    private static final long MIN_SCAN_LENGTH = 1;

    public static final String ACTION_SCAN_FINISHED = "cleaner.scan.finished";
    public static final String KEY_MEMORY_SCAN_RESULT = "memoryScanResult";
    public static final String KEY_STORAGE_SCAN_RESULT = "storageScanResult";
    private static final String KEY_SHOW_ARTIFICIAL_PROGRESS = "showArtificialProgress";

    private boolean showArtificialProgress = false;


    public static ScanFragment newInstance(boolean showArtificialProgress) {
        Bundle args = new Bundle();
        args.putBoolean(KEY_SHOW_ARTIFICIAL_PROGRESS, showArtificialProgress);

        ScanFragment fragment = new ScanFragment();
        fragment.setArguments(args);

        return fragment;
    }


    private class ArtificialScanProgressTimer extends CountDownTimer {

        public ArtificialScanProgressTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            updateAvailableMemory(millisUntilFinished);
            updateBackgroundAppsToClean(millisUntilFinished);
            updateAvailableStorage(millisUntilFinished);
            updateTotalStorageJunk(millisUntilFinished);
        }

        private void updateAvailableMemory(long millisUntilFinished) {
            float step = ((100 - memoryScanResult.getAvailableRAMPercentage()) / DEFAULT_SCAN_LENGTH)
                    * (DEFAULT_SCAN_LENGTH - millisUntilFinished);
            updateAvailableMemoryView(100 - step);
        }

        private void updateBackgroundAppsToClean(long millisUntilFinished) {
            int step = (int) ((memoryScanResult.getRunningProcesses().size() / (float) DEFAULT_SCAN_LENGTH)
                    * (DEFAULT_SCAN_LENGTH - millisUntilFinished));

            updateBackgroundAppsToCleanView(step, null);
        }

        private void updateAvailableStorage(long millisUntilFinished) {
            float step = ((100 - storageScanResult.getAvailableStoragePercentage()) / DEFAULT_SCAN_LENGTH)
                    * (DEFAULT_SCAN_LENGTH - millisUntilFinished);
            updateAvailableStorageView(100 - step);
        }

        private void updateTotalStorageJunk(long millisUntilFinished) {
            long step = (long) ((storageScanResult.calculateTotalJunk() / (float) DEFAULT_SCAN_LENGTH)
                    * (DEFAULT_SCAN_LENGTH - millisUntilFinished));
            updateTotalStorageJunkView(step);
        }

        @Override
        public void onFinish() {
            onScanFinished();
        }
    }

    private ArtificialScanProgressTimer artificialProgressTimer;

    private CacheScanReceiver cacheScanReceiver = new CacheScanReceiver();
    private CacheScanReceiver.CacheScanListener cacheScanListener =
            new CacheScanReceiver.CacheScanListener() {
                @Override
                public void onCacheScanInProgress(String packageName, int count, int size) {

                }

                @Override
                public void onCacheScanFinished(List<PackageCache> packageCaches,
                                                long totalCacheSize) {

                    launchJunkScan();
                }
            };

    private JunkScanReceiver junkScanReceiver = new JunkScanReceiver();
    private JunkScanReceiver.JunkScanListener junkScanListener =
            new JunkScanReceiver.JunkScanListener() {
                @Override
                public void onJunkScanInProgress(File file) {
                }

                @Override
                public void onJunkScanFinished(List<JunkFile> junkFiles, int totalJunkSize) {
                }
            };

    private StorageScanReceiver storageScanReceiver = new StorageScanReceiver();
    private StorageScanReceiver.StorageScanListener storageScanListener =
            new StorageScanReceiver.StorageScanListener() {
                @Override
                public void onPackagesScanFinished(List<PackageInfo> packagesInfo,
                                                   long totalInternalCacheSize) {
                    storageScanResult = new StorageScanResult.Builder()
                            .junkPackagesInfo(packagesInfo)
                            .availableStorage(ScanUtils.getAvailableStorage())
                            .totalStorage(ScanUtils.getTotalStorageSize())
                            .totalInternalCacheSize(totalInternalCacheSize)
                            .build();

                    updateStorageScanResultView();

                    startArtificialScanProgress();
                }
            };

    private MemoryScanReceiver memoryScanReceiver = new MemoryScanReceiver();
    private MemoryScanReceiver.MemoryScanListener memoryScanListener =
            new MemoryScanReceiver.MemoryScanListener() {

                @Override
                public void onMemoryScanFinished(MemoryScanResult scanResult) {
                    memoryScanResult = scanResult;

                    launchStorageScan();
                }
            };

    protected TextView noOfAppsToCleanTextView;
    protected TextView noOfTotalJunkTextView;

    private boolean isScanInProgress;
    private MemoryScanResult memoryScanResult;
    private StorageScanResult storageScanResult;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showArtificialProgress = getArguments().getBoolean(KEY_SHOW_ARTIFICIAL_PROGRESS);
    }

    @Override
    public void onDestroy() {
        stopArtificialProgress();

        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_scan, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews();

        registerScanReceivers();
        startScan();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        unregisterScanReceivers();
    }

    @Override
    public void onResume() {
        super.onResume();

        memoryScanReceiver.registerListener(memoryScanListener);
    }

    @Override
    public void onPause() {
        memoryScanReceiver.unregisterListener();

        super.onPause();
    }

    private void startScan() {
        if (!isScanInProgress) {
            isScanInProgress = true;
            registerScanReceivers();
            launchMemoryScan();
            updateView();
        }
    }

    private void startArtificialScanProgress() {
        long scanLength = DEFAULT_SCAN_LENGTH;
        if (!showArtificialProgress) {
            scanLength = MIN_SCAN_LENGTH;
        }

        artificialProgressTimer = new ArtificialScanProgressTimer(scanLength, 1000);
        artificialProgressTimer.start();
    }

    private void onScanFinished() {
        isScanInProgress = false;

        updateView();

        bounceAppsToCleanView();
        announceScanFinished();
    }

    private void registerScanReceivers() {
        junkScanReceiver.register(getActivity());
        cacheScanReceiver.register(getActivity());
        storageScanReceiver.register(getActivity());
        memoryScanReceiver.register(getActivity());

        junkScanReceiver.registerListener(junkScanListener);
        cacheScanReceiver.registerListener(cacheScanListener);
        storageScanReceiver.registerListener(storageScanListener);
    }

    private void unregisterScanReceivers() {
        cacheScanReceiver.unregister(getActivity());
        junkScanReceiver.unregister(getActivity());
        storageScanReceiver.unregister(getActivity());
        memoryScanReceiver.unregister(getActivity());

        unregisterScanListeners();
    }

    private void unregisterScanListeners() {
        cacheScanReceiver.unregisterListener();
        junkScanReceiver.unregisterListener();
        storageScanReceiver.unregisterListener();
    }

    private void announceScanFinished() {

        Intent intent = new Intent(ACTION_SCAN_FINISHED);
        intent.putExtra(KEY_MEMORY_SCAN_RESULT, memoryScanResult);
        intent.putExtra(KEY_STORAGE_SCAN_RESULT, storageScanResult);

        getActivity().sendStickyBroadcast(intent);
    }

    private void bindViews() {
        getView().findViewById(R.id.btn_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        getView().findViewById(R.id.btn_clean).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CleanerActivity) getActivity()).onCleanDevice();
            }
        });

        noOfAppsToCleanTextView = (TextView) getView().findViewById(R.id.tv_apps_to_clean_2);
        noOfTotalJunkTextView = (TextView) getView().findViewById(R.id.tv_junk_2);
    }

    private void bounceAppsToCleanView() {
        updateMemoryScanResultView();
    }

    private void updateView() {
        updateActionsView();
        updateTitleView();

        updateMemoryScanResultView();
        updateStorageScanResultView();
    }

    private void updateActionsView() {
        if (isScanInProgress) {
            getView().findViewById(R.id.btn_stop).setVisibility(View.VISIBLE);
            getView().findViewById(R.id.btn_clean).setVisibility(View.GONE);
        } else {
            getView().findViewById(R.id.btn_stop).setVisibility(View.GONE);
            getView().findViewById(R.id.btn_clean).setVisibility(View.VISIBLE);
        }
    }

    private void updateTitleView() {

        if (isScanInProgress) {
            ((TextView) getView().findViewById(R.id.tv_title)).setText("Performance test in " +
                    "progress..");

        } else {
            ((TextView) getView().findViewById(R.id.tv_title)).setText("There are following " +
                    "issues that can be optimised");
        }
    }

    protected void updateStorageScanResultView() {
        if (storageScanResult != null) {
            updateAvailableStorageView(storageScanResult.getAvailableStoragePercentage());
            updateTotalStorageJunkView(storageScanResult.calculateTotalJunk());
        }
    }

    protected void updateMemoryScanResultView() {
        if (memoryScanResult != null) {
            int appsToClean = memoryScanResult.getRunningProcesses().size();
            long averageMemoryUsage = memoryScanResult.calculateMemoryUsage();

            updateBackgroundAppsToCleanView(appsToClean, averageMemoryUsage);
            updateAvailableMemoryView(memoryScanResult.getAvailableRAMPercentage());
        }
    }

    protected void updateTotalStorageJunkView(long totalJunk) {
        noOfTotalJunkTextView.setText(String.format(
                "%.2fMB",
                MathUtils.convertBytesToMB(totalJunk)));

        ((ViewAnimator) getView().findViewById(R.id.va_junk)).setDisplayedChild(1);

        if (!isScanInProgress) {
            noOfTotalJunkTextView.setTypeface(null, Typeface.BOLD);
        }
    }

    protected void updateAvailableStorageView(float availableStoragePercentage) {
        ((TextView) getView().findViewById(R.id.tv_available_storage_2)).setText(String.format(
                "%.2f%%",
                availableStoragePercentage));

        ((ViewAnimator) getView().findViewById(R.id.va_storage)).setDisplayedChild(1);
    }

    protected void updateAvailableMemoryView(float availableMemoryPercentage) {
        ((TextView) getView().findViewById(R.id.tv_available_memory_2)).setText(String.format(
                "%.2f%%",
                availableMemoryPercentage));

        ((ViewAnimator) getView().findViewById(R.id.va_available_memory)).setDisplayedChild(1);
    }

    protected void updateBackgroundAppsToCleanView(int size, Long memoryUsage) {
        String noOfApps = "" + size;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(noOfApps);
        if (memoryUsage != null) {
            stringBuilder.append(String.format(" (%.2fMB)", MathUtils.convertBytesToMB
                    (memoryUsage)));
        }

        Spannable spannable = new SpannableString(stringBuilder);
        spannable.setSpan(new RelativeSizeSpan(0.8f), noOfApps.length(),
                stringBuilder.toString().length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        noOfAppsToCleanTextView.setText(spannable);

        ((ViewAnimator) getView().findViewById(R.id.va_apps_to_clean)).setDisplayedChild(1);

        if (!isScanInProgress) {
            noOfAppsToCleanTextView.setTypeface(null, Typeface.BOLD);
        }
    }

    private void stopArtificialProgress() {
        if (artificialProgressTimer != null) {
            artificialProgressTimer.cancel();
            artificialProgressTimer = null;
        }
    }

    private void launchCacheScan() {
        Intent intent = new Intent(getActivity(), CacheScanService.class);
        getActivity().startService(intent);
    }

    private void launchJunkScan() {
        Intent intent = new Intent(getActivity(), JunkScanService.class);
        getActivity().startService(intent);
    }

    private void launchStorageScan() {
        Intent intent = new Intent(getActivity(), StorageScanService.class);
        getActivity().startService(intent);
    }

    private void launchMemoryScan() {
        Intent intent = new Intent(getActivity(), MemoryScanService.class);
        getActivity().startService(intent);
    }
}
