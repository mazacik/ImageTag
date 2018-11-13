package control.logger;

public class Logger {
    private final int VALUE_LENGTH;

    public Logger() {
        VALUE_LENGTH = 16;
    }

    public void out(String message) {
        this.out(message, false);
    }
    public void out(String message, boolean keepLine) {
        if (keepLine) {
            System.out.print(formatMessage(message));
        } else {
            System.out.println(message.trim());
        }
    }
    public void out(String message, Class source) {
        this.out(message, source, false);
    }
    public void out(String message, Class source, boolean keepLine) {
        if (keepLine) {
            System.out.print(formatSource(source) + ": " + formatMessage(message));
        } else {
            System.out.println(formatSource(source) + ": " + message.trim());
        }

    }

    private String formatMessage(String message) {
        message.trim();
        message += " ";
        return message;
    }
    private String formatSource(Class source) {
        StringBuilder value;
        value = new StringBuilder(source.getSimpleName());
        value.toString().trim();
        int length = value.length();

        if (length >= VALUE_LENGTH) {
            value = new StringBuilder(value.substring(0, VALUE_LENGTH - 1));
            value.append("~");
        }

        while (length < VALUE_LENGTH) {
            value.append(" ");
            length++;
        }
        return value.toString();
    }
}
