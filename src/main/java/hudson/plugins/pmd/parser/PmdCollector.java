package hudson.plugins.pmd.parser;

import hudson.FilePath;
import hudson.FilePath.FileCallable;
import hudson.plugins.pmd.Messages;
import hudson.plugins.pmd.util.model.JavaProject;
import hudson.plugins.pmd.util.model.MavenModule;
import hudson.remoting.VirtualChannel;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.tools.ant.types.FileSet;
import org.xml.sax.SAXException;

/**
 * Parses the PMD files that match the specified pattern and creates a
 * corresponding Java project with a collection of annotations.
 *
 * @author Ulli Hafner
 */
public class PmdCollector implements FileCallable<JavaProject> {
    /** Slash separator on UNIX. */
    private static final String SLASH = "/";
    /** Generated ID. */
    private static final long serialVersionUID = -6415863872891783891L;
    /** Determines whether to skip old files. */
    private static final boolean SKIP_OLD_FILES = false;
    /** Logger. */
    private transient PrintStream logger;
    /** Build time stamp, only newer files are considered. */
    private final long buildTime;
    /** Ant file-set pattern to scan for PMD files. */
    private final String filePattern;

    /**
     * Creates a new instance of <code>PmdCollector</code>.
     *
     * @param listener
     *            the Logger
     * @param buildTime
     *            build time stamp, only newer files are considered
     * @param filePattern
     *            ant file-set pattern to scan for PMD files
     */
    public PmdCollector(final PrintStream listener, final long buildTime, final String filePattern) {
        logger = listener;
        this.buildTime = buildTime;
        this.filePattern = filePattern;
    }

    /** {@inheritDoc} */
    public JavaProject invoke(final File workspace, final VirtualChannel channel) throws IOException {
        String[] pmdFiles = findPmdFiles(workspace);
        JavaProject project = new JavaProject();

        if (pmdFiles.length == 0) {
            project.setError("No pmd report files were found. Configuration error?");
            return project;
        }

        try {
            for (String file : pmdFiles) {
                File pmdFile = new File(workspace, file);

                String moduleName = guessModuleName(pmdFile.getAbsolutePath());
                MavenModule module = new MavenModule(moduleName);

                if (SKIP_OLD_FILES && pmdFile.lastModified() < buildTime) {
                    String message = Messages.PMD_PMDCollector_Error_FileNotUpToDate(pmdFile);
                    getLogger().println(message);
                    module.setError(message);
                    continue;
                }
                if (!pmdFile.canRead()) {
                    String message = Messages.PMD_PMDCollector_Error_NoPermission(pmdFile);
                    getLogger().println(message);
                    module.setError(message);
                    continue;
                }
                if (new FilePath(pmdFile).length() <= 0) {
                    String message = Messages.PMD_PMDCollector_Error_EmptyFile(pmdFile);
                    getLogger().println(message);
                    module.setError(message);
                    continue;
                }

                module = parseFile(workspace, pmdFile, module);
                project.addModule(module);
            }
        }
        catch (InterruptedException exception) {
            getLogger().println("Parsing has been canceled.");
        }
        return project;
    }

    /**
     * Parses the specified PMD file and maps all warnings to a
     * corresponding annotation. If the file could not be parsed then an empty
     * module with an error message is returned.
     *
     * @param workspace
     *            the root of the workspace
     * @param pmdFile
     *            the file to parse
     * @param emptyModule
     *            an empty module with the guessed module name
     * @return the created module
     * @throws InterruptedException
     */
    private MavenModule parseFile(final File workspace, final File pmdFile, final MavenModule emptyModule) throws InterruptedException {
        Exception exception = null;
        MavenModule module = emptyModule;
        try {
            FilePath filePath = new FilePath(pmdFile);
            PmdParser pmdParser = new PmdParser();
            module = pmdParser.parse(filePath.read(), emptyModule.getName());
            getLogger().println("Successfully parsed PMD file " + pmdFile + " of module "
                    + module.getName() + " with " + module.getNumberOfAnnotations() + " warnings.");
        }
        catch (IOException e) {
            exception = e;
        }
        catch (SAXException e) {
            exception = e;
        }
        if (exception != null) {
            String errorMessage = Messages.PMD_PMDCollector_Error_Exception(pmdFile)
                    + "\n\n" + ExceptionUtils.getStackTrace(exception);
            getLogger().println(errorMessage);
            module.setError(errorMessage);
        }
        return module;
    }

    /**
     * Guesses the module name based on the specified file name. Actually works
     * only for maven projects.
     *
     * @param fileName
     *            the filename to guess the module name from
     * @return the module name
     */
    public static String guessModuleName(final String fileName) {
        String separator;
        if (fileName.contains(SLASH)) {
            separator = SLASH;
        }
        else {
            separator = "\\";
        }
        String path = StringUtils.substringBefore(fileName, separator + "target");
        if (fileName.equals(path)) {
            return "";
        }
        if (path.contains(separator)) {
            return StringUtils.substringAfterLast(path, separator);
        }
        else {
            return path;
        }
    }

    /**
     * Returns an array with the filenames of the PMD files that have been
     * found in the workspace.
     *
     * @param workspaceRoot
     *            root directory of the workspace
     * @return the filenames of the PMD files
     */
    private String[] findPmdFiles(final File workspaceRoot) {
        FileSet fileSet = new FileSet();
        org.apache.tools.ant.Project project = new org.apache.tools.ant.Project();
        fileSet.setProject(project);
        fileSet.setDir(workspaceRoot);
        fileSet.setIncludes(filePattern);

        return fileSet.getDirectoryScanner(project).getIncludedFiles();
    }

    /**
     * Returns the logger.
     *
     * @return the logger
     */
    private PrintStream getLogger() {
        if (logger == null) {
            logger = System.out;
        }
        return logger;
    }
}