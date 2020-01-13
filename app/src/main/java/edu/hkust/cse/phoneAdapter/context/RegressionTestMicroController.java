package edu.hkust.cse.phoneAdapter.context;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Config;

import junit.framework.Test;

import java.util.Date;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class RegressionTestMicroController extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_NEWCHANGE = "edu.hkust.cse.phoneAdapter.context.action.NEWCHANGE";

    // TODO: Rename parameters
    private static final String EXTRA_CONFIG1 = "edu.hkust.cse.phoneAdapter.context.extra.CONFIG1";
    private static final String EXTRA_CONFIG2 = "edu.hkust.cse.phoneAdapter.context.extra.CONFIG2";

    public RegressionTestMicroController() {
        super("RegressionTestMicroController");
    }

    /**
     * Starts this service to perform action Monitor with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startRegression(Context context, String configurationA, String configurationB) {
        Intent intent = new Intent(context, RegressionTestMicroController.class);
        intent.setAction(ACTION_NEWCHANGE);
        intent.putExtra(EXTRA_CONFIG1, configurationA);
        intent.putExtra(EXTRA_CONFIG2, configurationB);
        context.startService(intent);
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_NEWCHANGE.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_CONFIG1);
                final String param2 = intent.getStringExtra(EXTRA_CONFIG2);

                // Retrieving configurations A and B
                Configuration config1 = new Configuration(param1);
                Configuration config2 = new Configuration(param2);


                // Analysing: The test cases referring to the Configuration's sensors


                // Planning: Minimising, Selecting and Prioritizing   (e.


                // Executing: Executing test cases and update the knowledge


                Intent i = new Intent();
                i.setAction("edu.hkust.cse.phoneAdapter.minimisedTests");
                i.putExtra("minimisedTests", true);
                i.setAction("edu.hkust.cse.phoneAdapter.selectedTests");
                i.putExtra("selectedTests", true);
                i.setAction("edu.hkust.cse.phoneAdapter.PrioritizedTest");
                i.putExtra("PrioritizedTest", true);
                i.setAction("edu.hkust.cse.phoneAdapter.allExecutedTests");
                i.putExtra("allExecutedTests", true);
                i.setAction("edu.hkust.cse.phoneAdapter.allNonExecutedTests");
                i.putExtra("allNonExecutedTests", true);
                i.setAction("edu.hkust.cse.phoneAdapter.allPassedTests");
                i.putExtra("allPassedTests", true);
                i.setAction("edu.hkust.cse.phoneAdapter.anyFailedTests");
                i.putExtra("anyFailedTests", true);
                sendBroadcast(i);


                // ------------------------------------------------------------


                // Which sensors are related to the CONFIG2
                // Filtering test cases using the sensors of the CONFIG2
                // Removing test cases already validated in CONFIG1
                // Executing test cases
            }
        }
    }

    private class Knowledge
    {
        private List<Configuration> Configurations;
        private List<TestCase> TestCases;
        private List<Historic> Historic;

        public List<Configuration> getConfigurations() {
            return Configurations;
        }

        public void setConfigurations(List<Configuration> configurations) {
            Configurations = configurations;
        }

        public List<TestCase> getTestCases() {
            return TestCases;
        }

        public void setTestCases(List<TestCase> testCases) {
            TestCases = testCases;
        }

        public List<RegressionTestMicroController.Historic> getHistoric() {
            return Historic;
        }

        public void setHistoric(List<RegressionTestMicroController.Historic> historic) {
            Historic = historic;
        }
    }


    private class Configuration
    {
        private List<String> Sensors;

        public List<String> getSensors() {
            return Sensors;
        }

        public void setSensors(List<String> sensors) {
            Sensors = sensors;
        }


        public Configuration (String config)
        {

        }
    }



    private class Historic
    {
        private Configuration ConfigA;
        private Configuration ConfigB;
        private TestResult TestResult;
        private List<TestActivity> TestActivities;

        public Configuration getConfigA() {
            return ConfigA;
        }

        public void setConfigA(Configuration configA) {
            ConfigA = configA;
        }

        public Configuration getConfigB() {
            return ConfigB;
        }

        public void setConfigB(Configuration configB) {
            ConfigB = configB;
        }

        public RegressionTestMicroController.TestResult getTestResult() {
            return TestResult;
        }

        public void setTestResult(RegressionTestMicroController.TestResult testResult) {
            TestResult = testResult;
        }

        public List<TestActivity> getTestActivities() {
            return TestActivities;
        }

        public void setTestActivities(List<TestActivity> testActivities) {
            TestActivities = testActivities;
        }


    }

    private class TestCase {}

    private class TestResult {}

    private class TestActivity {}

}
