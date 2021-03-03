package io.github.sindrets.fling;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

import io.github.sindrets.jchalk.Chalk;

public class Logger {
    private static final LoggerInstance logger = new LoggerInstance();

    static {
        final String envDebug = System.getenv("DEBUG");
        setDebugMode(envDebug != null && envDebug.equals("1"));
    }

    public static LoggerInstance createLogger(PrintStream out, PrintStream err) {
        return new LoggerInstance(out, err);
    }

    public static LoggerInstance createLogger() {
        return new LoggerInstance();
    }

    public static String getStackTrace() {
        StringWriter sw = new StringWriter();
        new Throwable().printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public static String getStackTrace(Throwable e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    public static void log(Object... args) {
        logger.log(args);
    }

    public static void info(Object... args) {
        logger.info(args);
    }

    public static void warn(Object... args) {
        logger.warn(args);
    }

    public static void error(Object... args) {
        logger.error(args);
    }

    public static void success(Object... args) {
        logger.success(args);
    }

    public static void debug(Object... args) {
        logger.debug(args);
    }

    public static void stackTrace(Throwable e) {
        logger.stackTrace(e);
    }

    public static void stackTrace() {
        logger.stackTrace();
    }

    public static void setOut(PrintStream out) {
        logger.setOut(out);
    }

    public static void setErr(PrintStream err) {
        logger.setErr(err);
    }

    public static void setEnabled(boolean flag) {
        logger.setEnabled(flag);
    }

    public static void setDebugMode(boolean flag) {
        logger.setDebugMode(flag);
    }

    public static void setIndentLevel(int amount) {
        logger.setIndentLevel(amount);
    }

    public static void modIndentLevel(int amount) {
        logger.modIndentLevel(amount);
    }

    public static void setIndentSize(int size) {
        logger.indentSize = Math.max(0, size);
    }

    public static void modIndentSize(int amount) {
        logger.indentSize = Math.max(0, logger.indentSize + amount);
    }

    public static void indent() {
        logger.modIndentLevel(1);
    }

    public static void dedent() {
        logger.modIndentLevel(-1);
    }

    static class LoggerInstance {
        private static final Chalk chalk = new Chalk();
        private PrintStream out;
        private PrintStream err;
        private boolean enabled = true;
        private boolean debugMode = false;
        private int indentLevel = 0;
        private int indentSize = 4;

        public LoggerInstance(PrintStream out, PrintStream err) {
            this.out = out;
            this.err = err;
        }

        public LoggerInstance() {
            this.out = System.out;
            this.err = System.err;
        }

        private String indentObject(Object o, int n) {
            String[] lines = o.toString().split("\\n|\\r|(\\r\\n)", -1);
            StringBuilder sb = new StringBuilder();
            for (String line : lines) {
                sb.append(new String(new char[n]).replace("\0", " ") + line + "\n");
            }
            return sb.substring(0, sb.length() - 1);
        }

        private void write(PrintStream out, String decoration, Object... args) {
            if (!this.enabled) {
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append(decoration);

            for (int i = 0; i < args.length; i++) {
                if (i > 0) {
                    sb.append(" ");
                }

                if (args[i] == null) {
                    sb.append("null");
                } else if (args[i].getClass().isArray()) {
                    sb.append(Arrays.toString((Object[]) args[i]));
                } else {
                    sb.append(args[i].toString());
                }
            }

            if (this.indentLevel != 0) {
                out.print(this.indentObject(sb, this.indentLevel * this.indentSize));
            } else {
                out.print(sb.toString());
            }
        }

        private void writeLn(PrintStream out, String decoration, Object... args) {
            if (!this.enabled) {
                return;
            }

            this.write(out, decoration, args);
            out.println();
        }

        private void writeStyled(PrintStream out, String decoration, Chalk style, Object... args) {
            if (!this.enabled) {
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append(decoration);

            for (int i = 0; i < args.length; i++) {
                if (i > 0) {
                    sb.append(" ");
                }

                if (args[i] == null) {
                    sb.append(style.apply("null"));
                } else if (args[i].getClass().isArray()) {
                    sb.append(style.apply(Arrays.toString((Object[]) args[i])));
                } else {
                    sb.append(style.apply(args[i]));
                }
            }

            if (this.indentLevel != 0) {
                out.print(this.indentObject(sb, this.indentLevel * this.indentSize));
            } else {
                out.print(sb.toString());
            }
        }

        private void writeStyledLn(PrintStream out, String decoration, Chalk style,
                Object... args) {

            if (!this.enabled) {
                return;
            }

            this.writeStyled(out, decoration, style, args);
            out.println();
        }

        public void log(Object... args) {
            this.writeLn(this.out, "", args);
        }

        public void info(Object... args) {
            String decoration = chalk.bgBlue().black().bold().apply(" ℹ ") + " ";
            this.writeStyledLn(this.out, decoration, chalk.blue(), args);
        }

        public void warn(Object... args) {
            String decoration = chalk.bgYellow().black().bold().apply(" WARN ") + " ";
            this.writeStyledLn(this.out, decoration, chalk.yellow(), args);
        }

        public void error(Object... args) {
            String decoration = chalk.bgRed().black().bold().apply(" ✗ ") + " ";
            this.writeStyledLn(this.err, decoration, chalk.red(), args);
        }

        public void success(Object... args) {
            String decoration = chalk.bgGreen().black().bold().apply(" ✓ ") + " ";
            this.writeStyledLn(this.out, decoration, chalk.green(), args);
        }

        public void debug(Object... args) {
            if (debugMode) {
                boolean wasEnabled = this.enabled;
                this.setEnabled(true);
                String decoration = chalk.bgYellow().black().bold().apply(" DEBUG ") + " ";
                this.writeLn(this.out, decoration, args);
                this.setEnabled(wasEnabled);
            }
        }

        public void stackTrace(Throwable e) {
            this.indent();
            this.log(chalk.blackBright().apply(Logger.getStackTrace(e)));
            this.dedent();
        }

        public void stackTrace() {
            this.indent();
            this.log(chalk.blackBright().apply(Logger.getStackTrace()));
            this.dedent();
        }

        public void setOut(PrintStream out) {
            this.out = out;
        }

        public void setErr(PrintStream err) {
            this.err = err;
        }

        public void setEnabled(boolean flag) {
            this.enabled = flag;
        }

        public void setDebugMode(boolean flag) {
            this.debugMode = flag;
        }

        public void setIndentLevel(int amount) {
            this.indentLevel = Math.max(0, amount);
        }

        public void modIndentLevel(int amount) {
            this.indentLevel = Math.max(0, this.indentLevel + amount);
        }

        public void setIndentSize(int size) {
            this.indentSize = Math.max(0, size);
        }

        public void modIndentSize(int amount) {
            this.indentSize = Math.max(0, this.indentSize + amount);
        }

        public void indent() {
            this.modIndentLevel(1);
        }

        public void dedent() {
            this.modIndentLevel(-1);
        }
    }
}
