package com.worldventures.dreamtrips.modules.trips.presenter;

import com.worldventures.core.model.session.Feature;
import com.worldventures.core.modules.infopages.StaticPageProvider;
import com.worldventures.dreamtrips.modules.trips.command.GetTripDetailsCommand;
import com.worldventures.dreamtrips.modules.trips.model.ContentItem;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.service.TripsInteractor;
import com.worldventures.dreamtrips.modules.trips.service.analytics.BookItAction;
import com.worldventures.dreamtrips.modules.trips.service.analytics.ViewDreamTripsApptentiveAnalyticAction;
import com.worldventures.dreamtrips.modules.trips.view.bundle.TripViewPagerBundle;
import com.worldventures.dreamtrips.social.ui.tripsimages.model.TripImage;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;

public class TripDetailsPresenter extends BaseTripPresenter<TripDetailsPresenter.View> {

   @Inject TripsInteractor tripsInteractor;
   @Inject StaticPageProvider staticPageProvider;

   private List<TripImage> filteredImages;

   public TripDetailsPresenter(TripModel trip) {
      super(trip);
      filteredImages = new ArrayList<>();
      filteredImages.addAll(trip.getFilteredImages());
   }

   @Override
   public void takeView(View view) {
      super.takeView(view);
      analyticsInteractor.analyticsActionPipe().send(new ViewDreamTripsApptentiveAnalyticAction());
      subscribeForTripsDetails();
      loadTripDetails();
   }

   @Override
   public void onResume() {
      super.onResume();
      boolean isSoldOut = trip.isSoldOut();
      boolean canBook = featureManager.available(Feature.BOOK_TRIP);
      boolean showSignUpLabel = !canBook;

      if (showSignUpLabel) {
         view.showSignUp();
      }

      if (isSoldOut) {
         view.soldOutTrip();
      } else if (!canBook || (trip.isPlatinum() && !getAccount().isPlatinum())) {
         view.disableBookIt();
      }
   }

   @Override
   public void onMenuPrepared() {
      if (view != null && trip != null) {
         view.setup(trip);
      }
   }

   public void loadTripDetails() {
      tripsInteractor.detailsPipe().send(new GetTripDetailsCommand(trip.getUid()));
   }

   private void subscribeForTripsDetails() {
      tripsInteractor.detailsPipe()
            .observe()
            .compose(bindViewToMainComposer())
            .subscribe(new ActionStateSubscriber<GetTripDetailsCommand>()
                  .onProgress((command, progress) -> tripLoaded(command.getCachedModel()))
                  .onSuccess(command -> {
                     tripLoaded(command.getResult());
                     analyticsInteractor.analyticsActionPipe().send(new ViewDreamTripsApptentiveAnalyticAction());
                  }).onFail((command, e) -> {
                     if (command.getCacheData() == null || command.getCacheData().getContent() == null) {
                        view.setContent(null);
                     }
                     handleError(command, e);
                  }));
   }

   private void tripLoaded(TripModel tripModel) {
      trip = tripModel;
      view.setup(trip);
      view.setContent(tripModel.getContent());
   }

   public void onItemClick(int position) {
      view.openFullscreen(new TripViewPagerBundle(filteredImages, position));
   }

   public List<TripImage> getFilteredImages() {
      return filteredImages;
   }

   public void actionBookIt() {
      analyticsInteractor.analyticsActionPipe().send(new BookItAction(trip.getTripId(), trip.getName()));
      String url = staticPageProvider.getBookingPageUrl(trip.getTripId());
      view.openBookIt(url);
   }

   public interface View extends BaseTripPresenter.View {

      void setContent(List<ContentItem> contentItems);

      void disableBookIt();

      void soldOutTrip();

      void showSignUp();

      void openFullscreen(TripViewPagerBundle data);

      void openBookIt(String url);
   }
}
