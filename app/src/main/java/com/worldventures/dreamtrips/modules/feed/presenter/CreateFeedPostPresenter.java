package com.worldventures.dreamtrips.modules.feed.presenter;

public class CreateFeedPostPresenter extends CreateEntityPresenter<CreateFeedPostPresenter.View> {

   public static final int REQUEST_ID = CreateFeedPostPresenter.class.getSimpleName().hashCode();

   @Override
   public int getMediaRequestId() {
      return REQUEST_ID;
   }

   public interface View extends CreateEntityPresenter.View {

   }
}
