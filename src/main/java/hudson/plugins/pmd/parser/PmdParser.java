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

    /** PMD priorities smaller than this value are mapped to {@link Priority#HIGH}. */
    private static final int PMD_PRIORITY_MAPPED_TO_HIGH_PRIORITY = 3;
    /** PMD priorities greater than this value are mapped to {@link Priority#LOW}. */
    private static final int PMD_PRIORITY_MAPPED_TO_LOW_PRIORITY = 4;

    /**
     * Creates a new instance of {@link PmdParser}.
     */
    public PmdParser() {
        super(StringUtils.EMPTY);
    }

    /**
     * Creates a new instance of {@link PmdParser}.
     *
     * @param defaultEncoding
     *            the default encoding to be used when reading and parsing files
     */
    public PmdParser(final String defaultEncoding) {
        super(defaultEncoding);
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
                if (warning.getPriority() < PMD_PRIORITY_MAPPED_TO_HIGH_PRIORITY) {
                    priority = Priority.HIGH;
                }
                else if (warning.getPriority() >  PMD_PRIORITY_MAPPED_TO_LOW_PRIORITY) {
                    priority = Priority.LOW;
                }
                else {
                    priority = Priority.NORMAL;
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

