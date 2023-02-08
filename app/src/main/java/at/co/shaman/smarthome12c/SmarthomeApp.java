package at.co.shaman.smarthome12c;

import android.app.Application;

public class SmarthomeApp extends Application {
    private AppExecutors _executors;

    @Override
    public void onCreate() {
        super.onCreate();
        _executors = new AppExecutors();
    }

    public AppExecutors getExecutors() {
        return _executors;
    }
}
