package control;

public class Logger {
    private static final int CLASS_NAME_LENGTH = 16;
    private static final boolean ACTIVE = false;

    public Logger() {

    }

    private void out(String type, String message) {
        if (ACTIVE) {
            System.out.println(type + message);
        }
    }

    public void error(String message) {
        this.out("ERROR: ", message);
    }
    public void debug(String message) {
        this.out("DEBUG: ", message);
    }

    private String formatSource(Class source) {
        StringBuilder sb = new StringBuilder(source.getSimpleName());
        int length = sb.length();

        if (length >= CLASS_NAME_LENGTH) {
            sb = new StringBuilder(sb.substring(0, CLASS_NAME_LENGTH - 1));
            sb.append("~");
        } else {
            while (length < CLASS_NAME_LENGTH) {
                sb.append(" ");
                length++;
            }
        }

        return sb.toString();
    }
}
