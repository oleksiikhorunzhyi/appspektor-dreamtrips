package com.worldventures.dreamtrips.modules.feed.presenter;

import com.worldventures.dreamtrips.modules.feed.api.EditPostCommand;
import com.worldventures.dreamtrips.modules.feed.bundle.PostBundle;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;

import icepick.Icicle;

public class PostEditPresenter extends PostPresenter {

    @Icicle
    TextualPost textualPost;

    public PostEditPresenter(PostBundle postBundle) {
        this.textualPost = postBundle.getTextualPost();
    }

    @Override
    protected void updateUi() {
        cachedPostEntity.setText(textualPost.getDescription());
        view.hidePhotoControl();
        super.updateUi();
    }

    @Override
    protected void postTextualUpdate() {
        doRequest(new EditPostCommand(textualPost.getUid(), cachedPostEntity.getText()),
                this::processPost, spiceException -> {
                    PostEditPresenter.super.handleError(spiceException);
                    view.onPostError();
                });
    }
}