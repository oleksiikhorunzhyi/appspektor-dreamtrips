package com.worldventures.dreamtrips.modules.tripsimages.api;

import com.innahema.collections.query.queriables.Queryable;
import com.worldventures.dreamtrips.core.api.request.DreamTripsRequest;
import com.worldventures.dreamtrips.modules.common.model.UploadTask;
import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;
import com.worldventures.dreamtrips.modules.feed.model.FeedEntity;
import com.worldventures.dreamtrips.modules.tripsimages.model.AddPhotoTag;
import com.worldventures.dreamtrips.modules.tripsimages.model.DeletePhotoTag;
import com.worldventures.dreamtrips.modules.tripsimages.model.Photo;

import java.util.ArrayList;
import java.util.List;

public class EditPhotoCommand extends DreamTripsRequest<Photo> {

   private String uid;
   private UploadTask task;
   private List<PhotoTag> addedTags;
   private List<PhotoTag> removedTags;

   public EditPhotoCommand(String uid, UploadTask task, List<PhotoTag> addedTags, List<PhotoTag> removedTags) {
      super(Photo.class);
      this.uid = uid;
      this.task = task;
      this.addedTags = addedTags;
      this.removedTags = removedTags;
   }

   @Override
   public Photo loadDataFromNetwork() throws Exception {
      Photo entity = getService().editTripPhoto(uid, task);

      addedTags.removeAll(entity.getPhotoTags());
      if (addedTags.size() > 0) {
         pushPhotoTags(entity);
      }
      if (removedTags.size() > 0) {
         deletePhotoTags(entity);
      }
      entity.getPhotoTags().addAll(addedTags);
      entity.getPhotoTags().removeAll(removedTags);
      entity.setPhotoTagsCount(entity.getPhotoTags().size());
      return entity;
   }

   private ArrayList<PhotoTag> pushPhotoTags(FeedEntity entity) {
      return getService().addPhotoTags(entity.getUid(), new AddPhotoTag(addedTags));
   }

   private FeedEntity deletePhotoTags(FeedEntity feedEntity) {
      List<Integer> userIds = Queryable.from(removedTags).concat(((Photo) feedEntity).getPhotoTags()).map(photo -> photo
            .getUser()
            .getId()).toList();
      getService().deletePhotoTags(feedEntity.getUid(), new DeletePhotoTag(userIds));
      return feedEntity;
   }
}
