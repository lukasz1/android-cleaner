package co.qualityc.cleaner.scan.junk.rule;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.Serializable;

public class ExtensionRule extends JunkFileRule implements Serializable {

    private final String extension;

    public ExtensionRule(String extension) {
        this.extension = extension;
    }

    @Override
    public boolean isApplying(File file) {
        if (FilenameUtils.getExtension(file.getAbsolutePath()).equals(extension)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "ExtensionRule(" + extension + ")";
    }
}
