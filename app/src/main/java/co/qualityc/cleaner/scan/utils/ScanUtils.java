package co.qualityc.cleaner.scan.utils;

import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

public class ScanUtils {
    @SuppressWarnings("deprecation")
    public static double getAvailableStorage() {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());

        double sdAvailSize;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            sdAvailSize = (double) stat.getAvailableBlocksLong() * (double) stat.getBlockSizeLong();
        } else {
            sdAvailSize = (double) stat.getAvailableBlocks() * (double) stat.getBlockSize();
        }

        return sdAvailSize;
    }

    @SuppressWarnings("deprecation")
    public static double getTotalStorageSize() {
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());

        double totalSize;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            totalSize = (double) statFs.getBlockCountLong() * (double) statFs.getBlockSizeLong();
        } else {
            totalSize = (double) statFs.getBlockCount() * (double) statFs.getBlockSize();
        }

        return totalSize;
    }
}
