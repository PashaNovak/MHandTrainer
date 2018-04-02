package pavelnovak.com.mhandtrainer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * Created by PavelNovak on 20.03.2018.
 */

public class StartActivity extends Activity {
    public static final String APP_PREFERENCES = "mysettings";

    public static final String APP_PREFERENCE_LOGIN = "LOGIN";

    SharedPreferences mSettings;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        if (mSettings.contains(APP_PREFERENCE_LOGIN)){
            if(mSettings.getString(APP_PREFERENCE_LOGIN, "").equals("")){
                intent = new Intent(this, AuthorizationActivity.class);
            } else {
                intent = new Intent(this, BaseActivity.class);
                intent.putExtra(BaseActivity.EXTRA_FROM, "start");
            }
        } else {
            intent = new Intent(this, AuthorizationActivity.class);
        }

        Thread logoTimer = new Thread()
        {
            public void run()
            {
                try
                {
                    int logoTimer = 0;
                    while(logoTimer < 5000)
                    {
                        sleep(100);
                        logoTimer = logoTimer +100;
                    }
                    startActivity(intent);
                }
                catch (InterruptedException e)
                {
                    // TODO: автоматически сгенерированный блок catch.
                    e.printStackTrace();
                }
                finally
                {
                    finish();
                }
            }
        };
        logoTimer.start();
    }
}
