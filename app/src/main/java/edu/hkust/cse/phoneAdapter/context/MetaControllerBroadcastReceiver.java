package edu.hkust.cse.phoneAdapter.context;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MetaControllerBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context c, Intent i) {

        Log.i("Testing", "MetaController broadcastreceiver broadcastreceiver" + Thread.currentThread().getName());

        if (i.getAction().equals("edu.hkust.cse.phoneAdapter.sensorsFailure")) {

            enablingDisablingContextManager(c, i.getStringExtra(ContextName.CURRENT_CONTEXTMANAGER));

        } else if (i.getAction().equals("edu.hkust.cse.phoneAdapter.effectorsFailure")) {

            enablingDisablingAdaptationManager(c, i.getStringExtra(ContextName.CURRENT_ADAPTATIONMANAGER));

        } else {

        }

    }

    private void enablingDisablingContextManager(Context c, String currentContextManager) {
        if (currentContextManager.equals("NoBluetooth")) {
            Intent mContextManagerAllSensors = new Intent(c, ContextManagerAllSensors.class);
            c.stopService(mContextManagerAllSensors);

            Intent mContextManagerNoBluetooth = new Intent(c, ContextManagerNoBluetooth.class);
            c.startService(mContextManagerNoBluetooth);

            Intent mContextManagerNoGPS = new Intent(c, ContextManagerNoGPS.class);
            c.stopService(mContextManagerNoGPS);

            Intent mContextManagerNoBluetoothGPS = new Intent(c, ContextManagerNoBluetoothNoGPS.class);
            c.stopService(mContextManagerNoBluetoothGPS);
        } else if (currentContextManager.equals("NoGPS")) {
            Intent mContextManagerAllSensors = new Intent(c, ContextManagerAllSensors.class);
            c.stopService(mContextManagerAllSensors);

            Intent mContextManagerNoBluetooth = new Intent(c, ContextManagerNoBluetooth.class);
            c.stopService(mContextManagerNoBluetooth);

            Intent mContextManagerNoGPS = new Intent(c, ContextManagerNoGPS.class);
            c.startService(mContextManagerNoGPS);

            Intent mContextManagerNoBluetoothGPS = new Intent(c, ContextManagerNoBluetoothNoGPS.class);
            c.stopService(mContextManagerNoBluetoothGPS);
        } else if (currentContextManager.equals("NoBluetoothNoGPS")) {
            Intent mContextManagerAllSensors = new Intent(c, ContextManagerAllSensors.class);
            c.stopService(mContextManagerAllSensors);

            Intent mContextManagerNoBluetooth = new Intent(c, ContextManagerNoBluetooth.class);
            c.stopService(mContextManagerNoBluetooth);

            Intent mContextManagerNoGPS = new Intent(c, ContextManagerNoGPS.class);
            c.stopService(mContextManagerNoGPS);

            Intent mContextManagerNoBluetoothGPS = new Intent(c, ContextManagerNoBluetoothNoGPS.class);
            c.startService(mContextManagerNoBluetoothGPS);
        } else {
            Intent mContextManagerAllSensors = new Intent(c, ContextManagerAllSensors.class);
            c.startService(mContextManagerAllSensors);

            Intent mContextManagerNoBluetooth = new Intent(c, ContextManagerNoBluetooth.class);
            c.stopService(mContextManagerNoBluetooth);

            Intent mContextManagerNoGPS = new Intent(c, ContextManagerNoGPS.class);
            c.stopService(mContextManagerNoGPS);

            Intent mContextManagerNoBluetoothGPS = new Intent(c, ContextManagerNoBluetoothNoGPS.class);
            c.stopService(mContextManagerNoBluetoothGPS);
        }
    }

    private void enablingDisablingAdaptationManager(Context c, String currentAdaptationManager) {
        //NoVibration, NoRingtone, NoRingtoneNoVibration
        if (currentAdaptationManager.equals("NoVibration")) {
            Intent mAdaptationManagerAllEffectors = new Intent(c, AdaptationManagerAllEffectors.class);
            c.stopService(mAdaptationManagerAllEffectors);

            Intent mAdaptationManagerNoVibration = new Intent(c, AdaptationManagerNoVibration.class);
            c.startService(mAdaptationManagerNoVibration);

            Intent mAdaptationManagerNoRingtone = new Intent(c, AdaptationManagerNoRingtone.class);
            c.stopService(mAdaptationManagerNoRingtone);

            Intent mAdaptationManagerNoRingtoneNoVibration = new Intent(c, AdaptationManagerNoRingtoneNoVibration.class);
            c.stopService(mAdaptationManagerNoRingtoneNoVibration);
        } else if (currentAdaptationManager.equals("NoRingtone")) {
            Intent mAdaptationManagerAllEffectors = new Intent(c, AdaptationManagerAllEffectors.class);
            c.stopService(mAdaptationManagerAllEffectors);

            Intent mAdaptationManagerNoVibration = new Intent(c, AdaptationManagerNoVibration.class);
            c.stopService(mAdaptationManagerNoVibration);

            Intent mAdaptationManagerNoRingtone = new Intent(c, AdaptationManagerNoRingtone.class);
            c.startService(mAdaptationManagerNoRingtone);

            Intent mAdaptationManagerNoRingtoneNoVibration = new Intent(c, AdaptationManagerNoRingtoneNoVibration.class);
            c.stopService(mAdaptationManagerNoRingtoneNoVibration);
        } else if (currentAdaptationManager.equals("NoRingtoneNoVibration")) {
            Intent mAdaptationManagerAllEffectors = new Intent(c, AdaptationManagerAllEffectors.class);
            c.stopService(mAdaptationManagerAllEffectors);

            Intent mAdaptationManagerNoVibration = new Intent(c, AdaptationManagerNoVibration.class);
            c.stopService(mAdaptationManagerNoVibration);

            Intent mAdaptationManagerNoRingtone = new Intent(c, AdaptationManagerNoRingtone.class);
            c.stopService(mAdaptationManagerNoRingtone);

            Intent mAdaptationManagerNoRingtoneNoVibration = new Intent(c, AdaptationManagerNoRingtoneNoVibration.class);
            c.startService(mAdaptationManagerNoRingtoneNoVibration);
        } else {
            Intent mAdaptationManagerAllEffectors = new Intent(c, AdaptationManagerAllEffectors.class);
            c.startService(mAdaptationManagerAllEffectors);

            Intent mAdaptationManagerNoVibration = new Intent(c, AdaptationManagerNoVibration.class);
            c.stopService(mAdaptationManagerNoVibration);

            Intent mAdaptationManagerNoRingtone = new Intent(c, AdaptationManagerNoRingtone.class);
            c.stopService(mAdaptationManagerNoRingtone);

            Intent mAdaptationManagerNoRingtoneNoVibration = new Intent(c, AdaptationManagerNoRingtoneNoVibration.class);
            c.stopService(mAdaptationManagerNoRingtoneNoVibration);
        }
    }

}

