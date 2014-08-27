package co.qualityc.cleaner.scan.storage.service;

import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import co.qualityc.cleaner.PackageInfo;
import co.qualityc.cleaner.SCIntent;
import co.qualityc.cleaner.StorageItem;
import co.qualityc.cleaner.net.RequestManager;
import co.qualityc.cleaner.net.requests.GetRestrictedPackagesRequest;
import co.qualityc.cleaner.scan.StorageScanResult;
import co.qualityc.cleaner.scan.packages.task.PackagesScan;
import co.qualityc.cleaner.scan.service.ScanService;
import co.qualityc.cleaner.scan.utils.ScanUtils;


public class StorageScanService extends ScanService
        implements PackagesScan.OnProgressListener {
    private static final String TAG = StorageScanService.class.getName();
    public static final String ACTION_SCAN_IN_PROGRESS = "cleaner.scan.storage.inProgress";
    public static final String ACTION_SCAN_FINISHED = "cleaner.scan.storage.finished";

    public static final String KEY_FILE = "file";
    public static final String KEY_PACKAGES_INFO = "packagesInfo";
    public static final String KEY_INTERNAL_CACHE_SIZE = "internalCacheSize";
    public static final String KEY_RESULT = "cleaner.scan.storage.result";


    public StorageScanService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);
        List<String> restrictedPackages = null;
        try {
            restrictedPackages = getRestrictedPackages();
        } catch (IOException e) {
            e.printStackTrace();
            restrictedPackages = new ArrayList<String>();   // if cant get list use empty list
        }
        new PackagesScan(getPackageManager(), this, restrictedPackages).run();
    }

    private List<String> getRestrictedPackages() throws IOException,
            com.google.gson.JsonSyntaxException {
        RequestManager requestManager = new RequestManager();
        GetRestrictedPackagesRequest request = new GetRestrictedPackagesRequest.Builder().build();
        String response = requestManager.executeGetRequest(request);
        Type listType = new TypeToken<List<String>>() {}.getType();
        return new Gson().fromJson(response, listType);
    }

    @Override
    public void onPackageScanStarted() {

    }

    @Override
    public void onPackageScanFinished(List<PackageInfo> packageInfoList, long totalInternalCacheSize) {
        announceScanFinished(new ArrayList<PackageInfo>(packageInfoList), totalInternalCacheSize);
    }

    private void announceScanInProgress(File file) {
        Intent intent = new Intent(ACTION_SCAN_IN_PROGRESS);
        intent.putExtra(KEY_FILE, file);

        sendBroadcast(intent);
    }

    private void announceScanFinished(List<PackageInfo> packageInfoList, long totalInternalCacheSize) {
        Intent intent = new Intent(ACTION_SCAN_FINISHED);
        intent.putExtra(SCIntent.KEY_BACKGROUND_SCAN, isBackgroundScan);
        intent.putExtra(KEY_PACKAGES_INFO, new ArrayList<PackageInfo>(packageInfoList));
        intent.putExtra(KEY_INTERNAL_CACHE_SIZE, totalInternalCacheSize);
        intent.putExtra(KEY_RESULT, buildResult(packageInfoList, totalInternalCacheSize));

        sendBroadcast(intent);
    }

    private StorageScanResult buildResult(List<PackageInfo> packageInfoList, long totalInternalCacheSize) {
        return new StorageScanResult.Builder()
                .junkPackagesInfo(packageInfoList)
                .availableStorage(ScanUtils.getAvailableStorage())
                .totalStorage(ScanUtils.getTotalStorageSize())
                .totalInternalCacheSize(totalInternalCacheSize)
                .build();
    }

    @Override
    protected void onStarted() {
    }

    @Override
    protected void onFinished(List<? extends StorageItem> items) {
    }
}
