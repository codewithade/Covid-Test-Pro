package com.smatworld.covidtest;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.ListenerRegistration;

import java.util.List;
import java.util.Map;

public class StatisticsViewModel extends AndroidViewModel {

    private MutableLiveData<List<StateStatistics>> mLocalStatistics;
    private MutableLiveData<Map<String, Object>> mOtherStatistics;

    public StatisticsViewModel(@NonNull Application application) {
        super(application);
        StatisticsRepository statisticsRepository = new StatisticsRepository();
        mLocalStatistics = statisticsRepository.getLocalStatistics();
        mOtherStatistics = statisticsRepository.getOtherStatistics();
    }

    LiveData<List<StateStatistics>> getLocalStatistics() {
        return mLocalStatistics;
    }

    LiveData<Map<String, Object>> getOtherStatistics() {
        return mOtherStatistics;
    }
}
