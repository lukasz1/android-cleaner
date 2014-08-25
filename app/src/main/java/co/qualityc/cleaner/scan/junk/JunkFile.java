package co.qualityc.cleaner.scan.junk;


import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import co.qualityc.cleaner.StorageItem;
import co.qualityc.cleaner.scan.junk.rule.JunkFileRule;


public class JunkFile extends StorageItem implements Serializable {
    private final File file;
    private List<JunkFileRule> rules = new ArrayList<JunkFileRule>();


    public JunkFile(File file) {
        this.file = file;
    }

    public void addRule(JunkFileRule rule) {
        rules.add(rule);
    }

    public List<JunkFileRule> getRules() {
        return rules;
    }

    public File getFile() {
        return file;
    }

    @Override
    protected long getSize() {
        return file.length();
    }
}
