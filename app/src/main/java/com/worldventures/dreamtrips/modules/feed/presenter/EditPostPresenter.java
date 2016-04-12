package com.worldventures.dreamtrips.modules.feed.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

public class EditPostPresenter extends ActionEntityPresenter<EditPostPresenter.View> {

    private TextualPost post;

    public EditPostPresenter(TextualPost post) {
        this.post = post;
    }

    @Override
    public void takeView(View view) {
        if (isCachedTextEmpty())
            cachedText = post.getDescription();
        if (hasAttachments())
            Queryable.from(post.getAttachments()).forEachR(attachment -> {
                cachedCreationItems.add(createItemFromPhoto((Photo) attachment));
            });
        //
        super.takeView(view);
        //
        updateLocation(post.getLocation());
    }

    @Override
    protected void updateUi() {
        super.updateUi();
        //
        view.attachPhotos(cachedCreationItems);
    }

    @Override
    protected boolean isChanged() {
        return !cachedText.equals(post.getDescription());
    }

    @Override
    public void post() {
        //TODO update post
    }

    private boolean hasAttachments() {
        return post.getAttachments() != null && post.getAttachments().size() > 0;
    }

    public interface View extends ActionEntityPresenter.View {

    }
}
