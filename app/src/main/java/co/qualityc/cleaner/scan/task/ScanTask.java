package co.qualityc.cleaner.scan.task;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.List;

import co.qualityc.cleaner.StorageItem;


public abstract class ScanTask {


    protected final PackageManager packageManager;


    public ScanTask(PackageManager packageManager) {
        this.packageManager = packageManager;
    }

    public abstract List<? extends StorageItem> run();

    protected List<ApplicationInfo> getInstalledApplications() {
        return packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
    }

    protected List<String> getPackagesOfInstalledApplications() {
        List<String> result = new ArrayList<String>();
        for (ApplicationInfo applicationInfo : getInstalledApplications()) {
            result.add(applicationInfo.packageName);
        }

        return result;
    }
}
