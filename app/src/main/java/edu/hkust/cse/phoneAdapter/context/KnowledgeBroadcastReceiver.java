package edu.hkust.cse.phoneAdapter.context;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class KnowledgeBroadcastReceiver extends BroadcastReceiver {
    private static boolean running;
    public static boolean mGpsAvailable;
    public static boolean mBtAvailable;
    public static boolean mAudioAvailable;
    public static boolean mvibrationAvailable;
    public static String mCurrentContextManager;
    public static String mCurrentAdaptationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Testing", " Kownledge broadcastreceiver - Kownledge broadcastreceiver");
        // If there is no a failure
        if (!(
                intent.getAction().equals("edu.hkust.cse.phoneAdapter.sensorsFailure") ||
                intent.getAction().equals("edu.hkust.cse.phoneAdapter.effectorsFailure"))
        ){
            if (intent.getAction().equals("edu.hkust.cse.phoneAdapter.newContext")) {
                mGpsAvailable = intent.getBooleanExtra(ContextName.GPS_AVAILABLE, false);
                mBtAvailable = intent.getBooleanExtra(ContextName.BT_AVAILABLE, false);
                // It is also possible other fields and values
                mCurrentContextManager = intent.getStringExtra(ContextName.CURRENT_CONTEXTMANAGER);

            } else if (intent.getAction().equals("edu.hkust.cse.phoneAdapter.newActuatorData")) {
                mAudioAvailable = intent.getBooleanExtra(ContextName.AUDIO, false);
                mvibrationAvailable = intent.getBooleanExtra(ContextName.VIBRATION, false);
                // It is also possible other fields and values
                mCurrentAdaptationManager = intent.getStringExtra(ContextName.CURRENT_ADAPTATIONMANAGER);
            } else {

            }

            Intent knowledgeIntent = new Intent();
            knowledgeIntent.setAction("edu.hkust.cse.phoneAdapter.knowledge");
            knowledgeIntent.putExtra(ContextName.GPS_AVAILABLE, mGpsAvailable);
            knowledgeIntent.putExtra(ContextName.BT_AVAILABLE, mBtAvailable);
            knowledgeIntent.putExtra(ContextName.CURRENT_CONTEXTMANAGER, mCurrentContextManager);
            knowledgeIntent.putExtra(ContextName.AUDIO, mAudioAvailable);
            knowledgeIntent.putExtra(ContextName.VIBRATION, mvibrationAvailable);
            knowledgeIntent.putExtra(ContextName.CURRENT_ADAPTATIONMANAGER, mCurrentAdaptationManager);
            context.sendBroadcast(knowledgeIntent);
        }
        // If there is a failure
        else {
            if (intent.getAction().equals("edu.hkust.cse.phoneAdapter.KnowledgeSensorsFailure")) {
                mGpsAvailable = intent.getBooleanExtra(ContextName.GPS_AVAILABLE, false);
                mBtAvailable = intent.getBooleanExtra(ContextName.BT_AVAILABLE, false);
                mCurrentContextManager = intent.getStringExtra(ContextName.CURRENT_CONTEXTMANAGER);

                Intent contextManagerIntent = new Intent();
                contextManagerIntent.setAction("edu.hkust.cse.phoneAdapter.sensorsFailure");
                contextManagerIntent.putExtra(ContextName.GPS_AVAILABLE, mGpsAvailable);
                contextManagerIntent.putExtra(ContextName.BT_AVAILABLE, mBtAvailable);
                contextManagerIntent.putExtra(ContextName.CURRENT_CONTEXTMANAGER, mCurrentContextManager);
                context.sendBroadcast(contextManagerIntent);

            } else if (intent.getAction().equals("edu.hkust.cse.phoneAdapter.KnowledgeEffectorsFailure")) {
                mAudioAvailable = intent.getBooleanExtra(ContextName.AUDIO, false);
                mvibrationAvailable = intent.getBooleanExtra(ContextName.VIBRATION, false);
                mCurrentAdaptationManager = intent.getStringExtra(ContextName.CURRENT_ADAPTATIONMANAGER);

                Intent adaptationManagerIntent = new Intent();
                adaptationManagerIntent.setAction("edu.hkust.cse.phoneAdapter.effectorsFailure");
                adaptationManagerIntent.putExtra(ContextName.AUDIO, mAudioAvailable);
                adaptationManagerIntent.putExtra(ContextName.VIBRATION, mBtAvailable);
                adaptationManagerIntent.putExtra(ContextName.CURRENT_ADAPTATIONMANAGER, mCurrentAdaptationManager);
                context.sendBroadcast(adaptationManagerIntent);

            } else {

            }
        }
    }
}
