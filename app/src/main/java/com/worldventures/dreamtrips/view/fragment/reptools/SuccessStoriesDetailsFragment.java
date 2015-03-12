package com.worldventures.dreamtrips.view.fragment.reptools;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.SuccessStory;
import com.worldventures.dreamtrips.presentation.SuccessStoryDetailsFragmentPM;
import com.worldventures.dreamtrips.view.fragment.StaticInfoFragment;

import butterknife.InjectView;

@Layout(R.layout.fragment_webview)
@MenuResource(R.menu.menu_success_stores)
public class SuccessStoriesDetailsFragment extends StaticInfoFragment<SuccessStoryDetailsFragmentPM> implements SuccessStoryDetailsFragmentPM.View {


    @InjectView(R.id.progressBarWeb)
    ProgressBar progressBarWeb;
    public static final String STORY = "STORY";
    private SuccessStory story;
    private MenuItem item;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        item = menu.findItem(R.id.action_like);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_like:
                getPresentationModel().like(story);
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
        ((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(story.getAuthor());
        super.afterCreateView(rootView);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setUseWideViewPort(true);

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
    protected SuccessStoryDetailsFragmentPM createPresentationModel(Bundle savedInstanceState) {
        return new SuccessStoryDetailsFragmentPM(this);
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

    @Override
    public void likeRequestSuccess() {
        boolean isLike = !story.isLiked();
        story.setLiked(isLike);
        item.setIcon(isLike ? R.drawable.ic_success_heart_normal : R.drawable.ic_success_heart_selected);
        informUser("Success story has been added to favorites");
    }
}