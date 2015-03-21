package com.worldventures.dreamtrips.modules.reptools.view.fragment;

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
import com.worldventures.dreamtrips.modules.reptools.presenter.SuccessStoryDetailsFragmentPM;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.StaticInfoFragment;

import butterknife.InjectView;

@Layout(R.layout.fragment_webview)
@MenuResource(R.menu.menu_success_stores)
public class SuccessStoriesDetailsFragment extends StaticInfoFragment<SuccessStoryDetailsFragmentPM> implements SuccessStoryDetailsFragmentPM.View {

    public static final String STORY = "STORY";
    @InjectView(R.id.progressBarWeb)
    ProgressBar progressBarWeb;
    private SuccessStory story;
    private MenuItem favoriteMenuItem;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        favoriteMenuItem = menu.findItem(R.id.action_like);
        favoriteMenuItem.setIcon(story.isLiked() ? R.drawable.ic_success_heart_selected :
                R.drawable.ic_success_heart_normal);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_like:
                getPresenter().like(story);
                break;
            case R.id.action_share:
                getPresenter().share();
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

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                view.loadUrl(view.getUrl());
            }

        });

    }

    @Override
    protected String getURL() {
        return story.getUrl() + "?appMode=true";
    }

    @Override
    protected SuccessStoryDetailsFragmentPM createPresenter(Bundle savedInstanceState) {
        return new SuccessStoryDetailsFragmentPM(this);
    }

    @Override
    public void showShareDialog() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
        builder.title("Share")
                .items(R.array.share_dialog_items)
                .itemsCallback((dialog, view, which, text) -> {
                    if (which == 0) {
                        getPresenter().onFbShare(story);
                    } else {
                        getPresenter().onTwitterShare(story);
                    }
                }).show();
    }

    @Override
    public void likeRequestSuccess() {
        boolean isLike = !story.isLiked();
        story.setLiked(isLike);
        favoriteMenuItem.setIcon(isLike ? R.drawable.ic_success_heart_selected : R.drawable.ic_success_heart_normal);
        if (isLike) {
            informUser(getString(R.string.ss_has_been_added_to_favorites));
        } else {
            informUser(getString(R.string.ss_has_been_removed_from_favorites));
        }
    }
}