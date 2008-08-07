package hudson.plugins.pmd.parser;

import hudson.plugins.pmd.util.AnnotationParser;
import hudson.plugins.pmd.util.model.FileAnnotation;
import hudson.plugins.pmd.util.model.Priority;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

/**
 * A parser for PMD XML files.
 *
 * @author Ulli Hafner
 */
public class PmdParser implements AnnotationParser {
    /** Unique ID of this class. */
    private static final long serialVersionUID = 6507147028628714706L;

    /** {@inheritDoc} */
    public Collection<FileAnnotation>  parse(final File file, final String moduleName) throws InvocationTargetException {
        try {
            return parse(new FileInputStream(file), moduleName);
        }
        catch (FileNotFoundException exception) {
            throw new InvocationTargetException(exception);
        }
    }

    /** {@inheritDoc} */
    public Collection<FileAnnotation>  parse(final InputStream file, final String moduleName) throws InvocationTargetException {
        try {
            Digester digester = new Digester();
            digester.setValidating(false);
            digester.setClassLoader(PmdParser.class.getClassLoader());

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
                if (warning.getPriority() < 3) {
                    priority = Priority.HIGH;
                }
                else if (warning.getPriority() >  3) {
                    priority = Priority.LOW;
                }
                else {
                    priority = Priority.NORMAL;
                }
                Bug bug = new Bug(priority, warning.getMessage() + ".", warning.getRuleset(), warning.getRule(),
                            warning.getBeginline(), warning.getEndline());
                bug.setPackageName(warning.getPackage());
                bug.setModuleName(moduleName);
                bug.setFileName(file.getName());

                annotations.add(bug);
            }
        }
        return annotations;
    }

    /** {@inheritDoc} */
    public String getName() {
        return "PMD";
    }
}

