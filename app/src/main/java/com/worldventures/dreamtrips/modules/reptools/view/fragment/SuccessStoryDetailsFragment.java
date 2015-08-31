package com.worldventures.dreamtrips.modules.reptools.view.fragment;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.navigation.NavigationBuilder;
import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.utils.ViewUtils;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.StaticInfoFragment;
import com.worldventures.dreamtrips.modules.reptools.model.SuccessStory;
import com.worldventures.dreamtrips.modules.reptools.presenter.SuccessStoryDetailsPresenter;

import butterknife.InjectView;
import butterknife.OnClick;

@Layout(R.layout.fragment_success_stories_details)
public class SuccessStoryDetailsFragment extends StaticInfoFragment<SuccessStoryDetailsPresenter>
        implements SuccessStoryDetailsPresenter.View {

    public static final String EXTRA_STORY = "STORY";
    public static final String EXTRA_SLAVE = "SLAVE";

    private SuccessStory story;

    @InjectView(R.id.iv_share)
    protected ImageView ivShare;
    @InjectView(R.id.iv_like)
    protected ImageView ivLike;
    @InjectView(R.id.iv_full_screen)
    protected ImageView ivFullscreen;

    private boolean slave = false;

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
        if (!slave) {
            getActivity().finish();
        } else {
            Bundle bundle = new Bundle();
            bundle.putParcelable(SuccessStoryDetailsFragment.EXTRA_STORY, story);
            NavigationBuilder.create().with(activityRouter).args(bundle).move(Route.SUCCESS_STORES_DETAILS);
        }
    }

    @Override
    public void afterCreateView(View rootView) {
        slave = getArguments().getBoolean(EXTRA_SLAVE);

        if (!ViewUtils.isTablet(getActivity())) {
            ivFullscreen.setVisibility(View.GONE);
        }
        super.afterCreateView(rootView);
        if (story.isLiked()) {
            ivLike.setImageResource(R.drawable.ic_success_heart_selected);
        } else {
            ivLike.setImageResource(R.drawable.ic_success_heart_normal);
        }

        webView.getSettings().setUseWideViewPort(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!slave) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(story.getAuthor());
            ivFullscreen.setImageResource(R.drawable.ic_fullscreen_collapse);

            ivFullscreen.setVisibility(isTabletLandscape() ? View.VISIBLE : View.INVISIBLE);
        } else {
            ivFullscreen.setImageResource(R.drawable.ic_fullscreen_open);
        }
    }

    @Override
    protected String getURL() {
        return story != null ? story.getUrl() : "";
    }

    @Override
    protected SuccessStoryDetailsPresenter createPresenter(Bundle savedInstanceState) {
        story = getArguments().getParcelable(EXTRA_STORY);
        return new SuccessStoryDetailsPresenter(story, getURL());
    }

    @Override
    public void showShareDialog() {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getActivity());
        builder.title(R.string.action_share)
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