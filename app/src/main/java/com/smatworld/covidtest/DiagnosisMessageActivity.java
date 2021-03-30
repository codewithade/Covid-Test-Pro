package com.smatworld.covidtest;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

public class DiagnosisMessageActivity extends AppCompatActivity implements DiagnosisMessageAdapter.OnItemClickListener {

    private static final String TAG = "AppInfo";
    private DiagnosisMessageModel mDiagnosisMessageModel;
    private RecyclerView mDiagnosisChatRecyclerView;
    private ArrayList<DiagnosisMessage> mDiagnosisMessages;
    private DiagnosisMessageAdapter mDiagnosisMessageAdapter;
    private EditText enterMessageBox;
    private String presentTime;
    private Button mLaunchGoogleMap;

    final static int CHECKBOX_PRECAUTION_ID = 1;
    final static int CHECKBOX_DAYS_SYMPTOMS_ID = 2;
    final static int CHECKBOX_SYMPTOMS_ID = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagnosis_message);
        // Enable the Up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mLaunchGoogleMap = findViewById(R.id.launch_google_map);

        mDiagnosisMessageModel = new ViewModelProvider(this).get(DiagnosisMessageModel.class);
        mDiagnosisChatRecyclerView = findViewById(R.id.recycler_view_chat_area);
        mDiagnosisMessages = new ArrayList<>();

        enterMessageBox = findViewById(R.id.enter_message_box);
        enterMessageBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) sendMessage(v);
                return false;
            }
        });

        mDiagnosisMessageModel.loadChatData();

        // checks for case of configuration change
        // runs only if this is d first instance of the Activity
        if (mDiagnosisMessageModel.mIsNewlyCreated) {
            Log.i(TAG, "onCreate: model newly created");
            // reset counters when activity is first created
            DiagnosisMessage.COUNT = 0; // messageID counter
            DiagnosisMessageAdapter.RADIO_BUTTON_ID = 0;

            mDiagnosisMessageModel.chatSequence();
        }

        mDiagnosisMessageModel.mIsNewlyCreated = false;

        mDiagnosisMessageModel.getDiagnosisMessageLiveData().observe(this, new Observer<ArrayList<DiagnosisMessage>>() {
            @Override
            public void onChanged(ArrayList<DiagnosisMessage> diagnosisMessages) {
                mDiagnosisMessageAdapter.submitList(diagnosisMessages);
                mDiagnosisMessages = diagnosisMessages;
                Log.i(TAG, "onChanged: message size: " + mDiagnosisMessages.size());
                insertNewItem();
            }
        });
        mDiagnosisMessageAdapter = new DiagnosisMessageAdapter(this);
        mDiagnosisChatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mDiagnosisChatRecyclerView.setAdapter(mDiagnosisMessageAdapter);

        mDiagnosisMessageAdapter.setOnItemClickListener(this);
    }

    @Override
    public void getCheckedItems(ArrayList<String> itemsChecked, int checkboxResUsedID) {
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < itemsChecked.size(); i++)
            message.append(itemsChecked.get(i)).append("\n");
        message.deleteCharAt(message.lastIndexOf("\n"));
        presentTime = mDiagnosisMessageModel.getSimpleDateFormat().format(new Date()).toLowerCase();
        // remove the last newline character
        mDiagnosisMessageModel.userMessage(DiagnosisMessage.COUNT++, message.toString(), presentTime, mDiagnosisMessageModel.getUserID());
        // insertNewItem();

        String chatBotName = mDiagnosisMessageModel.getChatBotName();
        String[] keepTakingPrecaution = mDiagnosisMessageModel.getKeepTakingPrecaution();
        int mBotImage = mDiagnosisMessageModel.getBotImage();
        int botID = mDiagnosisMessageModel.getBotID();
        String[] priorIllness = mDiagnosisMessageModel.getPriorIllness();
        int radioID = mDiagnosisMessageModel.getRadioID();
        String[] travelHistory = mDiagnosisMessageModel.getTravelHistory();
        String[] poorPrecautionarySteps = mDiagnosisMessageModel.getPoorPrecautionarySteps();
        String[] fairPrecautionarySteps = mDiagnosisMessageModel.getFairPrecautionarySteps();
        // retrieves "take another test" statements
        String[] test = getResources().getStringArray(R.array.test);

        switch (checkboxResUsedID) {
            case 1: // based on precautions percentage taken, display specific messages.
                // converts array res into ArrayList
                ArrayList<String> originalItemList = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.c_precautionary_measures_adopted)));
                int percentChecked = getCheckedItemsPercent(itemsChecked.size(), originalItemList.size());

                if (percentChecked <= 30) { // user is taking poor precautions
                    String poorPrecautionaryMessage = poorPrecautionarySteps[new Random().nextInt(poorPrecautionarySteps.length)] + returnUncheckedItems(originalItemList, itemsChecked) + test[new Random().nextInt(test.length)];
                    mDiagnosisMessageModel.chatBotMessage(DiagnosisMessage.COUNT++, presentTime, chatBotName, poorPrecautionaryMessage, mBotImage, botID);
                } else if (percentChecked < 70) { // user is taking a fair amount of precautions
                    String fairPrecautionaryMessage = fairPrecautionarySteps[new Random().nextInt(fairPrecautionarySteps.length)] + returnUncheckedItems(originalItemList, itemsChecked) + test[new Random().nextInt(test.length)];
                    mDiagnosisMessageModel.chatBotMessage(DiagnosisMessage.COUNT++, presentTime, chatBotName, fairPrecautionaryMessage, mBotImage, botID);
                } else // user is protected well enough
                    mDiagnosisMessageModel.chatBotMessage(DiagnosisMessage.COUNT++, presentTime, chatBotName, keepTakingPrecaution[new Random().nextInt(keepTakingPrecaution.length)], mBotImage, botID);
                mLaunchGoogleMap.setVisibility(View.VISIBLE);
                break;

            case 2: // based on symptoms experienced. having prior respiratory illnesses?
                ArrayList<String> criticalSymptoms = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.critical_symptoms)));
                int severityPercentage = userSymptomSeverity(itemsChecked, criticalSymptoms);
                if (severityPercentage >= 40) { // user is likely infected
                    mDiagnosisMessageModel.radioOptionMessage(DiagnosisMessage.COUNT++, presentTime, chatBotName, priorIllness[new Random().nextInt(priorIllness.length)],
                            mBotImage, radioID);
                    Log.i(TAG, "getCheckedItems: percent: " + severityPercentage);
                } else { // user likely not infected and may be showing symptoms of some other ailment ask for current symptoms duration
                    String[] currentSymptomsDuration = mDiagnosisMessageModel.getCurrentSymptomsDuration();
                    mDiagnosisMessageModel.checkboxOptionMessage(DiagnosisMessage.COUNT++, presentTime, chatBotName, currentSymptomsDuration[new Random().nextInt(currentSymptomsDuration.length)],
                            mBotImage, mDiagnosisMessageModel.getCheckboxID(), CHECKBOX_DAYS_SYMPTOMS_ID);
                    Log.i(TAG, "getCheckedItems: maybe a similar illness percent: " + severityPercentage);
                }

                break;
            case 3:
                if (DiagnosisMessage.COUNT == 9) { //if TRUE, user likely not infected, Do something else
                    Log.i(TAG, "Count: " + DiagnosisMessage.COUNT);
                    String[] phcContact = mDiagnosisMessageModel.getPhcContact();
                    mDiagnosisMessageModel.chatBotMessage(DiagnosisMessage.COUNT++, presentTime, chatBotName, phcContact[new Random().nextInt(phcContact.length)] + test[new Random().nextInt(test.length)],
                            mBotImage, botID);
                    mLaunchGoogleMap.setVisibility(View.VISIBLE);
                } else // ask user for travel history as d user might likely be infected
                    mDiagnosisMessageModel.radioOptionMessage(DiagnosisMessage.COUNT++, presentTime, chatBotName, travelHistory[new Random().nextInt(travelHistory.length)],
                            mBotImage, radioID);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + checkboxResUsedID);
        }
        insertNewItem();
    }

    private int userSymptomSeverity(ArrayList<String> itemsChecked, ArrayList<String> criticalSymptoms) {
        int count = 0;
        for (String symptom : itemsChecked) {
            if (criticalSymptoms.contains(symptom))
                count++;
        }
        return getCheckedItemsPercent(count, criticalSymptoms.size());
    }

    // compares two lists and returns list of items that were unchecked by the user
    private String returnUncheckedItems(ArrayList<String> originalItemsList, ArrayList<String> checkedItems) {
        StringBuilder builder = new StringBuilder();
        for (String listItem : originalItemsList) {
            if (!checkedItems.contains(listItem))
                builder.append("-> ").append(listItem).append("\n");
        }
        return builder.toString();
    }

    // processes the percentage of items checked
    // @items = precautionary measures taken by users
    private int getCheckedItemsPercent(float itemsCheckedCount, float originalItemCount) {
        return Math.round((itemsCheckedCount / originalItemCount) * 100);
    }

    @Override
    public void getSelectedRadioButton(String buttonOption) {
        presentTime = mDiagnosisMessageModel.getSimpleDateFormat().format(new Date()).toLowerCase();

        mDiagnosisMessageModel.userMessage(DiagnosisMessage.COUNT++, buttonOption, presentTime, mDiagnosisMessageModel.getUserID());
        // insertNewItem();

        String chatBotName = mDiagnosisMessageModel.getChatBotName();
        String[] nursingSickness = mDiagnosisMessageModel.getNursingSickness();
        int mBotImage = mDiagnosisMessageModel.getBotImage();
        int radioID = mDiagnosisMessageModel.getRadioID();
        int botID = mDiagnosisMessageModel.getBotID();
        String[] inform = mDiagnosisMessageModel.getInform();
        String[] symptoms = mDiagnosisMessageModel.getSymptoms();
        String[] precautionQuestion = mDiagnosisMessageModel.getPrecautionQuestion();
        String[] prevSymptomsDuration = mDiagnosisMessageModel.getPrevSymptomsDuration();
        int checkboxID = mDiagnosisMessageModel.getCheckboxID();
        String[] currentSymptomsDuration = mDiagnosisMessageModel.getCurrentSymptomsDuration();
        String[] ncdcContact = mDiagnosisMessageModel.getNcdcContact();
        String[] stateTravelHistory = mDiagnosisMessageModel.getStateTravelHistory();
        String[] phcContact = mDiagnosisMessageModel.getPhcContact();

        String[] atHomeQuestion = mDiagnosisMessageModel.getAtHomeQuestion();
        String[] noAtHome = mDiagnosisMessageModel.getNoAtHome();
        String[] yesAtHome = mDiagnosisMessageModel.getYesAtHome();
        String[] yesAbove = mDiagnosisMessageModel.getYesAbove();
        String[] noBelow = mDiagnosisMessageModel.getNoBelow();

        // Log.i(TAG, "Formatted string: " + getString(R.string.test, 20, 30.9075f)); //Formatted string: 20 guesses, 30.91% correct

        switch (DiagnosisMessageAdapter.RADIO_BUTTON_ID) {
            case 1:
                // if Yes to start test, ask Are you feeling sick?
                // else No? Show information page
                if (buttonOption.toLowerCase().equals("yes"))
                    mDiagnosisMessageModel.radioOptionMessage(DiagnosisMessage.COUNT++, presentTime, chatBotName, nursingSickness[new Random().nextInt(nursingSickness.length)],
                            mBotImage, radioID);
                else {
                    mDiagnosisMessageModel.chatBotMessage(DiagnosisMessage.COUNT++, presentTime, chatBotName, inform[new Random().nextInt(inform.length)],
                            mBotImage, botID);
                    DiagnosisMessageAdapter.RADIO_BUTTON_ID--;
                }
                break;
            case 2:
                // if Yes, User is sick. ask for symptoms experienced using checkboxes.
                // else No, user isn't sick. ask for precautionary measures adopted using checkboxes.
                if (buttonOption.toLowerCase().equals("yes"))
                    mDiagnosisMessageModel.checkboxOptionMessage(DiagnosisMessage.COUNT++, presentTime, chatBotName, symptoms[new Random().nextInt(symptoms.length)],
                            mBotImage, checkboxID, CHECKBOX_SYMPTOMS_ID);
                else {
                    // Start the STAY AT HOME module
                    mDiagnosisMessageModel.radioOptionMessage(DiagnosisMessage.COUNT++, presentTime, chatBotName, atHomeQuestion[new Random().nextInt(atHomeQuestion.length)], mBotImage, radioID);
                    // mDiagnosisMessageModel.checkboxOptionMessage(DiagnosisMessage.COUNT++, presentTime, chatBotName, precautionQuestion[new Random().nextInt(precautionQuestion.length)], mBotImage, checkboxID, CHECKBOX_PRECAUTION_ID);
                }
                break;
            case 3:
                // if Yes, user has past respiratory issues. ask for recovery rate of past illnesses
                // if No, No past related illness, ask for how long current symptoms lasted.

                if (buttonOption.toLowerCase().equals("yes"))
                    if (DiagnosisMessage.COUNT == 7) { // user is at home. advise user to maintain proper precautions and ask user for precaution being practised
                        mDiagnosisMessageModel.chatBotMessage(DiagnosisMessage.COUNT++, presentTime, chatBotName, yesAtHome[new Random().nextInt(yesAtHome.length)], mBotImage, botID);
                        mDiagnosisMessageModel.checkboxOptionMessage(DiagnosisMessage.COUNT++, presentTime, chatBotName, precautionQuestion[new Random().nextInt(precautionQuestion.length)], mBotImage, checkboxID, CHECKBOX_PRECAUTION_ID);
                    } else
                        mDiagnosisMessageModel.checkboxOptionMessage(DiagnosisMessage.COUNT++, presentTime, chatBotName, prevSymptomsDuration[new Random().nextInt(prevSymptomsDuration.length)], mBotImage, checkboxID, CHECKBOX_DAYS_SYMPTOMS_ID);
                else {
                    if (DiagnosisMessage.COUNT == 7) // user not staying at home. ask user his/her age
                        mDiagnosisMessageModel.radioOptionMessage(DiagnosisMessage.COUNT++, presentTime, chatBotName, noAtHome[new Random().nextInt(noAtHome.length)], mBotImage, radioID);
                    else
                        mDiagnosisMessageModel.checkboxOptionMessage(DiagnosisMessage.COUNT++, presentTime, chatBotName, currentSymptomsDuration[new Random().nextInt(currentSymptomsDuration.length)], mBotImage, checkboxID, CHECKBOX_DAYS_SYMPTOMS_ID);
                }
                break;
            case 4:
                // if Yes, user visited some high risk country. contact NCDC ASAP!!!
                // if No, user didn't visit. ask user for states visited of late

                if (buttonOption.toLowerCase().equals("yes")) {
                    if (DiagnosisMessage.COUNT == 9) { // Tells user that is above 60years of age to take precaution and ask user for precaution being practised
                        mDiagnosisMessageModel.chatBotMessage(DiagnosisMessage.COUNT++, presentTime, chatBotName, yesAbove[new Random().nextInt(yesAbove.length)], mBotImage, botID);
                        mDiagnosisMessageModel.checkboxOptionMessage(DiagnosisMessage.COUNT++, presentTime, chatBotName, precautionQuestion[new Random().nextInt(precautionQuestion.length)], mBotImage, checkboxID, CHECKBOX_PRECAUTION_ID);
                    } else {
                        mDiagnosisMessageModel.chatBotMessage(DiagnosisMessage.COUNT++, presentTime, chatBotName, ncdcContact[new Random().nextInt(ncdcContact.length)], mBotImage, botID);
                        mLaunchGoogleMap.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (DiagnosisMessage.COUNT == 9) { // tells user that is below 60years to be careful and ask user for precaution being practised
                        mDiagnosisMessageModel.chatBotMessage(DiagnosisMessage.COUNT++, presentTime, chatBotName, noBelow[new Random().nextInt(noBelow.length)], mBotImage, botID);
                        mDiagnosisMessageModel.checkboxOptionMessage(DiagnosisMessage.COUNT++, presentTime, chatBotName, precautionQuestion[new Random().nextInt(precautionQuestion.length)], mBotImage, checkboxID, CHECKBOX_PRECAUTION_ID);
                    } else
                        mDiagnosisMessageModel.radioOptionMessage(DiagnosisMessage.COUNT++, presentTime, chatBotName, stateTravelHistory[new Random().nextInt(stateTravelHistory.length)], mBotImage, radioID);
                }
                break;
            case 5:
                // if Yes, user visited high risk states, contact NCDC ASAP!!!
                // If no, user didn't visit any state, contact PHC for proper diagnosis of ailment. give health tips. and recommend contacting closest hospital.
                if (buttonOption.toLowerCase().equals("yes")) {
                    mDiagnosisMessageModel.chatBotMessage(DiagnosisMessage.COUNT++, presentTime, chatBotName, ncdcContact[new Random().nextInt(ncdcContact.length)], mBotImage, botID);
                    mLaunchGoogleMap.setVisibility(View.VISIBLE);
                } else {
                    // retrieves "take another test" statements
                    String[] test = getResources().getStringArray(R.array.test);
                    mDiagnosisMessageModel.chatBotMessage(DiagnosisMessage.COUNT++, presentTime, chatBotName, phcContact[new Random().nextInt(phcContact.length)] + test[new Random().nextInt(test.length)], mBotImage, botID);
                    mLaunchGoogleMap.setVisibility(View.VISIBLE);
                }
                break;
            default:
                throw new IllegalStateException("Radio Unexpected value: " + DiagnosisMessageAdapter.RADIO_BUTTON_ID);
        }
        insertNewItem();
    }

    // onclick method for the send button for messages typed in the chat text box
    public void sendMessage(View view) {
        if (mLaunchGoogleMap.getVisibility() == View.VISIBLE)
            mLaunchGoogleMap.setVisibility(View.GONE);
        String messageTyped = enterMessageBox.getText().toString();
        if (messageTyped.trim().toLowerCase().equals("test")) {
            enterMessageBox.setText("");
            mDiagnosisMessageModel.userMessage(DiagnosisMessage.COUNT++, messageTyped.trim(), mDiagnosisMessageModel.getSimpleDateFormat().format(new Date()).toLowerCase(), mDiagnosisMessageModel.getUserID());
            // re-initialize the radioButtonId
            DiagnosisMessageAdapter.RADIO_BUTTON_ID = 0;
            DiagnosisMessage.COUNT = 0; // messageID counter
            mDiagnosisMessageModel.chatSequence();
            insertNewItem();
            // dismissKeyboard(view);
        } else
            Toast.makeText(this, "Type \"test\" to take a COVID-19 test", Toast.LENGTH_SHORT).show();
    }

    // adapter inserts a new item to the recyclerview
    // informs the adapter of the new item added
    private void insertNewItem() {
        // mDiagnosisMessages.size()-1 is the last element position
        mDiagnosisMessageAdapter.notifyItemInserted(mDiagnosisMessages.size() - 1);
        // update based on adapter
        mDiagnosisChatRecyclerView.scrollToPosition(mDiagnosisMessageAdapter.getItemCount() - 1);
    }

    // hide the keyboard when the user taps on an empty part of the  view
    public void dismissKeyboard(View view) {
        view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager methodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (methodManager != null)
                methodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void displayGoogleMap(View view) {
        final String GOOGLE_MAP = "https://www.google.com/maps/search/?api=1&query=hospital+clinic+health";

        // Create a Uri from an intent string. Use the result to create an Intent.
        Uri hospitalIntentUri = Uri.parse(GOOGLE_MAP);

        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, hospitalIntentUri);
        // Make the Intent explicit by setting the Google Maps package
        // mapIntent.setPackage("com.google.android.apps.maps");

        // Attempt to start an activity that can handle the Intent
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop: ");
        // mDiagnosisMessageAdapter.setOnItemClickListener(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy: ");
    }
}
