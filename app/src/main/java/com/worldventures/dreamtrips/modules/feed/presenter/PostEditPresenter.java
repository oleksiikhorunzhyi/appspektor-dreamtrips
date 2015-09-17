package com.worldventures.dreamtrips.modules.feed.presenter;

import com.worldventures.dreamtrips.modules.feed.api.EditPostCommand;
import com.worldventures.dreamtrips.modules.feed.bundle.PostBundle;
import com.worldventures.dreamtrips.modules.feed.event.TextualPostChangedEvent;
import com.worldventures.dreamtrips.modules.feed.model.IFeedObject;
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
    public void postInputChanged(String input) {
        super.postInputChanged(input);
        textualPost.setDescription(input);
    }

    @Override
    protected void postTextualUpdate() {
        doRequest(new EditPostCommand(textualPost.getUid(), cachedPostEntity.getText()),
                this::processPost, spiceException -> {
                    PostEditPresenter.super.handleError(spiceException);
                    view.onPostError();
                });
    }

    @Override
    protected void processPost(IFeedObject iFeedObject) {
        if (iFeedObject instanceof TextualPost) {
            eventBus.post(new TextualPostChangedEvent((TextualPost) iFeedObject));
            view.cancel();
            view = null;
        }
    }
}
