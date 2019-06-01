package control.logger;

public class Logger {
    private static final int CLASS_NAME_LENGTH = 16;
    private static final boolean ACTIVE = false;

    public Logger() {

    }

    public void error(Object source, String message) {
        this.out("ERROR: ", source, message);
    }
    public void debug(Object source, String message) {
        this.out("DEBUG: ", source, message);
    }
    private void out(String mode, Object source, String message) {
        if (ACTIVE) {
            System.out.println(mode + formatSource(source) + ": " + message.trim());
        }
    }

    private String formatSource(Object source) {
        StringBuilder sb;
        if (source instanceof Class)
            sb = new StringBuilder(((Class) source).getSimpleName().trim());
        else
            sb = new StringBuilder(source.getClass().getSimpleName().trim());

        int length = sb.length();

        if (length >= CLASS_NAME_LENGTH) {
            sb = new StringBuilder(sb.substring(0, CLASS_NAME_LENGTH - 1));
            sb.append("~");
        }

        while (length < CLASS_NAME_LENGTH) {
            sb.append(" ");
            length++;
        }

        return sb.toString();
    }
}
