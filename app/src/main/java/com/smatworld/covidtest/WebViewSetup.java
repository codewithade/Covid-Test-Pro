package com.smatworld.covidtest;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Random;
import java.util.concurrent.ExecutionException;

class WebViewSetup {

    private static final String TAG = "AppInfo";
    private WebView mWebView;
    private String URL;
    private Context mContext;
    private LinearLayout mErrorLayout;
    private String mFailingURL;
    static String mOriginalURL;

    private ProgressDialog progressDialog;
    private static String[] errorList;

    WebViewSetup(WebView webView, String URL, Context context, LinearLayout errorLayout) {
        this.mWebView = webView;
        errorList = context.getResources().getStringArray(R.array.error_list);
        this.URL = URL;
        mFailingURL = URL;
        this.mContext = context;
        this.mErrorLayout = errorLayout;

        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Loading Page...");

        final MyWebChromeClient chromeClient = new MyWebChromeClient(mErrorLayout);
        webView.setWebChromeClient(chromeClient);
        webView.setWebViewClient(new MyWebviewClient(mContext, mErrorLayout));

        progressDialog.setCancelable(true);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Log.i(TAG, "onCancel: ");
                progressDialog = null;
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    void init() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        String path = mContext.getApplicationContext().getFilesDir().getAbsolutePath() + "/cache";
        webSettings.setAppCachePath(path);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        checkConnectivity(URL);
    }

    void checkConnectivity(String URL) {
        boolean hasInternetConn = checkInternetTask();
        if (!hasInternetConn) {
            Log.i(TAG, "checkConnectivity: No internet connection");
            if (progressDialog != null)
                progressDialog.dismiss();
            mErrorLayout.setVisibility(View.VISIBLE);
            mWebView.setVisibility(View.INVISIBLE);
            Snackbar.make(mWebView, errorList[new Random().nextInt(errorList.length)], Snackbar.LENGTH_SHORT).show();
        } else {
            Log.i(TAG, "checkConnectivity: There is internet connection");
            if (progressDialog != null)
                progressDialog.show();
            mErrorLayout.setVisibility(View.INVISIBLE);
            mWebView.loadUrl(URL);
        }
    }

    private class MyWebviewClient extends WebViewClient {
        Context context;
        LinearLayout errorLayout;

        MyWebviewClient(Context context, LinearLayout layout) {
            this.context = context;
            this.errorLayout = layout;
        }

        // backward compatibility for API less than LOLLIPOP
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.i(TAG, "shouldOverrideUrlLoading: deprecated: " + url);
            return false;
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            Log.i(TAG, "shouldOverrideUrlLoading: " + request.getUrl().toString());
            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public void onReceivedError(final WebView webView, final int errorCode, final String description, final String failingUrl) {
            Log.i("AppInfo", "Deprecated onReceivedError Code: " + errorCode);
            Log.i("AppInfo", "Deprecated onReceivedError Desc: " + description);

            mFailingURL = failingUrl;
            mOriginalURL = webView.getOriginalUrl();
            Log.i("AppInfo", "Deprecated onReceivedError FailingURL: " + mFailingURL);
            // resets the webView and release resources making the webView blank
            webView.loadUrl("about:blank");
            Snackbar.make(webView, errorList[new Random().nextInt(errorList.length)], Snackbar.LENGTH_SHORT).show();
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(final WebView webView, final WebResourceRequest request, final WebResourceError error) {
            String failingUrl = request.getUrl().toString();

            Log.i("AppInfo", "onReceivedError description: " + error.getDescription());
            Log.i(TAG, "onReceivedError: Failing URL: " + failingUrl);

            mFailingURL = failingUrl;
            mOriginalURL = webView.getOriginalUrl();
            // resets the webView and release resources making the webView blank
            webView.loadUrl("about:blank");
            Snackbar.make(webView, errorList[new Random().nextInt(errorList.length)], Snackbar.LENGTH_SHORT).show();
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        LinearLayout mLayout;

        MyWebChromeClient(LinearLayout layout) {
            mLayout = layout;
        }

        @Override
        public void onProgressChanged(WebView webView, int progress) {
            if (progress < 100 && progressDialog != null) {
                progressDialog.show();
                webView.setVisibility(View.INVISIBLE);
            }
            if (progress == 100) {
                if (progressDialog != null)
                    progressDialog.dismiss();
                mLayout.setVisibility(View.INVISIBLE);
                webView.setVisibility(View.VISIBLE);
            }
        }
    }

    private static boolean checkInternetTask() {
        try {
            return new InternetAccessAsyncTask().execute().get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static class InternetAccessAsyncTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected void onPostExecute(Boolean internet) {
            super.onPostExecute(internet);
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                int timeoutMs = 1500;
                Socket sock = new Socket();
                SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);
                sock.connect(sockaddr, timeoutMs);
                sock.close();
                return true;
            } catch (IOException e) {
                return false;
            }
        }
    }
}
