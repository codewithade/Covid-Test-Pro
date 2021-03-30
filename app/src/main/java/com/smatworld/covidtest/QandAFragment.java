package com.smatworld.covidtest;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class QandAFragment extends Fragment {

    private static final String TAG = "AppInfo";
    private String URL = "https://www.who.int/news-room/q-a-detail/q-a-coronaviruses";
    private WebView mWebview;
    private Context mContext;
    private LinearLayout mErrorLayout;

    public QandAFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_qand_a, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mWebview = view.findViewById(R.id.webview);
        mErrorLayout = view.findViewById(R.id.error_layout);
        final WebViewSetup setup = new WebViewSetup(mWebview, URL, mContext, mErrorLayout);
        setup.init();

        Button retryButton = view.findViewById(R.id.retry_button);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: original" + WebViewSetup.mOriginalURL);
                if (WebViewSetup.mOriginalURL != null)
                    URL = WebViewSetup.mOriginalURL;
                setup.checkConnectivity(URL);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_webview_activity, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_refresh) {
            if (WebViewSetup.mOriginalURL != null)
                URL = WebViewSetup.mOriginalURL;
            new WebViewSetup(mWebview, URL, mContext, mErrorLayout).init();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
