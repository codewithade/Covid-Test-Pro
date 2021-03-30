package com.smatworld.covidtest;

import androidx.annotation.NonNull;

// Data model for statistics of covid-19 cases per state
class StateStatistics {

    private String stateName;
    private int casesConfirmed, activeCases, numberDischarged, numberOfDeaths;

    public StateStatistics() {
    }

    StateStatistics(String stateName, int casesConfirmed, int activeCases, int numberDischarged, int numberOfDeaths) {
        this.stateName = stateName;
        this.casesConfirmed = casesConfirmed;
        this.activeCases = activeCases;
        this.numberDischarged = numberDischarged;
        this.numberOfDeaths = numberOfDeaths;
    }

    StateStatistics(String stateName, int activeCases, int numberDischarged, int numberOfDeaths) {
        this.stateName = stateName;
        this.activeCases = activeCases;
        this.numberDischarged = numberDischarged;
        this.numberOfDeaths = numberOfDeaths;
    }

    public String getStateName() {
        return stateName;
    }

    public int getCasesConfirmed() {
        return casesConfirmed;
    }

    public int getActiveCases() {
        return activeCases;
    }

    public int getNumberDischarged() {
        return numberDischarged;
    }

    public int getNumberOfDeaths() {
        return numberOfDeaths;
    }

    @NonNull
    @Override
    public String toString() {
        return "{ " + getStateName() + ", " + getCasesConfirmed() + ", " + getActiveCases() + ", " + getNumberDischarged() + ", " + getNumberOfDeaths() + " }";
    }
}
