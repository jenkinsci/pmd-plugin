package hudson.plugins.pmd.parser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;

import org.apache.commons.lang.StringUtils;

/**
 * Provides access to rule descriptions and examples.
 *
 * @author Ulli Hafner
 */
public final class PmdMessages {
    /** Singleton instance. */
    private static final PmdMessages INSTANCE = new PmdMessages();

    /** Available rule sets. */
    private final Map<String, RuleSet> rules = new HashMap<String, RuleSet>();

    /**
     * Returns the singleton instance.
     *
     * @return the singleton instance
     */
    public static PmdMessages getInstance() {
        return INSTANCE;
    }

    /**
     * Creates a new instance of <code>PmdMessages</code>.
     */
    private PmdMessages() {
        try {
            Iterator<RuleSet> ruleSets = new RuleSetFactory().getRegisteredRuleSets();
            for (Iterator<RuleSet> iterator = ruleSets; iterator.hasNext();) {
                RuleSet ruleSet = iterator.next();
                rules.put(ruleSet.getName(), ruleSet);
            }
        }
        catch (RuleSetNotFoundException exception) {
            Logger.getLogger(PmdMessages.class.getName()).log(Level.SEVERE, "Installation problem: can't access PMD messages.");
        }
    }

    /**
     * Returns the message for the specified PMD rule.
     *
     * @param ruleSetName
     *            PMD rule set
     * @param ruleName
     *            PMD rule ID
     * @return the message
     */
    public String getMessage(final String ruleSetName, final String ruleName) {
        if (rules.containsKey(ruleSetName)) {
            RuleSet ruleSet = rules.get(ruleSetName);
            Rule rule = ruleSet.getRuleByName(ruleName);
            if (rule != null) {
                return createMessage(rule);
            }
        }
        return StringUtils.EMPTY;
    }

    /**
     * Creates the message string to be shown for the specified rule.
     *
     * @param rule
     *            the rule
     * @return the message string to be shown for the specified rule
     */
    private String createMessage(final Rule rule) {
        List<String> examples = rule.getExamples();
        if (!examples.isEmpty()) {
            return rule.getDescription() + "<pre>" + examples.get(0) + "</pre>";
        }
        return rule.getDescription();
    }
}

