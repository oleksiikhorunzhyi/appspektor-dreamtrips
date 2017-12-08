package com.worldventures.dreamtrips.social.ui.tripsimages.presenter.ysbh;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.YSBHPhoto;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.analytics.TripImageItemViewEvent;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.GetYSBHPhotosCommand;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;

public class YouShouldBeHerePresenter extends Presenter<YouShouldBeHerePresenter.View> {
   public static final int VISIBLE_THRESHOLD = 5;

   int previousScrolledTotal = 0;
   boolean loading = true;
   boolean lastPageReached = false;

   @State ArrayList<YSBHPhoto> currentItems;

   @Inject TripImagesInteractor tripImagesInteractor;

   @Override
   public void onViewTaken() {
      super.onViewTaken();
      if (currentItems == null) {
         currentItems = new ArrayList<>();
      } else {
         currentItems = new ArrayList<>(currentItems);
      }
      view.updatePhotos(currentItems);
      subscribeToNewItems();
      reload();
   }

   void subscribeToNewItems() {
      tripImagesInteractor.ysbhPhotosPipe()
            .observe()
            .filter(command -> !command.action.isFromCache())
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetYSBHPhotosCommand>()
                  .onStart(command -> {
                     loading = true;
                     view.startLoading();
                  })
                  .onFail((getYSBHPhotosCommand, throwable) -> {
                     loading = false;
                     view.finishLoading();
                     handleError(getYSBHPhotosCommand, throwable);
                  })
                  .onSuccess(getYSBHPhotosCommand -> {
                     view.finishLoading();
                     loading = false;
                     lastPageReached = getYSBHPhotosCommand.lastPageReached();
                     if (getYSBHPhotosCommand.getPage() == 1) {
                        currentItems.clear();
                     }
                     currentItems = new ArrayList<>(currentItems);
                     currentItems.addAll(getYSBHPhotosCommand.getResult());
                     view.updatePhotos(currentItems);
                  }));
   }

   void refreshImages() {
      loading = true;
      tripImagesInteractor.ysbhPhotosPipe().send(GetYSBHPhotosCommand.commandForPage(1));
   }

   public void reload() {
      refreshImages();
   }

   public void onItemClick(YSBHPhoto entity) {
      view.openFullscreen(new ArrayList<>(currentItems), lastPageReached, currentItems.indexOf(entity));
      analyticsInteractor.analyticsActionPipe().send(new TripImageItemViewEvent(String.valueOf(entity.getId())));
   }

   public void scrolled(int visibleItemCount, int totalItemCount, int firstVisibleItem) {
      if (totalItemCount > previousScrolledTotal) {
         loading = false;
         previousScrolledTotal = totalItemCount;
      }
      if (!lastPageReached && !loading && currentItems.size() > 0
            && (totalItemCount - visibleItemCount) <= (firstVisibleItem + VISIBLE_THRESHOLD)) {
         loadNext();
      }
   }

   void loadNext() {
      loading = true;
      tripImagesInteractor.ysbhPhotosPipe()
            .send(GetYSBHPhotosCommand.commandForPage((currentItems.size() / GetYSBHPhotosCommand.PER_PAGE) + 1));
   }

   public interface View extends Presenter.View {
      void startLoading();

      void finishLoading();

      void openFullscreen(List<YSBHPhoto> photos, boolean lastPageReached, int selectedItemIndex);

      void updatePhotos(List<YSBHPhoto> photoList);
   }
}
