package control.logger;

@SuppressWarnings("FieldCanBeLocal")
public class Logger {
    private final int classNameLength = 16;
    private boolean active = false;

    private Logger() {
        if (LoggerLoader.instance != null) {
            throw new IllegalStateException(this.getClass().getSimpleName() + " already instantiated");
        }
    }
    public static Logger getInstance() {
        return LoggerLoader.instance;
    }
    public void error(Object source, String message) {
        this.out("ERROR: ", source, message);
    }
    public void debug(Object source, String message) {
        this.out("DEBUG: ", source, message);
    }
    private void out(String mode, Object source, String message) {
        if (active) {
            System.out.println(mode + formatSource(source) + ": " + message.trim());
        }
    }

    private static class LoggerLoader {
        private static final Logger instance = new Logger();
    }
    private String formatSource(Object source) {
        StringBuilder sb;
        if (source instanceof Class)
            sb = new StringBuilder(((Class) source).getSimpleName().trim());
        else
            sb = new StringBuilder(source.getClass().getSimpleName().trim());

        int length = sb.length();

        if (length >= classNameLength) {
            sb = new StringBuilder(sb.substring(0, classNameLength - 1));
            sb.append("~");
        }

        while (length < classNameLength) {
            sb.append(" ");
            length++;
        }

        return sb.toString();
    }
}
