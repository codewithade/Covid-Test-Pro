package com.smatworld.covidtest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MainMenu {

    private int mImageResource;
    private int mId;
    private String mMainTitle;
    private String mDetailTitle;
    private int mUsageCount;

    MainMenu(int imageResource, int id, String mainTitle, String detailTitle, int usageCount) {
        mImageResource = imageResource;
        mId = id;
        mMainTitle = mainTitle;
        mDetailTitle = detailTitle;
        this.mUsageCount = usageCount;
    }

    String getDetailTitle() {
        return mDetailTitle;
    }

    int getImageResource() {
        return mImageResource;
    }

    int getId() {
        return mId;
    }

    String getMainTitle() {
        return mMainTitle;
    }

    int getUsageCount() {
        return mUsageCount;
    }

    @NonNull
    @Override
    public String toString() {
        return "{ ID => " + getId() + " Title => " + getMainTitle() + " Details => " + getDetailTitle() + " ImageID => " + getImageResource() + " }";
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof MainMenu))
            return false;
        MainMenu menu = (MainMenu) obj;
        return menu.getImageResource() == mImageResource
                && menu.getMainTitle().equals(mMainTitle)
                && menu.getDetailTitle().equals(mDetailTitle);
    }
}
