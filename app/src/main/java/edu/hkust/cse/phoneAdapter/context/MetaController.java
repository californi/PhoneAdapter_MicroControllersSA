package edu.hkust.cse.phoneAdapter.context;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import edu.hkust.cse.phoneAdapter.R;
import edu.hkust.cse.phoneAdapter.activity.MainActivity;

public class MetaController extends IntentService {

    private static boolean running;
    private static FeedbackLoopMetaController mFeedbackLoopMetaControllerReceiver;
    private static int countChanged = 0;

    /**
     * Instantiates a new context manager.
     */
    public MetaController(){
        super("MetaController");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        ConfigurationA = "AllSensors";

        mFeedbackLoopMetaControllerReceiver = new FeedbackLoopMetaController();
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction("edu.hkust.cse.phoneAdapter.knowledge");
        registerReceiver(mFeedbackLoopMetaControllerReceiver, iFilter);

        /**start foreground service**/
        // step 1: instantiate the notification
        int icon = R.drawable.icon;
        CharSequence tickerText = "meta controller";
        long when = System.currentTimeMillis();
        Notification noti = new Notification(icon, tickerText, when);

        //step 2: define the notification's message and PendingIntent
        Context context = getApplicationContext();
        CharSequence contentTitle = "PhoneAdapter";
        CharSequence contentText = "Meta Controller is running";
        Intent notiIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notiIntent, 0);
        noti.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

        startForeground(1346, noti);

        MetaController.running = true;
    }


    @Override
    protected void onHandleIntent(Intent arg0){

        while(MetaController.isRunning()){



        }


    }


    private static String ConfigurationA = "AllSensors";
    private static String ConfigurationB = "";
    private static RegressionTestMicroController regressionTest = new RegressionTestMicroController();

    /**
     * The Class FeedbackLoopMetaController.
     */
    private class FeedbackLoopMetaController extends BroadcastReceiver {

        @Override
        public void onReceive(Context c, Intent i) {
            String action=i.getAction();

            if(action.equals("edu.hkust.cse.phoneAdapter.requiredChange")){
                regressionTest.startRegression(c, ConfigurationA, ConfigurationB);
            }
            else if(action.equals("edu.hkust.cse.phoneAdapter.knowledge")){
                boolean gpsAvailable = i.getBooleanExtra(ContextName.GPS_AVAILABLE, false);
                boolean btAvailable = i.getBooleanExtra(ContextName.BT_AVAILABLE, false);
                ConfigurationA = ConfigurationB;

                if(gpsAvailable && !btAvailable) {
                    Intent contextManagerNoBluetoothIntent = new Intent(c, ContextManagerNoBluetooth.class);
                    ConfigurationB = "NoBluetooth";
                    startService(contextManagerNoBluetoothIntent);
                }
                if(!gpsAvailable && btAvailable){
                    Intent contextManagerNoGPSIntent = new Intent(c, ContextManagerNoGPS.class);
                    ConfigurationB = "NoGPS";
                    startService(contextManagerNoGPSIntent);
                }
                else if(!gpsAvailable && !btAvailable) {
                    Intent contextManagerNoBluetoothNoGPSIntent = new Intent(c, ContextManagerNoBluetoothNoGPS.class);
                    ConfigurationB = "NoBluetoothNoGPS";
                    startService(contextManagerNoBluetoothNoGPSIntent);
                }else{
                    Intent contextManagerCompleteIntent = new Intent(c, ContextManagerAllSensors.class);
                    ConfigurationB = "AllSensors";
                    startService(contextManagerCompleteIntent);
                }
            }
        }

    }


    @Override
    public void onDestroy(){
        /**stop foreground service**/
        stopForeground(true);

        MetaController.running = false;

        super.onDestroy();
    }



    public static boolean isRunning(){
        return MetaController.running;
    }

}
