package com.smatworld.covidtest;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class StatisticsRepository {

    private MutableLiveData<List<StateStatistics>> mLocalStatistics;
    private MutableLiveData<Map<String, Object>> mOtherStatistics;
    private final FirebaseFirestore mDb = FirebaseFirestore.getInstance();

    StatisticsRepository() {
        mLocalStatistics = getLocalStatistics();
        mOtherStatistics = getOtherStatistics();
    }

    MutableLiveData<List<StateStatistics>> getLocalStatistics() {
        if (mLocalStatistics == null) {
            mLocalStatistics = new MutableLiveData<>();
            mLocalStatistics.setValue(getLocalData());
        }
        return mLocalStatistics;
    }

    // I doubt if it will update. 21:25 29/04/2020
    MutableLiveData<Map<String, Object>> getOtherStatistics() {
        if (mOtherStatistics == null) {
            mOtherStatistics = new MutableLiveData<>();
            getOtherData();
        }
        return mOtherStatistics;
    }

    private ArrayList<StateStatistics> getLocalData() {
        final ArrayList<StateStatistics> list = new ArrayList<>();
        CollectionReference collectionReference = mDb.collection("local-statistics");

        collectionReference.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            list.clear();
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                StateStatistics stateStatistics = documentSnapshot.toObject(StateStatistics.class);
                                // Log.i("AppInfo", stateStatistics.toString());
                                list.add(stateStatistics);
                            }
                        } else
                            Log.i("AppInfo", "Error getting document: ", task.getException());
                    }
                });
        return list;
    }

    private void getOtherData() {
        final Map<String, Object> otherStatistics = new HashMap<>();
        final CollectionReference collectionReference = mDb.collection("total-statistics");

        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        Map<String, Object> map = documentSnapshot.getData();
                        Log.i("AppInfo", documentSnapshot.getId() + " => " + map);
                        if (documentSnapshot.getId().equals("global-doc")) {
                            otherStatistics.put("globalConfirmedCases", map.get("globalConfirmedCases"));
                            otherStatistics.put("countriesAffected", map.get("countriesAffected"));
                            otherStatistics.put("globalConfirmedDeaths", map.get("globalConfirmedDeaths"));
                            otherStatistics.put("timeStamp", map.get("timeStamp"));

                        } else if (documentSnapshot.getId().equals("local-doc")) {
                            otherStatistics.put("localCasesConfirmed", map.get("localCasesConfirmed"));
                            otherStatistics.put("localCasesDischarged", map.get("localCasesDischarged"));
                            otherStatistics.put("localActiveCases", map.get("localActiveCases"));
                            otherStatistics.put("localDeaths", map.get("localDeaths"));
                            otherStatistics.put("statesAffected", map.get("statesAffected"));
                            otherStatistics.put("localSamplesTested", map.get("localSamplesTested"));

                        } else if (documentSnapshot.getId().equals("africa-doc")) {
                            otherStatistics.put("africaCasesConfirmed", map.get("africaCasesConfirmed"));
                            otherStatistics.put("africaDeaths", map.get("africaDeaths"));
                            otherStatistics.put("africaRecoveries", map.get("africaRecoveries"));
                        }
                    }
                    mOtherStatistics.setValue(otherStatistics);
                } else
                    Log.i("AppInfo", "Task not successful: " + task.getException().getLocalizedMessage());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("AppInfo", "Failed to update. No internet: " + e.getLocalizedMessage());
            }
        });
    }
}
