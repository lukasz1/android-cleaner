package co.qualityc.cleaner.clean.junk.task;

import android.util.Log;

import java.util.List;

import co.qualityc.cleaner.clean.task.StorageCleanTask;
import co.qualityc.cleaner.scan.junk.JunkFile;


public class JunkCleanTask extends StorageCleanTask {

    public JunkCleanTask(List<JunkFile> junkFiles) {
        super(junkFiles);
    }

    @Override
    public void run() {
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                JunkFile file = (JunkFile) items.get(i);
                if (file.getFile().delete()) {
                    if (progressListener != null) {
                        progressListener.onProgress(file, i, items.size());
                    }
                } else {
                    Log.d("", "File cannot be removed: " + file.getFile().getAbsolutePath());
                }
            }
        }
    }


}
