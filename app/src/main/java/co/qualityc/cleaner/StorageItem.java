package co.qualityc.cleaner;

import java.io.Serializable;

public abstract class StorageItem implements Serializable {
    protected abstract long getSize();
}
