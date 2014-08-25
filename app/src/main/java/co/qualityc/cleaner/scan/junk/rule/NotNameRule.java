package co.qualityc.cleaner.scan.junk.rule;

import java.io.File;
import java.io.Serializable;

public class NotNameRule extends JunkFileRule implements Serializable {

    private String subString;

    @Override
    public boolean isApplying(File file) {
        if (file.getAbsolutePath().toLowerCase().contains(subString)) {
            return true;
        }

        return false;
    }

    public void setSubString(String subString) {
        this.subString = subString;
    }

    @Override
    public String toString() {
        return "NameRule, substring: " + subString;
    }

    public static class Builder {

        private String subString;

        public Builder nameContains(String subString) {
            this.subString = subString;

            return this;
        }

        public NotNameRule build() {
            NotNameRule rule = new NotNameRule();
            rule.setSubString(subString);

            return rule;
        }
    }
}
