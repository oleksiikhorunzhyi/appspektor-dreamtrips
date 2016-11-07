package com.worldventures.dreamtrips.modules.feed.presenter;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.modules.feed.event.FeedEntityChangedEvent;
import com.worldventures.dreamtrips.modules.feed.model.CreatePhotoPostEntity;
import com.worldventures.dreamtrips.modules.feed.model.PhotoCreationItem;
import com.worldventures.dreamtrips.modules.feed.model.TextualPost;
import com.worldventures.dreamtrips.modules.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.modules.feed.service.command.EditPostCommand;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class EditPostPresenter extends ActionEntityPresenter<EditPostPresenter.View> {

   @Inject PostsInteractor postsInteractor;

   private TextualPost post;

   public EditPostPresenter(TextualPost post) {
      this.post = post;
   }

   @Override
   public void takeView(View view) {
      if (isCachedTextEmpty()) cachedText = post.getDescription();
      if (hasAttachments()) Queryable.from(post.getAttachments()).forEachR(attachment -> {
         cachedCreationItems.add(createItemFromPhoto((Photo) attachment.getItem()));
      });
      //
      super.takeView(view);
      //
      if (location == null) updateLocation(post.getLocation());
   }

   @Override
   protected void updateUi() {
      super.updateUi();
      //
      view.attachPhotos(cachedCreationItems);
   }

   @Override
   protected boolean isChanged() {
      return textWasDeletedAndPhotosExists() || textIsNonEmptyAndChanged() || (locationChanged() && postInNonEmpty());
   }

   private boolean textWasDeletedAndPhotosExists() {
      return !cachedCreationItems.isEmpty() && isCachedTextEmpty() && !cachedText.equals(post.getDescription());
   }

   private boolean textIsNonEmptyAndChanged() {
      return !isCachedTextEmpty() && !cachedText.equals(post.getDescription());
   }

   private boolean locationChanged() {
      return !post.getLocation().equals(location);
   }

   private boolean postInNonEmpty() {
      return (!isCachedTextEmpty() || !cachedCreationItems.isEmpty());
   }

   @Override
   public void post() {
      postsInteractor.getEditPostPipe()
            .createObservable(new EditPostCommand(post.getUid(), createPostObject()))
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<EditPostCommand>()
               .onSuccess(editPostCommand -> {
                  eventBus.post(new FeedEntityChangedEvent(editPostCommand.getResult()));
                  view.cancel();
               })
               .onFail((editPostCommand, throwable) -> {
                  view.cancel();
                  handleError(editPostCommand, throwable);
               }));
   }

   private CreatePhotoPostEntity createPostObject() {
      CreatePhotoPostEntity entity = new CreatePhotoPostEntity();
      entity.setDescription(cachedText);
      entity.setLocation(location);
      Queryable.from(post.getAttachments())
            .forEachR(attachment -> entity.addAttachment(new CreatePhotoPostEntity.Attachment(attachment.getItem()
                  .getUid())));
      return entity;
   }

   @Override
   protected PhotoCreationItem createItemFromPhoto(Photo photo) {
      PhotoCreationItem item = super.createItemFromPhoto(photo);
      item.setCanEdit(false);
      item.setCanDelete(false);
      return item;
   }

   private boolean hasAttachments() {
      return post.getAttachments() != null && post.getAttachments().size() > 0;
   }

   public interface View extends ActionEntityPresenter.View {

   }
}
