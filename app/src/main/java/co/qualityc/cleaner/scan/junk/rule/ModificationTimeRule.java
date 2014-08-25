package co.qualityc.cleaner.scan.junk.rule;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

public class ModificationTimeRule extends JunkFileRule implements Serializable {

    private final Date beforeDate;

    public ModificationTimeRule(Date beforeDate) {
        this.beforeDate = beforeDate;
    }

    @Override
    public boolean isApplying(File file) {
        if (file.lastModified() < beforeDate.getTime()) {
            return true;
        }

        return false;
    }
}
