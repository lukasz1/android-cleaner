package co.qualityc.cleaner;

import android.app.Application;
import android.content.Context;


public class CleanerApplication extends Application {
    public static CleanerApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Context getContext() {
        return instance.getApplicationContext();
    }
}
