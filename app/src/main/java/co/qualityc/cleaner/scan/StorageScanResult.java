package co.qualityc.cleaner.scan;

import java.io.Serializable;
import java.util.List;

import co.qualityc.cleaner.PackageInfo;
import co.qualityc.cleaner.utils.MathUtils;


public class StorageScanResult implements Serializable {
    private static final long serialVersionUID = 0L;

    private List<PackageInfo> junkPackagesInfo;
    private double availableStorage;
    private double totalStorageSize;
    private long totalInternalCacheSize;


    public void setJunkPackagesInfo(List<PackageInfo> junkPackagesInfo) {
        this.junkPackagesInfo = junkPackagesInfo;
    }

    public List<PackageInfo> getJunkPackagesInfo() {
        return junkPackagesInfo;
    }

    public void setAvailableStorage(double availableStorage) {
        this.availableStorage = availableStorage;
    }

    public float getAvailableStoragePercentage() {
        return (float) ((availableStorage / totalStorageSize) * 100);
    }

    public double getAvailableStorage() {
        return availableStorage;
    }

    public void setTotalStorageSize(double totalStorageSize) {
        this.totalStorageSize = totalStorageSize;
    }

    public double getTotalStorageSize() {
        return totalStorageSize;
    }

    public void setTotalInternalCacheSize(long totalInternalCacheSize) {
        this.totalInternalCacheSize = totalInternalCacheSize;
    }

    public long getTotalInternalCacheSize() {
        return totalInternalCacheSize;
    }

    /*
        in bytes
    */
    public long calculateTotalJunk() {
        long totalJunk = 0;
        for (PackageInfo packageInfo : junkPackagesInfo) {
            totalJunk += packageInfo.getJunkSize();
            totalJunk += packageInfo.getCacheSize();
        }

        totalJunk += totalInternalCacheSize;

        return totalJunk;
    }

    public float calculateTotalJunkMB() {
        return MathUtils.convertBytesToMB(calculateTotalJunk());
    }


    public static class Builder {
        private List<PackageInfo> packagesInfo;
        private double availableStorage;
        private double totalStorageSize;
        private long totalInternalCacheSize;

        public Builder junkPackagesInfo(List<PackageInfo> packagesInfo) {
            this.packagesInfo = packagesInfo;
            return this;
        }

        public Builder availableStorage(double availableStorage) {
            this.availableStorage = availableStorage;
            return this;
        }

        public Builder totalStorage(double totalStorageSize) {
            this.totalStorageSize = totalStorageSize;
            return this;
        }

        public Builder totalInternalCacheSize(long totalInternalCacheSize) {
            this.totalInternalCacheSize = totalInternalCacheSize;
            return this;
        }

        public StorageScanResult build() {
            StorageScanResult result = new StorageScanResult();
            result.setJunkPackagesInfo(packagesInfo);
            result.setAvailableStorage(availableStorage);
            result.setTotalStorageSize(totalStorageSize);
            result.setTotalInternalCacheSize(totalInternalCacheSize);

            return result;
        }

    }
}
