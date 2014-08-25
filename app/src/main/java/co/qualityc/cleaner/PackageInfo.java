package co.qualityc.cleaner;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PackageInfo extends StorageItem implements Serializable {
    private static final long serialVersionUID = 0L;

    private String packageName;
    private long cacheSize;
    private long junkSize;
    private File dir;


    public static List<PackageInfo> findPackagesWithStorage(List<PackageInfo> packagesInfo) {
        List<PackageInfo> results = new ArrayList<PackageInfo>();
        for (PackageInfo packageInfo : packagesInfo) {
            if (packageInfo.getDir() != null && packageInfo.getDir().exists()) {
                results.add(packageInfo);
            }
        }

        return results;
    }


    public PackageInfo(String packageName) {
        this.packageName = packageName;
    }

    @Override
    protected long getSize() {
        return 0;
    }

    public void setCacheSize(long cacheSize) {
        this.cacheSize = cacheSize;
    }

    public long getCacheSize() {
        return cacheSize;
    }

    public void setJunkSize(long junkSize) {
        this.junkSize = junkSize;
    }

    public long getJunkSize() {
        return junkSize;
    }

    public void setDir(File dir) {
        this.dir = dir;
    }

    public File getDir() {
        return dir;
    }

    @Override
    public String toString() {
        return "name: " + packageName
                + ", dir: " + dir.getAbsolutePath()
                + ", junk: " + junkSize
                + ", cache: " + cacheSize;
    }


}
