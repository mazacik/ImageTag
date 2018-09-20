package project.control;

public abstract class Log {
    public static void out(Class source, String message) {
        System.out.println(formatSource(source) + ": " + message);
    }

    private static String formatSource(Class source) {
        int VALUE_LENGTH = 16;
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
