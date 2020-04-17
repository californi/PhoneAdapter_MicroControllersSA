package edu.hkust.cse.phoneAdapter.context;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;


public class SimulatingChanges extends IntentService {

    public SimulatingChanges() {
        super("SimulatingChanges");
    }

    private static boolean mGpsAvailable;
    private static boolean mBtAvailable;
    private static boolean mAudioAvailable;
    private static boolean mvibrationAvailable;
    private static String mCurrentContextManager = "AllSensors";
    private static String mCurrentAdaptationManager = "AllEffectors";
    private static boolean flag = true;

    BluetoothAdapter mBtAdapterEmulator;
    AudioManager mAudioManager;
    Location loc;
    BluetoothReceiver mBluetoothReceiver;
    private String mRetrievedBTAddress;
    private String mRetrievedBTName;

    /*
    * Creating methods for testing rules
    * */
    LocationManager locationManager;

    //General -> Office
    private void activate_office_gps(double latitude, double longitude)  {

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if(gpsEnabled) {

            locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
            locationManager.addTestProvider
                    (
                            LocationManager.GPS_PROVIDER,
                            "requiresNetwork" == "",
                            "requiresSatellite" == "",
                            "requiresCell" == "",
                            "hasMonetaryCost" == "",
                            "supportsAltitude" == "",
                            "supportsSpeed" == "",
                            "supportsBearing" == "",

                            android.location.Criteria.POWER_LOW,
                            android.location.Criteria.ACCURACY_FINE
                    );

            Location newLocation = new Location(LocationManager.GPS_PROVIDER);

            newLocation.setLatitude(latitude);
            newLocation.setLongitude(longitude);

            newLocation.setAccuracy(500);

            //locationManager.setTestProviderEnabled
            //        (
            //                LocationManager.GPS_PROVIDER,
            //                true
            //        );

            locationManager.setTestProviderStatus
                    (
                            LocationManager.GPS_PROVIDER,
                            LocationProvider.AVAILABLE,
                            null,
                            System.currentTimeMillis()
                    );

            locationManager.setTestProviderLocation
                    (
                            LocationManager.GPS_PROVIDER,
                            newLocation
                    );
        }else{
            Log.i("TestingLocationChange", "GPS is not enabled" + Thread.currentThread().getName());
        }

    }

    //General -> Office
    private boolean activate_office_bt(String bluetoothAddress){
        boolean result = false;
        BluetoothAdapter mBtAdapter;
        mBtAdapter=BluetoothAdapter.getDefaultAdapter();
        if(mBtAdapter==null){
            Toast.makeText(getApplicationContext(), "Bluetooth is not supported on this device!", Toast.LENGTH_SHORT).show();
            return false;
        } else{
            mBtAdapter.enable();
        }

        mRetrievedBTAddress = "";
        mBluetoothReceiver = new BluetoothReceiver();
        IntentFilter iFilter = new IntentFilter();
        if(mBtAdapter != null){
            if(mBtAdapter.isEnabled()){
                iFilter.addAction(BluetoothDevice.ACTION_FOUND);
                iFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
                iFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            }
        }

        Log.i("BluetoothBRS", mRetrievedBTAddress);

        registerReceiver(mBluetoothReceiver, iFilter);
        while (!mRetrievedBTAddress.equals(bluetoothAddress)) {
            Log.i("BluetoothBRS", "Testing - Waiting bluetooth value - currently: " +  mRetrievedBTName  + " - " + mRetrievedBTAddress);
            Toast.makeText(getApplicationContext(), "Testing - Waiting bluetooth value!", Toast.LENGTH_SHORT).show();
            SystemClock.sleep(1000);
        }

        result = true;
        unregisterReceiver(mBluetoothReceiver);

        Log.i("BluetoothBRS", mRetrievedBTAddress);
        return result;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        mAudioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);

        Log.i("Testing", "Working on activate_office_gps " + Thread.currentThread().getName());

        DeactivateGps();
        activate_office_gps(-21.979769,-47.880300);

        //ActivateGps();
        //activate_office_gps(-21.979769,-47.880300);

        Log.i("Testing", "Result in contextManagers: " + Thread.currentThread().getName());
        boolean booActivateOfficeBt = activate_office_bt("30:19:66:1D:36:D0");
        Log.i("Testing", "Result in booActivate_office_bt: " + booActivateOfficeBt + Thread.currentThread().getName());







        //Ajusting target system

        //ActivateGps();
        //DeactivateGps();

        //ActivateBt();
        //DeactivateBt();

        //A1 - ActivateGps e ActivateBt - ContextManagerAllSensors
        //B1 - ActivateGps e DeactivateBt - ContextManagerNoBluetooth
        //C1 - DeactivateGps e ActivateBt - ContextManagerNoGPS
        //D1 - DeactivateGps e DeactivateGps - ContextManagerNoBluetoothNoGPS

        //ActivateAudio();
        //DeactivateAudio();

        //ActivateVibrate();
        //DeactivateVibrate();

        //A2 - ActivateAudio e ActivateVibrate - AdaptationManagerAllEffectors
        //B2 - ActivateAudio e DeactivateVibration - AdaptationManagerNoVibration
        //C2 - DeactivateAudio e ActivateVibrate - AdaptationManagerNoRingtone
        //D2 - DeactivateAudio e DeactivateVibration - AdaptationManagerNoRingtoneNoVibration

        //A1 - A2
        //A1 - B2
        //A1 - C2
        //A1 - D2

        //B1 - A2
        //B1 - B2
        //B1 - C2
        //B1 - D2

        //C1 - A2
        //C1 - B2
        //C1 - C2
        //C1 - D2

        //D1 - A2
        //D1 - B2
        //D1 - C2
        //D1 - D2



    }

    public void ActivateGps (){
        //mGpsAvailable
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, true);
        boolean mGpsAvailable1 = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            Log.e("error", "Thread sleep exception");
        }
        Log.i("Testing_GPS_ENABLED", "mGpsAvailable1: " +  mGpsAvailable1 + " - " + Thread.currentThread().getName());
    }

    public void DeactivateGps (){
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        locationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER, false);
        boolean mGpsAvailable2 = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            Log.e("error", "Thread sleep exception");
        }
        Log.i("Testing_GPS_ENABLED", "mGpsAvailable2: " +  mGpsAvailable2 + " - " + Thread.currentThread().getName());
    }

    public void ActivateBt(){

        mBtAdapterEmulator.enable();
        mBtAdapterEmulator.isEnabled();
        boolean mBtAvailable2 = mBtAdapterEmulator.isEnabled();
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            Log.e("error", "Thread sleep exception");
        }
        Log.i("Testing_BT_ENABLED", "mBtAvailable2: " +  mBtAvailable2 + " - " + Thread.currentThread().getName());
    }

    public void DeactivateBt (){
        //mBtAvailable
        mBtAdapterEmulator.disable();
        boolean mBtAvailable1 = mBtAdapterEmulator.isEnabled();
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            Log.e("error", "Thread sleep exception");
        }
        Log.i("Testing_BT_DISABLED", "mBtAvailable1: " +  mBtAvailable1 + " - " + Thread.currentThread().getName());
    }

    public void ActivateAudio (){
        //mAudioAvailable
        AudioManager mAudioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
        int intMode = 0;
        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        intMode = mAudioManager.getRingerMode();
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            Log.e("error", "Thread sleep exception");
        }
        Log.i("Testing", "AUDIO_RINGER_MODE_NORMAL: intMode: " +  intMode + " - " + Thread.currentThread().getName());
    }

    public void DeactivateAudio(){
        //mAudioAvailable

        int intMode = 0;
        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        intMode = mAudioManager.getRingerMode();
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            Log.e("error", "Thread sleep exception");
        }
        Log.i("Testing", "AUDIO_RINGER_MODE_SILENT: intMode: " +  intMode + " - " + Thread.currentThread().getName());
    }

    public void ActivateVibrate(){
        mAudioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_ON);
        int intMode = mAudioManager.getRingerMode();
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            Log.e("error", "Thread sleep exception");
        }
        Log.i("Testing", "VIBRATE_SETTING_ON: intMode: " +  intMode + " - " + Thread.currentThread().getName());

        try {
            Thread.sleep(7000);
        } catch (Exception e) {
            Log.e("error", "Thread sleep exception");
        }
    }

    public void DeactivateVibrate (){
        mAudioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER, AudioManager.VIBRATE_SETTING_OFF);
        int intMode = mAudioManager.getRingerMode();
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            Log.e("error", "Thread sleep exception");
        }
        Log.i("Testing", "VIBRATE_SETTING_OFF: intMode: " +  intMode + " - " + Thread.currentThread().getName());
    }


    /**
     * The Class MyBroadcastReceiver.
     */
    private class BluetoothReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context c, Intent i) {
            String action=i.getAction();
            if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)){

            } else if( action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device=i.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.i("Testing", "Getting bluetooth: " + device.getName() + " - " +  device.getAddress() + Thread.currentThread().getName());
                mRetrievedBTAddress = device.getAddress();
                mRetrievedBTName = device.getName();
            } else if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){

            } else if(action.equals(BluetoothAdapter.ACTION_REQUEST_ENABLE)){

            } else if(action.equals(BluetoothAdapter.STATE_OFF)){

            } else {
            }

        }
    }

    private void getMockLocation()
    {

    }


}
