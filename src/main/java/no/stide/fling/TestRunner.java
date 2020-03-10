package no.stide.fling;

import no.stide.jchalk.Chalk;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class TestRunner {

    static final Chalk chalk = new Chalk();
    private String[] classpaths;
    private ArrayList<String> exclude = new ArrayList<>();
    private ArrayList<String> include = new ArrayList<>();
    private ArrayList<TestSuite> testSuites = new ArrayList<>();
    private TestStatus testStatus = TestStatus.NOT_STARTED;

    public TestRunner(String[] classpaths) {
        this.classpaths = classpaths;
    }

    public void run() throws TestFailedException {

        TestSuite.resetTestCounters();

        PathMatcher[] excludeMatchers = new PathMatcher[exclude.size()];
        for (int i = 0; i < this.exclude.size(); i++) {
            excludeMatchers[i] = FileSystems.getDefault().getPathMatcher("glob:" + exclude.get(i));
        }
        if (include.size() == 0) {
            include.add("**/*");
            include.add("*");
        }
        PathMatcher[] includeMatchers = new PathMatcher[include.size()];
        for (int i = 0; i < include.size(); i++) {
            includeMatchers[i] = FileSystems.getDefault().getPathMatcher("glob:" + include.get(i));
        }

        for (String classpath : this.classpaths) {
            try {
                Files.walk(Paths.get(classpath)).filter(Files::isRegularFile).forEach(path -> {
                    try {
                        Boolean process = false;
                        for (PathMatcher pm : includeMatchers) {
                            if (pm.matches(path)) {
                                process = true;
                                break;
                            }
                        }
                        // if (!process) System.out.println("Not included: " + path);
                        for (PathMatcher pm : excludeMatchers) {
                            if (pm.matches(path)) {
                                // System.out.println("Excluding file: " + path);
                                process = false;
                                break;
                            }
                        }
                        if (process) {
                            processClass(classpath, path);
                        }
                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }
                });
            } catch (InvalidPathException | NoSuchFileException e) {
                System.err.println(chalk.red().apply("Invalid path: " + classpath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.testStatus = TestSuite.hasPassedAllTests() ? TestStatus.PASSED : TestStatus.FAILED;

        try {
            fullTestSummary();
        } catch (TestNotStartedException e) {
            e.printStackTrace();
        }

        if (this.testStatus == TestStatus.FAILED) {
            throw new TestFailedException();
        }
    }

    private void processClass(String classpath, Path filePath) throws ClassNotFoundException, IOException {
        String packagePath = Paths.get(classpath).relativize(filePath).toString();
        packagePath = packagePath.replaceAll("\\" + File.separator, ".").replaceAll("\\.class$", "");
        // System.out.println("FOUND CLASS: " + packagePath);

        URL classPathUrl = new File(classpath).toURI().toURL();
        URLClassLoader cl = new URLClassLoader(new URL[] { classPathUrl });

        Class<?> clazz = cl.loadClass(packagePath);
        Method[] methods = clazz.getDeclaredMethods();

        Object instance = null;
        a: for (Method m : methods) {
            Annotation[] annotations = m.getAnnotations();
            for (Annotation annotation : annotations) {
                if (annotation.annotationType().equals(TestGroup.class)) {
                    try {
                        Type[] paramTypes = m.getGenericParameterTypes();
                        if (paramTypes.length < 1 || !(paramTypes[0] instanceof Class
                                && (Class<?>) paramTypes[0] == TestInitiator.class)) {
                            System.err.println(chalk.red()
                                    .apply("\n[ERROR] Method " + getMethodLocation(m)
                                            + " has the wrong signature! Expected one parameter of type '"
                                            + TestInitiator.class.getCanonicalName() + "'."));
                            continue a;
                        }
                        if (instance == null) {
                            System.out.println("Running tests in " + chalk.blue().bold().apply(packagePath) + "...\n");
                            instance = clazz.getConstructor().newInstance();
                        }
                        processMethod(packagePath, instance, m, annotation);
                    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                            | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        cl.close();
    }

    private void processMethod(String classPath, Object instance, Method method, Annotation annotation) {
        String description = ((TestGroup) annotation).description();
        final Object self = instance;
        TestSuite suite = new TestSuite(classPath, description, (TestInitiator ti) -> {
            try {
                method.invoke(self, ti);
            } catch (IllegalAccessException e) {
                System.err.println(chalk.red().apply("\n[ERROR] Can't access method " + getMethodLocation(method)
                        + "! Did you forget to make it public?\n"));
                System.err.println(chalk.blackBright().apply(Util.getStackTrace(e)));
            } catch (IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
        this.testSuites.add(suite);

        ByteArrayOutputStream out = Util.pushOutStack();
        suite.run();
        Util.popOutStack();
        System.out.println(Util.indent(out.toString(), 2));
    }

    private String getMethodLocation(Method m) {
        return String.format("'%s' in '%s'", m.getName(), m.getDeclaringClass().getCanonicalName());
    }

    public TestStatus getTestStatus() {
        return this.testStatus;
    }

    public void setClassPaths(String[] classpaths) {
        this.classpaths = classpaths;
    }

    public void setExcludes(String... excludes) {
        this.exclude = new ArrayList<>(Arrays.asList(excludes));
    }

    public void setExcludes(ArrayList<String> excludes) {
        this.exclude = excludes;
    }

    public void setIncludes(String... includes) {
        this.include = new ArrayList<>(Arrays.asList(includes));
    }

    public void setIncludes(ArrayList<String> includes) {
        this.include = includes;
    }

    public void fullTestSummary() throws TestNotStartedException {
        if (this.testStatus == TestStatus.NOT_STARTED) {
            throw new TestNotStartedException();
        }

        TestSuite.printFullSummary();
        if (!TestSuite.hasPassedAllTests()) {
            System.out.println(chalk.bgCyan().black().bold().apply(" Failed tests: "));
            for (TestSuite suite : this.testSuites) {
                if (!suite.hasPassedTests()) {
                    System.out.println(Util.indent(suite.toString(TestStatus.FAILED), 4));
                }
            }
            System.out.println(chalk.red().apply("\nTest failed!"));
        } else {
            System.out.println(chalk.green().apply("\nTest finished successfully!"));
        }
    }
}
