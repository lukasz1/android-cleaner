package co.qualityc.cleaner.scan.junk;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class JunkDir extends JunkFile {
    private List<JunkFile> childrenJunks = new ArrayList<JunkFile>();


    public JunkDir(File file) {
        super(file);
    }

    public void addFiles(List<JunkFile> files) {
        childrenJunks.addAll(files);
    }
}
