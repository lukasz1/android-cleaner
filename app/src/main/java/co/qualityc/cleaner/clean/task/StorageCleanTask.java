package co.qualityc.cleaner.clean.task;

import java.util.List;

import co.qualityc.cleaner.StorageItem;


public abstract class StorageCleanTask {
    public interface OnProgressListener {
        void onProgress(StorageItem file, int i, int size);
    }

    protected final List<? extends StorageItem> items;
    protected OnProgressListener progressListener;


    protected StorageCleanTask(List<? extends StorageItem> items) {
        this.items = items;
    }

    public abstract void run();

    public void setProgressListener(OnProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public OnProgressListener getProgressListener() {
        return progressListener;
    }

}
