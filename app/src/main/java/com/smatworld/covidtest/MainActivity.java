package com.smatworld.covidtest;

import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<MainMenu> mMainMenuList;
    static final String PREF_KEY = "com.smatworld.covidtest.PREF_KEY";
    static final String MENU1_KEY = "com.smatworld.covidtest.MENU1_KEY";
    static final String MENU2_KEY = "com.smatworld.covidtest.MENU2_KEY";
    static final String MENU3_KEY = "com.smatworld.covidtest.MENU3_KEY";
    static final String MENU4_KEY = "com.smatworld.covidtest.MENU4KEY";
    static final String MENU5_KEY = "com.smatworld.covidtest.MENU5_KEY";
    static final String MENU6_KEY = "com.smatworld.covidtest.MENU6_KEY";
    static final String MENU7_KEY = "com.smatworld.covidtest.MENU7_KEY";
    static final String TOTAL_USAGE_COUNT_KEY = "com.smatworld.covidtest.TOTAL_USAGE_COUNT_KEY";
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(R.string.app_name_main_page);
        mSharedPreferences = getSharedPreferences(PREF_KEY, MODE_PRIVATE);

        //The Android runtime will take care of deciding which integers.xml file to use, depending on the state of the device.
        int gridColumnCount = getResources().getInteger(R.integer.grid_column_count);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, gridColumnCount));

        mMainMenuList = new ArrayList<>();
        MainMenuAdapter menuAdapter = new MainMenuAdapter(this);
        menuAdapter.submitList(mMainMenuList);

        recyclerView.setAdapter(menuAdapter);
        recyclerView.hasFixedSize();
        init();
    }

    private int getMenuPercent(float countValue){
        int totalUsageCount = mSharedPreferences.getInt(TOTAL_USAGE_COUNT_KEY, -1);
        return totalUsageCount != 0 ? (Math.round((countValue/totalUsageCount) * 100)) : 0;
    }

    // populate recyclerview with data
    private void init() {
        final String[] mainCaptions = getResources().getStringArray(R.array.main_title);
        final String[] detailsText = getResources().getStringArray(R.array.title_comments);
        final int[] ID = getResources().getIntArray(R.array.menu_ID);
        final TypedArray mainImages = getResources().obtainTypedArray(R.array.main_menu_images);


        final int[] menuUsageStatistics = new int[mainCaptions.length];

        menuUsageStatistics[0] = getMenuPercent(mSharedPreferences.getInt(MENU1_KEY, 0));
        menuUsageStatistics[1] = getMenuPercent(mSharedPreferences.getInt(MENU2_KEY, 0));
        menuUsageStatistics[2] = getMenuPercent(mSharedPreferences.getInt(MENU3_KEY, 0));
        menuUsageStatistics[3] = getMenuPercent(mSharedPreferences.getInt(MENU4_KEY, 0));
        menuUsageStatistics[4] = getMenuPercent(mSharedPreferences.getInt(MENU5_KEY, 0));
        menuUsageStatistics[5] = getMenuPercent(mSharedPreferences.getInt(MENU6_KEY, 0));
        menuUsageStatistics[6] = getMenuPercent(mSharedPreferences.getInt(MENU7_KEY, 0));

        for (int i = 0; i < ID.length; i++)
            mMainMenuList.add(new MainMenu(mainImages.getResourceId(i, -1), ID[i], mainCaptions[i], detailsText[i], menuUsageStatistics[i]));

        mainImages.recycle();
    }
}
