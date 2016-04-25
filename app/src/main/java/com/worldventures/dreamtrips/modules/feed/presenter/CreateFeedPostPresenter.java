package com.worldventures.dreamtrips.modules.feed.presenter;

public class CreateFeedPostPresenter extends CreateEntityPresenter<CreateFeedPostPresenter.View> {

    @Override
    public int getMediaRequestId() {
        return CreateFeedPostPresenter.class.getSimpleName().hashCode();
    }

    public interface View extends CreateEntityPresenter.View {

    }
}
