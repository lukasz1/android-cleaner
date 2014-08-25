package co.qualityc.cleaner.utils;

public class MathUtils {
    public static int findIndexOfMinimum(long[] times) {
        int index = 0;
        long minTime = times[0];

        for (int i = 1; i < times.length; ++i) {
            if (times[i] < minTime) {
                minTime = times[i];
                index = i;
            }
        }

        return index;
    }

    public static float convertBytesToMB(long bytes) {
        return bytes / (float) (1024 * 1024);
    }

    public static float convertMBToGB(float megaBytes) {
        return megaBytes / (float) (1024);
    }

    public static double convertBytesToGB(long bytes) {
        return convertMBToGB(convertBytesToMB(bytes));
    }
}
