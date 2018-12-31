package control.logger;

public final class Logger {
    private final int maxClassNameLength = 16;
    private Logger() {
        if (LoggerLoader.instance != null) {
            throw new IllegalStateException(this.getClass().getSimpleName() + " already instantiated");
        }
    }
    public static Logger getInstance() {
        return LoggerLoader.instance;
    }
    private String formatSource(Object source) {
        String value;
        if (source instanceof Class)
            value = ((Class) source).getSimpleName().trim();
        else
            value = source.getClass().getSimpleName().trim();

        int length = value.length();

        if (length >= maxClassNameLength) {
            value = value.substring(0, maxClassNameLength - 1);
            value += "~";
        }

        while (length < maxClassNameLength) {
            value += " ";
            length++;
        }

        return value;
    }

    public void error(Object source, String message) {
        System.out.println("ERROR: " + formatSource(source) + ": " + message.trim());
    }
    public void debug(Object source, String message) {
        System.out.println("DEBUG: " + formatSource(source) + ": " + message.trim());
    }

    private static class LoggerLoader {
        private static final Logger instance = new Logger();
    }
}
