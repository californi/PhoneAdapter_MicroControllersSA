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
import android.util.Log;
import android.widget.Toast;

import edu.hkust.cse.phoneAdapter.R;
import edu.hkust.cse.phoneAdapter.activity.MainActivity;

public class MetaController extends IntentService {

    private static boolean running;
    private static FeedbackLoopMetaController mFeedbackLoopMetaControllerReceiver;
    private static boolean mGpsAvailable;
    private static boolean mBtAvailable;
    private static String mCurrentContextManager;
    private static boolean mAudioAvailable;
    private static boolean mVibrationAvailable;
    private static String mCurrentAdaptationManager;



    /**
     * Instantiates a new context manager.
     */
    public MetaController(){
        super("MetaController");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mFeedbackLoopMetaControllerReceiver = new FeedbackLoopMetaController();
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction("edu.hkust.cse.phoneAdapter.sensorsFailure");
        iFilter.addAction("edu.hkust.cse.phoneAdapter.effectorsFailure");
        registerReceiver(mFeedbackLoopMetaControllerReceiver, iFilter);

        //Intent knowledgeControllerIntent = new Intent(this, KnowledgeController.class);
        //startService(knowledgeControllerIntent);

        Intent failureManagerIntent = new Intent(this, FailureManager.class);
        startService(failureManagerIntent);

        Intent mContextManagerAllSensors = new Intent(this, ContextManagerAllSensors.class);
        startService(mContextManagerAllSensors);

        Intent mAdaptationManagerAllEffectors = new Intent(this, AdaptationManagerAllEffectors.class);
        startService(mAdaptationManagerAllEffectors);

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
            Log.i("Testando onHandleIntent MetaController", "Meta Controller" + Thread.currentThread().getName());

            try{
                Thread.sleep(10000);
            } catch(Exception e){
                Log.e("edu.hkust.cse.phoneAdapter.error", "Thread sleep exception");
            }

        }

    }

    /**
     * The Class FeedbackLoopMetaController.
     */
    private class FeedbackLoopMetaController extends BroadcastReceiver {

        @Override
        public void onReceive(Context c, Intent i) {
            String action = i.getAction();

            Log.i("Testando FeedbackLoopMetaController broadcastreceiver", "broadcastreceiver" + Thread.currentThread().getName());

            if(action.equals("edu.hkust.cse.phoneAdapter.sensorsFailure")){

                Log.i("BOVES - Testando sensorsFailure broadcastreceiver", "sensorsFailure" + Thread.currentThread().getName());

                mGpsAvailable = i.getBooleanExtra(ContextName.GPS_AVAILABLE, false);
                mBtAvailable = i.getBooleanExtra(ContextName.BT_AVAILABLE, false);
                mCurrentContextManager = i.getStringExtra(ContextName.CURRENT_CONTEXTMANAGER);
                if(mCurrentContextManager.equals("NoBluetooth")){
                    Intent mContextManagerNoBluetooth = new Intent(c, ContextManagerNoBluetooth.class);

                    startService(mContextManagerNoBluetooth);
                }else if(mCurrentContextManager.equals("NoGPS")){
                    Intent mContextManagerNoGPS = new Intent(c, ContextManagerNoGPS.class);
                    startService(mContextManagerNoGPS);
                }else if(mCurrentContextManager == "NoBluetoothNoGPS"){
                    Intent mContextManagerNoBluetoothGPS = new Intent(c, ContextManagerNoBluetoothNoGPS.class);
                    startService(mContextManagerNoBluetoothGPS);
                }else{
                    Intent mContextManagerAllSensors = new Intent(c, ContextManagerAllSensors.class);
                    startService(mContextManagerAllSensors);
                }
            }else if(action.equals("edu.hkust.cse.phoneAdapter.effectorsFailure")){

                Log.i(" BOVES -  Testando effectorsFailure broadcastreceiver", "effectorsFailure" + Thread.currentThread().getName());

                mAudioAvailable = i.getBooleanExtra(ContextName.AUDIO, false);
                mVibrationAvailable = i.getBooleanExtra(ContextName.VIBRATION, false);
                mCurrentAdaptationManager = i.getStringExtra(ContextName.CURRENT_ADAPTATIONMANAGER);
                if(mCurrentAdaptationManager.equals("NoVibration")){
                    Intent mAdaptationManagerNoVibration = new Intent(c, AdaptationManagerNoVibration.class);
                    startService(mAdaptationManagerNoVibration);
                }else if(mCurrentAdaptationManager.equals("NoRingtone")){
                    Intent mAdaptationManagerNoRingtone = new Intent(c, AdaptationManagerNoRingtone.class);
                    startService(mAdaptationManagerNoRingtone);
                }else if(mCurrentAdaptationManager.equals("NoRingtoneNoVibration")){
                    Intent mAdaptationManagerNoRingtoneNoVibration = new Intent(c, AdaptationManagerNoRingtoneNoVibration.class);
                    startService(mAdaptationManagerNoRingtoneNoVibration);
                }else{
                    Intent mAdaptationManagerAllEffectors = new Intent(c, AdaptationManagerAllEffectors.class);
                    startService(mAdaptationManagerAllEffectors);
                }


            }else{

            }
        }

    }


    public void EnableDisableReceiversContextManager(IntentService currentContextManager, String name){
        if(name.equals("NoBluetooth")){
            currentContextManager = new ContextManagerNoBluetooth();
            IntentFilter iFilter = new IntentFilter("edu.hkust.cse.phoneAdapter.newContext");
            iFilter.addAction("edu.hkust.cse.phoneAdapter.ruleChange");
            iFilter.addAction("edu.hkust.cse.phoneAdapter.stopService");

            if(mContextManagerNoGPS != null) {
                //unregisterReceiver(mContextManagerNoGPS);
            }else if(mContextManagerNoBluetoothNoGPS != null) {
                //unregisterReceiver(mContextManagerNoBluetoothNoGPS);
            }else {
                //unregisterReceiver(mContextManagerAllSensors);

                //registerReceiver(currentContextManager, iFilter);
            }
        }else if(name.equals("NoGPS")){
            //Intent mContextManagerNoGPS = new Intent(c, ContextManagerNoGPS.class);
            //startService(mContextManagerNoGPS);
        }else if(name == "NoBluetoothNoGPS"){
            //Intent mContextManagerNoBluetoothGPS = new Intent(c, ContextManagerNoBluetoothNoGPS.class);
            //startService(mContextManagerNoBluetoothGPS);
        }else{
            //Intent mContextManagerAllSensors = new Intent(c, ContextManagerAllSensors.class);
            //startService(mContextManagerAllSensors);
        }
    }

    public void EnableDisableReceiversAdaptationManager(IntentService currentAdaptationManager){

        //unregisterReceiver(mReceiver);
    }



    @Override
    public void onDestroy(){
        /**stop foreground service**/
        stopForeground(true);

        MetaController.running = false;

        super.onDestroy();
    }


    private ContextManagerNoBluetooth mContextManagerNoBluetooth;
    private ContextManagerNoGPS mContextManagerNoGPS;
    private ContextManagerNoBluetoothNoGPS mContextManagerNoBluetoothNoGPS;
    private ContextManagerAllSensors mContextManagerAllSensors;

    public static boolean isRunning(){
        return MetaController.running;
    }

}
