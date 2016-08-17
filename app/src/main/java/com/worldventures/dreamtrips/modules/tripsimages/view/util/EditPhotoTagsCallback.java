package com.worldventures.dreamtrips.modules.tripsimages.view.util;

import com.worldventures.dreamtrips.modules.common.view.custom.tagview.viewgroup.newio.model.PhotoTag;

import java.util.ArrayList;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class EditPhotoTagsCallback {

   private final Subject<TagsBundle, TagsBundle> bus = new SerializedSubject<>(PublishSubject.create());

   public void onTagsSelected(long requestId, ArrayList<PhotoTag> addedTags, ArrayList<PhotoTag> removedTags) {
      bus.onNext(new TagsBundle(requestId, addedTags, removedTags));
   }

   public Observable<TagsBundle> toObservable() {
      return bus;
   }

   public static class TagsBundle {

      public long requestId;
      public ArrayList<PhotoTag> addedTags;
      public ArrayList<PhotoTag> removedTags;

      public TagsBundle(long requestId, ArrayList<PhotoTag> addedTags, ArrayList<PhotoTag> removedTags) {
         this.requestId = requestId;
         this.addedTags = addedTags;
         this.removedTags = removedTags;
      }
   }
}
