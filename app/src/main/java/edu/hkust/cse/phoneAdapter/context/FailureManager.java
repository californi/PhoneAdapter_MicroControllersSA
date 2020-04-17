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

public class FailureManager extends IntentService {

    private static boolean mGpsAvailable;
    private static boolean mBtAvailable;
    private static boolean mAudioAvailable;
    private static boolean mvibrationAvailable;
    private static String mCurrentContextManager = "AllSensors";
    private static String mCurrentAdaptationManager = "AllEffectors";
    private ArrayList<String> mBtDeviceList;
    private BluetoothAdapter mBtAdapter;
    private MonitorBluetoothReceiver mMonitorBluetoothReceiver;
    private static boolean running;

    public FailureManager() {
        super("FailureManager");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Emulator does not support bluetooth
        mBtAdapter=BluetoothAdapter.getDefaultAdapter();
        if(mBtAdapter==null){
            Toast.makeText(getApplicationContext(), "Bluetooth is not supported on this device!", Toast.LENGTH_SHORT).show();
        } else{
            mBtAdapter.enable();
        }

        mMonitorBluetoothReceiver = new MonitorBluetoothReceiver();
        IntentFilter iFilter = new IntentFilter();
        if(mBtAdapter != null){
            if(mBtAdapter.isEnabled()){
                iFilter.addAction(BluetoothDevice.ACTION_FOUND);
                iFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                iFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            }
        }
        registerReceiver(mMonitorBluetoothReceiver, iFilter);

        /**start foreground service**/
        mBtDeviceList=new ArrayList<String>();

        // step 1: instantiate the notification
        int icon = R.drawable.icon;
        CharSequence tickerText = "FailureManager";
        long when = System.currentTimeMillis();
        Notification noti = new Notification(icon, tickerText, when);

        //step 2: define the notification's message and PendingIntent
        Context context = getApplicationContext();
        CharSequence contentTitle = "PhoneAdapter";
        CharSequence contentText = "FailureManager is running";
        Intent notiIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notiIntent, 0);
        noti.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

        startForeground(1346, noti);

        FailureManager.running = true;
    }

    @Override
    protected void onHandleIntent(Intent arg0) {
        /*

         */
        Context c = this;

        int timeMonitoring = 10000;
        while (true) {

            Log.i("Testing", "onHandleIntent FailureManager FailureManager" + Thread.currentThread().getName());

            // Verify if gps service is available
            LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            mGpsAvailable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            //Bluetooth
            mBtAvailable = mBtDeviceList.size() > 0;

            //Verifying Audio e Vibration
            try{
                // Verify if it is possible to change volume
                AudioManager mAudioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                int volume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);

                if(volume>0){
                    mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                    mAudioManager.setStreamVolume(AudioManager.STREAM_RING, volume, AudioManager.FLAG_SHOW_UI);
                } else{
                    mAudioManager.setStreamVolume(AudioManager.STREAM_RING, 0, AudioManager.FLAG_SHOW_UI);
                }
                mAudioAvailable = volume >= 0;

                if(!(mAudioManager.getMode() == AudioManager.VIBRATE_SETTING_ON)) {
                    mAudioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_ON);
                    mvibrationAvailable = mAudioManager.getMode() == AudioManager.VIBRATE_SETTING_ON;
                }else{
                    mAudioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_OFF);
                    mvibrationAvailable = mAudioManager.getMode() == AudioManager.VIBRATE_SETTING_OFF;
                }
            }catch(RuntimeException e){
                mAudioAvailable = false;
                mvibrationAvailable = false;
            }

            Intent contextManagerIntent = new Intent();
            String sensorsAction = "edu.hkust.cse.phoneAdapter.sensorsFailure";
            //Regarding to ContextManagers
            if(mGpsAvailable && !mBtAvailable) {
                contextManagerIntent.setAction(sensorsAction);
                contextManagerIntent.putExtra(ContextName.GPS_AVAILABLE, mGpsAvailable);
                contextManagerIntent.putExtra(ContextName.BT_AVAILABLE, mBtAvailable);
                contextManagerIntent.putExtra(ContextName.CURRENT_CONTEXTMANAGER, "NoBluetooth");
                sendBroadcast(contextManagerIntent);
            }else if(!mGpsAvailable && mBtAvailable){
                contextManagerIntent.setAction(sensorsAction);
                contextManagerIntent.putExtra(ContextName.GPS_AVAILABLE, mGpsAvailable);
                contextManagerIntent.putExtra(ContextName.BT_AVAILABLE, mBtAvailable);
                contextManagerIntent.putExtra(ContextName.CURRENT_CONTEXTMANAGER, "NoGPS");
                sendBroadcast(contextManagerIntent);
            }
            else if(!mGpsAvailable && !mBtAvailable) {
                contextManagerIntent.setAction(sensorsAction);
                contextManagerIntent.putExtra(ContextName.GPS_AVAILABLE, mGpsAvailable);
                contextManagerIntent.putExtra(ContextName.BT_AVAILABLE, mBtAvailable);
                contextManagerIntent.putExtra(ContextName.CURRENT_CONTEXTMANAGER, "NoBluetoothNoGPS");
                sendBroadcast(contextManagerIntent);
            }else{

                contextManagerIntent.setAction(sensorsAction);
                contextManagerIntent.putExtra(ContextName.GPS_AVAILABLE, mGpsAvailable);
                contextManagerIntent.putExtra(ContextName.BT_AVAILABLE, mBtAvailable);
                contextManagerIntent.putExtra(ContextName.CURRENT_CONTEXTMANAGER, "AllSensors");
                sendBroadcast(contextManagerIntent);
            }

            Intent adaptationManagerIntent = new Intent();
            String effectorsAction = "edu.hkust.cse.phoneAdapter.effectorsFailure";
            //Regarding to AdaptationManagers
            if(mAudioAvailable && !mvibrationAvailable) {
                adaptationManagerIntent.setAction(effectorsAction);
                adaptationManagerIntent.putExtra(ContextName.AUDIO, mAudioAvailable);
                adaptationManagerIntent.putExtra(ContextName.VIBRATION, mBtAvailable);
                adaptationManagerIntent.putExtra(ContextName.CURRENT_ADAPTATIONMANAGER, "NoVibration");
                sendBroadcast(adaptationManagerIntent);

            }else if(!mAudioAvailable && mvibrationAvailable){
                adaptationManagerIntent.setAction(effectorsAction);
                adaptationManagerIntent.putExtra(ContextName.AUDIO, mAudioAvailable);
                adaptationManagerIntent.putExtra(ContextName.VIBRATION, mBtAvailable);
                adaptationManagerIntent.putExtra(ContextName.CURRENT_ADAPTATIONMANAGER, "NoRingtone");
                sendBroadcast(adaptationManagerIntent);
            }
            else if(!mAudioAvailable && !mvibrationAvailable) {
                adaptationManagerIntent.setAction(effectorsAction);
                adaptationManagerIntent.putExtra(ContextName.AUDIO, mAudioAvailable);
                adaptationManagerIntent.putExtra(ContextName.VIBRATION, mBtAvailable);
                adaptationManagerIntent.putExtra(ContextName.CURRENT_ADAPTATIONMANAGER, "NoRingtoneNoVibration");
                sendBroadcast(adaptationManagerIntent);
            }else{
                adaptationManagerIntent.setAction(effectorsAction);
                adaptationManagerIntent.putExtra(ContextName.AUDIO, mAudioAvailable);
                adaptationManagerIntent.putExtra(ContextName.VIBRATION, mBtAvailable);
                adaptationManagerIntent.putExtra(ContextName.CURRENT_ADAPTATIONMANAGER, "AllEffectors");
                sendBroadcast(adaptationManagerIntent);
            }

            try{
                Thread.sleep(timeMonitoring);
            } catch(Exception e){
                Log.e("error", "Thread sleep exception");
            }
        }
    }

    public static boolean isRunning(){
        return FailureManager.running;
    }

    private class MonitorBluetoothReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context c, Intent i) {

            String action=i.getAction();
            if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)){
                mBtDeviceList=new ArrayList<String>();
            } else if( action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device=i.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(!listContainsMac(mBtDeviceList, device.getAddress())){
                    mBtDeviceList.add(device.getAddress());
                }
            } else if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){

            } else {

            }

        }
    }

    /**
     * Check whether a specific MAC address is contained in a list.
     * @param macList the list of MAC addresses
     * @param mac the specific MAC
     * @return true if the list contains the specific MAC, and false otherwise
     */
    private boolean listContainsMac(ArrayList<String> macList, String mac){
        for(int i=0;i<macList.size();i++){
            if(macList.get(i).equals(mac)){
                return true;
            }
        }
        return false;
    }

    /*
    protected class RetrievingKnowledgeData extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("edu.hkust.cse.phoneAdapter.knowledge")) {
                mGpsAvailable = intent.getBooleanExtra(ContextName.GPS_AVAILABLE, false);
                mBtAvailable = intent.getBooleanExtra(ContextName.BT_AVAILABLE, false);
                // It is also possible other fields and values
                mCurrentContextManager = intent.getStringExtra(ContextName.CURRENT_CONTEXTMANAGER);

                mAudioAvailable = intent.getBooleanExtra(ContextName.AUDIO, false);
                mvibrationAvailable = intent.getBooleanExtra(ContextName.VIBRATION, false);
                // It is also possible other fields and values
                mCurrentAdaptationManager = intent.getStringExtra(ContextName.CURRENT_ADAPTATIONMANAGER);
            }



        }
    }*/
}
