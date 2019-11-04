package application.backend.util;

public abstract class ConverterUtil {
    public static String msToHMS(long millis) {
        long totalSeconds = millis / 1000;
        long hours = (totalSeconds / 3600);
        long mins = (totalSeconds / 60) % 60;
        long secs = totalSeconds % 60;

        String hoursString = (hours == 0)
                ? "00"
                : ((hours < 10)
                ? "0" + hours
                : "" + hours);
        String minsString = (mins == 0)
                ? "00"
                : ((mins < 10)
                ? "0" + mins
                : "" + mins);
        String secsString = (secs == 0)
                ? "00"
                : ((secs < 10)
                ? "0" + secs
                : "" + secs);
        return hoursString + ":" + minsString + ":" + secsString;
    }
}
