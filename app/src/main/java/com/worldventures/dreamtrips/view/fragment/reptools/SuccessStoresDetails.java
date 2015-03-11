package com.worldventures.dreamtrips.view.fragment.reptools;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.techery.spares.annotations.Layout;
import com.techery.spares.annotations.MenuResource;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.core.model.SuccessStory;
import com.worldventures.dreamtrips.presentation.SuccessStoryDetailsPM;
import com.worldventures.dreamtrips.view.activity.MainActivity;
import com.worldventures.dreamtrips.view.fragment.StaticInfoFragment;

@Layout(R.layout.fragment_webview)
@MenuResource(R.menu.menu_success_stores)
public class SuccessStoresDetails extends StaticInfoFragment<SuccessStoryDetailsPM> implements SuccessStoryDetailsPM.View {

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