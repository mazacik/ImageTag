package control.logger;

public class Logger {
    private final int maxClassNameLength;

    public Logger() {
        maxClassNameLength = 16;
    }

    public void error(Object source, String message) {
        //todo use proper exceptions?
        System.out.println(formatSource(source.getClass()) + ": ERROR: " + message.trim());
    }
    public void debug(Object source, String message) {
        System.out.println(formatSource(source.getClass()) + ": DEBUG: " + message.trim());
    }

    private String formatSource(Class source) {
        StringBuilder value;
        value = new StringBuilder(source.getSimpleName());
        value.toString().trim();
        int length = value.length();

        if (length >= maxClassNameLength) {
            value = new StringBuilder(value.substring(0, maxClassNameLength - 1));
            value.append("~");
        }

        while (length < maxClassNameLength) {
            value.append(" ");
            length++;
        }
        return value.toString();
    }
}
