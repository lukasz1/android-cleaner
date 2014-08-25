package co.qualityc.cleaner.scan.junk.task;

import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import co.qualityc.cleaner.scan.junk.JunkDir;
import co.qualityc.cleaner.scan.junk.JunkFile;
import co.qualityc.cleaner.scan.junk.JunkRules;
import co.qualityc.cleaner.scan.junk.rule.JunkFileRule;
import co.qualityc.cleaner.scan.task.ScanTask;


public class JunkScanTask extends ScanTask {
    private static final List<JunkFileRule> JUNK_RULES = new ArrayList<JunkFileRule>();
    private static final List<JunkFileRule> NOT_JUNK_RULES = new ArrayList<JunkFileRule>();

    static {
        JUNK_RULES.add(JunkRules.DEFAULT_MODIFICATION_TIME_RULE);
        JUNK_RULES.addAll(JunkRules.DEFAULT_EXTENSION_RULES);
        NOT_JUNK_RULES.addAll(JunkRules.DEFAULT_NOT_NAME_RULES);
    }

    public interface OnProgressListener {
        void onProgress(File file);
    }

    protected OnProgressListener progressListener;


    public JunkScanTask(PackageManager packageManager) {
        super(packageManager);
    }

    public void setProgressListener(OnProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    protected void onProgress(File file) {
        if (progressListener != null) {
            progressListener.onProgress(file);
        }
    }

    @Override
    public List<JunkFile> run() {
        final String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return findJunk(Environment.getExternalStorageDirectory(), getPackagesOfInstalledApplications());
        } else {
            return null;
        }
    }

    private List<JunkFile> findJunk(File dir, List<String> excludedNames) {
        List<JunkFile> junkFiles = new ArrayList<JunkFile>();

        List<File> storageFiles = getFiles(dir, excludedNames);
        if (storageFiles != null) {
            for (int i = 0; i < storageFiles.size(); i++) {
                File file = storageFiles.get(i);

                onProgress(file);

                if (isNotJunk(file)) {
                    Log.d("", "Not junk: " + file.getAbsolutePath());
                    continue;
                }

                JunkFile junk = null;
                for (JunkFileRule rule : JUNK_RULES) {
                    if (rule.isApplying(file)) {
                        if (junk == null) {
                            junk = new JunkFile(file);
                        }

                        junk.addRule(rule);
                    }
                }

                if (junk != null) {
                    Log.d("", "Junk: " + file.getAbsolutePath());
                    junkFiles.add(junk);
                } else {
                    if (file.isDirectory()) {

                        List<JunkFile> junksInDir = findJunk(file, excludedNames);
                        if (junksInDir != null && !junksInDir.isEmpty()) {
                            JunkDir junkDir = new JunkDir(file);
                            junkDir.addFiles(junksInDir);

                            junkFiles.add(junkDir);
                        }

                    }
                }
            }
        }

        return junkFiles;
    }

    private List<File> getFiles(File directory, List<String> excludedFileNames) {
        List<File> files = new ArrayList<File>();

        final File[] dirFiles = directory.listFiles();
        if (dirFiles != null) {
            for (File file : dirFiles) {
                if (file != null) {
                    if (!isExcluded(file, excludedFileNames)) {
                        files.add(file);
                    }
                }
            }
        }

        return files;
    }

    private boolean isNotJunk(File file) {
        for (JunkFileRule rule : NOT_JUNK_RULES) {
            if (rule.isApplying(file)) {
                return true;
            }
        }

        return false;
    }

    private boolean isExcluded(File dir, List<String> excludedNames) {
        for (String excludedName : excludedNames) {
            if (dir.getAbsolutePath().contains(excludedName)) {
                return true;
            }
        }

        return false;
    }
}
