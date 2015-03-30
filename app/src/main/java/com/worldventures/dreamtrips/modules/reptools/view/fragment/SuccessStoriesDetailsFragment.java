package com.worldventures.dreamtrips.modules.reptools.view.fragment;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.infopages.view.fragment.staticcontent.StaticInfoFragment;
import com.worldventures.dreamtrips.modules.reptools.model.SuccessStory;
import com.worldventures.dreamtrips.modules.reptools.presenter.SuccessStoryDetailsFragmentPresenter;

import butterknife.InjectView;

@Layout(R.layout.fragment_webview)
@MenuResource(R.menu.menu_success_stores)
public class SuccessStoriesDetailsFragment extends StaticInfoFragment<SuccessStoryDetailsFragmentPresenter> implements SuccessStoryDetailsFragmentPresenter.View {

    public static final String STORY = "STORY";
    @InjectView(R.id.progressBarWeb)
    protected ProgressBar progressBarWeb;
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
    }

    @Override
    protected String getURL() {
        return story.getUrl();
    }

    @Override
    protected SuccessStoryDetailsFragmentPresenter createPresenter(Bundle savedInstanceState) {
        return new SuccessStoryDetailsFragmentPresenter(this);
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