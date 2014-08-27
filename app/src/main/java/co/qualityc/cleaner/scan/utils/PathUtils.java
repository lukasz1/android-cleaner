package co.qualityc.cleaner.scan.utils;

/**
 * Created by banan on 26.08.14.
 */
public class PathUtils {

    public static String getBottomLevelFolder(String absolutePath) {
        if (absolutePath.endsWith("/"))
            absolutePath.substring(0, absolutePath.length()-1);
        int index = absolutePath.lastIndexOf("/");
        if (index > 0)
            return absolutePath.substring(index+1);
        else
            return absolutePath;
    }

}
