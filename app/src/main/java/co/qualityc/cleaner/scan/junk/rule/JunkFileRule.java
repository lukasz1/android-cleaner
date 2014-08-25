package co.qualityc.cleaner.scan.junk.rule;

import java.io.File;

public abstract class JunkFileRule {
    public abstract boolean isApplying(File file);
}
