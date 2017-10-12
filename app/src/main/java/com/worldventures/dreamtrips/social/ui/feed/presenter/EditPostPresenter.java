package com.worldventures.dreamtrips.social.ui.feed.presenter;

import android.net.Uri;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.social.ui.feed.model.CreatePhotoPostEntity;
import com.worldventures.dreamtrips.social.ui.feed.model.ImmutableVideoCreationModel;
import com.worldventures.dreamtrips.social.ui.feed.model.PhotoCreationItem;
import com.worldventures.dreamtrips.social.ui.feed.model.TextualPost;
import com.worldventures.dreamtrips.social.ui.feed.model.VideoCreationModel;
import com.worldventures.dreamtrips.social.ui.feed.model.video.Video;
import com.worldventures.dreamtrips.social.ui.feed.service.PostsInteractor;
import com.worldventures.dreamtrips.social.ui.feed.service.command.EditPostCommand;
import com.worldventures.dreamtrips.modules.media_picker.model.VideoMetadata;
import com.worldventures.dreamtrips.modules.media_picker.service.command.GetVideoMetadataCommand;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Photo;

import javax.inject.Inject;

import io.techery.janet.Command;
import io.techery.janet.helper.ActionStateSubscriber;
import rx.Observable;

public class EditPostPresenter extends ActionEntityPresenter<ActionEntityPresenter.View> {

   @Inject PostsInteractor postsInteractor;

   protected TextualPost post;

   public EditPostPresenter(TextualPost post) {
      this.post = post;
   }

   @Override
   public void takeView(View view) {
      if (isCachedTextEmpty()) cachedText = post.getDescription();
      if (hasPhotoAttachments()) {
         Queryable.from(post.getAttachments())
               .forEachR(attachment -> cachedCreationItems.add(createItemFromPhoto((Photo) attachment.getItem())));
      } else if (hasVideoAttachments()) {
         selectedVideoPathUri = Uri.parse(((Video) post.getAttachments().get(0).getItem()).getThumbnail());
      }
      super.takeView(view);
      if (location == null) updateLocation(post.getLocation());
   }

   @Override
   protected Observable<VideoCreationModel> getVideoMetadata() {
      return mediaMetadataInteractor.videoMetadataCommandActionPipe()
            .createObservableResult(new GetVideoMetadataCommand((Video) post.getAttachments().get(0).getItem()))
            .map(Command::getResult)
            .map(this::getVideoCreationModel);
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
                  .onSuccess(editPostCommand -> view.cancel())
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

   private boolean hasPhotoAttachments() {
      return post.getAttachments() != null && post.getAttachments().size() > 0
            && post.getAttachments().get(0).getItem() instanceof Photo;
   }

   private boolean hasVideoAttachments() {
      return post.getAttachments() != null && post.getAttachments().size() > 0
            && post.getAttachments().get(0).getItem() instanceof Video;
   }

   @Override
   protected boolean canDeleteVideo() {
      return false;
   }
}
