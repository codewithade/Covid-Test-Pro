package com.smatworld.covidtest;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class WebviewActivity extends AppCompatActivity {

    private static final String TAG = "AppInfo";
    private WebView webView;
    String URL;
    private LinearLayout mErrorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        URL = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");
        setTitle(title);
        mErrorLayout = findViewById(R.id.activity_error_layout);
        webView = findViewById(R.id.webview_activity);
        final WebViewSetup webviewSetup = new WebViewSetup(webView, URL, this, mErrorLayout);
        webviewSetup.init();

        Button retryButton = findViewById(R.id.activity_error_retry);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: original" + WebViewSetup.mOriginalURL);
                if (WebViewSetup.mOriginalURL != null)
                    URL = WebViewSetup.mOriginalURL;
                webviewSetup.checkConnectivity(URL);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_webview_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_refresh) {
            if (WebViewSetup.mOriginalURL != null)
                URL = WebViewSetup.mOriginalURL;
            new WebViewSetup(webView, URL, this, mErrorLayout).init();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
