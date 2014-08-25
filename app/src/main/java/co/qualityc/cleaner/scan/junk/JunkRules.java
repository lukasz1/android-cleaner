package co.qualityc.cleaner.scan.junk;


import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

import co.qualityc.cleaner.scan.junk.rule.ExtensionRule;
import co.qualityc.cleaner.scan.junk.rule.JunkFileRule;
import co.qualityc.cleaner.scan.junk.rule.ModificationTimeRule;
import co.qualityc.cleaner.scan.junk.rule.NotNameRule;

public class JunkRules {
    public static JunkFileRule DEFAULT_MODIFICATION_TIME_RULE;
    public static List<ExtensionRule> DEFAULT_EXTENSION_RULES;
    public static List<NotNameRule> DEFAULT_NOT_NAME_RULES;

    static {
        DEFAULT_MODIFICATION_TIME_RULE = createDefaultModificationTimeRule();
        DEFAULT_EXTENSION_RULES = createExtensionRules();
        DEFAULT_NOT_NAME_RULES = createDefaultNotNameRules();
    }


    private static ModificationTimeRule createDefaultModificationTimeRule() {
        LocalDate date = new LocalDate();
        return new ModificationTimeRule(date.minusMonths(1).toDate());
    }

    private static List<ExtensionRule> createExtensionRules() {
        List<ExtensionRule> rules = new ArrayList<ExtensionRule>();
        rules.add(new ExtensionRule("png"));

        return rules;
    }

    private static List<NotNameRule> createDefaultNotNameRules() {
        List<NotNameRule> rules = new ArrayList<NotNameRule>();

        rules.add(new NotNameRule.Builder().nameContains("music").build());
        rules.add(new NotNameRule.Builder().nameContains("podcast").build());
        rules.add(new NotNameRule.Builder().nameContains("dcim").build());
        rules.add(new NotNameRule.Builder().nameContains("picture").build());
        rules.add(new NotNameRule.Builder().nameContains("photo").build());
        rules.add(new NotNameRule.Builder().nameContains("alarm").build());
        rules.add(new NotNameRule.Builder().nameContains("ringtone").build());
        rules.add(new NotNameRule.Builder().nameContains("notification").build());
        rules.add(new NotNameRule.Builder().nameContains("android").build());
        rules.add(new NotNameRule.Builder().nameContains("movies").build());

        return rules;
    }
}
