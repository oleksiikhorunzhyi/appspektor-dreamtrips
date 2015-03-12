package com.worldventures.dreamtrips.view.fragment.reptools;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.SuccessStory;
import com.worldventures.dreamtrips.presentation.SuccessStoryDetailsPM;
import com.worldventures.dreamtrips.view.activity.MainActivity;
import com.worldventures.dreamtrips.view.fragment.StaticInfoFragment;

import butterknife.InjectView;

@Layout(R.layout.fragment_webview)
@MenuResource(R.menu.menu_success_stores)
public class SuccessStoresDetails extends StaticInfoFragment<SuccessStoryDetailsPM> implements SuccessStoryDetailsPM.View {



    @InjectView(R.id.progressBarWeb)
    ProgressBar progressBarWeb;
    public static final String STORY = "STORY";
    private SuccessStory story;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_like:
                getPresentationModel().like();
                break;
            case R.id.action_share:
                getPresentationModel().share();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void afterCreateView(View rootView) {
        story = getArguments().getParcelable(STORY);
        ((MainActivity) getActivity()).setToolBarTitle(story.getAuthor());
        super.afterCreateView(rootView);

        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAppCachePath("/data/data/com.worldventures.dreamtrips/cache");
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.setWebViewClient(new WebViewClient() {

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

    }

    @Override
    protected String getURL() {
        return story.getUrl();
    }

    @Override
    protected SuccessStoryDetailsPM createPresentationModel(Bundle savedInstanceState) {
        return new SuccessStoryDetailsPM(this);
    }

    @Override
    public void showShareDialog() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
        builder.title("Share")
                .items(R.array.share_dialog_items)
                .itemsCallback((dialog, view, which, text) -> {
                    if (which == 0) {
                        getPresentationModel().onFbShare(story);
                    } else {
                        getPresentationModel().onTwitterShare(story);
                    }
                }).show();
    }
}