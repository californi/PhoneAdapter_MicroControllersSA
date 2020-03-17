package edu.hkust.cse.phoneAdapter.context;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import edu.hkust.cse.phoneAdapter.R;
import edu.hkust.cse.phoneAdapter.activity.MainActivity;

public class KnowledgeController extends IntentService {
    private static boolean running;
    public static boolean mGpsAvailable;
    public static boolean mBtAvailable;
    public static boolean mAudioAvailable;
    public static boolean mvibrationAvailable;
    public static String mCurrentContextManager;
    public static String mCurrentAdaptationManager;

    private BluetoothAdapter mBtAdapter;
    private Handler mHandler;

    /**
     * Instantiates a new context manager.
     */
    public KnowledgeController(){
        super("KnowledgeController");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mHandler=new Handler();

        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction("edu.hkust.cse.phoneAdapter.sensorsFailure");
        iFilter.addAction("edu.hkust.cse.phoneAdapter.effectorsFailure");

        /**start foreground service**/
        // step 1: instantiate the notification
        int icon = R.drawable.icon;
        CharSequence tickerText = "meta controller";
        long when = System.currentTimeMillis();
        Notification noti = new Notification(icon, tickerText, when);

        //step 2: define the notification's message and PendingIntent
        Context context = getApplicationContext();
        CharSequence contentTitle = "PhoneAdapter";
        CharSequence contentText = "Knowledge Controller is running";
        Intent notiIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notiIntent, 0);
        noti.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

        startForeground(1346, noti);

        KnowledgeController.running = true;
    }




    @Override
    protected void onHandleIntent(final Intent intent){

        Log.i("TESTANDOAQUI", "Knowledge Intent Service" + Thread.currentThread().getName());

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                //do something

                Log.i("Bento....", "broadcast " + intent.getAction());

                if (intent.getAction().equals("edu.hkust.cse.phoneAdapter.sensorsFailure")) {
                    mGpsAvailable = intent.getBooleanExtra(ContextName.GPS_AVAILABLE, false);
                    mBtAvailable = intent.getBooleanExtra(ContextName.BT_AVAILABLE, false);
                    mCurrentContextManager = intent.getStringExtra(ContextName.CURRENT_CONTEXTMANAGER);
                    sendBroadcast(intent);

                } else if (intent.getAction().equals("edu.hkust.cse.phoneAdapter.effectorsFailure")) {
                    mAudioAvailable = intent.getBooleanExtra(ContextName.AUDIO, false);
                    mvibrationAvailable = intent.getBooleanExtra(ContextName.VIBRATION, false);
                    mCurrentAdaptationManager = intent.getStringExtra(ContextName.CURRENT_ADAPTATIONMANAGER);
                    sendBroadcast(intent);
                } else {

                }
            }

        });
    }


    public static boolean isRunning(){
        return KnowledgeController.running;
    }

    @Override
    public void onDestroy(){
        stopSelf();
        super.onDestroy();
    }


}