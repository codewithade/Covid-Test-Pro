package com.smatworld.covidtest;

import android.app.Application;
import android.content.res.TypedArray;

import androidx.lifecycle.MutableLiveData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

class DiagnosisMessageRepository {

    private static final String TAG = "AppInfo";
    private final int userID = 1;
    private final int botID = 2;
    private final int radioID = 4;
    private final int checkboxID = 3;

    private String[] mAtHomeQuestion, mYesAtHome, mNoAtHome, mNoBelow, mYesAbove, mPoorPrecautionarySteps, mFairPrecautionarySteps, stateTravelHistory, keepTakingPrecaution, precautionQuestion, preliminary, takeTest, inform, nursingSickness, symptoms, priorIllness, prevSymptomsDuration, currentSymptomsDuration, travelHistory, ncdcContact, phcContact, retest;
    private SimpleDateFormat simpleDateFormat;
    private TypedArray profileImages;
    private String[] usernames;
    private String chatBotName;
    private int mBotImage;
    private MutableLiveData<ArrayList<DiagnosisMessage>> mDiagnosisMessageLiveData;
    private Application mApplication;
    private ArrayList<DiagnosisMessage> mDiagnosisMessages;

    DiagnosisMessageRepository(Application application) {
        this.mApplication = application;
        simpleDateFormat = new SimpleDateFormat("hh:mm", Locale.getDefault());
        mDiagnosisMessages = new ArrayList<>();
        mDiagnosisMessageLiveData = getDiagnosisMessageLiveData();
    }

    MutableLiveData<ArrayList<DiagnosisMessage>> getDiagnosisMessageLiveData() {
        if (mDiagnosisMessageLiveData == null) {
            mDiagnosisMessageLiveData = new MutableLiveData<>();
        }
        mDiagnosisMessageLiveData.setValue(mDiagnosisMessages);
        return mDiagnosisMessageLiveData;
    }

    String[] getAtHomeQuestion() {
        return mAtHomeQuestion;
    }

    String[] getYesAtHome() {
        return mYesAtHome;
    }

    String[] getNoAtHome() {
        return mNoAtHome;
    }

    String[] getNoBelow() {
        return mNoBelow;
    }

    String[] getYesAbove() {
        return mYesAbove;
    }

    void loadChatData() {
        mAtHomeQuestion = mApplication.getResources().getStringArray(R.array.r_at_home_question);
        mYesAtHome = mApplication.getResources().getStringArray(R.array.yes_at_home);
        mNoAtHome = mApplication.getResources().getStringArray(R.array.no_at_home);
        mNoBelow = mApplication.getResources().getStringArray(R.array.no_below_60);
        mYesAbove = mApplication.getResources().getStringArray(R.array.yes_above_60);

        mPoorPrecautionarySteps = mApplication.getResources().getStringArray(R.array.poor_precautionary_steps);
        mFairPrecautionarySteps = mApplication.getResources().getStringArray(R.array.fair_precautionary_steps);
        stateTravelHistory = mApplication.getResources().getStringArray(R.array.r_state_travel_statements);
        profileImages = mApplication.getResources().obtainTypedArray(R.array.profile_images);
        usernames = mApplication.getResources().getStringArray(R.array.usernames);
        keepTakingPrecaution = mApplication.getResources().getStringArray(R.array.keep_taking_precautionary_steps);
        precautionQuestion = mApplication.getResources().getStringArray(R.array.c_precautionary_measures_question);
        preliminary = mApplication.getResources().getStringArray(R.array.preliminary_statements);
        takeTest = mApplication.getResources().getStringArray(R.array.r_take_test_statements);
        inform = mApplication.getResources().getStringArray(R.array.info_statements);
        nursingSickness = mApplication.getResources().getStringArray(R.array.r_nursing_sickness);
        symptoms = mApplication.getResources().getStringArray(R.array.c_symptoms_statements);
        priorIllness = mApplication.getResources().getStringArray(R.array.r_prior_illness_statements);
        currentSymptomsDuration = mApplication.getResources().getStringArray(R.array.c_current_symptoms_duration);
        prevSymptomsDuration = mApplication.getResources().getStringArray(R.array.c_previous_symptoms_duration);
        travelHistory = mApplication.getResources().getStringArray(R.array.r_travel_statements);
        ncdcContact = mApplication.getResources().getStringArray(R.array.ncdc_contact_statements);
        phcContact = mApplication.getResources().getStringArray(R.array.phc_contact_statements);
        retest = mApplication.getResources().getStringArray(R.array.r_retest_statements);
    }

    void userMessage(int messageID, String message, String timeStamp, int ID) {
        mDiagnosisMessages.add(new DiagnosisMessage(messageID, message, timeStamp, ID));
    }

    String[] getPoorPrecautionarySteps() {
        return mPoorPrecautionarySteps;
    }

    String[] getFairPrecautionarySteps() {
        return mFairPrecautionarySteps;
    }

    // for normal chatbot messages
    void chatBotMessage(int messageID, String timeStamp, String userName, String message, int image, int ID) {
        mDiagnosisMessages.add(new DiagnosisMessage(messageID, ID, timeStamp, userName, message, image));
    }

    // for sending messages that require checkboxes or radio buttons
    void radioOptionMessage(int messageID, String timeStamp, String username, String question, int image, int ID) {
        mDiagnosisMessages.add(new DiagnosisMessage(messageID, timeStamp, username, question, image, ID));
    }

    // for sending messages that require checkboxes or radio buttons
    void checkboxOptionMessage(int messageID, String timeStamp, String username, String question, int image, int ID, int checkboxResID) {
        mDiagnosisMessages.add(new DiagnosisMessage(messageID, timeStamp, username, question, image, ID, checkboxResID));
    }

    void chatSequence() {
        chatBotName = getChatBot();
        mBotImage = profileImages.getResourceId(getBotImage(chatBotName), 0);
        String presentTime = simpleDateFormat.format(new Date()).toLowerCase();
        chatBotMessage(DiagnosisMessage.COUNT++, presentTime, chatBotName, preliminary[new Random().nextInt(preliminary.length)], mBotImage, botID);
        radioOptionMessage(DiagnosisMessage.COUNT++, presentTime, chatBotName, takeTest[new Random().nextInt(takeTest.length)], mBotImage, radioID);
        // insertNewItem();
    }

    // returns a random chat bot
    private String getChatBot() {
        return usernames[new Random().nextInt(usernames.length)];
    }

    // returns the array index of the botName in the usernames array
    private int getBotImage(String botName) {
        int result = -1;
        for (int i = 0; i < usernames.length; i++) if (usernames[i].equals(botName)) result = i;
        return result;
    }

    int getUserID() {
        return userID;
    }

    int getBotID() {
        return botID;
    }

    int getRadioID() {
        return radioID;
    }

    int getCheckboxID() {
        return checkboxID;
    }

    SimpleDateFormat getSimpleDateFormat() {
        return simpleDateFormat;
    }

    String getChatBotName() {
        return chatBotName;
    }

    int getBotImage() {
        return mBotImage;
    }

    String[] getStateTravelHistory() {
        return stateTravelHistory;
    }

    String[] getKeepTakingPrecaution() {
        return keepTakingPrecaution;
    }

    String[] getPrecautionQuestion() {
        return precautionQuestion;
    }

    String[] getPreliminary() {
        return preliminary;
    }

    String[] getTakeTest() {
        return takeTest;
    }

    String[] getInform() {
        return inform;
    }

    String[] getNursingSickness() {
        return nursingSickness;
    }

    String[] getSymptoms() {
        return symptoms;
    }

    String[] getPriorIllness() {
        return priorIllness;
    }

    String[] getPrevSymptomsDuration() {
        return prevSymptomsDuration;
    }

    String[] getCurrentSymptomsDuration() {
        return currentSymptomsDuration;
    }

    String[] getTravelHistory() {
        return travelHistory;
    }

    String[] getNcdcContact() {
        return ncdcContact;
    }

    String[] getPhcContact() {
        return phcContact;
    }

    String[] getRetest() {
        return retest;
    }

    TypedArray getProfileImages() {
        return profileImages;
    }

    String[] getUsernames() {
        return usernames;
    }
}
