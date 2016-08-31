package com.worldventures.dreamtrips.modules.trips.presenter;

import com.worldventures.dreamtrips.core.navigation.Route;
import com.worldventures.dreamtrips.core.session.acl.Feature;
import com.worldventures.dreamtrips.core.utils.tracksystem.TrackingHelper;
import com.worldventures.dreamtrips.modules.infopages.StaticPageProvider;
import com.worldventures.dreamtrips.modules.trips.command.GetTripDetailsCommand;
import com.worldventures.dreamtrips.modules.trips.model.ContentItem;
import com.worldventures.dreamtrips.modules.trips.model.TripModel;
import com.worldventures.dreamtrips.modules.trips.service.TripsInteractor;
import com.worldventures.dreamtrips.modules.tripsimages.bundle.FullScreenImagesBundle;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImage;
import com.worldventures.dreamtrips.modules.tripsimages.model.TripImagesType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.techery.janet.helper.ActionStateSubscriber;
import rx.android.schedulers.AndroidSchedulers;

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
      TrackingHelper.trip(String.valueOf(trip.getTripId()), getAccountUserId());
      loadTripDetails();
   }

   @Override
   public void onResume() {
      super.onResume();
      boolean isSoldOut = trip.isSoldOut();
      boolean canBook = featureManager.available(Feature.BOOK_TRAVEL);
      boolean showSignUpLabel = !featureManager.available(Feature.BOOK_TRAVEL);

      if (showSignUpLabel) view.showSignUp();

      if (isSoldOut) view.soldOutTrip();
      else if (!canBook || (trip.isPlatinum() && !getAccount().isPlatinum())) view.disableBookIt();
   }

   public List<TripImage> getFilteredImages() {
      return filteredImages;
   }

   public void actionBookIt() {
      TrackingHelper.actionBookIt(TrackingHelper.ATTRIBUTE_BOOK_IT, trip.getTripId(), getAccountUserId());

      String url = staticPageProvider.getBookingPageUrl(trip.getTripId());
      view.openBookIt(url);
   }

   @Override
   public void onMenuPrepared() {
      if (view != null && trip != null) {
         view.setup(trip);
      }
   }

   public void loadTripDetails() {
      view.bindUntilDropView(tripsInteractor.detailsPipe()
            .createObservable(new GetTripDetailsCommand(trip.getTripId()))
            .observeOn(AndroidSchedulers.mainThread()))
            .subscribe(new ActionStateSubscriber<GetTripDetailsCommand>().onSuccess(command -> {
               TrackingHelper.tripInfo(String.valueOf(trip.getTripId()), getAccountUserId());
               view.setContent(command.getResult().getContent());
            }).onFail((command, e) -> {
               view.setContent(null);
               view.informUser(command.getErrorMessage());
            }));
   }

   public void onItemClick(int position) {
      FullScreenImagesBundle data = new FullScreenImagesBundle.Builder().position(position)
            .route(Route.TRIP_PHOTO_FULLSCREEN)
            .type(TripImagesType.FIXED)
            .fixedList(new ArrayList<>(filteredImages))
            .build();

      view.openFullscreen(data);
   }

   public interface View extends BaseTripPresenter.View {

      void setContent(List<ContentItem> contentItems);

      void disableBookIt();

      void soldOutTrip();

      void showSignUp();

      void openFullscreen(FullScreenImagesBundle data);

      void openBookIt(String url);
   }
}
