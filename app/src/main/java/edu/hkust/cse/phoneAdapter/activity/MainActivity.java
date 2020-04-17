
package edu.hkust.cse.phoneAdapter.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import edu.hkust.cse.phoneAdapter.R;
import edu.hkust.cse.phoneAdapter.context.AdaptationManagerAllEffectors;
import edu.hkust.cse.phoneAdapter.context.ContextManagerAllSensors;
import edu.hkust.cse.phoneAdapter.context.ContextManagerNoBluetooth;
import edu.hkust.cse.phoneAdapter.context.ContextManagerNoBluetoothNoGPS;
import edu.hkust.cse.phoneAdapter.context.ContextManagerNoGPS;
import edu.hkust.cse.phoneAdapter.context.FailureManager;
import edu.hkust.cse.phoneAdapter.context.KnowledgeBroadcastReceiver;
import edu.hkust.cse.phoneAdapter.context.MetaControllerBroadcastReceiver;
import edu.hkust.cse.phoneAdapter.context.SimulatingChanges;

/**
 * The main activity of PhoneAdapter.
 * @author andrew
 */
public class MainActivity extends Activity {
	
	/** The create profile button. */
	private Button createProfileBtn;
    
    /** The create rule button. */
    private Button createRuleBtn;
    
    /** The view profile button. */
    private Button viewProfileBtn;
    
    /** The view rule button. */
    private Button viewRuleBtn; 
    
    /** The record context button. */
    private Button recordContextBtn;
    KnowledgeBroadcastReceiver mKnowledgeBroadcastReceiver;
    MetaControllerBroadcastReceiver mMetaControllerBroadcastReceiver;
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        /* set GUI layout */
        setContentView(R.layout.main);
        
        /* the buttons have already been created by VM, retrieve their references and register listeners */
        createProfileBtn=(Button) findViewById(R.id.main_btn_create_profiles);
        createRuleBtn=(Button) findViewById(R.id.main_btn_create_rules);
        viewProfileBtn=(Button) findViewById(R.id.main_btn_view_profiles);
        viewRuleBtn=(Button) findViewById(R.id.main_btn_view_rules); 
        recordContextBtn=(Button) findViewById(R.id.main_btn_record_constant);
        
        /* register listeners for buttons */
        registerListenerForCreateProfileBtn();
        registerListenerForViewProfileBtn();
        registerListenerForCreateRuleBtn();
        registerListenerForViewRuleBtn();
        registerListenerForRecordConstantBtn();
        
        /*
         * start intent service
         * (1) ContextManager intentService retrieves sensing data from both logical (e.g., clock) and physical (e.g., GPS) sensors
         * (2) AdaptationManagerAllEffectors evaluates active rules upon context change, and triggers the actions specified in the satisfied rule
         */

        //Registering events for Knowledge
		IntentFilter iFilterKnowledge = new IntentFilter();
		//Messages sent by the contextManagers
		iFilterKnowledge.addAction("edu.hkust.cse.phoneAdapter.newContext");
		//Messages sent by the AdaptationManagers
		iFilterKnowledge.addAction("edu.hkust.cse.phoneAdapter.newActuators");
		mKnowledgeBroadcastReceiver = new KnowledgeBroadcastReceiver();
		registerReceiver(mKnowledgeBroadcastReceiver, iFilterKnowledge);


		//Registering events for MetaController
		//IntentFilter iFilterFailures = new IntentFilter();
		//Messages sent by the contextManagers
		//iFilterFailures.addAction("edu.hkust.cse.phoneAdapter.sensorsFailure");
		//Messages sent by the AdaptationManagers
		//iFilterFailures.addAction("edu.hkust.cse.phoneAdapter.effectorsFailure");
		//mMetaControllerBroadcastReceiver = new MetaControllerBroadcastReceiver();
		//registerReceiver(mMetaControllerBroadcastReceiver, iFilterFailures);

		Intent contextManagerAllSensorsIntent = new Intent(this, ContextManagerAllSensors.class);
		startService(contextManagerAllSensorsIntent);

		Intent adaptationManagerAllSensorsIntent = new Intent(this, AdaptationManagerAllEffectors.class);
		startService(adaptationManagerAllSensorsIntent);

		Intent failureManagerIntent = new Intent(this, FailureManager.class);
		startService(failureManagerIntent);

		Intent SimulatingChangesIntent = new Intent(this, SimulatingChanges.class);
		startService(SimulatingChangesIntent);


		Log.i("Testing MainActivity", "MainActivity" + Thread.currentThread().getName());
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
    	menu.add(Menu.NONE, Menu.FIRST+1, Menu.NONE, "start sensing and adaptation");
    	menu.add(Menu.NONE, Menu.FIRST+2, Menu.NONE, "stop sensing and adaptation");
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	switch(item.getItemId()){
    	case Menu.FIRST+1:
    		startService();
    		break;
    	case Menu.FIRST+2:
    		stopService();
    		break;
    	default: break;
    	}
    	return true;
    }
    
    @Override
	protected void onDestroy() {
    	/* stop the ContextManager and Adaptation Manager service before destroying the main activity */
    	Intent i=new Intent("edu.hkust.cse.phoneAdapter.stopService");
    	sendBroadcast(i);
    	super.onDestroy();
	}

	/**
	 * Register listener for "Create Profile" button. When the button is clicked, create profile activity will be started.
	 * @see CreateProfileActivity
	 */
	public void registerListenerForCreateProfileBtn(){
    	createProfileBtn.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				/* when the "Create Profile" button is clicked, the create profile activity will be started */
				Intent i=new Intent(MainActivity.this, CreateProfileActivity.class);
				startActivity(i);
			}
		});
    }
    
    /**
     * Register listener for "View Profile" button. When the button is clicked, view profile activity will be started.
     * @see ViewProfileActivity
     */
    public void registerListenerForViewProfileBtn(){
    	viewProfileBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				/* when the "View Profile" button is clicked, the view profile activity will be started */
				Intent i=new Intent(MainActivity.this, ViewProfileActivity.class);
				startActivity(i);
			}
		});
    }
    
    /**
     * Register listener for "Create Rule" button. When the button is clicked, create rule activity will be started.
     * @see CreateRuleActivity
     */
    public void registerListenerForCreateRuleBtn(){
    	createRuleBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i=new Intent(MainActivity.this, CreateRuleActivity.class);
				startActivity(i);
			}
		});
    }
    
    /**
     * Register listener for "View Rule" button. When the button is clicked, view rule activity will be started.
     * @see ViewRuleActivity
     */
    public void registerListenerForViewRuleBtn(){
    	 viewRuleBtn.setOnClickListener(new OnClickListener() {
 			@Override
 			public void onClick(View arg0) {
 				Intent i=new Intent(MainActivity.this, ViewRuleActivity.class);
				startActivity(i);
 			}
 		});
    }
    
    /**
     * Register listener for "Record Constant" button. When the button is clicked, create context constant activity will be started.
     * @see CreateContextConstantActivity
     */
    public void registerListenerForRecordConstantBtn(){
    	recordContextBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i=new Intent(MainActivity.this, CreateContextConstantActivity.class);
				startActivity(i);
			}
		});
   }  

   private void startService(){
    	// Case there is no context manager running, AllSensors should be started.
	   if(!ContextManagerAllSensors.isRunning() && !ContextManagerNoGPS.isRunning() && !ContextManagerNoBluetoothNoGPS.isRunning() && !ContextManagerNoBluetooth.isRunning()){
		   Intent contextManagerAllSensorsIntent = new Intent(this, ContextManagerAllSensors.class);
		   startService(contextManagerAllSensorsIntent);
	   }

	   // Case FailureManager is not running, it should be started.
	   if(!FailureManager.isRunning()){
		   Intent failureManagerIntent = new Intent(this, FailureManager.class);
		   startService(failureManagerIntent);
	   }
   }
   
   private void stopService(){
	   if(FailureManager.isRunning() || ContextManagerAllSensors.isRunning() || ContextManagerNoGPS.isRunning() || ContextManagerNoBluetoothNoGPS.isRunning() || ContextManagerNoBluetooth.isRunning()){
		   /* stop the ContextManager and Adaptation Manager service before destroying the main activity */
	    	Intent i=new Intent("edu.hkust.cse.phoneAdapter.stopService");
	    	sendBroadcast(i);
	   }
   }
}