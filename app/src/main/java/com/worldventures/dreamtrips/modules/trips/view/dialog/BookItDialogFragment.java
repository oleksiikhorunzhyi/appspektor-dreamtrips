package com.worldventures.dreamtrips.modules.trips.view.dialog;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.trips.presenter.BookItDialogPresenter;
import com.worldventures.dreamtrips.modules.common.view.dialog.BaseDialogFragment;

import butterknife.InjectView;

/**
 * Created by Edward on 29.01.15.
 */
@Layout(R.layout.fragment_webview)
public class BookItDialogFragment extends BaseDialogFragment<BookItDialogPresenter> implements BookItDialogPresenter.View {

    public static final String EXTRA_TRIP_ID = "TRIP_ID";

    @InjectView(R.id.progressBarWeb)
    ProgressBar progressBarWeb;
    @InjectView(R.id.web_view)
    WebView webView;


    @Override
    public void afterCreateView(View rootView) {
        super.afterCreateView(rootView);
        getPresentationModel().setTripId(getArguments().getInt(EXTRA_TRIP_ID));

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBarWeb.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBarWeb.setVisibility(View.GONE);
            }

        });
        webView.loadUrl(getPresentationModel().getUrl());
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    protected BookItDialogPresenter createPresentationModel(Bundle savedInstanceState) {
        return new BookItDialogPresenter(this);
    }
}
