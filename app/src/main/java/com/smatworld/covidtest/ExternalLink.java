package com.smatworld.covidtest;

import androidx.annotation.Nullable;

class ExternalLink {
    private String URL;
    private String linkTitle;
    private int linkIcon;
    static int COUNT = 0;
    private int id;

    ExternalLink(String URL, String linkTitle, int linkIcon) {
        this.URL = URL;
        this.linkTitle = linkTitle;
        this.linkIcon = linkIcon;
        this.id = ExternalLink.COUNT++;
    }

    String getURL() {
        return URL;
    }

    String getLinkTitle() {
        return linkTitle;
    }

    int getLinkIcon() {
        return linkIcon;
    }

    int getId() {
        return id;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof ExternalLink)) // returns true if obj is not instance of ExternalLink model class
            return false;
        ExternalLink externalLink = (ExternalLink)obj;
        return externalLink.URL.equals(URL)
                && externalLink.linkIcon == linkIcon
                && externalLink.linkTitle.equals(linkTitle);
    }
}
