package com.worldventures.dreamtrips.modules.reptools.view.fragment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.StaticInfoFragment;
import com.worldventures.dreamtrips.modules.reptools.model.SuccessStory;
import com.worldventures.dreamtrips.modules.reptools.presenter.SuccessStoryDetailsFragmentPresenter;
import com.worldventures.dreamtrips.modules.reptools.view.activity.SuccessStoryDetailsActivity;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_success_stories_details)
public class SuccessStoriesDetailsFragment extends StaticInfoFragment<SuccessStoryDetailsFragmentPresenter>
        implements SuccessStoryDetailsFragmentPresenter.View {

    public static final String EXTRA_STORY = "STORY";

    private SuccessStory story;

    @InjectView(R.id.progressBarWeb)
    protected ProgressBar progressBarWeb;
    @InjectView(R.id.iv_share)
    protected ImageView ivShare;
    @InjectView(R.id.iv_like)
    protected ImageView ivLike;
    @InjectView(R.id.iv_full_screen)
    protected ImageView ivFullscreen;

    @OnClick(R.id.iv_like)
    public void onLike() {
        getPresenter().like(story);
    }

    @OnClick(R.id.iv_share)
    public void onShare() {
        getPresenter().share();
    }

    @OnClick(R.id.iv_full_screen)
    public void onFullScreen() {
        if (getActivity() instanceof SuccessStoryDetailsActivity) {
            getActivity().finish();
        } else {
            getPresenter().fullscreenEvent(story);
        }
    }

    @Override
    public void afterCreateView(View rootView) {
        if (!ViewUtils.isTablet(getActivity())) {
            ivFullscreen.setVisibility(View.GONE);
        }
        super.afterCreateView(rootView);
        if (story.isLiked()) {
            ivLike.setImageResource(R.drawable.ic_success_heart_selected);
        } else {
            ivLike.setImageResource(R.drawable.ic_success_heart_normal);
        }

        if (getActivity() instanceof SuccessStoryDetailsActivity) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(story.getAuthor());
            ivFullscreen.setImageResource(R.drawable.ic_fullscreen_collapse);
        } else {
            ivFullscreen.setImageResource(R.drawable.ic_fullscreen_open);
        }
        webView.getSettings().setUseWideViewPort(true);
    }

    @Override
    protected String getURL() {
        return story != null ? story.getUrl() : "";
    }

    @Override
    protected SuccessStoryDetailsFragmentPresenter createPresenter(Bundle savedInstanceState) {
        story = getArguments().getParcelable(EXTRA_STORY);
        return new SuccessStoryDetailsFragmentPresenter(getURL());
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
        ivLike.setImageResource(isLike ? R.drawable.ic_success_heart_selected : R.drawable.ic_success_heart_normal);
        if (isLike) {
            informUser(getString(R.string.ss_has_been_added_to_favorites));
        } else {
            informUser(getString(R.string.ss_has_been_removed_from_favorites));
        }
    }
}