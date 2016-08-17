package com.messenger.ui.helper;

import com.innahema.collections.query.queriables.Queryable;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.worldventures.dreamtrips.core.utils.events.ImagePickedEvent;
import com.worldventures.dreamtrips.modules.common.presenter.GalleryPresenter;

import java.util.List;

import de.greenrobot.event.EventBus;
import rx.Observable;
import rx.subjects.PublishSubject;

//TODO picking photo from camera based on event bus, get rid of it
public class LegacyPhotoPickerDelegate {
   private EventBus eventBus;

   private PublishSubject<List<ChosenImage>> stream = PublishSubject.create();

   public LegacyPhotoPickerDelegate(EventBus eventBus) {
      this.eventBus = eventBus;
   }

   public void onEvent(ImagePickedEvent event) {
      if (event.getRequesterID() == GalleryPresenter.REQUESTER_ID) {
         eventBus.cancelEventDelivery(event);
         eventBus.removeStickyEvent(event);

         stream.onNext(Queryable.from(event.getImages()).toList());
      }
   }

   public Observable<List<ChosenImage>> watchChosenImages() {
      return stream.asObservable();
   }

   public void register() {
      eventBus.register(this);
   }

   public void unregister() {
      eventBus.unregister(this);
   }
}