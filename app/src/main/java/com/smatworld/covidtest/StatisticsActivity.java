package com.smatworld.covidtest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class StatisticsActivity extends AppCompatActivity {

    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    // unique ID for each notification
    private static final int NOTIFICATION_ID = 0;
    private NotificationManager mNotificationManager;
    public static final String TOPIC = "statistics";

    private static final String TAG = "AppInfo";
    private ViewFlipper mViewFlipper;
    private TextView mGlobalConfirmedCases, mGlobalConfirmedDeaths, mCountriesAffected;
    private TextView mLocalConfirmedCases, mLocalActiveCases, mCasesDischarged;
    private TextView mDetailConfirmedDeaths, mSamplesTested, mDetailStatesAffected;
    private TextView mAfricaConfirmedDeaths, mAfricaConfirmedCases, mAfricaCasesRecovered;

    private StatisticsViewModel mStatisticsViewModel;
    private Map<String, Object> mOtherStatistics;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;
    private StatisticsAdapter mStatisticsAdapter;
    private TextView mLastUpdateTextView;
    private Handler mHandler;
    private Runnable mRunnable;
    private SharedPreferences mSharedPreferences;
    private final String KEY_IS_FIRST_RUN = "com.smatworld.covidtest.IS_FIRST_RUN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        setUpViews();

        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (mOtherStatistics.size() == 0) {
                    if (!isOnline())
                        Snackbar.make(mRecyclerView, "No internet connection", Snackbar.LENGTH_SHORT).show();
                    else {
                        Snackbar.make(mRecyclerView, "Updating...", Snackbar.LENGTH_SHORT).show();
                        // call the observe method and update the UI
                    }
                    delayViewDisplay();
                } else {
                    Log.i(TAG, "onCreate: I RAAAAN");
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putBoolean(KEY_IS_FIRST_RUN, false);
                    editor.apply();

                    updateOtherStatistics();
                    displayViews();
                    Snackbar.make(mRecyclerView, "Updated", Snackbar.LENGTH_SHORT).show();
                }
            }
        };

        mSharedPreferences = getPreferences(MODE_PRIVATE);

        // The Android runtime will take care of deciding which integers.xml file to use, depending on the state of the device.
        int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);

        mOtherStatistics = new HashMap<>();
        mStatisticsAdapter = new StatisticsAdapter(this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, gridColumnCount));
        mRecyclerView.setAdapter(mStatisticsAdapter);

        mStatisticsViewModel = new ViewModelProvider(this).get(StatisticsViewModel.class);
        observe();

        if (mSharedPreferences.getBoolean(KEY_IS_FIRST_RUN, true)) {
            Log.i(TAG, "onCreate: First RUN");
            delayViewDisplay();
        } else {
            Log.i(TAG, "onCreate: NOT FIRST TIME I RAN");
            updateFromCache();
        }

        createNotificationChannel();
        getFirebaseInstance();
    }

    private NotificationCompat.Builder getNotificationBuilder() {
        Intent notificationIntent = new Intent(this, StatisticsActivity.class);
        // notificationIntent.setFlags(Intent._)
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        return new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle("Statistics update")
                .setContentText("New COVID-19 statistics are available")
                .setSmallIcon(R.drawable.logoicon)
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true);
    }

    private void createNotificationChannel() {
        // creates a NotificationChannel, but only on API 26 and above because
        // the NotificationChannel class is new and not supported for older APIs
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID, name, importance);
            notificationChannel.setDescription(description);
            // Register the channel with the system; can's change the importance
            // or other notification behaviours after this
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void sendNotification() {
        NotificationCompat.Builder notificationBuilder = getNotificationBuilder();
        mNotificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }

    private void getFirebaseInstance() {
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                String msg = getString(R.string.msg_subscribed);
                if (!task.isSuccessful())
                    msg = getString(R.string.msg_subscribe_failed);
                Log.i(TAG, msg);
                Toast.makeText(StatisticsActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
        /*FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.i(TAG, "onComplete: getInstanceId failed", task.getException());
                    return;
                }

                // Get new Instance ID token
                String token = task.getResult().getToken();

                // Log and Toast
                String msg = getString(R.string.msg_token_fmt, token);
                Log.i(TAG, msg);
                Toast.makeText(StatisticsActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    private void updateFromCache() {
        Log.i(TAG, "updateFromCache: ");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mOtherStatistics.size() != 0) {
                    updateOtherStatistics();
                    displayViews();
                    Snackbar.make(mRecyclerView, "Updated", Snackbar.LENGTH_SHORT).show();
                } else
                    updateFromCache();
            }
        }, 1000);
    }

    private void delayViewDisplay() {
        Log.i(TAG, "delayViewDisplay: size: " + mOtherStatistics.size());
        mHandler.postDelayed(mRunnable, 5000);
    }

    @Override
    public void onBackPressed() {
        // removes the runnable thread to prevent it from
        // continuously running even when the activity is destroyed
        if (mHandler != null)
            mHandler.removeCallbacks(mRunnable);
        super.onBackPressed();
    }

    private void observe() {
        mStatisticsViewModel.getLocalStatistics().observe(this, new Observer<List<StateStatistics>>() {
            @Override
            public void onChanged(List<StateStatistics> localStatistics) {
                Log.i(TAG, "onChanged: local change observed");
                mStatisticsAdapter.setStatisticsList(localStatistics);
            }
        });

        mStatisticsViewModel.getOtherStatistics().observe(this, new Observer<Map<String, Object>>() {
            @Override
            public void onChanged(Map<String, Object> otherStatistics) {
                mOtherStatistics = otherStatistics;
                Log.i(TAG, "onChanged: other changes observed");
            }
        });
    }

    private void setUpViews() {
        // progress Bar
        mProgressBar = findViewById(R.id.progress_bar);

        mLastUpdateTextView = findViewById(R.id.last_update);

        mViewFlipper = findViewById(R.id.stats_view_flipper);
        mRecyclerView = findViewById(R.id.statistics_recycler_view);

        // global data
        mGlobalConfirmedCases = findViewById(R.id.global_value_1);
        mGlobalConfirmedDeaths = findViewById(R.id.global_value_2);
        mCountriesAffected = findViewById(R.id.global_value_3);
        // local data
        mLocalConfirmedCases = findViewById(R.id.local_value_1);
        mLocalActiveCases = findViewById(R.id.local_value_2);
        mCasesDischarged = findViewById(R.id.local_value_3);
        // local detailed data
        mSamplesTested = findViewById(R.id.local_detail_value_1);
        mDetailConfirmedDeaths = findViewById(R.id.local_detail_value_2);
        mDetailStatesAffected = findViewById(R.id.local_detail_value_3);
        // Africa data
        mAfricaConfirmedCases = findViewById(R.id.africa_value_1);
        mAfricaConfirmedDeaths = findViewById(R.id.africa_value_2);
        mAfricaCasesRecovered = findViewById(R.id.africa_value_3);
    }

    private void displayViews() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mViewFlipper.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mLastUpdateTextView.setVisibility(View.VISIBLE);
    }

    private void updateOtherStatistics() {
        // Global data
        mGlobalConfirmedCases.setText(String.valueOf(mOtherStatistics.get("globalConfirmedCases")));
        mCountriesAffected.setText(String.valueOf(mOtherStatistics.get("countriesAffected")));
        mGlobalConfirmedDeaths.setText(String.valueOf(mOtherStatistics.get("globalConfirmedDeaths")));

        // Timestamp formatting and update
        Timestamp timestamp = (Timestamp) mOtherStatistics.get("timeStamp");
        if (timestamp != null) {
            String lastUpdate = "Last update: " + new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss a", Locale.getDefault()).format(timestamp.toDate());
            Log.i(TAG, "updateOtherStatistics: " + lastUpdate);
            mLastUpdateTextView.setText(lastUpdate);
        }


        // local data
        mLocalConfirmedCases.setText(String.valueOf(mOtherStatistics.get("localCasesConfirmed")));
        mLocalActiveCases.setText(String.valueOf(mOtherStatistics.get("localActiveCases")));
        mCasesDischarged.setText(String.valueOf(mOtherStatistics.get("localCasesDischarged")));

        // local detailed data
        mSamplesTested.setText(String.valueOf(mOtherStatistics.get("localSamplesTested")));
        mDetailConfirmedDeaths.setText(String.valueOf(mOtherStatistics.get("localDeaths")));
        mDetailStatesAffected.setText(String.valueOf(mOtherStatistics.get("statesAffected")));

        // Africa Data
        mAfricaCasesRecovered.setText(String.valueOf(mOtherStatistics.get("africaRecoveries")));
        mAfricaConfirmedCases.setText(String.valueOf(mOtherStatistics.get("africaCasesConfirmed")));
        mAfricaConfirmedDeaths.setText(String.valueOf(mOtherStatistics.get("africaDeaths")));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_webview_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_refresh) {
            sendNotification();
            /*updateLocalStats();
            updateOtherStats();
            Toast.makeText(this, "No new updates", Toast.LENGTH_SHORT).show();*/
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateLocalStats() {
        String[] states = getResources().getStringArray(R.array.state_names);
        int[] activeCases = new int[]{2332, 690, 311, 239, 122, 89, 130, 103, 110, 66, 41, 78, 25, 54, 8, 40, 42, 5, 15, 28, 11, 10, 12, 16, 5, 4, 4, 8, 10, 12, 6, 0, 2, 5, 4};
        int[] numberDischarged = new int[]{582, 121, 115, 29, 89, 130, 78, 69, 48, 82, 92, 35, 75, 21, 63, 25, 14, 33, 18, 3, 17, 12, 13, 5, 13, 15, 12, 10, 6, 1, 1, 7, 5, 0, 1};
        int[] deaths = new int[]{40, 36, 9, 13, 24, 5, 3, 6, 4, 4, 3, 6, 13, 4, 5, 1, 1, 4, 1, 1, 4, 6, 1, 1, 2, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0};
        int[] casesConfirmed = new int[states.length];
        for (int i = 0; i < states.length; i++)
            casesConfirmed[i] = activeCases[i] + numberDischarged[i] + deaths[i];

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Toast.makeText(this, "Trying to add data...", Toast.LENGTH_SHORT).show();
        for (int i = 0; i < states.length; i++) {
            DocumentReference documentReference = db.collection("local-statistics").document(states[i]);

            // using a custom object class to write docs to db
            StateStatistics stateStatistics = new StateStatistics(states[i], casesConfirmed[i], activeCases[i], numberDischarged[i], deaths[i]);
            documentReference.set(stateStatistics);
        }

        Log.i("AppInfo", "Local Docs updated");
    }

    private void updateOtherStats() {
        int globalConfirmedCases = 4801202;
        int countriesAffected = 216;
        int globalConfirmedDeaths = 318935;

        // local stats
        int localCasesConfirmed = 6677;
        int localCasesDischarged = 1840;
        int localDeaths = 200;
        int statesAffected = 35;
        int localSamplesTested = 38231;

        // Africa stats
        int africaCasesConfirmed = 92348;
        int africaDeaths = 2912;
        int africaRecoveries = 36117;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("total-statistics");

        DocumentReference documentReference = collectionReference.document("global-doc");
        Map<String, Object> map = new HashMap<>();
        map.put("globalConfirmedCases", globalConfirmedCases);
        map.put("countriesAffected", countriesAffected);
        map.put("globalConfirmedDeaths", globalConfirmedDeaths);
        map.put("timeStamp", FieldValue.serverTimestamp());

        documentReference.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("AppInfo", "Global Docs Updated!");
            }
        });

        DocumentReference localDocs = collectionReference.document("local-doc");
        Map<String, Object> localMap = new HashMap<>();
        localMap.put("localCasesConfirmed", localCasesConfirmed);
        localMap.put("localDeaths", localDeaths);
        localMap.put("localCasesDischarged", localCasesDischarged);
        localMap.put("statesAffected", statesAffected);
        localMap.put("localSamplesTested", localSamplesTested);
        localDocs.set(localMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("AppInfo", "Local Docs Updated!");
            }
        });

        DocumentReference africaDocs = collectionReference.document("africa-doc");
        Map<String, Object> africaMap = new HashMap<>();
        africaMap.put("africaCasesConfirmed", africaCasesConfirmed);
        africaMap.put("africaDeaths", africaDeaths);
        africaMap.put("africaRecoveries", africaRecoveries);
        africaDocs.set(africaMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.i("AppInfo", "Africa docs updated");
            }
        });
    }

    private boolean isOnline() {
        try {
            return new InternetAccessAsyncTask().execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static class InternetAccessAsyncTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPostExecute(Boolean internet) {
            super.onPostExecute(internet);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                int timeoutMs = 1500;
                Socket sock = new Socket();
                SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);
                sock.connect(sockaddr, timeoutMs);
                sock.close();
                return true;
            } catch (IOException e) {
                return false;
            }
        }
    }
}

