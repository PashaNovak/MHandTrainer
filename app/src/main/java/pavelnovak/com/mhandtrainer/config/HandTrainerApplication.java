package pavelnovak.com.mhandtrainer.config;

import android.app.Application;

import com.activeandroid.ActiveAndroid;

/**
 * Created by G710 on 20.03.2018.
 */

public class HandTrainerApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ActiveAndroid.initialize(this);
    }
}
