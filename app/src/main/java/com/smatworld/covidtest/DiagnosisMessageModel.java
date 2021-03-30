package com.smatworld.covidtest;

import android.app.Application;
import android.content.res.TypedArray;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DiagnosisMessageModel extends AndroidViewModel {

    private MutableLiveData<ArrayList<DiagnosisMessage>> mDiagnosisMessageLiveData;
    private DiagnosisMessageRepository mMessageRepository;
    public static final String TAG = "AppInfo";

    boolean mIsNewlyCreated = true;


    public DiagnosisMessageModel(@NonNull Application application) {
        super(application);
        mMessageRepository = new DiagnosisMessageRepository(application);
        mDiagnosisMessageLiveData = mMessageRepository.getDiagnosisMessageLiveData();
    }

    LiveData<ArrayList<DiagnosisMessage>> getDiagnosisMessageLiveData() {
        return mDiagnosisMessageLiveData;
    }

    void chatBotMessage(int messageID, String timeStamp, String userName, String message, int image, int ID) {
        mMessageRepository.chatBotMessage(messageID, timeStamp, userName, message, image, ID);
    }

    void radioOptionMessage(int messageID, String timeStamp, String userName, String question, int image, int ID) {
        mMessageRepository.radioOptionMessage(messageID, timeStamp, userName, question, image, ID);
    }

    void checkboxOptionMessage(int messageID, String timeStamp, String userName, String question, int image, int ID, int checkboxResID) {
        mMessageRepository.checkboxOptionMessage(messageID, timeStamp, userName, question, image, ID, checkboxResID);
    }


    void loadChatData() {
        mMessageRepository.loadChatData();
    }

    void chatSequence() {
        mMessageRepository.chatSequence();
    }

    void userMessage(int messageID, String message, String timeStamp, int ID) {
        mMessageRepository.userMessage(messageID, message, timeStamp, ID);
    }

    int getUserID() {
        return mMessageRepository.getUserID();
    }

    int getBotID() {
        return mMessageRepository.getBotID();
    }

    int getRadioID() {
        return mMessageRepository.getRadioID();
    }

    int getCheckboxID() {
        return mMessageRepository.getCheckboxID();
    }

    SimpleDateFormat getSimpleDateFormat() {
        return mMessageRepository.getSimpleDateFormat();
    }

    String getChatBotName() {
        return mMessageRepository.getChatBotName();
    }

    int getBotImage() {
        return mMessageRepository.getBotImage();
    }

    String[] getAtHomeQuestion() {
        return mMessageRepository.getAtHomeQuestion();
    }

    String[] getYesAtHome() {
        return mMessageRepository.getYesAtHome();
    }

    String[] getNoAtHome() {
        return mMessageRepository.getNoAtHome();
    }

    String[] getNoBelow() {
        return mMessageRepository.getNoBelow();
    }

    String[] getYesAbove() {
        return mMessageRepository.getYesAbove();
    }

    String[] getPoorPrecautionarySteps() {
        return mMessageRepository.getPoorPrecautionarySteps();
    }

    String[] getFairPrecautionarySteps() {
        return mMessageRepository.getFairPrecautionarySteps();
    }

    String[] getStateTravelHistory() {
        return mMessageRepository.getStateTravelHistory();
    }

    String[] getKeepTakingPrecaution() {
        return mMessageRepository.getKeepTakingPrecaution();
    }

    String[] getPrecautionQuestion() {
        return mMessageRepository.getPrecautionQuestion();
    }

    String[] getPreliminary() {
        return mMessageRepository.getPreliminary();
    }

    String[] getTakeTest() {
        return mMessageRepository.getTakeTest();
    }

    String[] getInform() {
        return mMessageRepository.getInform();
    }

    String[] getNursingSickness() {
        return mMessageRepository.getNursingSickness();
    }

    String[] getSymptoms() {
        return mMessageRepository.getSymptoms();
    }

    String[] getPriorIllness() {
        return mMessageRepository.getPriorIllness();
    }

    String[] getPrevSymptomsDuration() {
        return mMessageRepository.getPrevSymptomsDuration();
    }

    String[] getCurrentSymptomsDuration() {
        return mMessageRepository.getCurrentSymptomsDuration();
    }

    String[] getTravelHistory() {
        return mMessageRepository.getTravelHistory();
    }

    String[] getNcdcContact() {
        return mMessageRepository.getNcdcContact();
    }

    String[] getPhcContact() {
        return mMessageRepository.getPhcContact();
    }

    String[] getRetest() {
        return mMessageRepository.getRetest();
    }

    TypedArray getProfileImages() {
        return mMessageRepository.getProfileImages();
    }

    String[] getUserNames() {
        return mMessageRepository.getUsernames();
    }

}
