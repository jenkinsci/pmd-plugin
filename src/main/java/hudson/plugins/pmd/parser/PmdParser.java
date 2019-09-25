package hudson.plugins.pmd.parser;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;

import hudson.plugins.analysis.core.AbstractAnnotationParser;
import hudson.plugins.analysis.util.SecureDigester;
import hudson.plugins.analysis.util.model.FileAnnotation;
import hudson.plugins.analysis.util.model.Priority;

/**
 * A parser for PMD XML files.
 *
 * @author Ulli Hafner
 */
public class PmdParser extends AbstractAnnotationParser {
    /** Unique ID of this class. */
    private static final long serialVersionUID = 6507147028628714706L;

    /** PMD priorities smaller or equal to this value are mapped to {@link Priority#HIGH}. */
    private static final int PMD_DEFAULT_CUTOFF_HIGH_PRIORITY = 2;
    /** PMD priorities smaller or equal to this value are mapped to {@link Priority#NORMAL}. */
    private static final int PMD_DEFAULT_CUTOFF_NORMAL_PRIORITY = 4;

    private final int cutoffHighPriority;
    private final int cutoffNormalPriority;

    /**
     * Creates a new instance of {@link PmdParser}.
     */
    public PmdParser() {
        this(StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY);
    }

    /**
     * Creates a new instance of {@link PmdParser}.
     *
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     *
     * @param cutoffHighPriority Cutoff for mapping to high priority.
     * @param cutoffNormalPriority Cutoff for mapping to normal priority.
     */
    public PmdParser(final String defaultEncoding, String cutoffHighPriority, String cutoffNormalPriority) {
        super(defaultEncoding);

        this.cutoffHighPriority = parseString(cutoffHighPriority, PMD_DEFAULT_CUTOFF_HIGH_PRIORITY);
        this.cutoffNormalPriority = parseString(cutoffHighPriority, PMD_DEFAULT_CUTOFF_NORMAL_PRIORITY);
    }

    private static final int parseString(String value, int defaultValue) {
        return StringUtils.isNotEmpty(value) ? Integer.valueOf(value) : defaultValue;
    }

    @Override
    public Collection<FileAnnotation> parse(final InputStream file, final String moduleName) throws InvocationTargetException {
        try {
            SecureDigester digester = new SecureDigester(PmdParser.class);

            String rootXPath = "pmd";
            digester.addObjectCreate(rootXPath, Pmd.class);
            digester.addSetProperties(rootXPath);

            String fileXPath = "pmd/file";
            digester.addObjectCreate(fileXPath, hudson.plugins.pmd.parser.File.class);
            digester.addSetProperties(fileXPath);
            digester.addSetNext(fileXPath, "addFile", hudson.plugins.pmd.parser.File.class.getName());

            String bugXPath = "pmd/file/violation";
            digester.addObjectCreate(bugXPath, Violation.class);
            digester.addSetProperties(bugXPath);
            digester.addCallMethod(bugXPath, "setMessage", 0);
            digester.addSetNext(bugXPath, "addViolation", Violation.class.getName());

            Pmd module = (Pmd)digester.parse(file);
            if (module == null) {
                throw new SAXException("Input stream is not a PMD file.");
            }

            return convert(module, moduleName);
        }
        catch (IOException exception) {
            throw new InvocationTargetException(exception);
        }
        catch (SAXException exception) {
            throw new InvocationTargetException(exception);
        }
    }

    /**
     * Converts the internal structure to the annotations API.
     *
     * @param collection
     *            the internal maven module
     * @param moduleName
     *            name of the maven module
     * @return a maven module of the annotations API
     */
    private Collection<FileAnnotation> convert(final Pmd collection, final String moduleName) {
        ArrayList<FileAnnotation> annotations = new ArrayList<FileAnnotation>();

        for (hudson.plugins.pmd.parser.File file : collection.getFiles()) {
            for (Violation warning : file.getViolations()) {
                Priority priority;
                if (warning.getPriority() <= cutoffHighPriority) {
                    priority = Priority.HIGH;
                }
                else if (warning.getPriority() <= cutoffNormalPriority) {
                    priority = Priority.NORMAL;
                }
                else {
                    priority = Priority.LOW;
                }
                Bug bug = new Bug(priority, createMessage(warning), warning.getRuleset(), warning.getRule(),
                            warning.getBeginline(), warning.getEndline());
                bug.setPackageName(warning.getPackage());
                bug.setModuleName(moduleName);
                bug.setFileName(file.getName());
                bug.setColumnPosition(warning.getBegincolumn(), warning.getEndcolumn());
                bug.setContextHashCode(createContextHashCode(file.getName(), warning.getBeginline(), warning.getRule()));

                annotations.add(bug);
            }
        }
        return annotations;
    }

    private String createMessage(final Violation warning) {
        String original = warning.getMessage();
        if (StringUtils.endsWith(original, ".")) {
            return original;
        }
        else {
            return original + ".";
        }
    }
}

