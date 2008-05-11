package hudson.plugins.pmd.parser;

import hudson.plugins.pmd.util.model.MavenModule;
import hudson.plugins.pmd.util.model.Priority;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

/**
 * A parser for PMD XML files.
 */
public class PmdParser {
    /**
     * Returns the parsed PMD analysis file.
     *
     * @param file
     *            the PMD analysis file
     * @param moduleName
     *            name of the maven module
     * @return the parsed result (stored in the module instance)
     * @throws IOException
     *             if the file could not be parsed
     * @throws SAXException
     *             if the file is not in valid XML format
     */
    public MavenModule parse(final InputStream file, final String moduleName) throws IOException, SAXException {
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

    /**
     * Converts the internal structure to the annotations API.
     *
     * @param collection
     *            the internal maven module
     * @param moduleName
     *            name of the maven module
     * @return a maven module of the annotations API
     */
    private MavenModule convert(final Pmd collection, final String moduleName) {
        MavenModule module = new MavenModule(moduleName);
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
                            warning.getBeginline(), warning.getEndline(), PmdMessages.getInstance().getMessage(warning.getRuleset(), warning.getRule()));
                bug.setPackageName(warning.getPackage());
                bug.setModuleName(moduleName);
                bug.setFileName(file.getName());

                module.addAnnotation(bug);
            }
        }
        return module;
    }
}

