package co.qualityc.cleaner;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PackageCache extends StorageItem implements Serializable {

    private final String packageName;
    private final long cacheSize;
    private long externalCacheSize;

    public PackageCache(String packageName, long cacheSize) {
        this.packageName = packageName;
        this.cacheSize = cacheSize;
    }

    public long getExternalCacheSize() {
        return externalCacheSize;
    }

    public void setExternalCacheSize(long externalCacheSize) {
        this.externalCacheSize = externalCacheSize;
    }

    public String getPackageName() {
        return packageName;
    }

    public long getCacheSize() {
        return cacheSize;
    }

    public List<File> getAllCacheFiles() {
        List<File> files = new ArrayList<File>();

        return files;
    }

    @Override
    protected long getSize() {
        return cacheSize + externalCacheSize;
    }
}
