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

import edu.hkust.cse.phoneAdapter.R;
import edu.hkust.cse.phoneAdapter.activity.MainActivity;

public class FailureManager extends IntentService {

    private static boolean mGpsAvailable;
    private static boolean mBtAvailable;
    private static boolean mAudioAvailable;
    private static boolean mvibrationAvailable;
    private LocationListener monitoringLocationStatus;
    private LocationManager mLocManager;
    private MonitoringBluetoothStatus mMonitoringBTStatusReceiver;
    private BluetoothAdapter mBtAdapter;
    private Handler mHandler;
    private static boolean running;

    public FailureManager() {
        super("FailureManager");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //GPS Monitoring
        mLocManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        monitoringLocationStatus = new FailureManager.MonitoringLocationStatus();
        mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, monitoringLocationStatus);

        mHandler=new Handler();

        mMonitoringBTStatusReceiver = new MonitoringBluetoothStatus();

        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction("edu.hkust.cse.phoneAdapter.stopService");
        if(mBtAdapter != null){
            if(mBtAdapter.isEnabled()){
                iFilter.addAction(BluetoothDevice.ACTION_FOUND);
                iFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                iFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            }
        }

        registerReceiver(mMonitoringBTStatusReceiver, iFilter);

        Intent mMonitoringEffectorsIntent =new Intent(this, MonitoringEffectorsStatus.class);
        startService(mMonitoringEffectorsIntent);

        /**start foreground service**/
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




        int timeMonitoring = 120000;
        while (true) {

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
                Log.e("edu.hkust.cse.phoneAdapter.error", "Thread sleep exception");
            }
        }
    }


    /**
     * The listener interface for receiving myLocation events.
     * The class that is interested in processing a myLocation
     * event implements this interface, and the object created
     * with that class is registered with a component using the
     * component's <code>addMyLocationListener<code> method. When
     * the myLocation event occurs, that object's appropriate
     * method is invoked.
     *
     * @see MonitoringLocationStatus
     */
    private class MonitoringLocationStatus implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            mGpsAvailable=true;
        }

        @Override
        public void onProviderDisabled(String arg0) {
            /* set mLocation to null, set last location to null and update last time to 0 */
            mGpsAvailable=false;
        }

        @Override
        public void onProviderEnabled(String arg0) {
            mGpsAvailable=true;
        }

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

        }
    }

    /**
     * The Class MonitoringBluetoothStatus.
     */
    private class MonitoringBluetoothStatus extends BroadcastReceiver {

        @Override
        public void onReceive(Context c, Intent i) {

            mBtAdapter= BluetoothAdapter.getDefaultAdapter();
            if(mBtAdapter==null){
                Toast.makeText(getApplicationContext(), "Bluetooth is not supported on this device!", Toast.LENGTH_SHORT).show();
                mBtAvailable = false;
                return;
            } else{
                mBtAdapter.enable();
            }

            String action=i.getAction();
            if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)){

            } else if( action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device=i.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mBtAvailable = device != null;
            } else if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {

            } else if(action.equals(BluetoothAdapter.ERROR) ||
                    action.equals(BluetoothAdapter.STATE_OFF) ||
                    action.equals(BluetoothAdapter.STATE_TURNING_OFF)){
                mBtAvailable = false;
            }
            else {

            }
        }
    }

    /**
     * The Class MonitoringEffectorsStatus.
     */
    private class MonitoringEffectorsStatus extends BroadcastReceiver {

        @Override
        public void onReceive(Context c, Intent i) {

            //dealing with audio
            int currentVolume = -1;
            AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            if(audio != null) {
                currentVolume = audio.getStreamVolume(AudioManager.STREAM_RING);
                if(currentVolume < 0){
                    mAudioAvailable = false;
                }
                else
                {
                    mAudioAvailable = true;
                }
            }
            else{
                mAudioAvailable = false;
            }

            //dealing with vibration
            Vibrator vibrator = (Vibrator)c.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                mvibrationAvailable = true;
            }
            else
            {
                mvibrationAvailable = false;
            }
        }
    }

}
