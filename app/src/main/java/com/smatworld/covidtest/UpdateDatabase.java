package com.smatworld.covidtest;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UpdateDatabase extends AppCompatActivity {

    public static final int MIN_STATE_NAMES_LIST_SIZE = 1;
    private static final String TAG = "AppInfo";
    TextInputLayout mCasesConfirmed, mCasesActive, mCasesDischarged, mCasesDeath, mOtherStats;
    private TextView mStateNameTextView, mStatesPopulatedCounter;
    private ArrayList<StateStatistics> mStateStatisticsArrayList;
    private ArrayAdapter<CharSequence> mStateAdapter, mOtherAdapter;
    private static int currentItemPosition, otherItemPosition;
    private Spinner mStateSpinner, mOtherSpinner;
    // global stats
    private int globalConfirmedCases, countriesAffected, globalConfirmedDeaths;
    // local stats
    private int localCasesConfirmed, localActiveCases, localCasesDischarged, localDeaths, statesAffected, localSamplesTested;
    // Africa stats
    private int africaCasesConfirmed, africaDeaths, africaRecoveries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_database);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mStateStatisticsArrayList = new ArrayList<>();

        mOtherStats = findViewById(R.id.spinner_other);
        mCasesConfirmed = findViewById(R.id.case_confirmed);
        mCasesActive = findViewById(R.id.case_active);
        mCasesDischarged = findViewById(R.id.case_discharged);
        mCasesDeath = findViewById(R.id.case_deaths);

        mOtherStats.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) addOtherData(v);
                return true;
            }
        });
        mCasesDeath.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) addStateData(v);
                return true;
            }
        });

        mStateNameTextView = findViewById(R.id.state_name);
        mStatesPopulatedCounter = findViewById(R.id.count_value);

        mOtherSpinner = findViewById(R.id.spinner_other_value);
        mOtherAdapter = ArrayAdapter.createFromResource(this, R.array.other_stats_names, android.R.layout.simple_spinner_item);
        mOtherAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mOtherSpinner.setAdapter(mOtherAdapter);
        mOtherSpinner.setOnItemSelectedListener(otherSpinnerListener);

        mStateSpinner = findViewById(R.id.spinner_value);
        mStateAdapter = ArrayAdapter.createFromResource(this, R.array.sorted_state_names, android.R.layout.simple_spinner_item);
        mStateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mStateSpinner.setAdapter(mStateAdapter);
        mStateSpinner.setOnItemSelectedListener(stateSpinnerListener);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//
                if (mStateStatisticsArrayList.size() >= MIN_STATE_NAMES_LIST_SIZE && globalConfirmedCases != 0 && africaRecoveries != 0 && localCasesConfirmed != 0) {
                    updateLocalStats(mStateStatisticsArrayList);
                    updateOtherStats();
                } else
                    Snackbar.make(view, "Data incomplete", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
            }
        });

    }

    private void updateLocalStats(ArrayList<StateStatistics> stateStatisticsList) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Toast.makeText(this, "Trying to add data with size: " + stateStatisticsList.size(), Toast.LENGTH_SHORT).show();
        for (StateStatistics stateStatistics : stateStatisticsList) {
            DocumentReference documentReference = db.collection("local-statistics").document(stateStatistics.getStateName());
            // using a custom object class to write docs to db
            documentReference.set(stateStatistics);
        }
        Toast.makeText(UpdateDatabase.this, "State Docs Updated!", Toast.LENGTH_SHORT).show();
    }

    private void updateOtherStats() {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionReference = db.collection("total-statistics");

        DocumentReference documentReference = collectionReference.document("global-doc");
        final Map<String, Object> map = new HashMap<>();
        map.put("globalConfirmedCases", globalConfirmedCases);
        map.put("countriesAffected", countriesAffected);
        map.put("globalConfirmedDeaths", globalConfirmedDeaths);
        map.put("timeStamp", FieldValue.serverTimestamp());

        documentReference.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(UpdateDatabase.this, "Global Docs Updated!", Toast.LENGTH_SHORT).show();
            }
        });

        DocumentReference localDocs = collectionReference.document("local-doc");
        Map<String, Object> localMap = new HashMap<>();
        localMap.put("localCasesConfirmed", localCasesConfirmed);
        localMap.put("localActiveCases", localActiveCases);
        localMap.put("localDeaths", localDeaths);
        localMap.put("localCasesDischarged", localCasesDischarged);
        localMap.put("statesAffected", statesAffected);
        localMap.put("localSamplesTested", localSamplesTested);
        localDocs.set(localMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(UpdateDatabase.this, "Local Docs Updated!", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(UpdateDatabase.this, "Africa Docs Updated!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private AdapterView.OnItemSelectedListener stateSpinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String selectedState = parent.getItemAtPosition(position).toString();
            mStateNameTextView.setText(selectedState);
            mCasesConfirmed.getEditText().setText("");
            mCasesConfirmed.requestFocus();
            mCasesActive.getEditText().setText("");
            mCasesDischarged.getEditText().setText("");
            mCasesDeath.getEditText().setText("");

            currentItemPosition = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    public void addStateData(View view) {
        String stateName = mStateNameTextView.getText().toString();
        String cases = mCasesConfirmed.getEditText().getText().toString();
        String active = mCasesActive.getEditText().getText().toString();
        String discharged = mCasesDischarged.getEditText().getText().toString();
        String deaths = mCasesDeath.getEditText().getText().toString();

        if (TextUtils.isEmpty(stateName) || TextUtils.isEmpty(cases) || TextUtils.isEmpty(active) || TextUtils.isEmpty(discharged) || TextUtils.isEmpty(deaths))
            Toast.makeText(this, "Fill in empty field(s)", Toast.LENGTH_SHORT).show();
        else {

            int casesConfirmed = Integer.parseInt(cases);
            int activeCases = Integer.parseInt(active);
            int casesDischarged = Integer.parseInt(discharged);
            int deathsRecorded = Integer.parseInt(deaths);

            mStateStatisticsArrayList.add(new StateStatistics(stateName, casesConfirmed, activeCases, casesDischarged, deathsRecorded));
            Toast.makeText(this, mStateStatisticsArrayList.get(mStateStatisticsArrayList.size() - 1).getStateName(), Toast.LENGTH_SHORT).show();

            mStatesPopulatedCounter.setText(String.valueOf(mStateStatisticsArrayList.size()));

            // automatically select next state in the spinner
            if (currentItemPosition < mStateAdapter.getCount() - 1)
                mStateSpinner.setSelection(currentItemPosition + 1, true);
        }
    }

    private AdapterView.OnItemSelectedListener otherSpinnerListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mOtherStats.getEditText().setText("");
            otherItemPosition = position;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    public void addOtherData(View view) {
        String value = mOtherStats.getEditText().getText().toString();
        if (TextUtils.isEmpty(value))
            Toast.makeText(this, "Fill in empty field", Toast.LENGTH_SHORT).show();
        else {
            switch (otherItemPosition) {
                case 0:
                    globalConfirmedCases = Integer.parseInt(value);
                    break;
                case 1:
                    countriesAffected = Integer.parseInt(value);
                    break;
                case 2:
                    globalConfirmedDeaths = Integer.parseInt(value);
                    break;
                case 3:
                    localCasesConfirmed = Integer.parseInt(value);
                    break;
                case 4:
                    localActiveCases = Integer.parseInt(value);
                    break;
                case 5:
                    localCasesDischarged = Integer.parseInt(value);
                    break;
                case 6:
                    localDeaths = Integer.parseInt(value);
                    break;
                case 7:
                    statesAffected = Integer.parseInt(value);
                    break;
                case 8:
                    localSamplesTested = Integer.parseInt(value);
                    break;
                case 9:
                    africaCasesConfirmed = Integer.parseInt(value);
                    break;
                case 10:
                    africaDeaths = Integer.parseInt(value);
                    break;
                case 11:
                    africaRecoveries = Integer.parseInt(value);
                    Log.i(TAG, "addOtherData: " + africaRecoveries);
                    break;
                default:
                    return;
            }
            Toast.makeText(UpdateDatabase.this, "position: " + otherItemPosition, Toast.LENGTH_SHORT).show();
            if (otherItemPosition < mOtherAdapter.getCount() - 1)
                mOtherSpinner.setSelection(otherItemPosition + 1, true);
        }

    }
}
