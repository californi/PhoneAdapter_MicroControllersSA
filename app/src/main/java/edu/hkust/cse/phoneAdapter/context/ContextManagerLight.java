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
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import edu.hkust.cse.phoneAdapter.R;
import edu.hkust.cse.phoneAdapter.activity.MainActivity;

public class ContextManagerLight extends IntentService {
    private boolean mGpsAvailable;
    private String mLocation;
    private double mSpeed;
    private Calendar mCal;
    private LocationManager mLocManager;
    private long mLastTime;
    private String mLastLocation;
    private Handler mHandler;
    private boolean mStop;
    private static final String TAG = "PhoneAdapterContextLog";
    private BufferedWriter bw;
    private BufferedWriter bwService;
    private LocationListener mLocListener;

    /**
     * Instantiates a new context manager.
     */
    public ContextManagerLight(){
        super("ContextManagerLight");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mLocManager=(LocationManager) getSystemService(LOCATION_SERVICE);
        mLocListener=new ContextManagerLight.MyLocationListener();
        mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0, mLocListener);

        mCal=Calendar.getInstance();
        mLastTime=0;
        mHandler=new Handler();

        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction("edu.hkust.cse.phoneAdapter.stopService");

        mStop=false;
        mLocation=new String();

        /* create a buffered writer to write context data to sd card */
        File dir = Environment.getExternalStorageDirectory();
        if(dir != null && dir.canWrite()){
            File file = new File(dir + "/contextLog");
            try{
                if(!file.exists()){
                    file.createNewFile();
                }
                bw = new BufferedWriter(new FileWriter(file, true));
            } catch(Exception e){
                Log.e(TAG, "cannot log context, "+e.getMessage());
            }
        } else{
            Toast.makeText(getApplicationContext(), "No writtable SD card!", Toast.LENGTH_SHORT).show();
        }

        /* for experiment, to study how service got destroyed */
        if(dir != null && dir.canWrite()){
            File file = new File(dir + "/serviceLog");
            try{
                if(!file.exists()){
                    file.createNewFile();
                }
                bwService = new BufferedWriter(new FileWriter(file, true));
                bwService.write(mCal.getTime().toString()+ "service starts" +System.getProperty("line.separator"));
                bwService.flush();
            } catch(Exception e){
                Log.e(TAG, "cannot log service life cycle issues, "+e.getMessage());
            }
        } else{
            Toast.makeText(getApplicationContext(), "No writtable SD card!", Toast.LENGTH_SHORT).show();
        }

        /**start foreground service**/
        // step 1: instantiate the notification
        int icon = R.drawable.icon;
        CharSequence tickerText = "hello";
        long when = System.currentTimeMillis();
        Notification noti = new Notification(icon, tickerText, when);

        //step 2: define the notification's message and PendingIntent
        Context context = getApplicationContext();
        CharSequence contentTitle = "PhoneAdapter";
        CharSequence contentText = "Context manager V2 is running";
        Intent notiIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notiIntent, 0);
        noti.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

        startForeground(1346, noti);

    }

    @Override
    public void onDestroy() {
        try{
            mLocManager.removeUpdates(mLocListener);
            bw.flush();
            bw.close();
            bwService.write(mCal.getTime().toString()+ "service stops" +System.getProperty("line.separator"));
            bwService.flush();
            bwService.close();
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


        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent arg0) {
        /*
         * collect contexts every a while and broadcast the context change
         * context type includes
         * (1) gps availability
         * (2) gps location
         * (3) gps speed
         * (4) bluetooth devices
         * (5) bluetooth count
         * (6) time
         * (7) weekday
         */
        while(!mStop){

            /* get weekday */
            mCal=Calendar.getInstance();


            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    /* broadcast new context*/
                    Intent i=new Intent();
                    i.setAction("edu.hkust.cse.phoneAdapter.newContext");
                    i.putExtra(ContextName.GPS_AVAILABLE, mGpsAvailable);
                    i.putExtra(ContextName.GPS_LOCATION, mLocation);
                    i.putExtra(ContextName.GPS_SPEED, mSpeed);
                    sendBroadcast(i);

                    /* log context data */
                    StringBuffer sb = new StringBuffer();
                    sb.append(mGpsAvailable);
                    sb.append('@');
                    sb.append(mLocation);
                    sb.append('@');
                    sb.append(mSpeed);
                    sb.append('@');
                    Log.i(TAG, sb.toString());

                    /* write context-data to external storage (SD card) */
                    String eol = System.getProperty("line.separator");
                    if(bw != null){
                        try{
                            bw.write(sb.toString() + eol);
                            bw.flush();
                        } catch(Exception e){
                            Log.e(TAG, "cannot log context, " + e.getMessage());
                        }
                    }
                }
            });
            try{
                Thread.sleep(120000);
            } catch(Exception e){
                Log.e("edu.hkust.cse.phoneAdapter.error", "Thread sleep exception");
            }
        }
    }

    // the unit of duration is millisecond
    /**
     * Calculate speed.
     *
     * @param lastLoc the previously sensed location (GPS coordinate)
     * @param curLoc the current location
     * @param duration the unit of duration is millisecond
     * @return speed
     */
    private double calculateSpeed(String lastLoc, String curLoc, int duration){
        double d=AdaptationManager.calculateDist(lastLoc, curLoc);
        double result=d/(duration/1000.0);
        return result;
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
     */
    private class MyLocationListener implements LocationListener{

        @Override
        public void onLocationChanged(Location loc) {
            /*
             * sense the current location, caculate speed, and set the current time
             */
            mLocation=loc.getLatitude()+","+loc.getLongitude();

            /* calculate speed and update current time */
            long curTime=System.currentTimeMillis();
            if(mLastTime!=0 && mLastLocation!=null){
                int duration=(int)(curTime-mLastTime);
                mSpeed=calculateSpeed(mLastLocation, mLocation, duration);
            } else{
                mSpeed=-1;
            }

            mLastLocation=mLocation;
            mLastTime=curTime;
        }

        @Override
        public void onProviderDisabled(String arg0) {
            /* set mLocation to null, set last location to null and update last time to 0 */
            mGpsAvailable=false;
            mLocation=null;
            mLastLocation=null;
            mLastTime=0;
        }

        @Override
        public void onProviderEnabled(String arg0) {
            mGpsAvailable=true;
        }

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

        }
    }
}