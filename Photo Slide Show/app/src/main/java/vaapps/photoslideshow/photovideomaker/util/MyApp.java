package vaapps.photoslideshow.photovideomaker.util;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class MyApp extends Application {
    private static Application instance;
//    private static AppOpenManager appOpenManager;
    private static Context instancecontext;

    @Override
    public void onCreate()
    {
        super.onCreate();



        instancecontext = getApplicationContext();
//        instancecontext = this;

        instance = this;
        MobileAds.initialize(
                this,
                new OnInitializationCompleteListener() {
                    @Override
                    public void onInitializationComplete(InitializationStatus initializationStatus) {}
                });

//        appOpenManager = new AppOpenManager(this);



//        AdSettings.addTestDevice("729bd4ca-ae0d-47c4-b8a6-a8c087486049");
    }
    public static Context getControlcontext(){
        return instancecontext;
        // or return instance.getApplicationContext();
    }
    public static Application getInstance ()
    {
        return instance;
    }



    public boolean checkIfHasNetwork()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
    }


}
