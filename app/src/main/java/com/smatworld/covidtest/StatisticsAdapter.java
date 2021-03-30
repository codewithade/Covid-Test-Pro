package com.smatworld.covidtest;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StatisticsAdapter extends RecyclerView.Adapter<StatisticsAdapter.StatisticsViewHolder> {

    private static final String TAG = "AppInfo";
    private Context mContext;
    private List<StateStatistics> mStatisticsList;

    StatisticsAdapter(Context context) {
        this.mContext = context;
    }

    @NonNull
    @Override
    public StatisticsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new StatisticsViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_statistics, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StatisticsViewHolder holder, int position) {
        if (mStatisticsList != null) holder.bind(mStatisticsList.get(position));
        else Log.i(TAG, "onBindViewHolder: List is null");
    }

    @Override
    public int getItemCount() {
        if (mStatisticsList != null)
            return mStatisticsList.size();
        else return 0;
    }

    void setStatisticsList(List<StateStatistics> statisticsList) {
        mStatisticsList = statisticsList;
        notifyDataSetChanged();
    }

    static class StatisticsViewHolder extends RecyclerView.ViewHolder {

        TextView stateName, activeCases, casesConfirmed, numberDischarged, numberOfDeaths;

        StatisticsViewHolder(@NonNull View itemView) {
            super(itemView);
            stateName = itemView.findViewById(R.id.state);
            casesConfirmed = itemView.findViewById(R.id.value_1);
            activeCases = itemView.findViewById(R.id.value_2);
            numberDischarged = itemView.findViewById(R.id.value_3);
            numberOfDeaths = itemView.findViewById(R.id.value_4);
        }

        void bind(StateStatistics stateStatistics) {
            stateName.setText(stateStatistics.getStateName());
            casesConfirmed.setText(String.valueOf(stateStatistics.getCasesConfirmed()));
            activeCases.setText(String.valueOf(stateStatistics.getActiveCases()));
            numberDischarged.setText(String.valueOf(stateStatistics.getNumberDischarged()));
            numberOfDeaths.setText(String.valueOf(stateStatistics.getNumberOfDeaths()));
        }
    }
}
