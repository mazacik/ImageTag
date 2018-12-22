package control.logger;

public class Logger {
    private final int maxClassNameLength;

    public Logger() {
        maxClassNameLength = 16;
    }

    public void error(Object source, String message) {
        //todo use proper exceptions?
        System.out.println("ERROR: " + formatSource(source) + ": " + message.trim());
    }
    public void debug(Object source, String message) {
        System.out.println("DEBUG: " + formatSource(source) + ": " + message.trim());
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
}
