package com.worldventures.dreamtrips.modules.feed.view.fragment;

import android.os.Bundle;

import com.techery.spares.annotations.Layout;
import com.worldventures.dreamtrips.R;
import com.worldventures.dreamtrips.modules.feed.presenter.BaseCommentPresenter;
import com.worldventures.dreamtrips.modules.feed.view.cell.FeedPhotoEventCell;

@Layout(R.layout.fragment_photo_fragment)
public class PhotoCommentsFragment extends CommentsFragment<BaseCommentPresenter, FeedPhotoEventCell> {

    @Override
    protected BaseCommentPresenter createPresenter(Bundle savedInstanceState) {
        return new BaseCommentPresenter();
    }
}
