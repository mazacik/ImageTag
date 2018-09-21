package project.control;

public class LogControl {
    private final int VALUE_LENGTH;

    public LogControl() {
        VALUE_LENGTH = 16;
    }

    public void out(Class source, String message) {
        System.out.println(formatSource(source) + ": " + message);
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
