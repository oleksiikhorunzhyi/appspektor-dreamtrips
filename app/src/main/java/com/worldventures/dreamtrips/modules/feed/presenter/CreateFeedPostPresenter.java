package com.worldventures.dreamtrips.modules.feed.presenter;

public class CreateFeedPostPresenter extends CreateEntityPresenter<CreateFeedPostPresenter.View> {

    public CreateFeedPostPresenter() {
        priorityEventBus = 1;
    }

    @Override
    public int getMediaRequestId() {
        return CreateFeedPostPresenter.class.getSimpleName().hashCode();
    }

    public interface View extends CreateEntityPresenter.View {

    }
}
