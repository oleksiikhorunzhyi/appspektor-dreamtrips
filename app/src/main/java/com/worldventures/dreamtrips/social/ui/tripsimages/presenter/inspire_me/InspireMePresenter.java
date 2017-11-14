package com.worldventures.dreamtrips.social.ui.tripsimages.presenter.inspire_me;

import com.worldventures.dreamtrips.modules.common.presenter.Presenter;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.Inspiration;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.TripImagesInteractor;
import com.worldventures.dreamtrips.social.ui.tripsimages.service.command.GetInspireMePhotosCommand;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import icepick.State;
import io.techery.janet.helper.ActionStateSubscriber;

public class InspireMePresenter extends Presenter<InspireMePresenter.View> {
   private static final int VISIBLE_THRESHOLD = 5;

   double randomSeed;
   int previousScrolledTotal = 0;
   boolean loading = true;
   boolean lastPageReached = false;

   @State ArrayList<Inspiration> currentItems;

   @Inject TripImagesInteractor tripImagesInteractor;

   @Override
   public void onViewTaken() {
      super.onViewTaken();
      if (randomSeed != 0) {
         randomSeed = Math.random() * 2 - 1;
      }
      if (currentItems == null) {
         currentItems = new ArrayList<>();
      }
      view.updatePhotos(new ArrayList<>(currentItems), true);
      subscribeToNewItems();
      reload();
   }

   void subscribeToNewItems() {
      tripImagesInteractor.inspireMePhotosPipe()
            .observe()
            .filter(command -> !command.action.isFromCache())
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetInspireMePhotosCommand>()
                  .onStart(command -> {
                     loading = true;
                     view.startLoading();
                  })
                  .onFail((inspireMePhotosCommand, throwable) -> {
                     loading = false;
                     view.finishLoading();
                     handleError(inspireMePhotosCommand, throwable);
                  }).onSuccess(inspireMePhotosCommand -> {
                     loading = false;
                     lastPageReached = inspireMePhotosCommand.lastPageReached();
                     view.finishLoading();

                     boolean forceUpdate = inspireMePhotosCommand.getPage() == 1;
                     if (forceUpdate) {
                        currentItems.clear();
                     }
                     currentItems.addAll(inspireMePhotosCommand.getResult());
                     view.updatePhotos(new ArrayList<>(currentItems), forceUpdate);
                  }));
   }

   void refreshImages() {
      randomSeed = Math.random() * 2 - 1;
      tripImagesInteractor.inspireMePhotosPipe().send(GetInspireMePhotosCommand.forPage(randomSeed, 1));
   }

   public void reload() {
      loading = true;
      refreshImages();
   }

   public void onItemClick(Inspiration entity) {
      view.openFullscreen(currentItems, randomSeed, lastPageReached, currentItems.indexOf(entity));
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
      tripImagesInteractor.inspireMePhotosPipe().send(GetInspireMePhotosCommand.forPage(randomSeed,
            (currentItems.size() / GetInspireMePhotosCommand.PER_PAGE) + 1));
   }

   public interface View extends Presenter.View {
      void openFullscreen(List<Inspiration> photos, double randomSeed, boolean lastPageReached, int selectedItemIndex);

      void startLoading();

      void finishLoading();

      void updatePhotos(List<Inspiration> photoList, boolean forceUpdate);
   }
}
