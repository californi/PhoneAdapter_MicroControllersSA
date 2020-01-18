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

    private FeedbackLoopUpdateKnowledge mFeedbackLoopUpdateKnowledge;
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

        mFeedbackLoopUpdateKnowledge = new KnowledgeController.FeedbackLoopUpdateKnowledge();
        mHandler=new Handler();


        Intent mFeedbackLoopUpdateKnowledge = new Intent(this, FeedbackLoopUpdateKnowledge.class);
        startService(mFeedbackLoopUpdateKnowledge);

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
    protected void onHandleIntent(Intent arg0){

        int timeMonitoring = 240000;
        while (true) {

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //do something
                }});

            try{
                Thread.sleep(timeMonitoring);
            } catch(Exception e){
                Log.e("edu.hkust.cse.phoneAdapter.error", "Thread sleep exception");
            }
        }
    }


    private class FeedbackLoopUpdateKnowledge extends BroadcastReceiver {

        @Override
        public void onReceive(Context c, Intent i) {
            String action=i.getAction();


            if(action.equals("edu.hkust.cse.phoneAdapter.sensorsFailure")){
                mGpsAvailable = i.getBooleanExtra(ContextName.GPS_AVAILABLE, false);
                mBtAvailable = i.getBooleanExtra(ContextName.BT_AVAILABLE, false);
                mCurrentContextManager = i.getStringExtra(ContextName.CURRENT_CONTEXTMANAGER);
                sendBroadcast(i);

            }else if(action.equals("edu.hkust.cse.phoneAdapter.effectorsFailure")){
                mAudioAvailable = i.getBooleanExtra(ContextName.AUDIO, false);
                mvibrationAvailable = i.getBooleanExtra(ContextName.VIBRATION, false);
                mCurrentAdaptationManager = i.getStringExtra(ContextName.CURRENT_ADAPTATIONMANAGER);
                sendBroadcast(i);
            }else{

            }
        }

    }

    public static boolean isRunning(){
        return KnowledgeController.running;
    }

    @Override
    public void onDestroy(){
        try{
            unregisterReceiver(mFeedbackLoopUpdateKnowledge);
        } catch(Exception e){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Failed to unregister broadcast receiver!", Toast.LENGTH_SHORT).show();
                }
            });
        }
        /**stop foreground service**/
        stopForeground(true);

        KnowledgeController.running = false;

        super.onDestroy();
    }


}