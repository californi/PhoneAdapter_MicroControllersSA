package edu.hkust.cse.phoneAdapter.context;

import android.app.IntentService;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
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

public class ContextManagerNoBluetoothNoGPS extends IntentService {

    private String mTime;
    private String mWeekday;
    private SimpleDateFormat mTimeFormat;
    private Calendar mCal;
    private long mLastTime;
    private Handler mHandler;
    private boolean mStop;
    private static final String TAG = "PhoneAdapterContextLog";
    private BufferedWriter bw;
    private BufferedWriter bwService;
    private static boolean running;
    private MonitorReceiver mMonitorReceiver;

    /**
     * Instantiates a new context manager.
     */
    public ContextManagerNoBluetoothNoGPS(){
        super("ContextManagerNoBluetoothNoGPS");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mCal=Calendar.getInstance();
        mTimeFormat=new SimpleDateFormat("HH:mm:ss");
        mLastTime=0;
        mHandler=new Handler();

        mMonitorReceiver=new MonitorReceiver();
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction("edu.hkust.cse.phoneAdapter.stopContextManager");
        registerReceiver(mMonitorReceiver, iFilter);

        mStop=false;

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
        CharSequence contentText = "Context manager is running";
        Intent notiIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notiIntent, 0);
        noti.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

        startForeground(1346, noti);

        ContextManagerNoBluetoothNoGPS.running = true;
    }


    public static boolean isRunning(){
        return ContextManagerNoBluetoothNoGPS.running;
    }

    /**
     * The Class MyBroadcastReceiver.
     */
    private class MonitorReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context c, Intent i) {
            String action=i.getAction();
            if(action.equals("edu.hkust.cse.phoneAdapter.stopContextManager")){
                mStop=true;
                stopSelf();
            } else {

            }
        }
    }


    @Override
    public void onDestroy() {
        try{
            unregisterReceiver(mMonitorReceiver);
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

        ContextManagerNoBluetoothNoGPS.running = false;

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
            mTime=mTimeFormat.format(mCal.getTime());
            switch(mCal.get(Calendar.DAY_OF_WEEK)){
                case 1:
                    mWeekday="sunday";
                    break;
                case 2:
                    mWeekday="monday";
                    break;
                case 3:
                    mWeekday="tuesday";
                    break;
                case 4:
                    mWeekday="wednesday";
                    break;
                case 5:
                    mWeekday="thursday";
                    break;
                case 6:
                    mWeekday="friday";
                    break;
                case 7:
                    mWeekday="saturday";
                    break;
                default:
                    break;
            }

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    /* broadcast new context*/
                    Intent i=new Intent();
                    i.setAction("edu.hkust.cse.phoneAdapter.newContext");
                    i.putExtra(ContextName.CURRENT_CONTEXTMANAGER, "NoBluetoothNoGPS");
                    i.putExtra(ContextName.TIME,mTime);
                    i.putExtra(ContextName.WEEKDAY, mWeekday);
                    sendBroadcast(i);

                    /* log context data */
                    StringBuffer sb = new StringBuffer();
                    sb.append(mTime);
                    sb.append('@');
                    sb.append(mWeekday);
                    Log.i(TAG, sb.toString());

                    /* write context-data to external storage (SD card) */
                    String eol = System.getProperty("line.separator");
                    if(bw != null){
                        try{
                            bw.write(sb.toString() + eol);
                            bw.flush();
//							mHandler.post(new Runnable() {
//
//								@Override
//								public void run() {
//									Toast.makeText(getApplicationContext(), "one entry added", Toast.LENGTH_SHORT).show();
//								}
//							});
                        } catch(Exception e){
                            Log.e(TAG, "cannot log context, " + e.getMessage());
                        }
                    }
                }
            });
            try{
                Thread.sleep(120000);
            } catch(Exception e){
                Log.e("error", "Thread sleep exception");
            }
        }
    }
}
