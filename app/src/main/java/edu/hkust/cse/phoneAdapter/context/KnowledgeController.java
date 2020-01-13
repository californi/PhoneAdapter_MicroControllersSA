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
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import edu.hkust.cse.phoneAdapter.R;
import edu.hkust.cse.phoneAdapter.activity.MainActivity;

public class KnowledgeController extends IntentService {
    private static boolean running;
    public static boolean mGpsAvailable;
    public static boolean mBtAvailable;
    private LocationListener monitoringLocationStatus;
    private LocationManager mLocManager;
    private MonitoringBluetoothStatus mMonitoringBTStatusReceiver;
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

        //GPS Monitoring
        mLocManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        monitoringLocationStatus = new KnowledgeController.MonitoringLocationStatus();
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

        while(KnowledgeController.isRunning()){

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    /* broadcast new context*/
                    Intent i = new Intent();
                    i.setAction("edu.hkust.cse.phoneAdapter.knowledge");
                    i.putExtra(ContextName.GPS_AVAILABLE, mGpsAvailable);
                    i.putExtra(ContextName.BT_AVAILABLE, mBtAvailable);
                    sendBroadcast(i);
                }});

            // We can put an identification of change HERE

            try{
                Thread.sleep(60000);
            } catch(Exception e){
                Log.e("edu.hkust.cse.phoneAdapter.error", "Thread sleep exception");
            }
        }
    }

    public static boolean isRunning(){
        return KnowledgeController.running;
    }

    @Override
    public void onDestroy(){
        try{
            unregisterReceiver(mMonitoringBTStatusReceiver);
            mLocManager.removeUpdates(monitoringLocationStatus);
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
    private class MonitoringLocationStatus implements LocationListener{

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

            mBtAdapter=BluetoothAdapter.getDefaultAdapter();
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
}