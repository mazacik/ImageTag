package control.logger;

public final class Logger {
    private final int classNameLength = 16;
    private boolean active = true;

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
        String value;
        if (source instanceof Class)
            value = ((Class) source).getSimpleName().trim();
        else
            value = source.getClass().getSimpleName().trim();

        int length = value.length();

        if (length >= classNameLength) {
            value = value.substring(0, classNameLength - 1);
            value += "~";
        }

        while (length < classNameLength) {
            value += " ";
            length++;
        }

        return value;
    }
}
