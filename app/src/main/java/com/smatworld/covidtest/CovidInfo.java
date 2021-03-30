package com.smatworld.covidtest;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class CovidInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid_info);

        // Enable the Up button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        int fragmentID = getIntent().getIntExtra("fragmentID", -1);
        Fragment fragment;
        switch (fragmentID){
            /*case 1:
                fragment = new StatisticsFragment();
                fragment = new StatisticsMainFragment();
                setTitle(getResources().getString(R.string.app_name_stats));
                break;*/
            case 3:
                fragment = new QandAFragment();
                setTitle(getResources().getString(R.string.app_name_qna));
                break;
            case 4:
                fragment = new ExternalLinkFragment();
                setTitle(getResources().getString(R.string.app_name_links));
                break;
            case 5:
                fragment = new PreventionFragment();
                setTitle(getResources().getString(R.string.app_name_prevention));
                break;
            case 6:
                fragment = new ContactFragment();
                setTitle(getResources().getString(R.string.app_name_contact));
                break;
            default:
                fragment = null;
        }
        if(fragment != null) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}
